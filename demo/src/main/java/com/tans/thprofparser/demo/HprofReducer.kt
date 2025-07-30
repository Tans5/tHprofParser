package com.tans.thprofparser.demo

import com.tans.thprofparser.reducer.reduceHprofFile
import java.io.File
import kotlin.time.measureTime

object HprofReducer {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        val outputReducedFile = File("./demo/output/reduced.zip")
        val cost = measureTime {
            reduceHprofFile(
                inputFile = inputHprofFile,
                outputFile = outputReducedFile
            )
        }
        println("Reduce profile cost: $cost")
    }
}