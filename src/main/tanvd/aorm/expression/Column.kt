package tanvd.aorm.expression

import tanvd.aorm.DbType
import tanvd.aorm.Table

class Column<E, out T : DbType<E>>(val name: String, type: T, val table: Table,
                                   default: (() -> E)? = null) : Expression<E, T>(type) {
    var defaultFunction: (() -> E)? = default


    fun toSqlDef(): String = "$name ${type.toSqlName()}"

    override fun toSql(): String = name

    /** Equals by name and table **/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column<*, *>

        if (name != other.name) return false
        if (table != other.table) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + table.hashCode()
        return result
    }
}


//Helper function
infix fun <E : Any, T : DbType<E>> Column<E, T>.default(func: () -> E): Column<E, T> {
    defaultFunction = func
    return this
}
