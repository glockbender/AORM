package tanvd.aorm.implementation.query

import org.testng.Assert
import org.testng.annotations.Test
import tanvd.aorm.implementation.QueryClickhouse
import tanvd.aorm.query.*
import tanvd.aorm.withDatabase
import tanvd.aorm.utils.ExampleTable
import tanvd.aorm.utils.TestDatabase

class QueryPrimitiveConditionTest {
    @Test
    fun eq_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id eq 1L)
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id = 1) ;")
        }
    }

    @Test
    fun less_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id less 1L)
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id < 1) ;")
        }
    }

    @Test
    fun lessOrEq_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id lessOrEq 1L)
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id <= 1) ;")
        }
    }

    @Test
    fun more_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id more 1L)
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id > 1) ;")
        }
    }

    @Test
    fun moreOrEq_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id moreOrEq 1L)
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id >= 1) ;")
        }
    }

    @Test
    fun between_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id between (1L to 2L))
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id between 1 and 2) ;")
        }
    }


    @Test
    fun like_stringValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.value like "string")
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (value LIKE 'string') ;")
        }
    }

    @Test
    fun regex_stringValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.value regex "string")
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (match(value, 'string')) ;")
        }
    }

    @Test
    fun inList_longValue_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id inList listOf(1L, 2L))
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (id in (1, 2)) ;")
        }
    }

    @Test
    fun inList_emptyList_sqlValid() {
        withDatabase(TestDatabase) {
            val expression = (ExampleTable.id inList emptyList())
            val query = ExampleTable.select() where expression

            val sql = QueryClickhouse.constructQuery(query)
            Assert.assertEquals(sql, "SELECT ${ExampleTable.columns.joinToString { it.name }} FROM" +
                    " ExampleTable WHERE (false) ;")
        }
    }
}