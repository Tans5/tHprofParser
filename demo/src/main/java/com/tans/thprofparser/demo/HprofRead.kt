package com.tans.thprofparser.demo

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofReader
import com.tans.thprofparser.HprofVisitor
import com.tans.thprofparser.REFERENCE_HPROF_TYPE
import com.tans.thprofparser.ValueHolder
import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext
import java.io.File

object HprofRead {
    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        inputHprofFile.inputStream().use { inputStream ->

            val reader = HprofReader(inputStream)

            var allSize = 0L

            var stringRecordSize = 0L
            var stringRecordCount = 0

            var loadClassRecordSize = 0L
            var loadClassRecordCount = 0

            var unloadClassRecordSize = 0L
            var unloadClassRecordCount = 0

            var stackFrameRecordSize = 0L
            var stackFrameRecordCount = 0

            var stackTraceRecordSize = 0L
            var stackTraceRecordCount = 0

            var unknownRecordSize = 0L
            var unknownRecordCount = 0

            var rootUnknownSubRecordSize = 0L
            var rootUnknownSubRecordCount = 0

            var rootJniGlobalSubRecordSize = 0L
            var rootJniGlobalSubRecordCount = 0

            var rootJniLocalSubRecordSize = 0L
            var rootJniLocalSubRecordCount = 0

            var rootJavaFrameSubRecordSize = 0L
            var rootJavaFrameSubRecordCount = 0

            var rootNativeStackSubRecordSize = 0L
            var rootNativeStackSubRecordCount = 0

            var rootStickyClassSubRecordSize = 0L
            var rootStickyClassSubRecordCount = 0

            var rootThreadBlockSubRecordSize = 0L
            var rootThreadBlockSubRecordCount = 0

            var rootMonitorUsedSubRecordSize = 0L
            var rootMonitorUsedSubRecordCount = 0

            var rootThreadObjectSubRecordSize = 0L
            var rootThreadObjectSubRecordCount = 0

            var rootInternedStringSubRecordSize = 0L
            var rootInternedStringSubRecordCount = 0

            var rootFinalizingSubRecordSize = 0L
            var rootFinalizingSubRecordCount = 0

            var rootDebuggerSubRecordSize = 0L
            var rootDebuggerSubRecordCount = 0

            var rootReferenceCleanupSubRecordSize = 0L
            var rootReferenceCleanupSubRecordCount = 0

            var rootVmInternalSubRecordSize = 0L
            var rootVmInternalSubRecordCount = 0

            var rootJniMonitorSubRecordSize = 0L
            var rootJniMonitorSubRecordCount = 0

            var rootUnreachableSubRecordSize = 0L
            var rootUnreachableSubRecordCount = 0

            var heapDumpInfoSubRecordSize = 0L
            var heapDumpInfoSubRecordCount = 0

            var classDumpSubRecordSize = 0L
            var classDumpSubRecordCount = 0

            var instanceDumpSubRecordSize = 0L
            var instanceDumpSubRecordCount = 0

            var primitiveArrayDumpSubRecordSize = 0L
            var primitiveArrayDumpSubRecordCount = 0

            var objectArrayDumpSubRecordSize = 0L
            var objectArrayDumpSubRecordCount = 0

            var constFieldSize = 0L
            var constFieldCount = 0

            var staticRefFieldSize = 0L
            var staticRefFieldCount = 0

            var staticPrimitiveFieldSize = 0L
            var staticPrimitiveFieldCount = 0

            var memberRefFieldSize = 0L
            var memberRefFieldCount = 0

            var memberPrimitiveFieldSize = 0L
            var memberPrimitiveFieldCount = 0

            val visitor = object : HprofVisitor() {

                override fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
                    allSize += context.record.bodyLen
                    stringRecordSize += context.record.bodyLen
                    stringRecordCount ++
                }

                override fun visitLoadClassRecord(context: RecordContext<Record.LoadClassRecord>) {
                    allSize += context.record.bodyLen
                    loadClassRecordSize += context.record.bodyLen
                    loadClassRecordCount ++
                }

                override fun visitUnloadClassRecord(context: RecordContext<Record.UnloadClassRecord>) {
                    allSize += context.record.bodyLen
                    unloadClassRecordSize += context.record.bodyLen
                    unloadClassRecordCount ++
                }

                override fun visitStackFrameRecord(context: RecordContext<Record.StackFrameRecord>) {
                    allSize += context.record.bodyLen
                    stackFrameRecordSize += context.record.bodyLen
                    stackFrameRecordCount ++
                }

                override fun visitStackTraceRecord(context: RecordContext<Record.StackTraceRecord>) {
                    allSize += context.record.bodyLen
                    stackTraceRecordSize += context.record.bodyLen
                    stackTraceRecordCount ++
                }

                override fun visitUnknownRecord(context: RecordContext<Record.UnknownRecord>) {
                    allSize += context.record.bodyLen
                    unknownRecordSize += context.record.bodyLen
                    unknownRecordCount ++
                }

                override fun visitHeapDumpRecord(
                    tag: Int,
                    timestamp: Long,
                    header: HprofHeader
                ): HeapDumpRecordVisitor? {
                    return object : HeapDumpRecordVisitor(tag, timestamp, header) {

                        override fun visitRootUnknownSubRecord(context: SubRecordContext<SubRecord.RootUnknownSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootUnknownSubRecordSize += size
                            rootUnknownSubRecordCount ++
                        }

                        override fun visitRootJniGlobalSubRecord(context: SubRecordContext<SubRecord.RootJniGlobalSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootJniGlobalSubRecordSize += size
                            rootJniGlobalSubRecordCount ++
                        }

                        override fun visitRootJniLocalSubRecord(context: SubRecordContext<SubRecord.RootJniLocalSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootJniLocalSubRecordSize += size
                            rootJniLocalSubRecordCount ++
                        }

                        override fun visitRootJavaFrameSubRecord(context: SubRecordContext<SubRecord.RootJavaFrameSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootJavaFrameSubRecordSize += size
                            rootJavaFrameSubRecordCount ++
                        }

                        override fun visitRootNativeStackSubRecord(context: SubRecordContext<SubRecord.RootNativeStackSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootNativeStackSubRecordSize += size
                            rootNativeStackSubRecordCount ++
                        }

                        override fun visitRootStickyClassSubRecord(context: SubRecordContext<SubRecord.RootStickyClassSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootStickyClassSubRecordSize += size
                            rootStickyClassSubRecordCount ++
                        }

                        override fun visitRootThreadBlockSubRecord(context: SubRecordContext<SubRecord.RootThreadBlockSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootThreadBlockSubRecordSize += size
                            rootThreadBlockSubRecordCount ++
                        }

                        override fun visitRootMonitorUsedSubRecord(context: SubRecordContext<SubRecord.RootMonitorUsedSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootMonitorUsedSubRecordSize += size
                            rootMonitorUsedSubRecordCount ++
                        }

                        override fun visitRootThreadObjectSubRecord(context: SubRecordContext<SubRecord.RootThreadObjectSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootThreadObjectSubRecordSize += size
                            rootThreadObjectSubRecordCount ++
                        }

                        override fun visitRootInternedStringSubRecord(context: SubRecordContext<SubRecord.RootInternedStringSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootInternedStringSubRecordSize += size
                            rootInternedStringSubRecordCount ++
                        }

                        override fun visitRootFinalizingSubRecord(context: SubRecordContext<SubRecord.RootFinalizingSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootFinalizingSubRecordSize += size
                            rootFinalizingSubRecordCount ++
                        }

                        override fun visitRootDebuggerSubRecord(context: SubRecordContext<SubRecord.RootDebuggerSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootDebuggerSubRecordSize += size
                            rootDebuggerSubRecordCount ++
                        }

                        override fun visitRootReferenceCleanupSubRecord(context: SubRecordContext<SubRecord.RootReferenceCleanupSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootReferenceCleanupSubRecordSize += size
                            rootReferenceCleanupSubRecordCount ++
                        }

                        override fun visitRootVmInternalSubRecord(context: SubRecordContext<SubRecord.RootVmInternalSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootVmInternalSubRecordSize += size
                            rootVmInternalSubRecordCount ++
                        }

                        override fun visitRootJniMonitorSubRecord(context: SubRecordContext<SubRecord.RootJniMonitorSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootJniMonitorSubRecordSize += size
                            rootJniMonitorSubRecordCount ++
                        }

                        override fun visitRootUnreachableSubRecord(context: SubRecordContext<SubRecord.RootUnreachableSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            rootUnreachableSubRecordSize += size
                            rootUnreachableSubRecordCount ++
                        }

                        override fun visitHeapDumpInfoSubRecord(context: SubRecordContext<SubRecord.HeapDumpInfoSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            heapDumpInfoSubRecordSize += size
                            heapDumpInfoSubRecordCount ++
                        }

                        override fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            classDumpSubRecordSize += size
                            classDumpSubRecordCount ++
                            val record = context.subRecord
                            for (constField in record.constFields) {
                                constFieldSize += constField.value.size + 4 + 1
                                constFieldCount ++
                            }
                            for (staticField in record.staticFields) {
                                val size  = staticField.value.size + header.identifierByteSize + 1
                                if (staticField.value is ValueHolder.ReferenceHolder) {
                                    staticRefFieldSize += size
                                    staticRefFieldCount ++
                                } else {
                                    staticPrimitiveFieldSize += size
                                    staticPrimitiveFieldCount ++
                                }
                            }
                            for (memberField in record.memberFields) {
                                val size = header.identifierByteSize + 1
                                if (memberField.type == REFERENCE_HPROF_TYPE) {
                                    memberRefFieldSize += size
                                    memberRefFieldCount ++
                                } else {
                                    memberPrimitiveFieldSize += size
                                    memberPrimitiveFieldCount ++
                                }
                            }
                        }

                        override fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            instanceDumpSubRecordSize += size
                            instanceDumpSubRecordCount ++
                        }

                        override fun visitPrimitiveArrayDumpSubRecord(context: SubRecordContext<SubRecord.PrimitiveArrayDumpSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            primitiveArrayDumpSubRecordSize += size
                            primitiveArrayDumpSubRecordCount ++
                        }

                        override fun visitObjectArrayDumpSubRecord(context: SubRecordContext<SubRecord.ObjectArrayDumpSubRecord>) {
                            val size = context.subRecord.calculateBodyLen(context.header)
                            allSize += size
                            objectArrayDumpSubRecordSize += size
                            objectArrayDumpSubRecordCount ++
                        }
                    }
                }
            }
            reader.accept(visitor)

            println("AllSize: ${allSize.toSizeString()}")
            println("Records:")
            println("StringRecord: Size=${stringRecordSize.toSizeString()}, Count=$stringRecordCount, SizeInPrecents=${(stringRecordSize.toDouble() / allSize).toPercentsString()}")
            println("LoadClassRecord: Size=${loadClassRecordSize.toSizeString()}, Count=$loadClassRecordCount, SizeInPrecents=${(loadClassRecordSize.toDouble() / allSize).toPercentsString()}")
            println("UnloadClassRecord: Size=${unloadClassRecordSize.toSizeString()}, Count=$unloadClassRecordCount, SizeInPrecents=${(unloadClassRecordSize.toDouble() / allSize).toPercentsString()}")
            println("StackFrameRecord: Size=${stackFrameRecordSize.toSizeString()}, Count=$stackFrameRecordCount, SizeInPrecents=${(stackFrameRecordSize.toDouble() / allSize).toPercentsString()}")
            println("StackTraceRecord: Size=${stackTraceRecordSize.toSizeString()}, Count=$stackTraceRecordCount, SizeInPrecents=${(stackTraceRecordSize.toDouble() / allSize).toPercentsString()}")
            println("UnknownRecord: Size=${unknownRecordSize.toSizeString()}, Count=$unknownRecordCount, SizeInPrecents=${(unknownRecordSize.toDouble() / allSize).toPercentsString()}")
            println("SubRecords: ")
            println("RootUnknownSubRecord: Size=${rootUnknownSubRecordSize.toSizeString()}, Count=$rootUnknownSubRecordCount, SizeInPrecents=${(rootUnknownSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootJniGlobalSubRecord: Size=${rootJniGlobalSubRecordSize.toSizeString()}, Count=$rootJniGlobalSubRecordCount, SizeInPrecents=${(rootJniGlobalSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootJniLocalSubRecord: Size=${rootJniLocalSubRecordSize.toSizeString()}, Count=$rootJniLocalSubRecordCount, SizeInPrecents=${(rootJniLocalSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootJavaFrameSubRecord: Size=${rootJavaFrameSubRecordSize.toSizeString()}, Count=$rootJavaFrameSubRecordCount, SizeInPrecents=${(rootJavaFrameSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootNativeStackSubRecord: Size=${rootNativeStackSubRecordSize.toSizeString()}, Count=$rootNativeStackSubRecordCount, SizeInPrecents=${(rootNativeStackSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootStickyClassSubRecord: Size=${rootStickyClassSubRecordSize.toSizeString()}, Count=$rootStickyClassSubRecordCount, SizeInPrecents=${(rootStickyClassSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootThreadBlockSubRecord: Size=${rootThreadBlockSubRecordSize.toSizeString()}, Count=$rootThreadBlockSubRecordCount, SizeInPrecents=${(rootThreadBlockSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootMonitorUsedSubRecord: Size=${rootMonitorUsedSubRecordSize.toSizeString()}, Count=$rootMonitorUsedSubRecordCount, SizeInPrecents=${(rootMonitorUsedSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootThreadObjectSubRecord: Size=${rootThreadObjectSubRecordSize.toSizeString()}, Count=$rootThreadObjectSubRecordCount, SizeInPrecents=${(rootThreadObjectSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootInternedStringSubRecord: Size=${rootInternedStringSubRecordSize.toSizeString()}, Count=$rootInternedStringSubRecordCount, SizeInPrecents=${(rootUnknownSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootFinalizingSubRecord: Size=${rootFinalizingSubRecordSize.toSizeString()}, Count=$rootFinalizingSubRecordCount, SizeInPrecents=${(rootFinalizingSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootDebuggerSubRecord: Size=${rootDebuggerSubRecordSize.toSizeString()}, Count=$rootDebuggerSubRecordCount, SizeInPrecents=${(rootDebuggerSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootReferenceCleanupSubRecord: Size=${rootReferenceCleanupSubRecordSize.toSizeString()}, Count=$rootReferenceCleanupSubRecordCount, SizeInPrecents=${(rootReferenceCleanupSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootVmInternalSubRecord: Size=${rootVmInternalSubRecordSize.toSizeString()}, Count=$rootVmInternalSubRecordCount, SizeInPrecents=${(rootVmInternalSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootJniMonitorSubRecord: Size=${rootJniMonitorSubRecordSize.toSizeString()}, Count=$rootJniMonitorSubRecordCount, SizeInPrecents=${(rootJniMonitorSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("RootUnreachableSubRecord: Size=${rootUnreachableSubRecordSize.toSizeString()}, Count=$rootUnreachableSubRecordCount, SizeInPrecents=${(rootUnreachableSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("HeapDumpInfoSubRecord: Size=${heapDumpInfoSubRecordSize.toSizeString()}, Count=$heapDumpInfoSubRecordCount, SizeInPrecents=${(heapDumpInfoSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("ClassDumpSubRecord: Size=${classDumpSubRecordSize.toSizeString()}, Count=$classDumpSubRecordCount, SizeInPrecents=${(classDumpSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("InstanceDumpSubRecord: Size=${instanceDumpSubRecordSize.toSizeString()}, Count=$instanceDumpSubRecordCount, SizeInPrecents=${(instanceDumpSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("PrimitiveArrayDumpSubRecord: Size=${primitiveArrayDumpSubRecordSize.toSizeString()}, Count=$primitiveArrayDumpSubRecordCount, SizeInPrecents=${(primitiveArrayDumpSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("ObjectArrayDumpSubRecord: Size=${objectArrayDumpSubRecordSize.toSizeString()}, Count=$objectArrayDumpSubRecordCount, SizeInPrecents=${(objectArrayDumpSubRecordSize.toDouble() / allSize).toPercentsString()}")
            println("ClassDumpFields: ")
            println("ConstField: Size=${constFieldSize.toSizeString()}, Count=$constFieldCount, SizeInPrecents=${(constFieldSize.toDouble() / allSize).toPercentsString()}")
            println("StaticRefField: Size=${staticRefFieldSize.toSizeString()}, Count=$staticRefFieldCount, SizeInPrecents=${(staticRefFieldSize.toDouble() / allSize).toPercentsString()}")
            println("StaticPrimitiveField: Size=${staticPrimitiveFieldSize.toSizeString()}, Count=$staticPrimitiveFieldCount, SizeInPrecents=${(staticPrimitiveFieldSize.toDouble() / allSize).toPercentsString()}")
            println("MemberRefField: Size=${memberRefFieldSize.toSizeString()}, Count=$memberRefFieldCount, SizeInPrecents=${(memberRefFieldSize.toDouble() / allSize).toPercentsString()}")
            println("MemberPrimitiveField: Size=${memberPrimitiveFieldSize.toSizeString()}, Count=$memberPrimitiveFieldCount, SizeInPrecents=${(memberPrimitiveFieldSize.toDouble() / allSize).toPercentsString()}")
        }
    }
}