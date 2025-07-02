package com.tans.thprofparser.records

enum class RecordType(val tag: Int) {
    STRING_IN_UTF8(0x01),
    LOAD_CLASS(0x02),
    UNLOAD_CLASS(0x03),
    STACK_FRAME(0x04),
    STACK_TRACE(0x05),
    HEAP_DUMP(0x0c),
    HEAP_DUMP_SEGMENT(0x1c),
    HEAP_DUMP_END(0x2c),
}