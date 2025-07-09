package com.tans.thprofparser.reducer

import com.tans.thprofparser.ConstField
import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.HprofWriter
import com.tans.thprofparser.MemberField
import com.tans.thprofparser.StaticField
import com.tans.thprofparser.ValueHolder
import com.tans.thprofparser.readValue
import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext
import com.tans.thprofparser.writeValue
import okio.buffer
import okio.sink
import okio.source
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun reduceHprofFile(
    inputFile: File,
    preScanVisitor: HprofVisitor? = null,
    preScanCallback: (stringMap: Map<Long, Record.StringRecord>, loadClassMap: Map<Long, Record.LoadClassRecord>, classDumpMap: Map<Long, SubRecord.ClassDumpSubRecord>) -> Unit = { _, _, _ ->  },
    reduceConstField: (className: String?, constField: ConstField) -> Boolean = { _, _ -> true },
    reduceStaticField: (className: String?, fieldName: String?, staticField: StaticField) -> Boolean = { _, _, _ -> true },
    reducePrimitiveArray: (primitiveArrayDumpSubRecord: SubRecord.PrimitiveArrayDumpSubRecord) -> Boolean = { _ -> true},
    reduceInstanceField: (className: String?, memberFieldName: String?, classDump: SubRecord.ClassDumpSubRecord, memberField: MemberField, instance: SubRecord.InstanceDumpSubRecord) -> Boolean = { _, _, _, _, _ -> true}
): File {
    val dir = inputFile.canonicalFile.parentFile
    val outputFile = File(dir, "${inputFile.nameWithoutExtension}.zip")
    if (outputFile.exists()) {
        outputFile.delete()
    }
    outputFile.createNewFile()
    try {
        reduceHprofFile(
            inputFile,
            outputFile,
            preScanVisitor,
            preScanCallback,
            reduceConstField,
            reduceStaticField,
            reducePrimitiveArray,
            reduceInstanceField
        )
    } catch (e: Throwable) {
        outputFile.delete()
        throw e
    }

    return outputFile
}

fun reduceHprofFile(
    inputFile: File,
    outputFile: File,
    preScanVisitor: HprofVisitor? = null,
    preScanCallback: (stringMap: Map<Long, Record.StringRecord>, loadClassMap: Map<Long, Record.LoadClassRecord>, classDumpMap: Map<Long, SubRecord.ClassDumpSubRecord>) -> Unit = { _, _, _ ->  },
    reduceConstField: (className: String?, constField: ConstField) -> Boolean = { _, _ -> true },
    reduceStaticField: (className: String?, fieldName: String?, staticField: StaticField) -> Boolean = { _, _, _ -> true },
    reducePrimitiveArray: (primitiveArrayDumpSubRecord: SubRecord.PrimitiveArrayDumpSubRecord) -> Boolean = { _ -> true},
    reduceInstanceField: (className: String?, memberFieldName: String?, classDump: SubRecord.ClassDumpSubRecord, memberField: MemberField, instance: SubRecord.InstanceDumpSubRecord) -> Boolean = { _, _, _, _, _ -> true}
) {
    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }
    try {
        outputFile.outputStream().use {
            reduceHprofFile(
                inputFile,
                it,
                preScanVisitor,
                preScanCallback,
                reduceConstField,
                reduceStaticField,
                reducePrimitiveArray,
                reduceInstanceField
            )
        }
    } catch (e: Throwable) {
        outputFile.delete()
        throw e
    }
}

fun reduceHprofFile(
    inputFile: File,
    outputStream: OutputStream,
    preScanVisitor: HprofVisitor? = null,
    preScanCallback: (stringMap: Map<Long, Record.StringRecord>, loadClassMap: Map<Long, Record.LoadClassRecord>, classDumpMap: Map<Long, SubRecord.ClassDumpSubRecord>) -> Unit = { _, _, _ ->  },
    reduceConstField: (className: String?, constField: ConstField) -> Boolean = { _, _ -> true },
    reduceStaticField: (className: String?, fieldName: String?, staticField: StaticField) -> Boolean = { _, _, _ -> true },
    reducePrimitiveArray: (primitiveArrayDumpSubRecord: SubRecord.PrimitiveArrayDumpSubRecord) -> Boolean = { _ -> true},
    reduceInstanceField: (className: String?, memberFieldName: String?, classDump: SubRecord.ClassDumpSubRecord, memberField: MemberField, instance: SubRecord.InstanceDumpSubRecord) -> Boolean = { _, _, _, _, _ -> true}
) {
    if (!inputFile.canRead()) {
        throw HprofParserException("${inputFile.canonicalPath} can't read.")
    }
    val parentDirFile = inputFile.canonicalFile.parentFile
    if (!parentDirFile.canWrite()) {
        throw HprofParserException("$parentDirFile dir can't create new file.")
    }
    val stringMap: HashMap<Long, Record.StringRecord> = HashMap()
    val loadClassMap: HashMap<Long, Record.LoadClassRecord> = HashMap()
    val classDumpMap: HashMap<Long, SubRecord.ClassDumpSubRecord> = HashMap()

    inputFile.inputStream().use { inputStream ->
        val reader = HprofReader(inputStream)
        reader.accept(object : HprofVisitor(preScanVisitor) {

            override fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
                stringMap[context.record.id] = context.record
            }

            override fun visitLoadClassRecord(context: RecordContext<Record.LoadClassRecord>) {
                loadClassMap[context.record.id] = context.record
            }

            override fun visitHeapDumpRecord(
                tag: Int,
                timestamp: Long,
                header: HprofHeader
            ): HeapDumpRecordVisitor? {
                return object : HeapDumpRecordVisitor(tag, timestamp, header) {
                    override fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {
                        classDumpMap[context.subRecord.id] = context.subRecord
                    }
                }
            }
        })
    }

    preScanCallback(stringMap, loadClassMap, classDumpMap)

    val reducedFile = File(parentDirFile, "${inputFile.nameWithoutExtension}_reduced.hprof")
    if (reducedFile.exists()) {
        reducedFile.delete()
    }
    reducedFile.createNewFile()

    val classMemberFieldMap: HashMap<Long, List<MemberField>> = HashMap()

    try {
        inputFile.inputStream().use { inputStream ->
            reducedFile.outputStream().use { outputStream ->
                val reader = HprofReader(inputStream)
                val writer = HprofWriter(outputStream)
                reader.accept(object : HprofVisitor(writer) {
                    override fun visitHeapDumpRecord(
                        tag: Int,
                        timestamp: Long,
                        header: HprofHeader
                    ): HeapDumpRecordVisitor? {
                        return object : HeapDumpRecordVisitor(tag, timestamp, header, super.visitHeapDumpRecord(tag, timestamp, header)) {

                            override fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {
                                val record = context.subRecord
                                val constFields = record.constFields
                                val staticFields = record.staticFields
                                val className = stringMap[loadClassMap[record.id]?.classNameStringId]?.string
                                super.visitClassDumpSubRecord(
                                    context.copy(
                                        subRecord = context.subRecord.copy(
                                            constFields = constFields.map {
                                                if (it.value !is ValueHolder.ReferenceHolder && reduceConstField(className, it)) {
                                                    it.copy(value = it.value.setPrimitiveValueToZero())
                                                } else {
                                                    it
                                                }
                                            },
                                            staticFields = staticFields.map {
                                                if (it.value !is ValueHolder.ReferenceHolder && reduceStaticField(className, stringMap[it.fieldNameStrId]?.string, it)) {
                                                    it.copy(value = it.value.setPrimitiveValueToZero())
                                                } else {
                                                    it
                                                }
                                                it
                                            }
                                        )
                                    )
                                )
                            }

                            override fun visitPrimitiveArrayDumpSubRecord(context: SubRecordContext<SubRecord.PrimitiveArrayDumpSubRecord>) {
                                val record = context.subRecord
                                if (reducePrimitiveArray(record)) {
                                    super.visitPrimitiveArrayDumpSubRecord(context.copy(
                                        subRecord = record.copy(
                                            elements = record.elements.map { it.setPrimitiveValueToZero() }
                                        )
                                    ))
                                } else {
                                    super.visitPrimitiveArrayDumpSubRecord(context)
                                }
                            }

                            override fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {
                                val record = context.subRecord
                                val classDump = classDumpMap[record.classId]
                                if (classDump == null) {
                                    super.visitInstanceDumpSubRecord(context)
                                } else {
                                    val memberFields = findClassMemberFields(classDump.id, classMemberFieldMap, classDumpMap)
                                    if (memberFields.isEmpty()) {
                                        super.visitInstanceDumpSubRecord(context)
                                    } else {
                                        val className = stringMap[loadClassMap[classDump.id]?.classNameStringId]?.string
                                        val inputStream = ByteArrayInputStream(record.contentBytes)
                                        val outputStream = ByteArrayOutputStream()
                                        val source = inputStream.source().buffer()
                                        val sink = outputStream.sink().buffer()
                                        val header = context.header
                                        for (field in memberFields) {
                                            val value = source.readValue(field.type, header)
                                            if (value !is ValueHolder.ReferenceHolder && reduceInstanceField(className, stringMap[field.fieldNameStrId]?.string, classDump, field, record)) {
                                                sink.writeValue(value.setPrimitiveValueToZero(), header, false)
                                            } else {
                                                sink.writeValue(value, header, false)
                                            }
                                        }
                                        sink.flush()
                                        super.visitInstanceDumpSubRecord(context.copy(subRecord = record.copy(contentBytes = outputStream.toByteArray())))
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
        zipFiles(inputFiles = listOf(reducedFile), outputStream)
    } finally {
        // reducedFile.delete()
    }
}

private fun ValueHolder.setPrimitiveValueToZero(): ValueHolder {
    return when (this) {
        is ValueHolder.BooleanHolder -> this.copy(value = false)
        is ValueHolder.ByteHolder -> this.copy(value = 0)
        is ValueHolder.CharHolder -> this.copy(value = 0.toChar())
        is ValueHolder.DoubleHolder -> this.copy(value = 0.0)
        is ValueHolder.FloatHolder -> this.copy(value = 0.0f)
        is ValueHolder.IntHolder -> this.copy(value = 0)
        is ValueHolder.LongHolder -> this.copy(value = 0L)
        is ValueHolder.ShortHolder -> this.copy(value = 0)
        is ValueHolder.ReferenceHolder -> this
    }
}

private fun findClassMemberFields(
    classId: Long,
    cache: HashMap<Long, List<MemberField>>,
    classDumpMap: Map<Long, SubRecord.ClassDumpSubRecord>
): List<MemberField> {
    val c = cache[classId]
    if (c != null) {
        return c
    }
    val classDump = classDumpMap[classId]
    if (classDump == null) {
        return emptyList()
    }
    val mineMemberFields = classDump.memberFields
    val parentMemberFields = findClassMemberFields(classDump.superClassId, cache, classDumpMap)
    val result = mineMemberFields + parentMemberFields
    cache[classId] = result
    return result
}


private fun zipFiles(inputFiles: List<File>, outputStream: OutputStream) {
    val fixedInputFiles = inputFiles.filter { it.isFile && it.exists() }.map { it.canonicalPath }
    var entryNameCutIndex = 0
    if (fixedInputFiles.isNotEmpty()) {
        if (fixedInputFiles.size > 1) {
            while (true) {
                var finish = false
                val c = fixedInputFiles[0].getOrNull(entryNameCutIndex) ?: break
                for (i in 1 until fixedInputFiles.size) {
                    val fc = fixedInputFiles[i].getOrNull(entryNameCutIndex)
                    if (fc != c) {
                        finish = true
                        break
                    }
                }
                if (finish) {
                    break
                } else {
                    entryNameCutIndex ++
                }
            }
        } else {
            entryNameCutIndex = fixedInputFiles[0].lastIndexOf('/').coerceAtLeast(0)
        }
    }

    outputStream.use { fos ->
        ZipOutputStream(fos).use { zos ->
            for (f in fixedInputFiles) {
                val entryName = f.substring(entryNameCutIndex).removeSuffix(File.separator).replace(File.separator, "/")
                val entry = ZipEntry(entryName)
                entry.method = ZipEntry.DEFLATED
                zos.putNextEntry(entry)
                FileInputStream(f).use { it.copyTo(zos) }
            }
        }
    }
}

