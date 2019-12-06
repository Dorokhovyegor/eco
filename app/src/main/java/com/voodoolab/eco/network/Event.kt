package com.voodoolab.eco.network

class Event<T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? { // берем контент, если не был обработан
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T {  // берем контент, даже если уже обработали его
        return content
    }

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled"
    }

    companion object {

        // получаем датаэвент, если есть дата
        fun <T> dataEvent(data: T?): Event<T>? {
            data?.let {
                return Event(it)
            }
            return null
        }

        // получаем мессаджевент, если есть мессадж
        fun messageEvent(message: String?): Event<String>? {
            message?.let {
                return Event(message)
            }
            return null
        }
    }
}