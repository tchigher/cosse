package com.arlo.sampleApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_start.setOnClickListener {

            val request = HttpRequest("https://myurl.com", 40000)

            //example of added header
            request.headers.put("Authorization", "1234456")

            val test = SseClient(request, object : SseListener {
                override fun onOpen(response: HttpResponse) {
                    text_status.setText("We connected!")
                }

                override fun onMessage(line: String) {
                    text_status.setText(line)
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
            })

            test.test()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
}
