package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import javax.annotation.Nullable

/**
 * Le contexte est nullable car, si la connexion à une base de données externe n'est pas configurée,
 * il n'est pas possible de l'initialiser correctement.
 * Le repository doit cependant rester injectable, bien qu'il soit inutile sans contexte.
 * Le contexte est donc null lorsque les informations de connexion ne sont pas définies.
 * Il est donc nécessaire, à chaque utilisation du contexte, de vérifier qu'il n'est pas nul (dsl!!.select[...]) ;
 * dans le cas contraire, une RuntimeException est justifiée.
*/
class SigRepository @Inject constructor(@Sig @Nullable private val dsl: DSLContext?) {
    // TODO: Ajouter les services au besoin
}
