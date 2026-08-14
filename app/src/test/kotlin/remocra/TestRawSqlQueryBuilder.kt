package remocra

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import remocra.db.rawsql.RawSqlQueryBuilder.Companion.toRawSqlQueryBuilder

class TestRawSqlQueryBuilder {

    private val ctx = DSL.using(SQLDialect.POSTGRES)

    @Test
    fun `On peut avoir une simple query`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table WHERE id = 'RENFORCE' AND toto #DSM_DEBIT_RETENU#"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withBindParam ("RENFORCE" to "Salut ça va")
        rawSqlQueryBuilder withRawReplace (Regex("#DSM_DEBIT_RETENU#") to "IS NOT NULL")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table WHERE id = ? AND toto IS NOT NULL",
            requete.toString(),
        )
        assertEquals(
            listOf("Salut ça va"),
            ctx.extractBindValues(requete),
        )
    }

    @Test
    fun `On peut avoir une simple query avec et sans quotes dans les params à remplacer`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table WHERE id = RENFORCE AND toto IS 'RENFORCE'"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withBindParam ("RENFORCE" to "Salut ça va")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table WHERE id = ? AND toto IS ?",
            requete.toString(),
        )
        assertEquals(
            listOf("Salut ça va", "Salut ça va"),
            ctx.extractBindValues(requete),
        )
    }

    @Test
    fun `On peut avoir plusieurs occurrence dans la requête`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table WHERE id = 'RENFORCE' AND toto = 'RENFORCE'"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withBindParam ("RENFORCE" to "Salut ça va")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table WHERE id = ? AND toto = ?",
            requete.toString(),
        )
        assertEquals(
            listOf("Salut ça va", "Salut ça va"),
            ctx.extractBindValues(requete),
        )
    }

    @Test
    fun `On peut avoir plusieurs occurrence dans la requête et avec des raw replace`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table WHERE id = 'RENFORCE' AND toto = 'RENFORCE' AND test #DSM_DEBIT_RETENU#"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withRawReplace (Regex("#DSM_DEBIT_RETENU#") to "IS NOT NULL")
        rawSqlQueryBuilder withBindParam ("RENFORCE" to "Salut ça va")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table WHERE id = ? AND toto = ? AND test IS NOT NULL",
            requete.toString(),
        )
        assertEquals(
            listOf("Salut ça va", "Salut ça va"),
            ctx.extractBindValues(requete),
        )
    }

    @Test
    fun `On peut avoir plusieurs occurrence de raw replace dans la requête`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table WHERE id #TEST# AND toto #AZE#"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withRawReplace (Regex("#AZE#") to "IS NULL")
        rawSqlQueryBuilder withRawReplace (Regex("#TEST#") to "IS NOT NULL")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table WHERE id IS NOT NULL AND toto IS NULL",
            requete.toString(),
        )
        assertEquals(
            emptyList<Any>(),
            ctx.extractBindValues(requete),
        )
    }

    @Test
    fun `On peut avoir plusieurs occurrence de raw replace et de bind params dans la requête dans n'importe qu'elle ordre`() {
        val rawSqlQueryBuilder = "SELECT * FROM my_table LEFT JOIN 'OSAMODAS' WHERE id #TEST# AND toto #AZE# AND 'OSAMODAS' = 'IOP' OR 'CAFE' IS NOT NULL"
            .toRawSqlQueryBuilder()

        rawSqlQueryBuilder withRawReplace (Regex("#AZE#") to "IS NULL")
        rawSqlQueryBuilder withRawReplace (Regex("#TEST#") to "IS NOT NULL")
        rawSqlQueryBuilder withBindParam ("CAFE" to "As-tu la référence ?")
        rawSqlQueryBuilder withBindParam ("IOP" to "Encore du travail")
        rawSqlQueryBuilder withBindParam ("OSAMODAS" to "Par dessus la 3eme corde")

        val requete = rawSqlQueryBuilder.build()
        assertEquals(
            "SELECT * FROM my_table LEFT JOIN ? WHERE id IS NOT NULL AND toto IS NULL AND ? = ? OR ? IS NOT NULL",
            requete.toString(),
        )
        assertEquals(
            listOf(
                "Par dessus la 3eme corde",
                "Par dessus la 3eme corde",
                "Encore du travail",
                "As-tu la référence ?",
            ),
            ctx.extractBindValues(requete),
        )
    }
}
