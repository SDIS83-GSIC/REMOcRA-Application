package remocra.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import jakarta.inject.Inject
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import remocra.security.SecurityHeadersFilter

class UserInfoFilter @Inject constructor(
    private val objectMapper: ObjectMapper,
) : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, no-store")

        val userInfo = (request.userPrincipal as? RemocraUserPrincipal)?.userInfo

        if (userInfo?.isActif == false) {
            response.sendError(403, "Votre compte n'est pas actif. Veuillez contacter le SDIS.")
            return
        }

        val nonce = request.getAttribute(SecurityHeadersFilter.NONCE_ATTRIBUTE_NAME) as String
        val javascriptUser = objectMapper.writeValueAsString(userInfo?.asJavascriptUserProfile())
        val script = """<script nonce="$nonce">const userInfo = $javascriptUser</script>"""
        val scriptBytes = script.toByteArray(Charsets.UTF_8)

        // Il faut wrapper le ServletOutputStream pour bypasser une optimisation de Jetty
        chain.doFilter(
            request,
            object : HttpServletResponseWrapper(response) {

                private fun isContentLength(headerName: String?): Boolean {
                    return "content-length".equals(headerName, ignoreCase = true)
                }

                override fun setContentLength(len: Int) {
                    super.setContentLength(len + scriptBytes.size)
                }

                override fun setContentLengthLong(len: Long) {
                    super.setContentLengthLong(len + scriptBytes.size)
                }

                override fun setIntHeader(name: String?, value: Int) {
                    var v = value
                    if (isContentLength(name)) {
                        v += scriptBytes.size
                    }
                    super.setIntHeader(name, v)
                }

                override fun setHeader(name: String?, value: String?) {
                    var v = value
                    if (isContentLength(name) && value != null) {
                        v = value.toLongOrNull()?.let { (it + scriptBytes.size).toString() }
                    }
                    super.setHeader(name, v)
                }

                override fun addHeader(name: String?, value: String?) {
                    var v = value
                    if (isContentLength(name) && value != null) {
                        v = value.toLongOrNull()?.let { (it + scriptBytes.size).toString() }
                    }
                    super.addHeader(name, v)
                }

                override fun addIntHeader(name: String?, value: Int) {
                    var v = value
                    if (isContentLength(name)) {
                        v += scriptBytes.size
                    }
                    super.addIntHeader(name, v)
                }

                override fun getOutputStream(): ServletOutputStream {
                    val os = super.getOutputStream()
                    return object : ServletOutputStream() {
                        override fun isReady() = os.isReady

                        override fun write(b: Int) = os.write(b)
                        override fun write(b: ByteArray) = os.write(b)
                        override fun write(b: ByteArray, off: Int, len: Int) =
                            os.write(b, off, len)

                        override fun setWriteListener(writeListener: WriteListener?) =
                            os.setWriteListener(writeListener)
                    }
                }
            },
        )

        response.outputStream?.write(scriptBytes)
    }
}
