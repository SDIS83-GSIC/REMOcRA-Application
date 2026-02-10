package remocra.data

class ApiUpdateVisiteFormData {
    val agent1: String? = null
    val agent2: String? = null

    val anomaliesControlees: Collection<String> = emptyList()
    val anomaliesConstatees: Collection<String> = emptyList()
    val debit: Int? = null
    val pression: Double? = null
    val pressionDynamique: Double? = null
    val observations: String? = null
}
