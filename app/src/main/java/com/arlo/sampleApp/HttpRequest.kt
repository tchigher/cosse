package com.arlo.sampleApp

import org.json.JSONObject

open class HttpRequest(val url: String, var connectTimeout: Int) {

    var headers: MutableMap<String,String>
    val requestMethod: RequestMethod

    init {
        headers = mutableMapOf()
        headers.put("User-Agent", System.getProperties().getProperty("http.agent"))
        headers.put("Connection", "Keep-Alive")
        headers.put("Accept-Encoding", "")
        headers.put("Accept", "text/event-stream")
        headers.put("Cache-Control", "no-cache")

        connectTimeout = 40000
        requestMethod = RequestMethod.GET
    }

}