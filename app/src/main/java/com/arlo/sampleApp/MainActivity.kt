package com.arlo.sampleApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.arlo.cosse.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = HttpRequest("https://myurl.com", 10000)
        request.headers.put("Authorization", "123456789")

        val test = SseClient(request, object : SseListener {
            override fun onOpen(response: HttpResponse) {
                Log.i(TAG, response.responseCode.toString())
                Log.i(TAG, "Channel opened")
                response.headers.forEach {
                        Log.d(TAG, "Response Header: ${it.key}, Value: ${it.value}")
                }
            }

            override fun onMessage(line: String, obj: JSONObject) {
                Log.i(TAG, line)
                Log.i(TAG, obj.toString())
                text_response.setText(line)
            }

            override fun onTimeout() {
                Log.i(TAG, "SSE Timed out")
            }

            override fun onError(status: Int) {
                Log.i(TAG, "Error response code: " + status);
            }

            override fun onClose() {
                Log.i(TAG, "SSE Closed")
            }
        });

        btn_test.setOnClickListener {
            test.execute()
        }
    }
}
