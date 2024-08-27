package remocra.data

import jakarta.ws.rs.QueryParam

data class Params<T, U>(
    @QueryParam("limit")
    val limit: Int? = 10,
    @QueryParam("offset")
    val offset: Int? = 0,
    @QueryParam("filterBy")
    val filterBy: T?,
    @QueryParam("sortBy")
    val sortBy: U?,
)
