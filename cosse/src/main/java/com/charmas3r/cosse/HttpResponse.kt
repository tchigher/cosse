package com.charmas3r.cosse

class HttpResponse(val request: HttpRequest) {

    var responseCode: Int
    var headers: MutableMap<String, String>

    var body: String? = null
    var isErrorStream: Boolean = false
    var exception: Throwable? = null

    init {
        responseCode = -99
        headers = mutableMapOf()
    }

}