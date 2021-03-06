# AORM

[ ![Download](https://api.bintray.com/packages/tanvd/aorm/aorm/images/download.svg) ](https://bintray.com/tanvd/aorm/aorm/_latestVersion)

AORM is analytical SQL framework. Basically, it is a fork of [Exposed SQL framework](https://github.com/JetBrains/Exposed) for ClickHouse database dialect.

AORM supports Exposed-like DSL for standard SQL operations and rich set of ClickHouse dialect features (like state-aggregation functions, different engines, replicated context of execution and so on)


## Setup

AORM releases are published to [JCenter](https://bintray.com/tanvd/aorm/aorm).

Also you can get snapshot versions from [Artifactory](https://oss.jfrog.org) (see libs-snapshot/tanvd/aorm/aorm packages group)

## How to

First of all you'll need to setup Database object. Provide it with DataSource (self-constructed, JNDI -- it doesn't matter, but don't forget to use pooling :) ). In context of Database (`withContext(db)` call) you will perform all operations.

```
val TestDatabase = Database("default", 
         ClickHouseDataSource("jdbc:clickhouse://localhost:8123",
                 ClickHouseProperties().apply {
                     user = "default"
                     password = ""
                 }))
```

If you have replicated cluster and want to balance load you may need to setup few Database objects and use ReplicatedConnectionContext.

Once Database is created you can setup Table objects. Right now AORM supports a lot of ClickHouse types, but does not support nullability. Instead, null values will be fallbacked to ClickHouse defaults. Support of nullability is considered to be implemented.

```
object TestTable : Table("test_table") {
     val dateCol = date("date_col")
     val int8Col = int8("int8_col")
     val int64Col = int64("int64_col")
     val stringCol = string("string_col")
     val arrayStringCol = arrayString("arrayString_col")
 
     override val engine: Engine = Engine.MergeTree(dateCol, listOf(dateCol))
}
 ```

Please note, that table is not linked to specific database. Table object is only declaration of scheme. You can use it in different contexts during work with different databases.

Once you have create table object, you can align scheme of your table with you database. Aligning of scheme means, that table will be created if it does not exist, or, if it exists, not existing columns will be added. AORM, by default, not performing any removal operations on aligning of scheme. It will not drop not existing tables or columns.

```
withDatabase(TestDatabase) {
    TestTable.syncScheme() // this call will align scheme of TestTable in TestDatabase
}
```

Once everything is setup, you can insert some data:

```
withDatabase(TestDatabase) {
    TestTable.insert {
        it[TestTable.dateCol] = SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01")
        it[TestTable.int8Col] = 1.toByte()
        it[TestTable.int64Col] = 1L
        it[TestTable.stringCol] = "test"
        it[TestTable.arrayStringCol] = listOf("test1", "test2")
    }
}
```

And then load it:

```
withDatabase(TestDatabase) {
    val query = TestTable.select() where (TestTable.int8Col eq 1.toByte())
    val res = query.toResult()
}
```

There are a lot more query functions, types of columns, and so on. You can take a closer look at all of features of AORM at it's tests or at next section


## More examples

A lot of examples of AORM production usage are located at [JetAudit library](https://github.com/TanVD/JetAudit). JetAudit is library for reliable and fast saving of business processes events (audit-events) and for reading of such events. It uses ClickHouse as data warehouse and AORM is used as programmatic interface.