package com.ogoons.coroutinesample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_basic_1).setOnClickListener {
            startCoroutineBasics1()
        }

        findViewById<Button>(R.id.btn_basic_2).setOnClickListener {
            startCoroutineBasics2()
        }

        findViewById<Button>(R.id.btn_basic_3).setOnClickListener {
            startCoroutineBasics3()
        }

        findViewById<Button>(R.id.btn_basic_4).setOnClickListener {
            startLongRunningCoroutine()
        }

        findViewById<Button>(R.id.btn_non_scoped).setOnClickListener {
            startActivity(Intent(this, NonScopedActivity::class.java))
        }

        findViewById<Button>(R.id.btn_scoped).setOnClickListener {
            startActivity(Intent(this, ScopedActivity::class.java))
        }
    }

    /**
     * launch & runBlocking example
     */
    private fun startCoroutineBasics1() = runBlocking { // this expression blocks the main thread (default)
        launch {
            // launch new coroutine in background and continue
            delay(1000L)
            println("Coroutine World!")
        }
        print("Hello, ") // main coroutine continues here immediately
        delay(2000L) // delaying for 2 seconds to keep JVM alive
        println("Finish")
    }

    /**
     * launch & runBlocking example
     */
    private fun startCoroutineBasics2() {
        GlobalScope.launch { // launch new coroutine in background and continue
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            println("sequence 4") // print after delay
        }
        println("sequence 1")
        runBlocking { // but this expression blocks the main thread
            delay(4000L) // ... while we delay for 4 seconds to keep JVM alive
            println("sequence 3")
        }
        println("sequence 2")
    }

    /**
     * Join example
     */
    private fun startCoroutineBasics3() = runBlocking {
        val job = GlobalScope.launch { // launch new coroutine and keep a reference to its Job
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
        println("Finish")
    }
}
