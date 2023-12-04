package com.sschr15.templates.test

import com.sschr15.templates.invoke
import java.lang.StringTemplate.RAW
import java.lang.StringTemplate.STR
import java.util.FormatProcessor.FMT
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlError : Throwable()

data class SqlQuery(val query: String, val args: List<Any?>)

class Tests {
    @Test
    fun `Traditional STR template works`() {
        val text = STR { "Hello, ${!"world"}!" }
        assertEquals("Hello, world!", text)
    }

    @Test
    fun `RAW template provides StringTemplate containing exact parts`() {
        val text = RAW { "Hello, ${!"world"}! You're number ${!1}!" }
        val expected = StringTemplate.of(listOf("Hello, ", "! You're number ", "!"), listOf("world", 1))
        assertEquals(expected, text)
    }

    @Test
    fun `FMT template returns values as expected`() {
        val text = FMT { "Pi is approximately %.2f${!Math.PI}" }
        assertEquals("Pi is approximately 3.14", text)
    }

    @Test
    fun `SQL avoids SQL injection`() {
        val template = StringTemplate.Processor<SqlQuery, SqlError> {
            val query = it.fragments().joinToString("?")
            val args = it.values().toList()
            SqlQuery(query, args)
        }

        val query = template { "SELECT * FROM users WHERE id = ${!123}" }
        assertEquals(SqlQuery("SELECT * FROM users WHERE id = ?", listOf(123)), query)
    }
}
