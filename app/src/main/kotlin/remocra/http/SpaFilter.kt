package remocra.http

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper

/**
 * Pour utilisation avec `history.pushState()`, catche les 404 et retourne l'index.html à la place.
 */
class SpaFilter : HttpFilter() {

    override fun doFilter(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        if (req.pathInfo == "/favicon.ico") {
            chain.doFilter(req, res)
            return
        }

        val response = NotFoundCatcherResponse(res)
        chain.doFilter(req, response)
        if (response.isError) {
            res.setHeader("Content-Location", "/")
            req.getRequestDispatcher("/").forward(req, res)
        }
    }

    private class NotFoundCatcherResponse(res: HttpServletResponse) : HttpServletResponseWrapper(res) {
        var isError: Boolean = false

        override fun sendError(sc: Int) {
            if (sc == SC_NOT_FOUND) {
                check(!isCommitted)
                isError = true
            } else {
                super.sendError(sc)
            }
        }

        override fun sendError(sc: Int, msg: String?) {
            if (sc == SC_NOT_FOUND) {
                check(!isCommitted)
                isError = true
            } else {
                super.sendError(sc)
            }
        }
    }
}
