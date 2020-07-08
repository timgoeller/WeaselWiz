class TypeCheckingDataRecorder() {
    private var currentSeq = 0
    private val recordList : ArrayList<Record> = ArrayList()

    fun record(ctx: Context, expr: Expr, type: Monotype, sequence: Int) {
        recordList.add(Record(ctx, expr, type, sequence))
    }

    fun getNextSequenceNumber(): Int {
        return currentSeq++
    }

    fun getRecords(): ArrayList<Record> {
        return recordList
    }

    fun applySolutionToRecords(solution : Solution) {
        for (record in recordList) {
            record.type = applySolution(solution, record.type)
        }
    }

    data class Record(val ctx : Context, val expr : Expr, var type : Monotype, val seq : Int)
}