package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.ConstField
import com.tans.thprofparser.MemberField
import com.tans.thprofparser.StaticField
import com.tans.thprofparser.records.HeapDumpContext

data class ClassDumpContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val stackTraceSerialNumber: Long,
    val superClassId: Long,
    val classLoaderId: Long,
    val signerId: Long,
    val protectionDomainId: Long,
    val unknownId1: Long,
    val unknownId2: Long,
    val instanceSize: Long,
    val constFields: List<ConstField>,
    val staticFields: List<StaticField>,
    val memberFields: List<MemberField>
)