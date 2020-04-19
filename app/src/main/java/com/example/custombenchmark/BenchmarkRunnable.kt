package com.example.custombenchmark

import java.util.*
import kotlin.concurrent.thread

class BenchmarkRunnable(
    private val c: (BenchmarkResult) -> Unit
) : Runnable {

    companion object {
        private val data: LongArray = Random(17).longs(50 * 1000).toArray()
    }

    override fun run() {
        val result = BenchmarkResult()

        Thread.sleep(5000)

        // single core
        val t = thread(
            start = true,
            isDaemon = true,
            priority = Thread.MAX_PRIORITY,
            name = "benchmark-single"
        ) {
            result.SingleCoreScore = benchmark(data.copyOf())
        }

        while (t.isAlive) Thread.sleep(1000)
        Thread.sleep(5000)

        // multi core
        val n = Runtime.getRuntime().availableProcessors()
        val ts = mutableListOf<Thread>()
        repeat(n) {
            ts.add(thread(
                start = false,
                isDaemon = true,
                priority = Thread.MAX_PRIORITY,
                name = "benchmark-multi-$it"
            ) {
                val score = benchmark(data.copyOf())
                synchronized(result) {
                    result.MultiCoreScore.add(score)
                }
            })
        }
        ts.forEach { it.start() }

        while (ts.any { it.isAlive }) Thread.sleep(1000)

        c.invoke(result)
    }

    fun benchmark(array: LongArray): Long {
        val stopwatch = System.currentTimeMillis()
        bubbleSort(array)
        return System.currentTimeMillis() - stopwatch
    }

    fun bubbleSort(arr: LongArray) {
        val n = arr.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) if (arr[j] > arr[j + 1]) {
                // swap arr[j+1] and arr[i]
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
            }
        }
    }
}