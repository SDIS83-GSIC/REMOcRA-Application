package remocra.apimobile.usecase

/** Va chercher les type visites que l'utilisateur peut réaliser  */
class GetTypeVisiteUtilisateur {
//    @Inject
//    lateinit var typeDroitRepository: TypeDroitRepository
//
//    fun execute(
//            idUtilisateur: Long?, typeHydrantSaisies: List<TypeHydrantSaisieModel?>): List<TypeHydrantSaisieModel> {
//        val codesTypeDroitsVisite: List<String> =
//                typeDroitRepository.getDroitsUtilisateurVisite(idUtilisateur)
//
//        val typeVisiteUtilisateur: MutableList<TypeHydrantSaisieModel> = ArrayList<TypeHydrantSaisieModel>()
//        for (typeSaisie in typeHydrantSaisies) {
//            checkDroit(
//                    typeVisiteUtilisateur,
//                    typeSaisie,
//                    codesTypeDroitsVisite,
//                    GlobalConstants.TypeVisite.CREATION.getCode(),
//                    TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_CREATION_C.getCodeDroitMobile())
//            checkDroit(
//                    typeVisiteUtilisateur,
//                    typeSaisie,
//                    codesTypeDroitsVisite,
//                    GlobalConstants.TypeVisite.CONTROLE.getCode(),
//                    TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_CONTROLE_C.getCodeDroitMobile())
//            checkDroit(
//                    typeVisiteUtilisateur,
//                    typeSaisie,
//                    codesTypeDroitsVisite,
//                    GlobalConstants.TypeVisite.NON_PROGRAMMEE.getCode(),
//                    TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_ANOMALIES_C.getCodeDroitMobile())
//            checkDroit(
//                    typeVisiteUtilisateur,
//                    typeSaisie,
//                    codesTypeDroitsVisite,
//                    GlobalConstants.TypeVisite.RECONNAISSANCE.getCode(),
//                    TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_RECONNAISSANCE_C.getCodeDroitMobile())
//            checkDroit(
//                    typeVisiteUtilisateur,
//                    typeSaisie,
//                    codesTypeDroitsVisite,
//                    GlobalConstants.TypeVisite.RECEPTION.getCode(),
//                    TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_RECEPTION_C.getCodeDroitMobile())
//        }
//        return typeVisiteUtilisateur
//    }
//
//    private fun checkDroit(
//            list: MutableList<TypeHydrantSaisieModel>,
//            typeSaisie: TypeHydrantSaisieModel,
//            codesTypeDroitsVisite: List<String>,
//            typeVisite: String,
//            typeDroit: String) {
//        if (typeSaisie.getCode().equals(typeVisite)) {
//            if (codesTypeDroitsVisite.contains(typeDroit)) {
//                list.add(typeSaisie)
//            }
//        }
//    }
}
