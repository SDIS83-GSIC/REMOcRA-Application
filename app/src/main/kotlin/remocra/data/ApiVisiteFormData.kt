package remocra.data

class ApiVisiteFormData {
    lateinit var date: String
    lateinit var typeVisite: String
    val agent1: String? = null
    val agent2: String? = null

    // TODO tableau, collection ou json ?
    val anomaliesControlees: Collection<String> = emptyList()
    val anomaliesConstatees: Collection<String> = emptyList()
    val debit: Int? = null
    val debitMax: Int? = null
    val pression: Double? = null
    val pressionDynamique: Double? = null
    val pressionDynamiqueDebitMax: Double? = null
    val observations: String? = null
}
