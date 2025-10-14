package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.impl.DSL
import remocra.GlobalConstants

class EntrepotSigRepository @Inject constructor(private val dsl: DSLContext) {

    /** Méthode permettant d'exécuter n'importe quelle requete SQL
     *  Est utilisée dans le cadre de la synchronisation SIG, pour le ScriptPostRecuperation
     *  pour les tables dont synchro avec remocra n'est pas prévue
     */
    fun executeFromString(requete: String) =
        dsl.execute(requete)

    fun dropTable(tableDestination: String) =
        dsl.execute("DROP TABLE IF EXISTS ${GlobalConstants.SCHEMA_ENTREPOT_SIG}.$tableDestination;")

    fun createTable(tableDestination: String, concatColumn: String) =
        dsl.execute("CREATE TABLE ${GlobalConstants.SCHEMA_ENTREPOT_SIG}.$tableDestination ($concatColumn);")

    fun insertAllInto(results: Result<Record>, nomTableDestination: String) =
        dsl.batch(
            results.map { record ->
                dsl.insertInto(
                    DSL.table(
                        "${GlobalConstants.SCHEMA_ENTREPOT_SIG}.$nomTableDestination",
                    ),
                ).set(record.intoMap())
            },
        ).execute()
}
