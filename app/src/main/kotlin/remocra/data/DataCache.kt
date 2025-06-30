package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.CriseCategorie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.pojos.Materiau
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Niveau
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAcces
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAction
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAvis
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeDebroussaillement
import remocra.db.jooq.remocra.tables.pojos.OldebTypeResidence
import remocra.db.jooq.remocra.tables.pojos.OldebTypeSuite
import remocra.db.jooq.remocra.tables.pojos.OldebTypeZoneUrbanisme
import remocra.db.jooq.remocra.tables.pojos.RcciIndiceRothermel
import remocra.db.jooq.remocra.tables.pojos.RcciTypeDegreCertitude
import remocra.db.jooq.remocra.tables.pojos.RcciTypeOrigineAlerte
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeCategorie
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeFamille
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheePartition
import remocra.db.jooq.remocra.tables.pojos.Reservoir
import remocra.db.jooq.remocra.tables.pojos.TypeCanalisation
import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import remocra.db.jooq.remocra.tables.pojos.TypeEngin
import remocra.db.jooq.remocra.tables.pojos.TypeOrganisme
import remocra.db.jooq.remocra.tables.pojos.TypeReseau
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import java.util.UUID

data class DataCache(
    var mapAnomalie: Map<UUID, Anomalie>,
    var mapAnomalieCategorie: Map<UUID, AnomalieCategorie>,
    var mapCouches: Map<UUID, CoucheData>,
    var mapCriseCategorie: Map<UUID, CriseCategorie>,
    var mapDiametre: Map<UUID, Diametre>,
    var mapDomaine: Map<UUID, Domaine>,
    var mapMarquePibi: Map<UUID, MarquePibi>,
    var mapMateriau: Map<UUID, Materiau>,
    var mapModelePibi: Map<UUID, ModelePibi>,
    var mapNature: Map<UUID, Nature>,
    var mapNatureDeci: Map<UUID, NatureDeci>,
    var mapNiveau: Map<UUID, Niveau>,
    var mapOldebTypeAcces: Map<UUID, OldebTypeAcces>,
    var mapOldebTypeAction: Map<UUID, OldebTypeAction>,
    var mapOldebTypeAnomalie: Map<UUID, OldebTypeAnomalie>,
    var mapOldebTypeAvis: Map<UUID, OldebTypeAvis>,
    var mapOldebTypeCaracteristique: Map<UUID, OldebTypeCaracteristique>,
    var mapOldebTypeCategorieAnomalie: Map<UUID, OldebTypeCategorieAnomalie>,
    var mapOldebTypeCategorieCaracteristique: Map<UUID, OldebTypeCategorieCaracteristique>,
    var mapOldebTypeDebrousaillement: Map<UUID, OldebTypeDebroussaillement>,
    var mapOldebTypeResidence: Map<UUID, OldebTypeResidence>,
    var mapOldebTypeSuite: Map<UUID, OldebTypeSuite>,
    var mapOldebTypeZoneUrbanisme: Map<UUID, OldebTypeZoneUrbanisme>,
    var mapRcciIndiceRothermel: Map<UUID, RcciIndiceRothermel>,
    var mapRcciTypeDegreCertitude: Map<UUID, RcciTypeDegreCertitude>,
    var mapRcciTypeOrigineAlerte: Map<UUID, RcciTypeOrigineAlerte>,
    var mapRcciTypePrometheeCategorie: Map<UUID, RcciTypePrometheeCategorie>,
    var mapRcciTypePrometheeFamille: Map<UUID, RcciTypePrometheeFamille>,
    var mapRcciTypePrometheePartition: Map<UUID, RcciTypePrometheePartition>,
    var mapReservoir: Map<UUID, Reservoir>,
    var mapTypeCanalisation: Map<UUID, TypeCanalisation>,
    var mapTypeCrise: Map<UUID, TypeCrise>,
    var mapTypeEngin: Map<UUID, TypeEngin>,
    var mapTypeOrganisme: Map<UUID, TypeOrganisme>,
    var mapTypeReseau: Map<UUID, TypeReseau>,
    var utilisateurSysteme: Utilisateur,
)
