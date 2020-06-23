class TypeCheckingDataRecorder() {
    private var currentSeq = 0
    private val recordList : ArrayList<Record> = ArrayList()

    fun record(ctx: Context, expr: Expr) {
        recordList.add(Record(ctx, expr, currentSeq++))
    }

    data class Record(val ctx : Context, val expr : Expr, val seq : Int)
}