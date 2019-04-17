package com.ogoons.coroutinesample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            // launch는 코루틴의 빌더
            delay(1000)
            println("world")
            repeat(100) {
                delay(1000)
                println(System.currentTimeMillis())
            }
        }
        job.start()
        Thread.sleep(2000)
        println("Hello, ")

        runBlocking {

        }

    }
}
