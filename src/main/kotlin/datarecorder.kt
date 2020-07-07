class TypeCheckingDataRecorder() {
    private var currentSeq = 0
    private val recordList : ArrayList<Record> = ArrayList()

    fun record(ctx: Context, expr: Expr, type: Typechecker.Monotype, sequence: Int) {
        recordList.add(Record(ctx, expr, type, sequence))
    }

    fun getNextSequenceNumber(): Int {
        return currentSeq++
    }

    fun getRecords(): ArrayList<Record> {
        return recordList
    }

    data class Record(val ctx : Context, val expr : Expr, val type : Typechecker.Monotype, val seq : Int)
}