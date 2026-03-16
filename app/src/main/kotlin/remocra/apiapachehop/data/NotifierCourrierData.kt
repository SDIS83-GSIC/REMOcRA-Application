package remocra.apiapachehop.data

import remocra.data.DestinataireData
import java.util.UUID

data class NotifierCourrierData(
    val modeleCourrierCode: String,
    val jobId: UUID,
    val courrierInfos: List<CourrierWrapper>,
)

data class CourrierWrapper(
    val courrierParametres: CourrierParametres,
)

data class CourrierParametres(
    val listeParametre: List<ParametreCleValeur>,
    val listeDestinataire: Collection<DestinataireData>,
)

data class ParametreCleValeur(
    val cle: String,
    val valeur: String,
)
