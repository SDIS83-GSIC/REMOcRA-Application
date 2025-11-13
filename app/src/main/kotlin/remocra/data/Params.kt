package remocra.data

import jakarta.ws.rs.QueryParam

data class Params<T, U>(
    @param:QueryParam("limit")
    val limit: Int? = 10,
    @param:QueryParam("offset")
    val offset: Int? = 0,
    @param:QueryParam("filterBy")
    val filterBy: T?,
    @param:QueryParam("sortBy")
    val sortBy: U?,
)
