package remocra.eventbus

interface EventListener<in T : Event> {

    fun onEvent(event: T)
}
