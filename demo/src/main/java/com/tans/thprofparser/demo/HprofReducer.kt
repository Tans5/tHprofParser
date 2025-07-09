package com.tans.thprofparser.demo

import com.tans.thprofparser.reducer.reduceHprofFile
import java.io.File
import kotlin.time.measureTime

object HprofReducer {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump1.hprof")
        val outputReducedFile = File("./demo/output/reduced.zip")
        val cost = measureTime {
            reduceHprofFile(inputHprofFile, outputReducedFile)
        }
        println("Reduce profile cost: $cost")
    }
}