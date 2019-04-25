package com.ogoons.coroutinesample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    private lateinit var tvCount: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCount = findViewById(R.id.tv_count)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener { countJob = startCount() }
        btnStop.setOnClickListener { stopCount() }

        startCoroutineBasics()
//        startLongRunningCoroutine()
//        startPausableCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    private var countJob: Job? = null

    private var timeCount: Int = 0

    private fun startCount() = launch {
        withContext(Dispatchers.Main) {
            tvCount.text = timeCount.toString()
        }
        while (isActive) {
            delay(1000)
            withContext(Dispatchers.Main) {
                tvCount.text = (++timeCount).toString()
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

    fun main(args: Array<String>) = runBlocking {
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500L)
                println("Task from nested launch")
            }
            delay(100L)
            println("Task from coroutine scope")
        }
        println("Coroutine scope is over")
    }
}
