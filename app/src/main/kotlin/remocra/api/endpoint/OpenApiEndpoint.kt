package remocra.api.endpoint

import io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import jakarta.annotation.security.PermitAll
import jakarta.servlet.ServletConfig
import jakarta.servlet.http.HttpServletRequest
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
import remocra.security.SecurityHeadersFilter
import java.util.Collections
import kotlin.reflect.jvm.javaMethod

@Path("/openapi")
class OpenApiEndpoint : BaseOpenApiResource() {
    companion object {
        // !!!!! Attention à bien maintenir la version synchronisée avec la dépendance swagger-ui-dist dans le libs.versions.tml !!!!!
        const val SWAGGER_UI_VERSION = "5.31.0"
    }

    @Context
    lateinit var uriInfo: UriInfo

    init {
        val oas = OpenAPI()
            .servers(Collections.singletonList(Server().url("/api")))
            .info(Info().title("REMOcRA - API"))
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        io.swagger.v3.oas.models.security.SecurityScheme()
                            .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"),
                    ),
            )
            .security(listOf(io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth")))

        val oasConfig = SwaggerConfiguration()
            .openAPI(oas)
            .prettyPrint(true)
            .readAllResources(false)
            .scannerClass(JaxrsAnnotationScanner::class.java.getName())

        setOpenApiConfiguration(oasConfig)
    }

    @Public("Page de description des capacités de l'API, les appels à l'API, eux, sont authentifiés")
    @NoCsrf("Uniquement consultatif")
    @GET
    @Path("openapi.{type:json|yaml}")
    @Produces(value = [MediaType.APPLICATION_JSON, "application/yaml"])
    @Operation(hidden = true)
    @PermitAll
    @Throws(Exception::class)
    fun getOpenApiDescription(
        @Context headers: HttpHeaders,
        @Context config: ServletConfig,
        @Context app: Application,
        @PathParam("type") type: String,
    ): Response {
        return super.getOpenApi(headers, config, app, uriInfo, type)
    }

    @Public("Page de description des capacités de l'API, les appels à l'API, eux, sont authentifiés")
    @NoCsrf("Uniquement consultatif")
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    @PermitAll
    fun showOpenApi(@Context req: HttpServletRequest): Response {
        val nonce = req.getAttribute(SecurityHeadersFilter.NONCE_ATTRIBUTE_NAME) as String

        return Response.ok()
            .entity(
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <meta name="description" content="SwaggerUI" />
                  <title>API - Points d'eau</title>
                  <link rel="stylesheet" href="/webjars/swagger-ui-dist/$SWAGGER_UI_VERSION/swagger-ui.css" />
                </head>
                <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui-dist/$SWAGGER_UI_VERSION/swagger-ui-bundle.js"></script>
                <script nonce="$nonce">
                  window.onload = () => {
                    window.ui = SwaggerUIBundle({
                      url: "${uriInfo.baseUriBuilder.path(OpenApiEndpoint::class.java).path(this::getOpenApiDescription.javaMethod).build("yaml")}",
                      dom_id: '#swagger-ui',
                    });
                  };
                </script>
                </body>
                </html>
                """.trimIndent(),
            )
            .build()
    }
}
