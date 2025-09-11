package remocra.data

import jakarta.servlet.http.Part
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import java.util.UUID

data class ModeleCourrierData(
    val modeleCourrierId: UUID? = UUID.randomUUID(),
    val modeleCourrierActif: Boolean,
    val modeleCourrierCode: String,
    val modeleCourrierLibelle: String,
    val modeleCourrierDescription: String?,
    val modeleCourrierSourceSql: String,
    val modeleCourrierModule: TypeModuleRapportCourrier,
    val modeleCourrierCorpsEmail: String,
    val modeleCourrierObjetEmail: String,
    val listeGroupeFonctionnalitesId: Collection<UUID>,
    val listeModeleCourrierParametre: Collection<ModeleCourrierParametreData>,
    val documentId: UUID?,
    val documentNomFichier: String?,
    val documentRepertoire: String?,
    val part: Part?,
)

data class ModeleCourrierParametreData(
    val modeleCourrierParametreId: UUID = UUID.randomUUID(),
    val modeleCourrierParametreCode: String,
    val modeleCourrierParametreLibelle: String,
    val modeleCourrierParametreSourceSql: String?,
    val modeleCourrierParametreDescription: String?,
    val modeleCourrierParametreSourceSqlId: String?,
    val modeleCourrierParametreSourceSqlLibelle: String?,
    val modeleCourrierParametreValeurDefaut: String?,
    val modeleCourrierParametreIsRequired: Boolean,
    val modeleCourrierParametreType: TypeParametreRapportCourrier,
    val modeleCourrierParametreOrdre: Int,
)
