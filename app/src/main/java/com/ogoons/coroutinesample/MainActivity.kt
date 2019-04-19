package com.ogoons.coroutinesample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        startCoroutineBasics()
//        startLongRunningCoroutine()
//        startPausableCoroutine()
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

    private fun startCoroutineBasics() = runBlocking { // start main coroutine
        launch { // launch new coroutine in background and continue
            delay(1000L)
            println("World!")
        }
        print("Hello, ") // main coroutine continues here immediately
        delay(2000L) // delaying for 2 seconds to keep JVM alive
        println("Complete")
    }

    fun startLongRunningCoroutine() = runBlocking {
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

    private val dispatcher = PausableDispatcher(Handler(Looper.getMainLooper()))

    private fun startPausableCoroutine() {
        val job = GlobalScope.launch(dispatcher) {
            if (this.isActive) {
                suspendFunc()
            }
        }
        GlobalScope.launch {
            repeat(100) {
                delay(300)
                println("I'm working... count : $it")
            }
            println("Start Coroutine...")
            delay(3000L)
            dispatcher.pause()
            println("Pause Coroutine...")
            delay(3000L)
            dispatcher.resume()
            println("Resume Coroutine...")
            delay(3000L)
            job.cancelAndJoin()
            println("Cancel Coroutine...")
        }
    }

    private suspend fun suspendFunc() {
        repeat(100) {
            delay(300)
            println("I'm working... count : $it")
        }
    }



}
