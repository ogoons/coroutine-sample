package com.ogoons.coroutinesample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvCount: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCount = findViewById(R.id.tv_count)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener { startCount() }
        btnStop.setOnClickListener { stopCount() }

        testLongRunningCoroutine()
    }

    private var countJob: Job? = null

    private var timeCount: Int = 0

    private fun startCount() {
        tvCount.text = timeCount.toString()
        countJob = GlobalScope.launch {
            while (isActive) {
                delay(1000)
                runOnUiThread {
                    tvCount.text = (++timeCount).toString()
                }
            }
        }
    }

    private fun stopCount() {
        countJob?.cancel()
    }

    /**
     * 메인 스레드에서 실행
     */
    fun testLongRunningCoroutine() = runBlocking<Unit> {
        val job = launch(Dispatchers.IO) {
            repeat(10) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // just quit after delay
        println("Not yet")
        job.join()
        println("Complete")
    }
}
