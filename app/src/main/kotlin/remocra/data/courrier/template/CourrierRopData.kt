package remocra.data.courrier.template

import org.jooq.JSONB
import remocra.auth.UserInfo
import remocra.db.CourrierRopRepository

/**
 * Tous les attributs sont nullables. On les remplit en fonction des SDIS
 */
class CourrierRopData(
    // Commun à tous les courriers
    override val courrierPath: String,
    override val courrierSubReport: JSONB?,
    override val userConnecte: UserInfo,
    override val dateGeneration: String,
    override var destinataire: String? = null,
    override var destinataireEmail: String? = null,
    override var destinataireVoie: String? = null,
    override var destinataireVille: String? = null,
    override var destinataireLieuDit: String? = null,
    override var destinataireCodePostal: String? = null,
    override var expediteur: String = "${userConnecte.prenom} ${userConnecte.nom}",

    // Juste pour les ROP
    var affaireSuiviePar: String? = null,
    var affaireSuivieParEmail: String? = null,
    var affaireSuivieParInitiales: String? = null,

    var communeLibelle: String? = null,
    var annee: String? = null,
    var groupement: String? = null,
    var cis: String? = null,
    var groupementTelephone: String? = null,
    var groupementEmail: String? = null,

    // Liste des PEI indisponibles
    var listPeiIndispoWithAnomalie: Collection<CourrierRopRepository.PeiIndisponible>? = null,

    // Liste de tous les PIBI
    var listPibiWithAnomalie: Collection<CourrierRopRepository.PibiRop>? = null,

    // Liste de tous les PENA
    var listPenaWithAnomalie: Collection<CourrierRopRepository.PenaRop>? = null,

    // Liste de tous les PEI
    var listPeiWithAnomalie: Collection<CourrierRopRepository.PeiRop>? = null,

    // Liste des PIBI sans ROP
    var listPibiSansRop: Collection<CourrierRopRepository.PibiRop>? = null,

    // Liste des PENA sans ROP
    var listPenaSansRop: Collection<CourrierRopRepository.PenaRop>? = null,

    // Liste de PIBI indispo
    var listPibiIndispoWithAnomalie: Collection<CourrierRopRepository.PibiRop>? = null,

    // Liste des PENA indispo
    var listPenaIndispoWithAnomalie: Collection<CourrierRopRepository.PenaRop>? = null,

    // Liste des PIBI disponible ou non conforme
    var listPibiDispoNonConformeWithAnomalie: Collection<CourrierRopRepository.PibiRop>? = null,

    // Liste des PENA disponible ou non conforme
    var listPenaDispoNonConformeWithAnomalie: Collection<CourrierRopRepository.PenaRop>? = null,

    // Liste des PEI publics
    var listPeiPublicWithAnomalie: Collection<CourrierRopRepository.PeiRop>? = null,

    // Liste des PEI privés
    var listPeiPriveWithAnomalie: Collection<CourrierRopRepository.PeiRop>? = null,

    var reference: String? = null,
    var derniereDateReco: String? = null,
    var premiereDateReco: String? = null,
    var insee: String? = null,
    var expediteurGrade: String? = null,
    var expediteurStatut: String? = null,
    var nbTotalPrives: Int? = null,
    var nbTotalPublics: Int? = null,
    var nbPrivesWithAnomalie: Int? = null,
    var nbPublicsWithAnomalie: Int? = null,
) : AbstractCourrierData()
