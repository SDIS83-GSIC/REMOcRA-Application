package remocra.data.enums
/**
 * Types d'environnement permettant d'autoriser ou refuser certaines tâches.
 * @see [remocra.tasks.SimpleTask.getAuthorizedEnvironments]
 */
enum class Environment {
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
