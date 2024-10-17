enum TYPE_ENVIRONNEMENT {
  /** Environnement de développement - infrastructure Atol uniquement */
  DEVELOPPEMENT,

  /** Environnement de formation - infrastructure Atol uniquement */
  FORMATION,

  /** Environnement de recette - tester la dernière version et co-construction de modules avec Atol  */
  RECETTE,

  /** Environnement de pré-production - qualifier le bon fonctionnement de la mise en production et certains traitements sensibles */
  PREPRODUCTION,

  /** Environnement de production - aucun bridage particulier */
  PRODUCTION,
}
export default TYPE_ENVIRONNEMENT;
