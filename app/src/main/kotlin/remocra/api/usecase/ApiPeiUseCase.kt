package remocra.api.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.api.PeiUtils
import remocra.app.DataCacheProvider
import remocra.auth.OrganismeInfo
import remocra.data.ApiPenaFormData
import remocra.data.ApiPibiFormData
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiData
import remocra.data.PeiDiffData
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.TracabiliteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException
import remocra.usecase.pei.UpdatePeiUseCase
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

class ApiPeiUseCase @Inject
constructor(
    override val peiRepository: PeiRepository,
    private val penaRepository: PenaRepository,
    private val pibiRepository: PibiRepository,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val tracabiliteRepository: TracabiliteRepository,
    private val dataCacheProvider: DataCacheProvider,
    private val objectMapper: ObjectMapper,
) : AbstractApiPeiUseCase(peiRepository) {

    fun getPeiCaracteristiques(numero: String, organismeInfo: OrganismeInfo): Result {
        val peiId = peiRepository.getPeiIdFromNumero(numero)
            ?: return Result.Error(ErrorType.PEI_INEXISTANT.toString())
//         TODO peut pas marcher sans avoir l'organisme connecté !
        if (!this.isPeiAccessible(peiId, organismeInfo)) {
            return Result.Error(
                ErrorType.FORBIDDEN.toString(),
            )
        }

        // On sérialise maintenant à cause des 2 types concrets distincts (PIBI / PENA)
        return Result.Success(objectMapper.writeValueAsString(peiRepository.getPeiCaracteristiques(numero)))
    }

    /**
     * Retourne les PEI ayant subi une modification depuis un instant spécifique
     *
     * @param dateString La date format YYYY-MM-DD hh:mm à partir de laquelle rechercher les changements
     * @return List<PeiDiffData>
     */
    fun diff(dateString: String?, organismeInfo: OrganismeInfo): Result {
        var moment: ZonedDateTime? = null
        var valide = true
        if (dateString != null) {
            try {
                moment = dateUtils.getMoment(dateString)
            } catch (dtpe: DateTimeParseException) {
                valide = false
            }
        } else {
            valide = false
        }
        if (!valide) {
            return Result.Error(ErrorType.BAD_PATTERN.toString())
        }

        val events = tracabiliteRepository.getTracabilitePeiAndVisiteSince(moment!!)
        val diffs = events.map { traca ->
            val peiId: UUID
            val numeroComplet: String
            if (TypeObjet.VISITE == traca.tracabiliteTypeObjet) {
                val visiteData: VisiteData = objectMapper.readValue(traca.tracabiliteObjetData.data(), VisiteData::class.java)
                peiId = visiteData.visitePeiId
                numeroComplet = peiRepository.getInfoPei(peiId).peiNumeroComplet!!
            } else {
                peiId = traca.tracabiliteObjetId
                val peiData: PeiData = objectMapper.readValue(traca.tracabiliteObjetData.data(), PeiData::class.java)
                numeroComplet = peiData.peiNumeroComplet!!
            }

            val auteur: AuteurTracabiliteData = objectMapper.readValue(traca.tracabiliteAuteurData.data(), AuteurTracabiliteData::class.java)

            return@map PeiDiffData(
                peiId = peiId,
                numeroComplet = numeroComplet,
                momentModification = traca.tracabiliteDate,
                auteurModification = auteur.nom,
                auteur = auteur,
                typeOperation = traca.tracabiliteTypeOperation,
                typeObjet = traca.tracabiliteTypeObjet,
            )
        }

        // On va chercher les ID de tous les PEI concernés par une modification (PEI + visite)
        val listModifiedPei = diffs.map { it.peiId }.toSet()

        // Une seule requête pour calculer leur accessibilité, on se servira de la map<numero, POJO> par la suite
        val mapAccessibilite = listPeiAccessibilite(listModifiedPei, organismeInfo).associateBy { it.numero }

        return Result.Success(diffs.filter { p -> mapAccessibilite[p.numeroComplet] != null && mapAccessibilite[p.numeroComplet]!!.isAccessible || (TypeOperation.DELETE == p.typeOperation && TypeObjet.PEI == p.typeObjet) })
    }

    /**
     * Indique si l'utilisateur actuel peut modifier le PEI actuel et ses caractéristiques
     * L'utilisateur peut modifier le pei si son organisme est relié à celui-ci, sauf quand il n'est
     * que le service des eaux de ce PEI
     *
     * @param idPei ID du pei
     * @return
     */
    fun userCanEditPei(idPei: UUID, organismeInfo: OrganismeInfo): Boolean {
        val organisme: PeiUtils.OrganismeIdType = PeiUtils.OrganismeIdType(organismeInfo)

        if (PeiUtils.isApiAdmin(organisme)) {
            return true
        }

        val peiAccessibilite =
            listPeiAccessibilite(setOf(idPei), organismeInfo)[0]

        if (PeiUtils.isApiAdmin(organisme)) {
            return true
        }

        if (PeiUtils.isMaintenanceDECI(peiAccessibilite.maintenanceDECI, organisme)) {
            return true
        }

        if (PeiUtils.isServicePublicDECI(peiAccessibilite.servicePublicDECI, organisme)) {
            return true
        }
        return false
    }

    /**
     * Met à jour les caractéristiques d'un PIBI en s'appuyant sur le [UpdatePeiUseCase]
     * @param numeroComplet: Numéro complet du PEI à modifier
     * @param peiForm: Formulaire des données à modifier
     */
    fun updatePibiCaracteristiques(numeroComplet: String, peiForm: ApiPibiFormData, organismeInfo: OrganismeInfo): Result {
        val pei = peiRepository.getPeiFromNumero(numeroComplet)
        try {
            checkDroits(pei, organismeInfo)
        } catch (rre: RemocraResponseException) {
            return Result.Error(rre.message)
        }

        // On va chercher l'objet spécifique pour exécuter le useCase de modification d'un PEI
        val pibiData = pibiRepository.getInfoPibi(pei!!.peiId)

        // On modifie toutes les propriétés demandées dans le Form
        peiForm.codeDiametre?.apply { pibiData.pibiDiametreId = dataCacheProvider.getDiametres().values.first { it.diametreCode == this }.diametreId }
        peiForm.diametreCanalisation?.apply { pibiData.pibiDiametreCanalisation = this }
        peiForm.peiJumele?.apply { pibiData.pibiJumeleId = peiRepository.getPeiIdFromNumero(this) }
        peiForm.inviolabilite?.apply { pibiData.pibiDispositifInviolabilite = this }
        peiForm.renversable?.apply { pibiData.pibiRenversable = this }
        peiForm.codeMarque?.apply { pibiData.pibiMarqueId = dataCacheProvider.getMarquesPibi().values.first { it.marquePibiCode == this }.marquePibiId }
        peiForm.codeModele?.apply { pibiData.pibiModeleId = dataCacheProvider.getModelesPibi().values.first { it.modelePibiCode == this }.modelePibiId }
        peiForm.anneeFabrication?.apply { pibiData.peiAnneeFabrication = this }
        peiForm.codeTypeReseau?.apply { pibiData.pibiTypeReseauId = dataCacheProvider.getTypesReseau().values.first { it.typeReseauCode == this }.typeReseauId }
        peiForm.codeTypeCanalisation?.apply { pibiData.pibiTypeCanalisationId = dataCacheProvider.getTypesCanalisation().values.first { it.typeCanalisationCode == this }.typeCanalisationId }
        peiForm.reseauSurpresse?.apply { pibiData.pibiSurpresse = this }
        peiForm.reseauAdditive?.apply { pibiData.pibiAdditive = this }

        // TODO voir comment on veut gérer ce cas, pour l'instant on n'a pas d'a12n dans l'API
        return updatePeiUseCase.execute(null, pibiData)
    }

    /**
     * Met à jour les caractéristiques d'un PIBI en s'appuyant sur le [UpdatePeiUseCase]
     * @param numeroComplet: Numéro complet du PEI à modifier
     * @param peiForm: Formulaire des données à modifier
     */
    fun updatePenaCaracteristiques(numeroComplet: String, peiForm: ApiPenaFormData, organismeInfo: OrganismeInfo): Result {
        val pei = peiRepository.getPeiFromNumero(numeroComplet)
        try {
            checkDroits(pei, organismeInfo)
        } catch (rre: RemocraResponseException) {
            return Result.Error(rre.message)
        }

        val penaData = penaRepository.getInfoPena(pei!!.peiId)

        peiForm.capaciteIllimitee?.apply { penaData.penaCapaciteIllimitee = this }
        peiForm.capacite?.apply { penaData.penaCapacite = this }
        peiForm.capaciteIncertaine?.apply { penaData.penaCapaciteIncertaine = this }
        peiForm.quantiteAppoint?.apply { penaData.penaQuantiteAppoint = this }
        peiForm.codeMateriau?.apply { penaData.penaMateriauId = dataCacheProvider.getMateriaux().values.first { it.materiauCode == this }.materiauId }
        peiForm.equipeHBE?.apply { penaData.penaDisponibiliteHbe }

        // TODO idem PIBI
        return updatePeiUseCase.execute(null, penaData)
    }

    /**
     * Retourne un PEI spécifique, en s'appuyant sur [getPeiSpecifique], sous forme de Result pour encapsulation.
     * @see [getPeiSpecifique]
     * @param numeroComplet: String
     * @return [Result]
     */
    fun getPeiSpecifiqueAsResult(numeroComplet: String, organismeInfo: OrganismeInfo): Result {
        return try {
            Result.Success(getPeiSpecifique(numeroComplet, organismeInfo))
        } catch (rre: RemocraResponseException) {
            Result.Error(rre.message)
        }
    }
}

/**
 * Classe utilitaire permettant de connaitre les infos d'accessibilité d'un hydrant, soit au
 * travers du getter *isAccessible*, soit des propriétés élémentaires qui le composent
 */
class PeiAccessibilite(
    val id: UUID,
    val numero: String,
    val maintenanceDECI: UUID?,
    val servicePublicDECI: UUID?,
    val serviceEaux: UUID?,
    val isAccessible: Boolean,
)
