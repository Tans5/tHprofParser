package com.tans.thprofparser

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor
import com.tans.thprofparser.recorders.StackFrameRecorderVisitor
import com.tans.thprofparser.recorders.StackTraceRecorderVisitor
import com.tans.thprofparser.recorders.StringRecorderVisitor
import com.tans.thprofparser.recorders.UnknownRecorderVisitor
import com.tans.thprofparser.recorders.UnloadClassRecorderVisitor

open class HprofVisitor(protected val nextVisitor: HprofVisitor? = null) {

    open fun visit(header: HprofHeader) {
        nextVisitor?.visit(header)
    }

    open fun visitStringRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader): StringRecorderVisitor? {
        return nextVisitor?.visitStringRecorder(tag, timeStamp, header)
    }

    open fun visitLoadClassRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ) : LoadClassRecorderVisitor? {
        return nextVisitor?.visitLoadClassRecorder(tag, timeStamp, header)
    }

    open fun visitUnloadClassRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ) : UnloadClassRecorderVisitor? {
        return nextVisitor?.visitUnloadClassRecorder(tag, timeStamp, header)
    }

    open fun visitStackFrameRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): StackFrameRecorderVisitor? {
        return nextVisitor?.visitStackFrameRecorder(tag, timeStamp, header)
    }

    open fun visitStackTraceRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): StackTraceRecorderVisitor? {
        return nextVisitor?.visitStackTraceRecorder(tag, timeStamp, header)
    }

    open fun visitUnknownRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): UnknownRecorderVisitor? {
        return nextVisitor?.visitUnknownRecorder(tag, timeStamp, header)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}