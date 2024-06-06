package remocra.eventbus

interface EventBus {

    fun post(event: Event)
}
