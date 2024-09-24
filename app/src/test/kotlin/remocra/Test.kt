package remocra
import com.tngtech.archunit.junit.AnalyzeClasses
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import remocra.data.enums.ErrorType

@AnalyzeClasses(packages = ["remocra.data.enums"])
class Test {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Vérifie l'unicité des codes définis dans ErrorType
     */
    @Test
    fun findDuplicates() {
        val codeDuplicates = ErrorType.entries.groupingBy { it.code }.eachCount().filter { it.value > 1 }.map { it.key }
        codeDuplicates.map { "Duplicate error code: $it" }.forEach { logger.error(it) }
        Assertions.assertTrue(codeDuplicates.isEmpty())
    }
}
