package remocra.web.api

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.security.PermitAll
import jakarta.servlet.ServletConfig
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Application
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import remocra.auth.Public
import remocra.security.NoCsrf

// TODO débrayer l'a12n, soit en faisant une exception dans la servlet, soit en faisant une servlet à part !

@Path("/openapi")
class OpenApiEndpoint : BaseOpenApiResource() {
    @Context
    var config: ServletConfig? = null

    @Context
    var app: Application? = null

    @Public("Page de description des capacités de l'API, les appels à l'API, eux, sont authentifiés")
    @NoCsrf("Uniquement consultatif")
    @GET
    @Path("openapi.{type:json|yaml}")
    @Produces(value = [MediaType.APPLICATION_JSON, "application/yaml"])
    @Operation(hidden = true)
    @PermitAll
    @Throws(Exception::class)
    fun getOpenApi(
        @Context headers: HttpHeaders?,
        @Context uriInfo: UriInfo?,
        @PathParam("type") type: String?,
    ): Response {
        return super.getOpenApi(headers, config, app, uriInfo, type)
    }

    @Public("Page de description des capacités de l'API, les appels à l'API, eux, sont authentifiés")
    @NoCsrf("Uniquement consultatif")
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    @PermitAll
    fun showOpenApi(): Response {
        return Response.ok()
            .entity(
                "\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <title>API REMOcRA</title>\n" +
                    "  <link rel=\"stylesheet\" type=\"text/css\"" +
                    " href=\"//unpkg.com/swagger-ui-dist@3/swagger-ui.css\">\n" +
                    "  <script" +
                    " src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\"></script>\n" +
                    "  <script" +
                    " src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-standalone-preset.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <script>\n" +
                    "window.onload = function() {\n" +
                    "const ui = SwaggerUIBundle({\n" +
                    "    url: \"openapi/openapi.yaml\",\n" +
                    "    dom_id: '#swagger-ui',\n" +
                    "    presets: [\n" +
                    "      SwaggerUIBundle.presets.apis,\n" +
                    "      SwaggerUIBundle.SwaggerUIStandalonePreset\n" +
                    "    ],\n" +
                    "  })\n" +
                    "}\n" +
                    "</script>\n" +
                    "<div id=\"swagger-ui\">\n" +
                    "</body>\n" +
                    "</html>\n",
            )
            .build()
    }
}
