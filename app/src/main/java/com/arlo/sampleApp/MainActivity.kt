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

            val request = HttpRequest("https://mydev3.arlo.com/hmsweb/client/subscribe/", 40000)

            //example of added header
            request.headers.put("Authorization", "2_57a1gozMkqXeebXgbfuQH7M_f22BDIIOkOTyY6YzdZXT0s_zowbFF422clS19vjyYxycnbkzqu-uh2Wqg76M2A3UKt-Hl42CqRfJkpA3w_O5ZAsGub2aLR-_C-RDFoUIhQeHWYZ0qI7FVOq-cQKCiHTC_FB4zhDBfJtiO7FnhxZ1")

            val test = SseClient(request, object : SseListener {
                override fun onOpen(response: HttpResponse) {
                    //log error codes here.
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
