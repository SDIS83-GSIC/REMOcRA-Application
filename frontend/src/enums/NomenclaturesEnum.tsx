/**
 * Les nomenclatuer possible a demande au endpoint nomenclatures/list
 */
enum NOMENCLATURES {
  NATURE = "nature",
  NATURE_DECI = "nature_deci",
  DIAMETRE = "diametre",
  DOMAINE = "domaine",
  MARQUE_PIBI = "marque_pibi",
  MATERIAU = "materiau",
  MODELE_PIBI = "modele_pibi",
  NIVEAU = "niveau",
  TYPE_CANALISATION = "type_canalisation",
  TYPE_RESEAU = "type_reseau",
}

export enum NOMENCLATURE_ORGANISME {
  SERVICE_PUBLIC_DECI = "servicePublicDeci",
  AUTORITE_DECI = "autoriteDeci",
}
export default NOMENCLATURES;
