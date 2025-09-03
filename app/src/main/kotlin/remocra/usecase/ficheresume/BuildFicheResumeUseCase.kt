package remocra.usecase.ficheresume

import com.google.inject.Inject
import remocra.db.FicheResumeRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeResumeElement
import remocra.usecase.AbstractUseCase
import remocra.utils.AdresseDecorator
import remocra.utils.AdresseForDecorator
import remocra.utils.DateUtils
import java.util.UUID
import kotlin.text.isNullOrBlank

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
                            data = DisponibiliteWithIndispoTemp(
                                disponibilite = peiData.peiDisponibiliteTerrestre,
                                hasIndispoTemp = peiData.hasIndispoTemp,
                            ),
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
                    val decoratedAdresse = AdresseDecorator().decorateAdresse(
                        AdresseForDecorator(
                            enFace = peiData.peiEnFace,
                            numeroVoie = peiData.peiNumeroVoie,
                            suffixeVoie = peiData.peiSuffixeVoie,
                            voie = null,
                            voieTexte = peiData.peiVoieTexte ?: peiData.voieLibelle,
                        ),
                    )

                    var data = """
                                $decoratedAdresse
                                ${peiData.communeCodePostal} ${peiData.communeLibelle}
                                Code INSEE : ${peiData.communeCodeInsee}
                    """.trimIndent().trim()

                    if (!peiData.peiComplementAdresse.isNullOrBlank()) {
                        data += """
                             
                            Commentaire de localisation :
                            ${peiData.peiComplementAdresse.takeIfNotNullElseNonRenseigne()}
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
                    var data = if (peiData.peiTypePei == TypePei.PIBI) {
                        """
                               Diamètre : ${peiData.diametreLibelle.takeIfNotNullElseNonRenseigne()}
                               Diamètre de canalisation : ${peiData.pibiDiametreCanalisation?.toString()?.takeIfNotNullElseNonRenseigne()}
                               Débit renforcé : ${if (peiData.pibiDebitRenforce == true) "Oui" else "Non"}
                        """.trimIndent().let {
                            peiData.pibiJumele?.let { jumele ->
                                "$it\n Jumelé avec : $jumele"
                            }
                        }
                    } else {
                        """
                           Capacité : ${peiData.penaCapacite.toString().takeIfNotNullElseNonRenseigne()}
                        """.trimIndent()
                    }

                    // Commun aux deux
                    data += """
                        
                        Dernière ROP : ${
                        peiData.lastRop?.let {
                            dateUtils.format(
                                it,
                                DateUtils.PATTERN_NATUREL,
                            )
                        }.takeIfNotNullElseNonRenseigne()
                    }
                        Dernier CTP : ${peiData.lastCtp?.let {
                        dateUtils.format(
                            it,
                            DateUtils.PATTERN_NATUREL,
                        )
                    }.takeIfNotNullElseNonRenseigne()}
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
                            data = ficheResumeRepository.getCis(peiId)?.joinToString(),
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
                TypeResumeElement.GESTIONNAIRE -> {
                    listeResumeElement.add(
                        ResumeElement(
                            type = it.ficheResumeBlocTypeResumeData,
                            titre = it.ficheResumeBlocTitre,
                            data = GestionnaireInfo(
                                gestionnaireId = peiData.gestionnaireId,
                                gestionnaireLibelle = peiData.gestionnaireLibelle,
                                siteLibelle = peiData.siteLibelle,
                            ),
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

    data class DisponibiliteWithIndispoTemp(
        val disponibilite: Disponibilite,
        val hasIndispoTemp: Boolean,
    )

    data class GestionnaireInfo(
        val gestionnaireId: UUID?,
        val gestionnaireLibelle: String?,
        val siteLibelle: String?,
    )

    private fun String?.takeIfNotNullElseNonRenseigne() =
        this.takeIf { !it.isNullOrBlank() && it != "null" } ?: "Non renseigné"
}
