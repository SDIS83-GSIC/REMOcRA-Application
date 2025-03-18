package remocra.data

import jakarta.servlet.http.Part
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import java.time.ZonedDateTime
import java.util.UUID

data class CriseData(
    val criseId: UUID,
    val criseLibelle: String?,
    val criseDescription: String?,
    val criseDateDebut: ZonedDateTime?,
    val criseDateFin: ZonedDateTime?,
    val criseTypeCriseId: UUID?,
    val criseStatutType: TypeCriseStatut,
    val listeCommuneId: Collection<UUID>?,
    val listeToponymieId: Collection<UUID>?,
    val couchesWMS: Collection<CouchesData>? = null,
)

data class EvenementGeometrieData(
    val eventId: UUID,
    val eventGeometrie: Geometry,
)

data class TypeToponymies(
    val typeToponymieId: UUID,
    val typeToponymieLibelle: String?,
    val typeToponymieCode: String?,
)

data class CreateDoc(
    val criseId: UUID,
    val criseDocName: String,
    val criseDocument: Part?,
    val criseDocumentGeometrie: Geometry,
)

data class CouchesWms(
    val coucheId: UUID,
    val coucheCode: String,
    val coucheLibelle: String,
)

data class CouchesData(
    val coucheId: UUID?,
    val anticipation: Boolean,
    val operationnel: Boolean,
    val libelle: String? = null,
    val code: String? = null,
)
