package com.arlo.sampleApp

interface SseListener {

    fun onOpen(response: HttpResponse)
    fun onMessage(line: String)
    fun onTimeout()
    fun onError(status: Int)
    fun onClose()

}