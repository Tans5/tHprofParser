package com.tans.thprofparser

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor
import com.tans.thprofparser.recorders.RecorderContext
import com.tans.thprofparser.recorders.StackFrameRecorderVisitor
import com.tans.thprofparser.recorders.StackTraceRecorderVisitor
import com.tans.thprofparser.recorders.StringRecorderVisitor
import com.tans.thprofparser.recorders.UnknownRecorderVisitor
import com.tans.thprofparser.recorders.UnloadClassRecorderVisitor

open class HprofVisitor(protected val nextVisitor: HprofVisitor? = null) {

    open fun visit(header: HprofHeader) {
        nextVisitor?.visit(header)
    }

    open fun visitStringRecorder(recorderContext: RecorderContext): StringRecorderVisitor? {
        return nextVisitor?.visitStringRecorder(recorderContext)
    }

    open fun visitLoadClassRecorder(
        recorderContext: RecorderContext
    ) : LoadClassRecorderVisitor? {
        return nextVisitor?.visitLoadClassRecorder(recorderContext)
    }

    open fun visitUnloadClassRecorder(
        recorderContext: RecorderContext
    ) : UnloadClassRecorderVisitor? {
        return nextVisitor?.visitUnloadClassRecorder(recorderContext)
    }

    open fun visitStackFrameRecorder(
        recorderContext: RecorderContext
    ): StackFrameRecorderVisitor? {
        return nextVisitor?.visitStackFrameRecorder(recorderContext)
    }

    open fun visitStackTraceRecorder(
        recorderContext: RecorderContext
    ): StackTraceRecorderVisitor? {
        return nextVisitor?.visitStackTraceRecorder(recorderContext)
    }

    open fun visitUnknownRecorder(
        recorderContext: RecorderContext
    ): UnknownRecorderVisitor? {
        return nextVisitor?.visitUnknownRecorder(recorderContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}