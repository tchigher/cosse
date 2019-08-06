package com.arlo.cosse

/*
Copyright 2019 Evan Smith

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection
import java.net.URL

class SseClient(val request: HttpRequest, val listener: SseListener) {

    val TAG = SseClient::class.simpleName
    lateinit var urlConnection: HttpsURLConnection
    lateinit var response: HttpResponse
    var connectionStatus: Boolean

    init {
        connectionStatus = false;
    }

    fun closeChannel() {
        connectionStatus = false;
        urlConnection.setConnectTimeout(2);
        urlConnection.setReadTimeout(2);
        urlConnection.disconnect();
        listener.onClose()
    }

    fun execute(): Unit {

        GlobalScope.launch {

            val status = GlobalScope.async {  openChannel() }.await()

            if (status == HttpURLConnection.HTTP_OK) {
                connectionStatus = true
                buildResponse()

                launch (Dispatchers.Main) {
                    listener.onOpen(response)
                }

                while (connectionStatus) {

                    val message = GlobalScope.async { reader() }.await()

                    launch(Dispatchers.Main) {
                        listener.onMessage(message)
                    }
                }

                if (!connectionStatus) {
                    listener.onTimeout()
                }
            }
            else {
                connectionStatus = false;
                listener.onError(status)
                urlConnection.disconnect()
            }
        }
    }

    fun buildResponse() {
        response = HttpResponse(request)
        response.responseCode = urlConnection.responseCode
        response.body = urlConnection.responseMessage
        val responseHeaders = urlConnection.headerFields

        responseHeaders.forEach {
            if (it.key != null && it.value != null) {
                response.headers.put(it.key, it.value.iterator().next())
//                Log.d(TAG, "Response Headers: ${it.key}, Value: ${it.value.iterator().next()}")
            }
        }

    }

    fun openChannel(): Int {

        val url = URL(request.url)
        val headers = request.headers

        urlConnection = url.openConnection() as HttpsURLConnection
        urlConnection.sslSocketFactory = TLSSocketFactory()
        urlConnection.requestMethod = request.requestMethod.toString()

        headers.forEach {
            urlConnection.setRequestProperty(it.key, it.value)
        }

        urlConnection.doInput = true
        urlConnection.useCaches = false
        urlConnection.connectTimeout = request.connectTimeout

        val properties = urlConnection.getRequestProperties()
        urlConnection.connect()

        return urlConnection.responseCode
    }

    fun reader() : String{

        val br = BufferedReader(InputStreamReader(urlConnection.getInputStream(), "UTF-8"))
        val sb = StringBuilder(5000)
        var line: String? = null

        while ({ line = br.readLine(); line }() != null && line!!.isNotBlank()) {
            sb.append(line + "\n")
        }

        return sb.toString()
    }

}