package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.pojos.Materiau
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Niveau
import remocra.db.jooq.remocra.tables.pojos.TypeCanalisation
import remocra.db.jooq.remocra.tables.pojos.TypeReseau
import java.util.UUID

data class DataCache(
    var mapAnomalie: Map<UUID, Anomalie>,
//    var mapCommune: Map<UUID, Commune>, // TODO 'tention, volume important, à qualifier (SANS géométrie si possible)
    var mapDiametre: Map<UUID, Diametre>,
    var mapDomaine: Map<UUID, Domaine>,
    var mapMarquePibi: Map<UUID, MarquePibi>,
    var mapMateriau: Map<UUID, Materiau>,
    var mapModelePibi: Map<UUID, ModelePibi>,
    var mapNature: Map<UUID, Nature>,
    var mapNatureDeci: Map<UUID, NatureDeci>,
    var mapNiveau: Map<UUID, Niveau>,
    var mapTypeCanalisation: Map<UUID, TypeCanalisation>,
    var mapTypeReseau: Map<UUID, TypeReseau>,
)
