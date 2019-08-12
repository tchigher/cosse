package com.charmas3r.cosse

interface SseListener {

    fun onOpen(response: HttpResponse)
    fun onMessage(line: String)
    fun onTimeout()
    fun onError(status: Int)
    fun onClose()
}