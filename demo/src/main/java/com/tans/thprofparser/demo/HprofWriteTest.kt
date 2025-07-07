package com.tans.thprofparser.demo


import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.HprofWriter
import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.HeapDumpRecordWriter
import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.ByteArrayOutputStream
import java.io.File

object HprofWriteTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        inputHprofFile.inputStream().use { inputStream ->
            val reader = HprofReader(inputStream)
            val visitor = object : HprofVisitor() {

                fun recordTest(
                    recordContext: RecordContext<*>,
                    msg: String,
                    writeRecord: HprofWriter.Companion.(sink: BufferedSink) -> Unit) {
                    val outputStream = ByteArrayOutputStream()
                    val sink = outputStream.sink().buffer()
                    with(HprofWriter.Companion) {
                        writeRecord(sink)
                        sink.flush()
                    }
                    val bytes1 = outputStream.toByteArray()
                    val bytes2 = readFileBytes(inputHprofFile, recordContext.fileOffset, recordContext.record.calculateBodyLen(recordContext.header) + 9)
                    try {
                        assertIsSame(bytes1, bytes2, msg)
                    } catch (e: Throwable) {
                        println("FileOffset=${String.format("0x%X", recordContext.fileOffset)}: ")
                        throw e
                    }
                }

                fun subRecordTest(
                    subRecordContext: SubRecordContext<*>,
                    msg: String,
                    writeSubRecord: HeapDumpRecordWriter.Companion.(sink: BufferedSink) -> Unit
                ) {
                    val outputStream = ByteArrayOutputStream()
                    val sink = outputStream.sink().buffer()
                    with(HeapDumpRecordWriter.Companion) {
                        writeSubRecord(sink)
                        sink.flush()
                    }
                    val bytes1 = outputStream.toByteArray()
                    val bytes2 = readFileBytes(inputHprofFile, subRecordContext.fileOffset, subRecordContext.subRecord.calculateBodyLen(subRecordContext.header) + 1)
                    try {
                        assertIsSame(bytes1, bytes2, msg)
                    } catch (e: Throwable) {
                        print("Byte1: 0x")
                        for (b in bytes1) {
                            print(String.format("%02X", b))
                        }
                        println()
                        println("FileOffset=${String.format("0x%X", subRecordContext.fileOffset)}: ")
                        print("Byte2: 0x")
                        for (b in bytes2) {
                            print(String.format("%02X", b))
                        }
                        println()
                        throw e
                    }
                }

                override fun visitHeader(header: HprofHeader) {
                    val outputStream = ByteArrayOutputStream()
                    val sink = outputStream.sink().buffer()
                    with(HprofWriter.Companion) {
                        sink.writeHprofHeader(header)
                        sink.flush()
                    }
                    val bytes1 = outputStream.toByteArray()
                    val bytes2 = readFileBytes(inputHprofFile, 0, header.len)
                    assertIsSame(bytes1, bytes2, "HprofHeader")
                }

                override fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
                    recordTest( context, "StringRecord") { sink ->
                        sink.writeStringRecord(context.record, context.header)
                    }
                }

                override fun visitLoadClassRecord(context: RecordContext<Record.LoadClassRecord>) {
                    recordTest(context, "LoadClassRecord") { sink ->
                        sink.writeLoadClassRecord(context.record, context.header)
                    }
                }

                override fun visitUnloadClassRecord(context: RecordContext<Record.UnloadClassRecord>) {
                    recordTest(context, "UnloadClassRecord") { sink ->
                        sink.writeUnloadClassRecord(context.record, context.header)
                    }
                }

                override fun visitStackFrameRecord(context: RecordContext<Record.StackFrameRecord>) {
                    recordTest(context, "StackFrameRecord") { sink ->
                        sink.writeStackFrameRecord(context.record, context.header)
                    }
                }

                override fun visitStackTraceRecord(context: RecordContext<Record.StackTraceRecord>) {
                    recordTest(context, "StackTraceRecord") { sink ->
                        sink.writeStackTraceRecord(context.record, context.header)
                    }
                }

                override fun visitUnknownRecord(
                    context: RecordContext<Record.UnknownRecord>
                ) {
                    recordTest(context, "UnknownRecord") { sink ->
                        sink.writeUnknownRecord(context.record, context.header)
                    }
                }

                override fun visitHeapDumpRecord(
                    tag: Int,
                    timestamp: Long,
                    header: HprofHeader
                ): HeapDumpRecordVisitor? {
                    return object : HeapDumpRecordVisitor(tag, timestamp, header) {

                        override fun visitRootUnknownSubRecord(context: SubRecordContext<SubRecord.RootUnknownSubRecord>) {
                            subRecordTest(context, "RootUnknownSubRecord") { sink ->
                                sink.writeRootUnknownSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootJniGlobalSubRecord(context: SubRecordContext<SubRecord.RootJniGlobalSubRecord>) {
                            subRecordTest(context, "RootJniGlobalSubRecord") { sink ->
                                sink.writeRootJniGlobalSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootJniLocalSubRecord(context: SubRecordContext<SubRecord.RootJniLocalSubRecord>) {
                            subRecordTest(context, "RootJniLocalSubRecord") { sink ->
                                sink.writeRootJniLocalSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootJavaFrameSubRecord(context: SubRecordContext<SubRecord.RootJavaFrameSubRecord>) {
                            subRecordTest(context, "RootJavaFrameSubRecord") { sink ->
                                sink.writeRootJavaFrameSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootNativeStackSubRecord(context: SubRecordContext<SubRecord.RootNativeStackSubRecord>) {
                            subRecordTest(context, "RootNativeStackSubRecord") { sink ->
                                sink.writeRootNativeStackSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootStickyClassSubRecord(context: SubRecordContext<SubRecord.RootStickyClassSubRecord>) {
                            subRecordTest(context, "RootStickySubRecord") { sink ->
                                sink.writeRootStickyClassSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootThreadBlockSubRecord(context: SubRecordContext<SubRecord.RootThreadBlockSubRecord>) {
                            subRecordTest(context, "RootThreadBlockSubRecord") { sink ->
                                sink.writeRootThreadBlockSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootMonitorUsedSubRecord(context: SubRecordContext<SubRecord.RootMonitorUsedSubRecord>) {
                            subRecordTest(context, "RootMonitorUsedSubRecord") { sink ->
                                sink.writeRootMonitorUsedSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootThreadObjectSubRecord(context: SubRecordContext<SubRecord.RootThreadObjectSubRecord>) {
                            subRecordTest(context, "RootThreadObjectSubRecord") { sink ->
                                sink.writeRootThreadObjectSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootInternedStringSubRecord(context: SubRecordContext<SubRecord.RootInternedStringSubRecord>) {
                            subRecordTest(context, "RootInternedStringSubRecord") { sink ->
                                sink.writeRootInternedStringSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootFinalizingSubRecord(context: SubRecordContext<SubRecord.RootFinalizingSubRecord>) {
                            subRecordTest(context, "RootFinalizingSubRecord") { sink ->
                                sink.writeRootFinalizingSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootDebuggerSubRecord(context: SubRecordContext<SubRecord.RootDebuggerSubRecord>) {
                            subRecordTest(context, "RootDebuggerSubRecord") { sink ->
                                sink.writeRootDebuggerSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootReferenceCleanupSubRecord(context: SubRecordContext<SubRecord.RootReferenceCleanupSubRecord>) {
                            subRecordTest(context, "RootReferenceCleanupSubRecord") { sink ->
                                sink.writeRootReferenceCleanupSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootVmInternalSubRecord(context: SubRecordContext<SubRecord.RootVmInternalSubRecord>) {
                            subRecordTest(context, "RootVmInternalSubRecord") { sink ->
                                sink.writeRootVmInternalSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootJniMonitorSubRecord(context: SubRecordContext<SubRecord.RootJniMonitorSubRecord>) {
                            subRecordTest(context, "RootJniMonitorSubRecord") { sink ->
                                sink.writeRootJniMonitorSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitRootUnreachableSubRecord(context: SubRecordContext<SubRecord.RootUnreachableSubRecord>) {
                            subRecordTest(context, "RootUnreachableSubRecord") { sink ->
                                sink.writeRootUnreachableSubRecord(context.subRecord, context.header)
                            }
                        }

                        override fun visitHeapDumpInfoSubRecord(context: SubRecordContext<SubRecord.HeapDumpInfoSubRecord>) {

                        }

                        override fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {

                        }

                        override fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {

                        }

                        override fun visitPrimitiveArrayDumpSubRecord(context: SubRecordContext<SubRecord.PrimitiveArrayDumpSubRecord>) {

                        }

                        override fun visitObjectArrayDumpSubRecord(context: SubRecordContext<SubRecord.ObjectArrayDumpSubRecord>) {

                        }
                    }
                }

                override fun visitEnd() { }
            }
            reader.accept(visitor)
        }
    }
}