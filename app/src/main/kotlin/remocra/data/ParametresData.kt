package remocra.data

import remocra.data.enums.PeiCaracteristique
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.pojos.Task

data class ParametresData(
    val mapParametres: Map<String, Parametre>,
    val mapTasksInfo: Map<TypeTask, Task>,
)

data class ParametresAdminData(
    val general: ParametresSectionGeneral,
    val mobile: ParametresSectionMobile,
    val cartographie: ParametresSectionCartographie,
    val couvertureHydraulique: ParametresSectionCouvertureHydraulique,
    val permis: ParametresSectionPermis,
    val pei: ParametresSectionPei,
    val peiLongueIndispo: ParametresSectionPeiLongueIndispo,

)

data class ParametresAdminDataInput(
    val general: ParametresSectionGeneral,
    val mobile: ParametresSectionMobile,
    val cartographie: ParametresSectionCartographie,
    val couvertureHydraulique: ParametresSectionCouvertureHydrauliqueInput,
    val permis: ParametresSectionPermis,
    val pei: ParametresSectionPei,
    val peiLongueIndispo: ParametresSectionPeiLongueIndispo,
)

fun mapToParametresSectionCouvertureHydraulique(
    input: ParametresSectionCouvertureHydrauliqueInput,
): ParametresSectionCouvertureHydraulique {
    return ParametresSectionCouvertureHydraulique(
        deciDistanceMaxParcours = input.deciDistanceMaxParcours,
        deciIsodistances = input.deciIsodistances?.split(",")?.map { it.trim().toInt() },
        profondeurCouverture = input.profondeurCouverture,
    )
}

data class ParametresSectionGeneral(
    val mentionCnil: String?,
    val messageEntete: String?,
    val titrePage: String?,
    val toleranceVoiesMetres: Int?,

)

data class ParametresSectionMobile(
    val affichageIndispo: Boolean?,
    val affichageSymbolesNormalises: Boolean?,
    val caracteristiquesPena: List<PeiCaracteristique>?,
    val caracteristiquesPibi: List<PeiCaracteristique>?,
    val dureeValiditeToken: Int,
    val gestionAgent: String?,
    val mdpAdministrateur: String?,
    val modeDeconnecte: Boolean?,
    val creationPeiMobile: Boolean?,

)

data class ParametresSectionCartographie(
    val coordonneesFormatAffichage: String?,
)

data class ParametresSectionPeiLongueIndispo(
    val peiLongueIndisponibiliteMessage: String,
    val peiLongueIndisponibiliteJours: Int?,
    val peiLongueIndisponibiliteTypeOrganisme: List<String>?,
)

data class ParametresSectionCouvertureHydraulique(
    val deciDistanceMaxParcours: Int?,
    val deciIsodistances: List<Int>?,
    val profondeurCouverture: Int?,
)
data class ParametresSectionCouvertureHydrauliqueInput(
    val deciDistanceMaxParcours: Int?,
    val deciIsodistances: String?,
    val profondeurCouverture: Int?,
)

data class ParametresSectionPermis(
    val permisToleranceChargementMetres: Int?,
)

data class ParametresSectionPei(
    val peiDeplacementDistWarn: Int?,
    val peiGenerationCarteTournee: Boolean?,
    val bufferCarte: Int?,
    val peiDelaiCtrlUrgent: Int?,
    val peiDelaiCtrlWarn: Int?,
    val peiDelaiRecoUrgent: Int?,
    val peiDelaiRecoWarn: Int?,
    val peiColonnes: List<String>?,
    val peiMethodeTriAlphanumerique: Boolean?,
    val peiRenouvellementCtrlPrive: Int?,
    val vitesseEau: Int?,
    val peiRenouvellementCtrlPublic: Int?,
    val peiRenouvellementRecoPrive: Int?,
    val peiRenouvellementRecoPublic: Int?,
    val peiToleranceCommuneMetres: Int?,
    val peiHighlightDuree: Int?,
    val peiRenumerotationInterneAuto: Boolean?,
    val voieSaisieLibre: Boolean?,
)
