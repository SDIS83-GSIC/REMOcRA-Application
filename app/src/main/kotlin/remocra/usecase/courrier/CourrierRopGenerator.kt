package remocra.usecase.courrier

import com.google.inject.Inject
import jakarta.ws.rs.core.Response
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.api.DateUtils
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.courrier.parametres.CourrierParametresRopData
import remocra.data.courrier.template.CourrierRopData
import remocra.data.enums.CodeSdis
import remocra.data.enums.ErrorType
import remocra.db.CourrierRopRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.exception.RemocraResponseException
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

/***
 * Classe permettant de générer les courriers de ROP pour les différents SDIS
 */
class CourrierRopGenerator : AbstractCourrierGenerator<CourrierParametresRopData>() {

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    lateinit var courrierRopRepository: CourrierRopRepository

    @Inject
    lateinit var clock: Clock

    companion object {
        const val CODE_MODELE_COURRIER_ROP = "ROP"

        // Pour le SDIS 61
        private const val CODE_ANOMALIE_INACTIF = "INACTIF"
    }

    override fun checkProfilDroit(userInfo: UserInfo) {
        if (!courrierRopRepository.checkProfilDroitRop(userInfo.utilisateurId)) {
            throw RemocraResponseException(ErrorType.MODELE_COURRIER_DROIT_FORBIDDEN)
        }
    }

    override fun execute(element: CourrierParametresRopData, userInfo: UserInfo): CourrierRopData {
        // on va chercher le courrier en base qui a le code ROP
        val modeleCourrier = modeleCourrierRepository.getByCode(CODE_MODELE_COURRIER_ROP)
        val date = ZonedDateTime.now(clock)

        // Données communes à toutes les rop
        val courrierDataRop = CourrierRopData(
            courrierPath = modeleCourrier.modeleCourrierChemin,
            courrierSubReport = modeleCourrier.modeleCourrierSubreports,
            dateGeneration = DateUtils.formatDateOnly(date)!!,
            userConnecte = userInfo,
        )

        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> courrierDataRop.getData01(element)
            CodeSdis.SDIS_09 -> courrierDataRop.getData09(element)
            CodeSdis.SDIS_14 -> courrierDataRop.getData14(element)
            CodeSdis.SDIS_21 -> courrierDataRop.getData21(element)
            CodeSdis.SDIS_38 -> TODO()
            CodeSdis.SDIS_39 -> courrierDataRop.getData39(element)
            CodeSdis.SDIS_42 -> TODO()
            CodeSdis.SDIS_49 -> courrierDataRop.getData49(element)
            CodeSdis.SDIS_53 -> courrierDataRop.getData53(element)
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> courrierDataRop.getData61(element)
            CodeSdis.SDIS_66 -> courrierDataRop.getData66(element)
            CodeSdis.SDIS_71 -> TODO()
            CodeSdis.SDIS_77 -> TODO()
            CodeSdis.SDIS_78 -> TODO()
            CodeSdis.SDIS_83 -> TODO()
            CodeSdis.SDIS_89 -> TODO()
            CodeSdis.SDIS_91 -> courrierDataRop.getData91(element)
            CodeSdis.SDIS_95 -> courrierDataRop.getData95(element)
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> TODO()
            CodeSdis.SDMIS -> TODO()
        }
    }

    private fun CourrierRopData.getData01(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)
        // L'organisme correspondant à la commune sélectionnée
        val organismeId = ensureOrganismeCommune(element.communeId!!)

        // On va chercher le destinataire :
        val destinataireContact = ensureDestinataireRop(organismeId)

        val cisUser = courrierRopRepository.getCis01(userConnecte.organismeId!!)
            ?: throw IllegalArgumentException("Impossible de générer le courrier : votre organisme n'est pas un CIS")

        val groupementUser = courrierRopRepository.getGroupement01(userConnecte.organismeId!!)
            ?: throw IllegalArgumentException("Le parent de votre CIS est nul ou n'est pas un groupement")

        val listePibiRop = courrierRopRepository.getPibi(element.communeId)

        val mapPeiByDateCtp = courrierRopRepository.getLastDateCtp(listePibiRop.map { it.peiId })
        setDonneesCtp(listePibiRop, mapPeiByDateCtp)

        destinataire = getCiviliteWithMaire(destinataireContact.contactCivilite)
        destinataireEmail = destinataireContact.contactEmail
        destinataireVoie = destinataireContact.contactVoie
        destinataireVille = destinataireContact.contactVille
        destinataireCodePostal = destinataireContact.contactCodePostal
        listPeiIndispoWithAnomalie = courrierRopRepository.getPeiIndisponibles(element.communeId)
        listPibiWithAnomalie = listePibiRop
        listPenaWithAnomalie = courrierRopRepository.getPena(element.communeId)
        cis = cisUser
        groupement = groupementUser.groupementLibelle
        groupementTelephone = groupementUser.groupementTelephone
        groupementEmail = groupementUser.groupementEmail

        return this
    }

    private fun CourrierRopData.getData09(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)

        val listePibiRop = courrierRopRepository.getPibi(element.communeId!!)
        val listePenaRop = courrierRopRepository.getPena(element.communeId)
        val listPeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listPeiRop.map { it.peiId })

        listPeiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }
        listPeiWithAnomalie = listPeiRop

        return this
    }

    private fun CourrierRopData.getData14(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)

        // On regarde si on a bien un organisme de type commune qui correspond à la commune renseignée
        val organismeId = ensureOrganismeCommune(element.communeId!!)

        // Puis on va chercher le destinataire
        val destinataireContact = ensureDestinataireRop(organismeId)

        destinataire = getCiviliteWithMaire(destinataireContact.contactCivilite)
        destinataireEmail = destinataireContact.contactEmail.orEmpty()
        destinataireVoie = "${destinataireContact.contactNumeroVoie.orEmpty()} ${destinataireContact.contactVoie}"
        destinataireVille = destinataireContact.contactVille.orEmpty()
        destinataireLieuDit = destinataireContact.contactLieuDit.orEmpty()
        destinataireCodePostal = destinataireContact.contactCodePostal.orEmpty()

        val listePibiRop = courrierRopRepository.getPibi(element.communeId)
        val listePenaRop = courrierRopRepository.getPena(element.communeId)
        val listPeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listPeiRop.map { it.peiId })

        listPeiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }

        derniereDateReco = DateUtils.formatDateOnly(listPeiWithLastRop.values.sortedByDescending { it }.firstOrNull())
        listPeiWithAnomalie = listPeiRop

        affaireSuiviePar = "${userConnecte.prenom} ${userConnecte.nom}"

        // La référence est de la forme "codeInsee-ddMMyyyy"
        val commune = courrierRopRepository.getCommune(element.communeId)
        reference = "${commune.communeCodeInsee}-${dateGeneration.replace("/", "")}"
        communeLibelle = commune.communeLibelle

        return this
    }

    private fun CourrierRopData.getData21(element: CourrierParametresRopData): CourrierRopData {
        if (element.isOnlyPublic == true) {
            ensureCommune(element.communeId)
            ensureCis(element.cis)

            val commune = courrierRopRepository.getCommune(element.communeId!!)
            val cisOrganisme = courrierRopRepository.getCis(element.cis!!)

            communeLibelle = commune.communeLibelle
            insee = commune.communeCodeInsee
            cis = cisOrganisme.organismeLibelle
        } else {
            ensureGestionnaire(element.gestionnaireId)
            // TODO voir pour remonter la commune depuis le gestionnaire
        }

        val listePibiRop = courrierRopRepository.getPibi(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listPeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listPeiRop.map { it.peiId })

        listePibiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }

        val mapPeiByDateCtp = courrierRopRepository.getLastDateCtp(listePibiRop.map { it.peiId })

        listePenaRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
            it.dateCtp = DateUtils.formatDateOnly(mapPeiByDateCtp[it.peiId])
        }

        setDonneesCtp(listePibiRop, mapPeiByDateCtp)

        listPibiWithAnomalie = listePibiRop
        listPenaWithAnomalie = listePenaRop

        listPibiSansRop = listePibiRop.filter { it.dateRop == null }
        listPenaSansRop = listePenaRop.filter { it.dateRop == null }

        return this
    }

    private fun CourrierRopData.getData39(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)

        val organismeId = ensureOrganismeCommune(element.communeId!!)

        val destinataireContact = ensureDestinataireRop(organismeId)

        val listePibiRop = courrierRopRepository.getPibi(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listPeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listPeiRop.map { it.peiId })

        listePibiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }

        val mapPeiByDateCtp = courrierRopRepository.getLastDateCtp(listePibiRop.map { it.peiId })

        val listePenaAspiration = courrierRopRepository.getPenaAspiration(listePenaRop.map { it.peiId })
        listePenaRop.forEach {
            val penaAspirationWithType = listePenaAspiration.firstOrNull { pa -> pa.penaId == it.peiId }
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
            it.dateCtp = DateUtils.formatDateOnly(mapPeiByDateCtp[it.peiId])
            it.penaAspirationEstNormalise = penaAspirationWithType?.penaAspirationEstNormalise ?: false
            it.typePenaAspirationLibelle = penaAspirationWithType?.typePenaAspirationLibelle
        }

        setDonneesCtp(listePibiRop, mapPeiByDateCtp)

        // TODO affaire suivie par + expediteur

        destinataire = "${destinataireContact.contactPrenom} ${destinataireContact.contactNom}"
        destinataireEmail = destinataireContact.contactEmail.orEmpty()
        destinataireVoie = "${destinataireContact.contactNumeroVoie.orEmpty()} ${destinataireContact.contactVoie}"
        destinataireVille = destinataireContact.contactVille.orEmpty()
        destinataireLieuDit = destinataireContact.contactLieuDit.orEmpty()
        destinataireCodePostal = destinataireContact.contactCodePostal.orEmpty()
        expediteur = "${userConnecte.prenom} ${userConnecte.prenom}"
        annee = ZonedDateTime.now().year.toString()
        listPeiIndispoWithAnomalie = courrierRopRepository.getPeiIndisponibles(element.communeId)
        listPibiWithAnomalie = courrierRopRepository.getPibi(element.communeId)
        listPenaWithAnomalie = courrierRopRepository.getPena(element.communeId)
        communeLibelle = courrierRopRepository.getCommune(element.communeId).communeLibelle

        expediteurGrade = element.expediteurGrade
        expediteurStatut = element.expediteurStatut

        return this
    }

    private fun CourrierRopData.getData49(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)
        ensureCis(element.cis)
        val organismeId = ensureOrganismeCommune(element.communeId!!)
        val cisOrganisme = courrierRopRepository.getCis(element.cis!!)

        cis = cisOrganisme.organismeLibelle

        val listePibiRop = courrierRopRepository.getPibi(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listePeiRop.map { it.peiId })

        listePibiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }
        listePenaRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }
        val groupementData = ensureGroupement(organismeId)

        // Pour le signataire, il faut aller chercher le groupement de la commune et chercher le contact qui a le rôle SIGNATAIRE
        groupement = groupementData.libelle

        val expediteurGroupement = courrierRopRepository.getExpediteurGroupement(organismeId)
            ?: throw IllegalArgumentException("Aucun contact pour le groupement ${groupementData.libelle} : le contact doit être associé au rôle ${GlobalConstants.ROLE_SIGNATAIRE_GROUPEMENT}")

        expediteur = "${expediteurGroupement.contactFonction} ${expediteurGroupement.contactPrenom} ${expediteurGroupement.contactNom?.uppercase()}"

        derniereDateReco = DateUtils.formatDateOnly(listPeiWithLastRop.values.sortedByDescending { it }.firstOrNull())
        premiereDateReco = DateUtils.formatDateOnly(listPeiWithLastRop.values.sortedBy { it }.firstOrNull())

        // TODO affaireSuiviePar

        return this
    }

    private fun CourrierRopData.getData53(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)

        val commune = courrierRopRepository.getCommune(element.communeId!!)
        val listePibiRop = courrierRopRepository.getPibi(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, element.gestionnaireId, null, element.isOnlyPublic == true)
        val listePeiRop = listePibiRop.plus(listePenaRop)

        val listPeiWithLastRop = courrierRopRepository.getLastDateRop(listePeiRop.map { it.peiId })
        listePeiRop.forEach {
            it.dateRop = DateUtils.formatDateOnly(listPeiWithLastRop[it.peiId])
        }

        communeLibelle = commune.communeLibelle

        listPeiPublicWithAnomalie = listePeiRop.filter { it.natureDeciCode != CourrierRopRepository.NATURE_DECI_PRIVE }
        listPeiPriveWithAnomalie = listePeiRop.minus(listPeiPublicWithAnomalie ?: setOf())

        derniereDateReco = DateUtils.formatDateOnly(listPeiWithLastRop.values.sortedByDescending { it }.firstOrNull())
        premiereDateReco = DateUtils.formatDateOnly(listPeiWithLastRop.values.sortedBy { it }.firstOrNull())

        // TODO civilité ? plutôt utiliser des contacts ?

        return this
    }

    private fun CourrierRopData.getData61(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)
        ensureCis(element.cis)

        val commune = courrierRopRepository.getCommune(element.communeId!!)
        val condition = DSL.and(ANOMALIE.CODE.notEqual(CODE_ANOMALIE_INACTIF))
        val listePibiRop = courrierRopRepository.getPibi(element.communeId, null, condition)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, null, condition)

        val cisOrganisme = courrierRopRepository.getCis(element.cis!!)

        communeLibelle = commune.communeLibelle
        affaireSuiviePar = "${userConnecte.prenom} ${userConnecte.nom}"
        cis = cisOrganisme.organismeLibelle

        listPibiIndispoWithAnomalie = listePibiRop.filter { it.peiDisponibiliteTerrestre == Disponibilite.INDISPONIBLE.literal }
        listPenaIndispoWithAnomalie = listePenaRop.filter { it.peiDisponibiliteTerrestre == Disponibilite.INDISPONIBLE.literal }

        listPibiDispoNonConformeWithAnomalie = listePibiRop.minus(listPibiIndispoWithAnomalie ?: setOf())
        listPenaDispoNonConformeWithAnomalie = listePenaRop.minus(listPenaIndispoWithAnomalie ?: setOf())

        return this
    }

    private fun CourrierRopData.getData66(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)

        // TODO gérer la civilité
        destinataire = if (element.isEPCI == true) "Madame la présidente d'EPCI" else "Madame la Maire"
        expediteur = "${userConnecte.prenom} ${userConnecte.prenom}"
        annee = ZonedDateTime.now().year.toString()
        listPeiIndispoWithAnomalie = courrierRopRepository.getPeiIndisponibles(element.communeId!!)
        listPibiWithAnomalie = courrierRopRepository.getPibi(element.communeId)
        listPenaWithAnomalie = courrierRopRepository.getPena(element.communeId)

        return this
    }

    private fun CourrierRopData.getData91(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)
        val organismeId = ensureOrganismeCommune(element.communeId!!)

        val commune = courrierRopRepository.getCommune(element.communeId)
        val condition = DSL.and(ANOMALIE_CATEGORIE.CODE.notEqual(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME))
        val listePibiRop = courrierRopRepository.getPibi(element.communeId, null, condition)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, null, condition)

        communeLibelle = commune.communeLibelle

        listPeiWithAnomalie = listePibiRop.plus(listePenaRop)
        val groupementData = ensureGroupement(organismeId)

        groupement = groupementData.libelle

        // TODO affaire suivie par

        return this
    }

    private fun CourrierRopData.getData95(element: CourrierParametresRopData): CourrierRopData {
        ensureCommune(element.communeId)
        ensureCommune(element.cis)
        val organismeId = ensureOrganismeCommune(element.communeId!!)
        val cisOrganisme = courrierRopRepository.getCis(element.cis!!)

        cis = cisOrganisme.organismeLibelle

        val commune = courrierRopRepository.getCommune(element.communeId)
        val condition = DSL.and(ANOMALIE_CATEGORIE.CODE.notEqual(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME))
        val listePibiRop = courrierRopRepository.getPibi(element.communeId, null, condition)
        val listePenaRop = courrierRopRepository.getPena(element.communeId, null, condition)
        val listePeiRop = listePibiRop.plus(listePenaRop)

        communeLibelle = commune.communeLibelle

        listPeiIndispoWithAnomalie = courrierRopRepository.getPeiIndisponibles(element.communeId)

        val destinataireContact = ensureDestinataireRop(organismeId)

        destinataire = "${destinataireContact.contactPrenom} ${destinataireContact.contactNom}"
        destinataireVoie = "${destinataireContact.contactNumeroVoie.orEmpty()} ${destinataireContact.contactVoie}"
        destinataireVille = destinataireContact.contactVille
        destinataireCodePostal = destinataireContact.contactCodePostal

        val listePeiPrives = listePeiRop.filter { it.natureDeciCode == CourrierRopRepository.NATURE_DECI_PRIVE }
        val listPeiPublics = listePeiRop.minus(listePeiPrives)
        nbTotalPrives = listePeiPrives.count()
        nbTotalPublics = listPeiPublics.count()

        nbPrivesWithAnomalie = listePeiPrives.filter { it.listeAnomalie.isNullOrEmpty() }.count()
        nbPublicsWithAnomalie = listPeiPublics.filter { it.listeAnomalie.isNullOrEmpty() }.count()

        // TODO affaire suivie par (Chef de centre)

        return this
    }

    /**
     * Méthode utils et commune aux SDIS
     *
     */

    private fun getCiviliteWithMaire(civilite: TypeCivilite?) =
        if (civilite == TypeCivilite.MADAME) {
            "${civilite.literal.lowercase().replaceFirstChar { it.uppercase() }} la Maire"
        } else {
            "${civilite?.literal?.lowercase()?.replaceFirstChar { it.uppercase() }} le Maire"
        }

    private fun ensureCommune(communeId: UUID?) {
        if (communeId == null) {
            throw RemocraResponseException(ErrorType.COURRIER_SAISIR_COMMUNE)
        }
    }

    private fun ensureCis(cisId: UUID?) {
        if (cisId == null) {
            throw RemocraResponseException(ErrorType.COURRIER_SAISIR_CIS)
        }
    }

    private fun ensureGestionnaire(gestionnaireId: UUID?) {
        if (gestionnaireId == null) {
            throw RemocraResponseException(ErrorType.COURRIER_SAISIR_GESTIONNAIRE)
        }
    }

    private fun ensureGroupement(organismeCommuneId: UUID) =
        courrierRopRepository.getGroupement(organismeCommuneId)
            ?: throw RemocraResponseException(ErrorType.COURRIER_GROUPEMENT_INTROUVABLE)

    private fun ensureOrganismeCommune(communeId: UUID) =
        // L'organisme correspondant à la commune sélectionnée
        courrierRopRepository.getOrganismeCommune(communeId)
            ?: throw RemocraResponseException(ErrorType.COURRIER_ORGANISME_COMMUNE)

    private fun ensureDestinataireRop(organismeId: UUID) =
        courrierRopRepository.getDestinataireMaire(organismeId)
            ?: throw RemocraResponseException(5006, "Aucun destinataire Maire pour cette commune. Vérifier que le contact existe et qu'il a bien le rôle '${GlobalConstants.ROLE_DESTINATAIRE_MAIRE_ROP}'", Response.Status.BAD_REQUEST)

    private fun setDonneesCtp(listePibiRop: Collection<CourrierRopRepository.PibiRop>, mapPibiByDateCtp: Map<UUID?, ZonedDateTime?>) {
        val ctrlDebitPression = courrierRopRepository.getLastCtrlDebitPression(mapPibiByDateCtp)

        listePibiRop.forEach {
            it.visiteCtrlDebitPressionDebit = ctrlDebitPression[it.peiId]
                ?.visiteCtrlDebitPression?.visiteCtrlDebitPressionDebit
            it.visiteCtrlDebitPressionPression = ctrlDebitPression[it.peiId]?.visiteCtrlDebitPression
                ?.visiteCtrlDebitPressionPression?.toDouble()
            it.visiteCtrlDebitPressionPressionDyn = ctrlDebitPression[it.peiId]?.visiteCtrlDebitPression
                ?.visiteCtrlDebitPressionPressionDyn?.toDouble()
            it.dateCtp = DateUtils.formatDateOnly(mapPibiByDateCtp[it.peiId])
        }
    }
}
