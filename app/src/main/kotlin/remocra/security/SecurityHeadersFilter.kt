package remocra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import remocra.auth.AuthnConstants
import java.security.SecureRandom
import java.util.Base64

class SecurityHeadersFilter : HttpFilter() {
    companion object {
        const val NONCE_ATTRIBUTE_NAME = "remocra.security.nonce"

        // En dev avec "parcel watch" il faut assouplir la CSP pour autoriser le live-reload / HMR
        private val RELAX_CSP_FOR_DEVELOPMENT = java.lang.Boolean.getBoolean("remocra.http.relax-csp-for-development")

        private val BASE_CSP = "base-uri 'self'; frame-ancestors 'none'; default-src 'self'; " +
            "connect-src 'self' https://data.geopf.fr ${if (RELAX_CSP_FOR_DEVELOPMENT) "ws://localhost:*" else ""}; " +
            "script-src 'self' ${if (RELAX_CSP_FOR_DEVELOPMENT) "'unsafe-eval' 'unsafe-inline'" else ""}; " +
            // FIXME: on inclut unsafe-inline dans style-src-elem à cause de react-select: https://github.com/JedWatson/react-select/issues/4631
            // Il existe un workaround en passant le nonce à l'appli React, en utilisant @emotion/cache et un CacheProvider
            "style-src-elem 'self' 'unsafe-inline'; " +
            // On a besoin de unsafe-inline pour la ScaleLine OpenLayers: https://github.com/openlayers/openlayers/blob/bc0b4201dac2121b509de489a5c4770b57af50b9/src/ol/control/ScaleLine.js#L390
            "style-src-attr 'self' 'unsafe-inline'; " +
            // On a besoin de data: pour OpenLayers et https: pour permettre à l'admin d'ajouter des images sur la page d'accueil
            "img-src 'self' data: https:"
    }
    override fun doFilter(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        res.setHeader("X-Content-Type-Options", "nosniff")
        res.setHeader("X-Frame-Options", "DENY")
        res.setHeader("Referrer-Policy", "strict-origin")
        res.setHeader("Permissions-Policy", "accelerometer=(), camera=(), fullscreen=(self), geolocation=(self), gyroscope=(), magnetometer=(), microphone=(), payment=(), usb=()")
        res.setHeader("Cross-Origin-Resource-Policy", "same-site")
        // OSM memomaps n'autorise pas CORS et/ou n'utilise pas CORP, on passe donc par credentialless
        res.setHeader("Cross-Origin-Embedder-Policy", "credentialless")
        res.setHeader("Cross-Origin-Opener-Policy", "same-origin")

        var csp = BASE_CSP
        if (req.httpServletMapping.servletName == AuthnConstants.DEFAULT_SERVLET_NAME || req.requestURI.trimEnd('/') == AuthnConstants.OPENAPI_PATH) {
            // Inutile de générer un nonce pour chaque requête, on n'en a besoin que s'il sera utilisé (UserInfoFilter ou OpenApiEndpoint)
            val nonce = Base64.getEncoder().withoutPadding().encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })
            req.setAttribute(NONCE_ATTRIBUTE_NAME, nonce)
            csp += " ; script-src-elem 'self' 'nonce-$nonce'"
        }
        res.setHeader("Content-Security-Policy", csp)

        super.doFilter(req, res, chain)
    }
}
