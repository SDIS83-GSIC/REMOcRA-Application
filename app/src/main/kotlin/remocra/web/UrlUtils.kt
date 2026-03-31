package remocra.web

import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.web.documents.DocumentEndpoint
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

/**
 * On met les fonctions de construction d'url ici pour respecter la règle archunit "dontDependOnWeb"
 */

fun documentTelechargerRessourceUrl(documentId: UUID) =
    UriBuilder.fromPath(AuthnConstants.API_PATH)
        .path(DocumentEndpoint::class.java)
        .path(DocumentEndpoint::telechargerRessource.javaMethod)
        .build(documentId)
        .toString()

fun documentTelechargerRessourceFrom(urlSite: String, documentId: UUID) =
    UriBuilder
        .fromUri(urlSite)
        .path(AuthnConstants.API_PATH)
        .path(DocumentEndpoint::class.java)
        .path(DocumentEndpoint::telechargerRessource.javaMethod)
        .build(documentId)
        .toString()
