A JVM memory dump HPROF file parser for JVM/Android.  
Inspired by [ASM](https://asm.ow2.io/)'s implementation.  

## Installation  

```Groovy
dependencies {
	 // ...
    implementation 'io.github.tans5:thprofparser:1.0.0'
    // reduce hprof file size
    implementation 'io.github.tans5:thprofparser-reducer:1.0.0'
    // ...
}
```

## Usage

### Read and Write Hprof File

```Kotlin

    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        val outputDir = File("./demo/output")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val outputHprofFile = File(outputDir, "output.hprof")
        if (!outputHprofFile.exists()) {
            outputHprofFile.createNewFile()
        }
        inputHprofFile.inputStream().use { inputStream ->
            outputHprofFile.outputStream().use { outputStream ->
                val reader = HprofReader(inputStream)
                val writer = HprofWriter(outputStream)
                val visitor = object : HprofVisitor(writer) {
                    override fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
                        super.visitStringRecord(context)
                        println("Read string record: ${context.record.string}")
                    }

                    override fun visitHeapDumpRecord(
                        tag: Int,
                        timestamp: Long,
                        header: HprofHeader
                    ): HeapDumpRecordVisitor? {
                        return object : HeapDumpRecordVisitor(tag, timestamp, header,  super.visitHeapDumpRecord(tag, timestamp, header)) {
                            override fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {
                                super.visitInstanceDumpSubRecord(context)
                                println("Read instance: ${context.subRecord.id}")
                            }
                        }
                    }
                }
                reader.accept(visitor)
            }
        }
    }
```


### Reduce Hprof File Size

```Kotlin
    @JvmStatic
    fun main(args: Array<String>) {
        val inputHprofFile = File("./demo/input/dump.hprof")
        val outputReducedFile = File("./demo/output/reduced.zip")
        val cost = measureTime {
            reduceHprofFile(
                inputFile = inputHprofFile,
                outputFile = outputReducedFile
            )
        }
        println("Reduce profile cost: $cost")
    }
```