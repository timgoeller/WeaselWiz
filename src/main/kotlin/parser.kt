import kotlin.math.exp

class Parser(val tokens: Lexer) {

    fun parseExpression() = parseOperatorExpression(0)

    private fun parseOperatorExpression(minBindingPower: Int): Expr {
        var leftHandSide = parseApplication()
        loop@ while (true) {
            when (val op = tokens.peek()) {
                is Token.OPERATOR -> {
                    val fn = functionForOperator(op)
                    val (leftBP, rightBP) = bindingPowerForOperator(op.operator)
                    if (leftBP < minBindingPower) break@loop
                    expectNext<Token.OPERATOR>("operator")
                    val rightHandSide = parseOperatorExpression(rightBP)
                    leftHandSide =  Expr.Application(Expr.Application(fn, leftHandSide), rightHandSide)
                }
                else -> break@loop
            }
        }
        return leftHandSide
    }

    private fun functionForOperator(operator: Token.OPERATOR): Expr {
        val expr: Expr = when (operator.operator) {
            "+" -> Expr.Var("add")
            "-" -> Expr.Var("subtract")
            "*" -> Expr.Var("multiply")
            "==" -> Expr.Var("equals")
            else -> throw Exception("Unknown operator $operator")
        }
        expr.span.start = operator.span.start
        expr.span.end = operator.span.end
        return expr
    }

    private fun bindingPowerForOperator(operator: String): Pair<Int, Int> = when (operator) {
        "==" -> 1 to 2
        "+", "-" -> 2 to 3
        "*" -> 3 to 4
        else -> throw Exception("Unknown operator $operator")
    }

    private fun parseApplication(): Expr {
        val atoms = mutableListOf<Expr>()
        while (true) {
            val atom = parseAtom() ?: break
            atoms += atom
        }

//        println(atoms)
        return when {
            atoms.isEmpty() -> throw Exception("Unexpected ${tokens.peek()} expected expression")
            else -> atoms.drop(1).fold(atoms[0]) { acc, expr ->
                println("ACC: " + acc + " EXPR: " + expr)
                val applicationExpression = Expr.Application(acc, expr)
                applicationExpression.span.start = acc.span.start
                applicationExpression.span.end = expr.span.end
//                println(applicationExpression.span)
                return applicationExpression
            }
        }
    }

    private fun parseAtom(): Expr? = when (tokens.peek()) {
        is Token.NUMBER -> number()
        is Token.BOOLEAN -> boolean()
        is Token.IDENT -> ident()
        is Token.LEFT_PAREN -> parenthesized()
        is Token.LAMBDA -> lambda()
        is Token.IF -> ifExpression()
        is Token.LET -> letExpression()
        is Token.LEFT_BRACKET -> listExpression()
        else -> null
    }

    private fun listExpression(): Expr {
        val values = mutableListOf<Expr>()
        val leftBracketToken = expectNext<Token.LEFT_BRACKET>("left bracket")
        if (tokens.peek() !is Token.RIGHT_BRACKET) {
            values += parseExpression()
            while (tokens.peek() is Token.COMMA) {
                expectNext<Token.COMMA>("comma")
                values += parseExpression()
            }
        }
        val rightBracketToken = expectNext<Token.RIGHT_BRACKET>("right bracket")
        val linkedListExpr = Expr.LinkedList(values)
        linkedListExpr.span.start = leftBracketToken.span.start
        linkedListExpr.span.end = rightBracketToken.span.end
        return linkedListExpr
    }

    private fun letExpression(): Expr {
        val letToken = expectNext<Token.LET>("let")
        var isRecursive = false
        if (tokens.peek() is Token.REC) {
            expectNext<Token.REC>("rec")
            isRecursive = true
        }
        val binder = expectNext<Token.IDENT>("binder").ident
        expectNext<Token.EQUALS>("equals")
        val expr = parseExpression()
        expectNext<Token.IN>("in")
        val body = parseExpression()
        val letExpr = Expr.Let(isRecursive, binder, expr, body)
        letExpr.span.start = letToken.span.start
        letExpr.span.end = body.span.end
        return letExpr
    }

    private fun ifExpression(): Expr.If {
        val ifToken = expectNext<Token.IF>("if")
        val condition = parseExpression()
        expectNext<Token.THEN>("then")
        val thenBranch = parseExpression()
        expectNext<Token.ELSE>("else")
        val elseBranch = parseExpression()
        val ifExpression = Expr.If(condition, thenBranch, elseBranch)
        ifExpression.span.start = ifToken.span.start
        ifExpression.span.end = elseBranch.span.end
        return ifExpression
    }

    private fun lambda(): Expr.Lambda {
        val lambdaToken = expectNext<Token.LAMBDA>("lambda")
        val binder = expectNext<Token.IDENT>("binder").ident
        expectNext<Token.RIGHT_ARROW>("right arrow")
        val body = parseExpression()
        val lambdaExpression = Expr.Lambda(binder, body)
        lambdaExpression.span.start = lambdaToken.span.start
        lambdaExpression.span.end = body.span.end
        return lambdaExpression
    }

    private fun parenthesized(): Expr {
        expectNext<Token.LEFT_PAREN>("opening paren")
        val expr = parseExpression()
        expectNext<Token.RIGHT_PAREN>("closing paren")
        return expr
    }

    private fun ident(): Expr.Var {
        val identToken = expectNext<Token.IDENT>("ident")
        val identExpr = Expr.Var(identToken.ident)
        identExpr.span.start = identExpr.span.start
        identExpr.span.end = identExpr.span.end
        return identExpr
    }

    private fun boolean(): Expr.Boolean {
        val booleanToken = expectNext<Token.BOOLEAN>("boolean")
        val booleanExpr = Expr.Boolean(booleanToken.boolean)
        booleanExpr.span.start = booleanExpr.span.start
        booleanExpr.span.end = booleanExpr.span.end
        return booleanExpr
    }

    private fun number(): Expr.Number {
        val numberToken = expectNext<Token.NUMBER>("number")
        val numberExpr = Expr.Number(numberToken.number)
        numberExpr.span.start = numberToken.span.start
        numberExpr.span.end = numberToken.span.end
        return numberExpr
    }

    private inline fun <reified T> expectNext(msg: String): T {
        val next = tokens.next()
        if (next is T) {
            return next
        } else {
            throw Exception("Expected $msg, but saw $next")
        }
    }
}

fun main() {
//    val input = """
//        let x =
//          let y = 10 in
//          y + 11 in
//        x + x
//    """.trimIndent()

//    val lexer = Lexer(input)
//    val parser = Parser(lexer)
//
//    val expr = parser.parseExpression()
//    println("Parsed\n=======")
//    println(expr)
//    println()
//    println("Evaled\n=======")
//    println(eval(initialEnv, expr))

    fun parseExpr(s: String) = Parser(Lexer(s)).parseExpression()

//    val z = parseExpr("""\f -> (\x -> f \v -> x x v) (\x -> f \v -> x x v)""")
//    val faculty = parseExpr(
//        """
//        \fac -> \x ->
//            if x == 0
//            then 1
//            else x * fac (x - 1)
//    """.trimIndent()
//    )

    val input = parseExpr("""
        if (\x1 -> equals 20 x1) 25 // Kommentar
        then true
        else add 3 (4 * 5)
    """.trimIndent())

    println(eval(initialEnv, input))
//    println(eval(initialEnv, Expr.Application(Expr.Application(z, faculty), Expr.Number(5))))
}