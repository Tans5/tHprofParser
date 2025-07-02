package com.tans.thprofparser.demo


import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.HprofWriter
import com.tans.thprofparser.records.StringContext
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
                    override fun visitStringRecord(context: StringContext) {
                        super.visitStringRecord(context)
                        println("Id: ${context.id}, Str: ${context.str}")
                    }
                }
                reader.accept(visitor)
            }
        }
    }
}