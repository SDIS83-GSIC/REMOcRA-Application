package remocra.favicon

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import remocra.GlobalConstants
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URLConnection
import java.nio.file.Files

class FaviconServlet : HttpServlet() {
    companion object {
        private val pathFavicon = GlobalConstants.DOSSIER_IMAGES_RESSOURCES.resolve("favicon")
    }
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate")

        val (input, fallback) = if (Files.exists(pathFavicon)) {
            Files.newInputStream(pathFavicon) to false
        } else {
            javaClass.getResourceAsStream("/favicon/favicon.png") to true
        }

        if (input == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }

        input.use { stream ->
            val bufferedStream = BufferedInputStream(stream)
            val contentType = if (fallback) {
                "image/png"
            } else {
                detectImageContentType(bufferedStream)
            }

            resp.contentType = contentType
            bufferedStream.copyTo(resp.outputStream)
        }
    }

    private fun detectImageContentType(stream: InputStream): String {
        if (!stream.markSupported()) {
            return "application/octet-stream"
        }

        stream.mark(8192)
        val guessed = URLConnection.guessContentTypeFromStream(stream)
        stream.reset()

        return guessed ?: "application/octet-stream"
    }
}
