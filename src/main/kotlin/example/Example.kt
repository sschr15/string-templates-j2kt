package com.sschr15.templates.example

import com.sschr15.templates.invoke

class SqlError : Throwable()

data class SqlQuery(val query: String, val args: List<Any?>, val db: String)

fun sqlTemplate(db: String): StringTemplate.Processor<SqlQuery, SqlError> = TODO()

fun example() {
    val template: StringTemplate.Processor<SqlQuery, SqlError> = sqlTemplate("my-data")
    val dangerousInput = "'; DROP TABLE users; --"
    val query = template { "SELECT * FROM users WHERE id = ${!dangerousInput}" }
    println(query) // SqlQuery(query=SELECT * FROM users WHERE id = ?, args=[...], db=my-data)
}
