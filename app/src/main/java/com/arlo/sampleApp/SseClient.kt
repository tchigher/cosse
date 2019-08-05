package com.arlo.sampleApp

import android.util.Log
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
        Log.d(TAG, "!!!!!! Stream connection was forced to disconnect");
    }

    fun test(): Unit {

        GlobalScope.launch {
            Log.i(TAG, "coroutine starts")

            //starts connection asynchronously
            val status = GlobalScope.async {  openChannel() }.await()
            Log.i(TAG, status.toString())

            if (status == HttpURLConnection.HTTP_OK) {
                connectionStatus = true
                buildResponse()

                //start coroutine on main thread
                launch (Dispatchers.Main) {
                    listener.onOpen(response)
                }

                //listen for message
                while (connectionStatus) {

                    val message = GlobalScope.async { reader() }.await()
                    Log.i(TAG, message)

                    launch(Dispatchers.Main) {
                        listener.onMessage(message)
                    }
                }

                if (!connectionStatus) {
                    Log.d(TAG, "Stream connection timed out.");
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
                Log.d(TAG, "Response Headers: ${it.key}, Value: ${it.value.iterator().next()}")
            }
        }

    }

    //start the channel
    fun openChannel(): Int {

        //no checked exceptions??
        val url = URL(request.url)

        Log.i(TAG, "Starting connection")

        urlConnection = url.openConnection() as HttpsURLConnection
        urlConnection.sslSocketFactory = TLSSocketFactory()
        urlConnection.requestMethod = request.requestMethod.toString()

        val headers = request.headers

        headers.forEach {
            urlConnection.setRequestProperty(it.key, it.value)
        }

        urlConnection.doInput = true
        urlConnection.useCaches = false
        urlConnection.connectTimeout = request.connectTimeout

        Log.d(
            TAG, "httpConnection:" + url.toString() +
                    " Method:" + urlConnection.getRequestMethod() +
                    " DoInput:" + urlConnection.getDoInput() +
                    " DoOutput:" + urlConnection.getDoOutput() +
                    " UseCaches:" + urlConnection.getUseCaches() +
                    " Connect Timeout:" + urlConnection.getConnectTimeout() +
                    " Read Timeout:" + urlConnection.getReadTimeout()
        )

        val properties = urlConnection.getRequestProperties()

        properties.forEach {
            Log.d(TAG, "Request Property: ${it.key}, Value: ${it.value}")
        }

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