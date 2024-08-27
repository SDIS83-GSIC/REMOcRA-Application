package remocra.data

/**
 * Classe qui permet de renvoyer les informations nécessaires à l'affichage d'un tableau côté front
 * @param list : liste des élements à envoyer
 * @param count : Nombre d'éléments en tout servant pour la pagination
 */
data class DataTableau<T>(
    val list: Collection<T>,
    val count: Int,
)
