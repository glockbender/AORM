package utils

import org.jetbrains.annotations.TestOnly
import tanvd.aorm.Engine
import tanvd.aorm.Table
import tanvd.aorm.expression.default
import tanvd.aorm.withDatabase

object ExampleTable : Table("ExampleTable") {
    val date = date("date")

    val id = int64("id").default { 1L }
    val value = string("value")

    val arrayValue = arrayString("string_array").default { listOf("array1", "array2") }

    override val engine = Engine.MergeTree(date, listOf(id), 8192)

    @TestOnly
    fun resetTable() {
        withDatabase(TestDatabase) {
            ignoringExceptions {
                ExampleTable.drop()
            }
        }
        ExampleTable.columns.clear()
        columns.add(date)
        columns.add(id)
        columns.add(value)
        columns.add(arrayValue)
    }
}