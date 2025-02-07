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
    }
    override fun doFilter(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        res.setHeader("X-Content-Type-Options", "nosniff")
        res.setHeader("X-Frame-Options", "DENY")
        res.setHeader("Referrer-Policy", "strict-origin")
        res.setHeader("Permissions-Policy", "accelerometer=(), camera=(), fullscreen=(), geolocation=(self), gyroscope=(), magnetometer=(), microphone=(), payment=(), usb=()")
        res.setHeader("Cross-Origin-Resource-Policy", "same-site")
        res.setHeader("Cross-Origin-Embedder-Policy", "require-corp")
        res.setHeader("Cross-Origin-Opener-Policy", "same-origin")

        var csp = "base-uri 'self'; frame-ancestors 'none'; default-src 'self'; " +
            "connect-src 'self' https://api-adresse.data.gouv.fr; " +
            // FIXME: on inclut unsafe-inline dans style-src-elem à cause de react-select: https://github.com/JedWatson/react-select/issues/4631
            // Il existe un workaround en passant le nonce à l'appli React, en utilisant @emotion/cache et un CacheProvider
            "style-src-elem 'self' 'unsafe-inline'; " +
            // On a besoin de unsafe-inline pour la ScaleLine OpenLayers: https://github.com/openlayers/openlayers/blob/bc0b4201dac2121b509de489a5c4770b57af50b9/src/ol/control/ScaleLine.js#L390
            "style-src-attr 'self' 'unsafe-inline'; " +
            // On a besoin de data: pour OpenLayers…
            "img-src 'self' data:"
        if (req.httpServletMapping.servletName == AuthnConstants.DEFAULT_SERVLET_NAME) {
            // Inutile de générer un nonce pour chaque requête, on n'en a besoin que si UserInfoFilter peut être dans la FilterChain
            val nonce = Base64.getEncoder().withoutPadding().encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })
            req.setAttribute(NONCE_ATTRIBUTE_NAME, nonce)
            csp += "; script-src 'self' 'nonce-$nonce'"
        }
        res.setHeader("Content-Security-Policy", csp)

        super.doFilter(req, res, chain)
    }
}
