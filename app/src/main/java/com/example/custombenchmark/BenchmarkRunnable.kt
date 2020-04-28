package com.example.custombenchmark

import java.util.*

class BenchmarkRunnable(
    private val c: (BenchmarkResult) -> Unit
) : Runnable {

    override fun run() {
        val result = BenchmarkResult();
        Thread.sleep(2000)
        val array = Random(17).longs(25 * 1000).toArray()
        Thread.sleep(3000)
        val stopwatch = System.currentTimeMillis()
        bubbleSort(array)
        result.SingleCoreScore = System.currentTimeMillis() - stopwatch
        Thread.sleep(3000)
        c.invoke(result)
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