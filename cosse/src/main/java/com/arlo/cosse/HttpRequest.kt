package com.arlo.cosse

open class HttpRequest(val url: String) {

    var headers: MutableMap<String,String>
    val requestMethod: RequestMethod
    val connectTimeout: Int
    var channelTimeout: Long

    constructor(url:String, _channelTimeout: Long):this(url) {
        this.channelTimeout = _channelTimeout
    }

    init {
        headers = mutableMapOf()
        headers.put("User-Agent", System.getProperties().getProperty("http.agent"))
        headers.put("Connection", "Keep-Alive")
        headers.put("Accept-Encoding", "")
        headers.put("Accept", "text/event-stream")
        headers.put("Cache-Control", "no-cache")
        connectTimeout = 10000
        channelTimeout = -999
        requestMethod = RequestMethod.GET
    }

}