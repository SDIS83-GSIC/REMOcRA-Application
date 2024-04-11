package remocra.resteasy

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.ext.Provider
import org.jboss.resteasy.plugins.providers.multipart.InputPart
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationReader
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInputImpl
import org.jboss.resteasy.spi.ReaderException
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.ParameterizedType

/** Version du MultipartFormAnnotationReader qui gère les listes. */
@Provider
@Consumes("multipart/form-data")
class MultipartFormAnnotationReader : MultipartFormAnnotationReader() {

    @Throws(IOException::class)
    override fun setFields(type: Class<*>, input: MultipartFormDataInputImpl, obj: Any): Boolean {
        var hasInputStream = false
        for (field in type.declaredFields) {
            if (field.isAnnotationPresent(FormParam::class.java)) {
                val param = field.getAnnotation(FormParam::class.java)
                val list = input.formDataMap[param.value]
                if (list == null || list.isEmpty()) continue
                val part = list[0] ?: continue

                val data =
                    if (InputPart::class.java == field.type) {
                        hasInputStream = true
                        part
                    } else {
                        if (InputStream::class.java == field.type) {
                            hasInputStream = true
                        }
                        // TODO: gérer les tableaux
                        if (field.type == List::class.java) {
                            val elementType =
                                (field.genericType as ParameterizedType).actualTypeArguments[0]
                            list.map { p -> p.getBody(elementType as Class<*>, field.genericType) }
                        } else {
                            part.getBody(field.type, field.genericType)
                        }
                    }
                try {
                    field.set(obj, data)
                } catch (e: IllegalAccessException) {
                    throw ReaderException(e)
                }
            }
        }
        return hasInputStream
    }
}
