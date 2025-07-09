package com.tans.thprofparser.demo


import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.HprofWriter
import java.io.File

object HprofWrite {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        val outputDir = File("./demo/output")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val outputHprofFile = File(outputDir, "output.hprof")
        if (!outputHprofFile.exists()) {
            outputHprofFile.createNewFile()
        }
        inputHprofFile.inputStream().use { inputStream ->
            outputHprofFile.outputStream().use { outputStream ->
                val reader = HprofReader(inputStream)
                val writer = HprofWriter(outputStream)
                val visitor = object : HprofVisitor(writer) {}
                reader.accept(visitor)
            }
        }
    }
}