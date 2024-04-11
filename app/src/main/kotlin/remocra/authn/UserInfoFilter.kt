package remocra.authn

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper

class UserInfoFilter @Inject constructor(private val objectMapper: ObjectMapper) : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        // Il faut wrapper le ServletOutputStream pour bypasser une optimisation de Jetty
        chain.doFilter(
            request,
            object : HttpServletResponseWrapper(response) {
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

        // On regarde si on a déjà un user connecté
        val userInfo = (request.userPrincipal as? UserPrincipal)?.userInfo

        val javascriptUser = objectMapper.writeValueAsString((userInfo)?.asJavascriptUserProfile())

        response.outputStream?.println("""<script>const userInfo = $javascriptUser</script>""")
    }
}
