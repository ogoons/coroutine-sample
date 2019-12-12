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
        get() = countJob!! + Dispatchers.Default // Background Thread

    private lateinit var tvCount: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        startCoroutineBasics1()
//        startCoroutineBasics2()
//        startCoroutineBasics3()
//        startLongRunningCoroutine()

//        startPausableCoroutine()
    }

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
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
        timeCount = 0
        tvCount.text = timeCount.toString()
    }

    /**
     * launch example
     */
    private fun startCoroutineBasics1() = runBlocking {
        // this expression blocks the main thread
        launch {
            // launch new coroutine in background and continue
            delay(1000L)
            println("Coroutine World!")
        }
        print("Hello, ") // main coroutine continues here immediately
        delay(2000L) // delaying for 2 seconds to keep JVM alive
        println("Complete")
    }

    /**
     * launch & runBlocking example
     */
    private fun startCoroutineBasics2() {
        GlobalScope.launch {
            // launch new coroutine in background and continue
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            println("sequence 4") // print after delay
        }
        println("sequence 1")
        runBlocking {
            // but this expression blocks the main thread
            delay(4000L) // ... while we delay for 2 seconds to keep JVM alive
            println("sequence 3")
        }
        println("sequence 2")
    }

    /**
     * Join example
     */
    private fun startCoroutineBasics3() = runBlocking {
        val job = GlobalScope.launch {
            // launch new coroutine and keep a reference to its Job
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        job.join() // wait until child coroutine completes
        println("AD1")
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
        job.join() // 이 블록에서부터 메인스레드를 block 하고 job이 완료되길 기다린다.
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
