package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext

open class HeapDumpRecordVisitor(
    protected val tag: Int,
    protected val timestamp: Long,
    protected val header: HprofHeader,
    protected val nextVisitor: HeapDumpRecordVisitor? = null
) {

    open fun visitRootUnknownSubRecord(context: SubRecordContext<SubRecord.RootUnknownSubRecord>) {
        nextVisitor?.visitRootUnknownSubRecord(context)
    }

    open fun visitRootJniGlobalSubRecord(context: SubRecordContext<SubRecord.RootJniGlobalSubRecord>) {
        nextVisitor?.visitRootJniGlobalSubRecord(context)
    }

    open fun visitRootJniLocalSubRecord(context: SubRecordContext<SubRecord.RootJniLocalSubRecord>) {
        nextVisitor?.visitRootJniLocalSubRecord(context)
    }

    open fun visitRootJavaFrameSubRecord(context: SubRecordContext<SubRecord.RootJavaFrameSubRecord>) {
        nextVisitor?.visitRootJavaFrameSubRecord(context)
    }

    open fun visitRootNativeStackSubRecord(context: SubRecordContext<SubRecord.RootNativeStackSubRecord>) {
        nextVisitor?.visitRootNativeStackSubRecord(context)
    }

    open fun visitRootStickyClassSubRecord(context: SubRecordContext<SubRecord.RootStickyClassSubRecord>) {
        nextVisitor?.visitRootStickyClassSubRecord(context)
    }

    open fun visitRootThreadBlockSubRecord(context: SubRecordContext<SubRecord.RootThreadBlockSubRecord>) {
        nextVisitor?.visitRootThreadBlockSubRecord(context)
    }

    open fun visitRootMonitorUsedSubRecord(context: SubRecordContext<SubRecord.RootMonitorUsedSubRecord>) {
        nextVisitor?.visitRootMonitorUsedSubRecord(context)
    }

    open fun visitRootThreadObjectSubRecord(context: SubRecordContext<SubRecord.RootThreadObjectSubRecord>) {
        nextVisitor?.visitRootThreadObjectSubRecord(context)
    }

    open fun visitRootInternedStringSubRecord(context: SubRecordContext<SubRecord.RootInternedStringSubRecord>) {
        nextVisitor?.visitRootInternedStringSubRecord(context)
    }

    open fun visitRootFinalizingSubRecord(context: SubRecordContext<SubRecord.RootFinalizingSubRecord>) {
        nextVisitor?.visitRootFinalizingSubRecord(context)
    }

    open fun visitRootDebuggerSubRecord(context: SubRecordContext<SubRecord.RootDebuggerSubRecord>) {
        nextVisitor?.visitRootDebuggerSubRecord(context)
    }

    open fun visitRootReferenceCleanupSubRecord(context: SubRecordContext<SubRecord.RootReferenceCleanupSubRecord>) {
        nextVisitor?.visitRootReferenceCleanupSubRecord(context)
    }

    open fun visitRootVmInternalSubRecord(context: SubRecordContext<SubRecord.RootVmInternalSubRecord>) {
        nextVisitor?.visitRootVmInternalSubRecord(context)
    }

    open fun visitRootJniMonitorSubRecord(context: SubRecordContext<SubRecord.RootJniMonitorSubRecord>) {
        nextVisitor?.visitRootJniMonitorSubRecord(context)
    }

    open fun visitRootUnreachableSubRecord(context: SubRecordContext<SubRecord.RootUnreachableSubRecord>) {
        nextVisitor?.visitRootUnreachableSubRecord(context)
    }

    open fun visitHeapDumpInfoSubRecord(context: SubRecordContext<SubRecord.HeapDumpInfoSubRecord>) {
        nextVisitor?.visitHeapDumpInfoSubRecord(context)
    }

    open fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {
        nextVisitor?.visitClassDumpSubRecord(context)
    }

    open fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {
        nextVisitor?.visitInstanceDumpSubRecord(context)
    }

    open fun visitPrimitiveArrayDumpSubRecord(context: SubRecordContext<SubRecord.PrimitiveArrayDumpSubRecord>) {
        nextVisitor?.visitPrimitiveArrayDumpSubRecord(context)
    }

    open fun visitObjectArrayDumpSubRecord(context: SubRecordContext<SubRecord.ObjectArrayDumpSubRecord>) {
        nextVisitor?.visitObjectArrayDumpSubRecord(context)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}