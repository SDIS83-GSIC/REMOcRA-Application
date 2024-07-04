package remocra.data

import remocra.db.jooq.remocra.enums.Disponibilite
import java.util.UUID

/**
 * Modèle général de représentation d'un PEI pour utilisation dans le back et le front ;
 * ce modèle est décliné (hérité) pour chaque type (PIBI, PENA) afin de rajouter la sémantique nécessaire à ces spécificités.
 */
open class PeiData(
    open val peiId: UUID,
    open var numeroComplet: String?,
    open var numeroInterne: Int?,
    open var disponibiliteTerrestre: Disponibilite,

    open val autoriteDeciId: UUID?,
    open val servicePublicDeciId: UUID?,
    open val maintenanceDeci: UUID?,

    open val communeId: UUID,
    open val voieId: UUID?,
    open val numeroVoie: Int?,
    open val suffixeVoie: String?,
    open val lieuDitId: UUID?,
    open val croisementId: UUID?,
    open val complementAdresse: String?,
    open val enFace: Boolean = false,

    open val domaineId: UUID,
    open val natureId: UUID,
    open val siteId: UUID,
    open val natureDeciId: UUID,
    open val zoneSpecialeId: UUID?,
    open val anneeFabrication: Int?,
    open val niveauId: UUID,
    open val observation: String?,

    // TODO ajouter la géométrie
)

data class PibiData(
    override val peiId: UUID,
    override var numeroComplet: String?,
    override var numeroInterne: Int?,
    override var disponibiliteTerrestre: Disponibilite,

    override val autoriteDeciId: UUID?,
    override val servicePublicDeciId: UUID?,
    override val maintenanceDeci: UUID?,

    override val communeId: UUID,
    override val voieId: UUID?,
    override val numeroVoie: Int?,
    override val suffixeVoie: String?,
    override val lieuDitId: UUID?,
    override val croisementId: UUID?,
    override val complementAdresse: String?,
    override val enFace: Boolean = false,

    override val domaineId: UUID,
    override val natureId: UUID,
    override val siteId: UUID,
    override val natureDeciId: UUID,
    override val zoneSpecialeId: UUID?,
    override val anneeFabrication: Int?,
    override val niveauId: UUID,
    override val observation: String?,

    val diametreId: UUID?,
    val serviceEauId: UUID?,
    val numeroScp: String?,
    val renversable: Boolean = false,
    val dispositifInviolabilite: Boolean = false,
    val modeleId: UUID?,
    val marqueId: UUID?,
    val reservoirId: UUID?,
    val debitRenforce: Boolean = false,
    val typeCanalisationId: UUID?,
    val typeReseauId: UUID?,
    val diametreCanalisation: Int?,
    val surpresse: Boolean = false,
    val additive: Boolean = false,
    // TODO à compléter au fur et à mesure
) : PeiData(
    peiId,
    numeroComplet,
    numeroInterne,
    disponibiliteTerrestre,
    autoriteDeciId,
    servicePublicDeciId,
    maintenanceDeci,
    communeId,
    voieId,
    numeroVoie,
    suffixeVoie,
    lieuDitId,
    croisementId,
    complementAdresse,
    enFace,
    domaineId,
    natureId,
    siteId,
    natureDeciId,
    zoneSpecialeId,
    anneeFabrication,
    niveauId,
    observation,
)

class PenaData(
    override val peiId: UUID,
    override var numeroComplet: String?,
    override var numeroInterne: Int?,
    override var disponibiliteTerrestre: Disponibilite,

    override val autoriteDeciId: UUID?,
    override val servicePublicDeciId: UUID?,
    override val maintenanceDeci: UUID?,

    override val communeId: UUID,
    override val voieId: UUID?,
    override val numeroVoie: Int?,
    override val suffixeVoie: String?,
    override val lieuDitId: UUID?,
    override val croisementId: UUID?,
    override val complementAdresse: String?,
    override val enFace: Boolean = false,

    override val domaineId: UUID,
    override val natureId: UUID,
    override val siteId: UUID,
    override val natureDeciId: UUID,
    override val zoneSpecialeId: UUID?,
    override val anneeFabrication: Int?,
    override val niveauId: UUID,
    override val observation: String?,

    val disponibiliteHbe: Disponibilite? = null,
    val capacite: Int? = null,
    val capaciteIllimitee: Boolean = false,
    val materiauId: UUID? = null,
) : PeiData(
    peiId,
    numeroComplet,
    numeroInterne,
    disponibiliteTerrestre,
    autoriteDeciId,
    servicePublicDeciId,
    maintenanceDeci,
    communeId,
    voieId,
    numeroVoie,
    suffixeVoie,
    lieuDitId,
    croisementId,
    complementAdresse,
    enFace,
    domaineId,
    natureId,
    siteId,
    natureDeciId,
    zoneSpecialeId,
    anneeFabrication,
    niveauId,
    observation,
)
