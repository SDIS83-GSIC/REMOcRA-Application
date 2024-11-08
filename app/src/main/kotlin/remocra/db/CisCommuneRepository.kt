package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.L_COMMUNE_CIS
import remocra.db.jooq.remocra.tables.references.ORGANISME
import java.util.UUID

class CisCommuneRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getCisCommune(): Collection<GlobalData.IdCodeLibelleLienData> =
        dsl.select(
            ORGANISME.ID.`as`("id"),
            ORGANISME.CODE.`as`("code"),
            ORGANISME.LIBELLE.`as`("libelle"),
            L_COMMUNE_CIS.COMMUNE_ID.`as`("lienId"),
        )
            .from(ORGANISME)
            .join(L_COMMUNE_CIS)
            .on(ORGANISME.ID.eq(L_COMMUNE_CIS.CIS_ID))
            .fetchInto()

    data class CisWithCommune(
        val id: UUID,
        val code: String,
        val libelle: String,
        val communeId: UUID,
    )
}
