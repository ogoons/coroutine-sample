package com.ogoons.coroutinesample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class NonScopedActivity : AppCompatActivity() {

    private var countJob: Job? = null
    private var timeCount: Int = 0

    private lateinit var tvCount: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scope)

        tvCount = findViewById(R.id.tv_count)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            stopCount()
            countJob = startCount()
        }
        btnStop.setOnClickListener {
            stopCount()
        }
    }

    override fun onDestroy() {
        countJob?.cancel()
        super.onDestroy()
    }

    /**
     * launch -> withContext
     */
    private fun startCount() = GlobalScope.launch(Dispatchers.Main) {
        tvCount.text = timeCount.toString()
        launch(Dispatchers.Default) { // children
            while (isActive) {
                delay(1000)
                launch(Dispatchers.Main) { // grandchildren????? OMG~
                    tvCount.text = (++timeCount).toString()
                }
            }
        }
    }

    private fun stopCount() {
        countJob?.cancel()
        timeCount = 0
        tvCount.text = timeCount.toString()
    }
}
