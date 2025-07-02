package com.tans.thprofparser

import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.LoadClassContext
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.StackFrameContext
import com.tans.thprofparser.records.StackTraceContext
import com.tans.thprofparser.records.StringContext
import com.tans.thprofparser.records.UnknownContext
import com.tans.thprofparser.records.UnloadClassContext

open class HprofVisitor(protected val nextVisitor: HprofVisitor? = null) {

    open fun visitHeader(header: HprofHeader) {
        nextVisitor?.visitHeader(header)
    }

    open fun visitStringRecord(context: StringContext) {
        nextVisitor?.visitStringRecord(context)
    }

    open fun visitLoadClassRecord(
        context: LoadClassContext
    ) {
        nextVisitor?.visitLoadClassRecord(context)
    }

    open fun visitUnloadClassRecord(
        context: UnloadClassContext
    ) {
        nextVisitor?.visitUnloadClassRecord(context)
    }

    open fun visitStackFrameRecord(
        context: StackFrameContext
    ) {
        nextVisitor?.visitStackFrameRecord(context)
    }

    open fun visitStackTraceRecord(
        context: StackTraceContext
    ) {
        nextVisitor?.visitStackTraceRecord(context)
    }

    open fun visitHeapDumpRecorder(
        recordContext: RecordContext
    ): HeapDumpRecordVisitor? {
        return nextVisitor?.visitHeapDumpRecorder(recordContext)
    }

    open fun visitUnknownRecord(
        context: UnknownContext
    ) {
        nextVisitor?.visitUnknownRecord(context)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}