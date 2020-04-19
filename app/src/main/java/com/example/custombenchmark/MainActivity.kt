package com.example.custombenchmark

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var logTextView:  TextView
    private lateinit var startButton: Button

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logTextView = findViewById(R.id.log_txt)
        logTextView.text = "Ready"

        startButton = findViewById(R.id.start_button)

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val r = msg.obj as Array<BenchmarkResult>
                r.forEach {
                    logTextView.append(
                        "SC-Score: ${it.SingleCoreScore} | " +
                                "MC-Score: ${it.MultiCoreScore.average().toLong()}\n\n"
                    )
                }
                logTextView.append("-------- AVG ---------\n")
                logTextView.append(
                    "SC-Score: ${r.map { it.SingleCoreScore }.average().toLong()} | " +
                            "MC-Score: ${r.flatMap { it.MultiCoreScore }.average().toLong()}\n\n"
                )

                startButton.text = "Start"
                startButton.isEnabled = true
            }
        }

        startButton.setOnClickListener { view ->
            startButton.isEnabled = false
            startButton.text = "Benchmarking..."
            logTextView.text = ""
            runBenchmark()
            logTextView.append("Started benchmarking!\n")
        }
    }

    private fun runBenchmark(n: Int = 10, results: MutableList<BenchmarkResult> = mutableListOf()) {
        Thread(BenchmarkRunnable { r ->
            results.add(r)
            if (results.size == n) handler.obtainMessage(1, results.toTypedArray()).sendToTarget()
            else runBenchmark(n, results)
        }).apply {
            isDaemon = true
            priority = Thread.MAX_PRIORITY
            name = "benchmark"
        }.start()
    }
}
