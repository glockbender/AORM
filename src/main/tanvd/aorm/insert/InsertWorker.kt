package tanvd.aorm.insert

import org.slf4j.LoggerFactory
import tanvd.aorm.Database
import tanvd.aorm.implementation.InsertClickhouse
import tanvd.aorm.namedFactory
import tanvd.aorm.shutdownNowGracefully
import tanvd.aorm.withExceptionLogger
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

interface InsertWorker {
    fun add(db: Database, insertExpression: InsertExpression)

    fun stop()
}

//TODO-tanvd need to work on delays between inserts
class DefaultInsertWorker(val name: String, queueSizeMax: Int = 10000, delayTimeMs: Long = 30 * 1000, betweenCallsTimeMs: Long = 10 * 1000): InsertWorker {

    private val logger = LoggerFactory.getLogger(DefaultInsertWorker::class.java)

    private var isWorking = AtomicBoolean(true)


    private val insertWorker = Executors.newSingleThreadScheduledExecutor(namedFactory(name)).withExceptionLogger(logger)
    private val assetUsageQueue = ArrayBlockingQueue<Pair<Database, InsertExpression>>(queueSizeMax)

    init {
        insertWorker.scheduleWithFixedDelay({
            val data = arrayListOf<Pair<Database, InsertExpression>>().apply { assetUsageQueue.drainTo(this) }
            data.groupBy { it.first }.mapValues { it.value.map { it.second } }.forEach { (db, data) ->
                val groupedByTables = data.groupBy { it.table }
                for ((table, inserts) in groupedByTables) {
                    val columns = table.columnsWithDefaults.toMutableSet()
                    columns += inserts.flatMap { it.columns }
                    val batchedInsertExpression = InsertExpression(table, columns, inserts.flatMap { it.values }.toMutableList())
                    InsertClickhouse.insert(db, batchedInsertExpression)
                    Thread.sleep(betweenCallsTimeMs)
                }
            }

        }, delayTimeMs, delayTimeMs, TimeUnit.MILLISECONDS)
    }

    override fun add(db: Database, insertExpression: InsertExpression) {
        if (isWorking.get()) {
            assetUsageQueue.add(db to insertExpression)
        }
    }

    override fun stop() {
        isWorking.set(false)
        insertWorker.shutdownNowGracefully(logger)
    }

}