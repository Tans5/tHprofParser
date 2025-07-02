package com.tans.thprofparser.records

import com.tans.thprofparser.records.subrecorders.FrameRootContext
import com.tans.thprofparser.records.subrecorders.HeapDumpInfoContext
import com.tans.thprofparser.records.subrecorders.IdRootContext
import com.tans.thprofparser.records.subrecorders.RefRootContext
import com.tans.thprofparser.records.subrecorders.StackRootContext
import com.tans.thprofparser.records.subrecorders.ThreadRootContext

open class HeapDumpRecordVisitor(
    protected val recordContext: RecordContext,
    protected val nextVisitor: HeapDumpRecordVisitor? = null
) {

    open fun visitRootUnknownSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootUnknownSubRecord(context)
    }

    open fun visitRootJniGlobalSubRecord(context: RefRootContext) {
        nextVisitor?.visitRootJniGlobalSubRecord(context)
    }

    open fun visitRootJniLocalSubRecord(context: FrameRootContext) {
        nextVisitor?.visitRootJniLocalSubRecord(context)
    }

    open fun visitRootJavaFrameSubRecord(context: FrameRootContext) {
        nextVisitor?.visitRootJavaFrameSubRecord(context)
    }

    open fun visitRootNativeStackSubRecord(context: ThreadRootContext) {
        nextVisitor?.visitRootNativeStackSubRecord(context)
    }

    open fun visitRootStickyClassSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootStickyClassSubRecord(context)
    }

    open fun visitRootThreadBlockSubRecord(context: ThreadRootContext) {
        nextVisitor?.visitRootThreadBlockSubRecord(context)
    }

    open fun visitRootMonitorUsedSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootMonitorUsedSubRecord(context)
    }

    open fun visitRootThreadObjectSubRecord(context: FrameRootContext) {
        nextVisitor?.visitRootThreadObjectSubRecord(context)
    }

    open fun visitRootInternedStringSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootInternedStringSubRecord(context)
    }

    open fun visitRootFinalizingSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootFinalizingSubRecord(context)
    }

    open fun visitRootDebuggerSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootDebuggerSubRecord(context)
    }

    open fun visitRootReferenceCleanupSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootReferenceCleanupSubRecord(context)
    }

    open fun visitRootVmInternalSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootVmInternalSubRecord(context)
    }

    open fun visitRootJniMonitorSubRecord(context: StackRootContext) {
        nextVisitor?.visitRootJniMonitorSubRecord(context)
    }

    open fun visitRootUnreachableSubRecord(context: IdRootContext) {
        nextVisitor?.visitRootUnreachableSubRecord(context)
    }

    open fun visitHeapDumpInfoSubRecord(context: HeapDumpInfoContext) {
        nextVisitor?.visitHeapDumpInfoSubRecord(context)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}