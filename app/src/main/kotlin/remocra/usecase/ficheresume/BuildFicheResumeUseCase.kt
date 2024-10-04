package remocra.usecase.ficheresume

import com.google.inject.Inject
import remocra.api.DateUtils
import remocra.db.FicheResumeRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeResumeElement
import remocra.usecase.AbstractUseCase
import java.util.UUID

class BuildFicheResumeUseCase : AbstractUseCase() {
    @Inject
    private lateinit var ficheResumeRepository: FicheResumeRepository

    fun execute(peiId: UUID): MutableList<ResumeElement> {
        // On remonte tous les éléments qui devront apparaître dans la fiche ordonner par colonnes / lignes
        val listeElement = ficheResumeRepository.getFicheResume()

        val peiData = ficheResumeRepository.getPeiInfoFicheResume(peiId)

        val listeResumeElement: MutableList<ResumeElement> = mutableListOf()

        // Puis on va chercher toutes les informations en fonction du type élément
        listeElement.forEach {
            when (it.ficheResumeBlocTypeResumeData) {
                TypeResumeElement.TOURNEE -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = peiData.listeTournee,
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.DISPONIBILITE -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = peiData.peiDisponibiliteTerrestre,
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.ANOMALIES -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = peiData.listeAnomalieValIndispo,
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.LOCALISATION -> {
                    var data = """
                                ${peiData.peiNumeroVoie?.toString().orEmpty()} ${peiData.peiSuffixeVoie.orEmpty()} ${peiData.voieLibelle}
                                ${peiData.communeCodePostal} ${peiData.communeLibelle}
                                Code INSEE : ${peiData.communeCodeInsee}
                    """.trimIndent().trim()

                    if (!peiData.peiComplementAdresse.isNullOrBlank()) {
                        data += """
                             
                            Commentaire de localisation :
                            ${peiData.peiComplementAdresse}
                        """.trimIndent()
                    }

                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = data,
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.CARACTERISTIQUES -> {
                    // TODO Date de dernière ctp quand on aura ce qu'il faut dans la vue
                    var data = if (peiData.peiTypePei == TypePei.PIBI) {
                        """
                               Diamètre : ${peiData.diametreLibelle}
                               Diamètre de canalisation : ${peiData.pibiDiametreCanalisation?.toString().orEmpty()}
                               Débit renforcé : ${if (peiData.pibiDebitRenforce == true) "Oui" else "Non"}
                        """.trimIndent().apply {
                            peiData.pibiJumele?.let { jumele ->
                                this.plus("\n Jumelé avec : $jumele")
                            }
                        }
                    } else {
                        """
                           Capacité : ${peiData.capacite.takeIf { c -> c != null }}
                        """.trimIndent()
                    }

                    // Commun aux deux
                    data += """
                        
                        Dernière RECO : ${
                        peiData.peiLastRecop?.let {
                            DateUtils.format(
                                peiData.peiLastRecop,
                                DateUtils.PATTERN_NATUREL,
                            )
                        }
                    }
                    """.trimIndent()

                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = data,
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.CIS -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = ficheResumeRepository.getCis(peiId),
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.CASERNE -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = ficheResumeRepository.getCaserne(peiId),
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
                TypeResumeElement.OBSERVATION -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = ficheResumeRepository.getLastObservation(peiId),
                            colonne = it.ficheResumeBlocColonne,
                            ligne = it.ficheResumeBlocLigne,
                        ),
                    )
                }
            }
        }

        return listeResumeElement
    }
    data class ResumeElement(
        val type: TypeResumeElement,
        val titre: String,
        val data: Any?, // On renvoie soit une liste, soit un string
        val colonne: Int,
        val ligne: Int,
    )
}
