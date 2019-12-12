package com.ogoons.coroutinesample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ScopedActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    private var parentJob = SupervisorJob()
    private var childJob: Job? = null
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
            childJob = startCount()
        }
        btnStop.setOnClickListener {
            stopCount()
        }
    }

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
    }

    private fun startCount() = launch { // 실행하는 순간 SupervisorJob의 자식 Job으로 등록
        tvCount.text = timeCount.toString()
        withContext(Dispatchers.Default) {
            while (isActive) {
                delay(1000)
                updateView() // call suspend function
            }
        }
    }

    private fun stopCount() {
        childJob?.cancel()
        timeCount = 0
        tvCount.text = timeCount.toString()
    }

    private suspend fun updateView() = withContext(Dispatchers.Main) {
        tvCount.text = (++timeCount).toString()
    }
}
