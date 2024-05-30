package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.ParametresData
import remocra.db.ParametreRepository

@Singleton
class ParametresProvider
@Inject
constructor(
    private val parametreRepository: ParametreRepository,
) : Provider<ParametresData> {
    private lateinit var parametres: ParametresData
    override fun get(): ParametresData {
        if (!this::parametres.isInitialized) {
            parametres = getData()
        }
        return parametres
    }

    fun reloadParametres() {
        parametres = getData()
    }

    private fun getData(): ParametresData {
        val mapParametres = parametreRepository.getMapParametres()

        return ParametresData(mapParametres = mapParametres)
    }
}
