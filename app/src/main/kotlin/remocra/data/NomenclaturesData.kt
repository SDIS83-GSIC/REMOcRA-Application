package remocra.data

import remocra.db.jooq.tables.pojos.Diametre
import remocra.db.jooq.tables.pojos.Domaine
import remocra.db.jooq.tables.pojos.MarquePibi
import remocra.db.jooq.tables.pojos.Materiau
import remocra.db.jooq.tables.pojos.ModelePibi
import remocra.db.jooq.tables.pojos.Nature
import remocra.db.jooq.tables.pojos.NatureDeci
import remocra.db.jooq.tables.pojos.Niveau
import remocra.db.jooq.tables.pojos.TypeCanalisation
import remocra.db.jooq.tables.pojos.TypeReseau
import java.util.UUID

data class NomenclaturesData(
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
