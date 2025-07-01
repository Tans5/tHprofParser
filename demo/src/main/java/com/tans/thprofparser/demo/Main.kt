package com.tans.thprofparser.demo


import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.HprofWriter
import com.tans.thprofparser.recorders.RecorderContext
import com.tans.thprofparser.recorders.StringRecorderVisitor
import java.io.File

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/dump.hprof")
        val outputHprofFile = File("./demo/output_dump.hprof")
        if (!outputHprofFile.exists()) {
            outputHprofFile.createNewFile()
        }
        inputHprofFile.inputStream().use { inputStream ->
            outputHprofFile.outputStream().use { outputStream ->
                val reader = HprofReader(inputStream)
                val writer = HprofWriter(outputStream)
                val visitor = object : HprofVisitor(writer) {
                    override fun visitStringRecorder(recorderContext: RecorderContext): StringRecorderVisitor? {
                        val next = super.visitStringRecorder(recorderContext)
                        return object : StringRecorderVisitor(next) {
                            override fun visitString(stringContext: Companion.StringContext) {
                                println("Id: ${stringContext.id}, String: ${stringContext.str}")
                                super.visitString(stringContext)
                            }
                        }
                    }
                }
                reader.accept(visitor)
            }
        }
    }
}