package remocra.data

import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import java.util.UUID

data class RapportPersonnaliseData(
    val rapportPersonnaliseId: UUID,
    val rapportPersonnaliseActif: Boolean,
    val rapportPersonnaliseCode: String,
    val rapportPersonnaliseLibelle: String,
    val rapportPersonnaliseChampGeometrie: String?,
    val rapportPersonnaliseDescription: String?,
    val rapportPersonnaliseSourceSql: String,
    val rapportPersonnaliseModule: TypeModuleRapportCourrier,
    val rapportPersonnaliseProtected: Boolean,
    val listeGroupeFonctionnalitesId: Collection<UUID>,
    val listeRapportPersonnaliseParametre: Collection<RapportPersonnaliseParametreData>,
)

data class RapportPersonnaliseParametreData(
    val rapportPersonnaliseParametreId: UUID = UUID.randomUUID(),
    val rapportPersonnaliseParametreCode: String,
    val rapportPersonnaliseParametreLibelle: String,
    val rapportPersonnaliseParametreSourceSql: String?,
    val rapportPersonnaliseParametreDescription: String?,
    val rapportPersonnaliseParametreSourceSqlId: String?,
    val rapportPersonnaliseParametreSourceSqlLibelle: String?,
    val rapportPersonnaliseParametreValeurDefaut: String?,
    val rapportPersonnaliseParametreIsRequired: Boolean,
    val rapportPersonnaliseParametreType: TypeParametreRapportCourrier,
    val rapportPersonnaliseParametreOrdre: Int,
)

data class IdLibelleRapportPersonnalise(
    val id: String,
    val libelle: String?,
)

data class GenererRapportPersonnaliseData(
    val rapportPersonnaliseId: UUID,
    val listeParametre: Collection<Parametre>,
)

data class Parametre(
    val rapportPersonnaliseParametreCode: String,
    val value: String?,
)
