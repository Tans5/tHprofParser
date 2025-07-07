package com.tans.thprofparser.demo

import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import java.io.File

object HprofRead {
    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/output/output.hprof")
        inputHprofFile.inputStream().use { inputStream ->
            val reader = HprofReader(inputStream)
            val visitor = object : HprofVisitor() {
            }
            reader.accept(visitor)
        }
    }
}