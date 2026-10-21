package com.vk.zenspend.domain.util

/**
 * Safe, zero-dependency inline math evaluator for ZenSpend.
 * Parses and computes arithmetic expressions like "120 + 35 * 2" or "45.5 / 2 + 10"
 * in real-time as the user types.
 */
object MathEvaluator {

    fun evaluate(expression: String): Double? {
        val sanitized = expression.replace(" ", "").replace("×", "*").replace("÷", "/")
        if (sanitized.isEmpty()) return null

        return try {
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return null
            parseExpression(tokens)
        } catch (_: Exception) {
            null
        }
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c in "+-*/" -> {
                    // Check for unary minus/plus at start or after an operator
                    if ((c == '-' || c == '+') && (tokens.isEmpty() || tokens.last() in "+-*/")) {
                        var numberStr = c.toString()
                        i++
                        while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                            numberStr += expr[i]
                            i++
                        }
                        tokens.add(numberStr)
                        continue
                    } else {
                        tokens.add(c.toString())
                        i++
                    }
                }
                c.isDigit() || c == '.' -> {
                    var numberStr = ""
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        numberStr += expr[i]
                        i++
                    }
                    tokens.add(numberStr)
                }
                else -> i++
            }
        }
        return tokens
    }

    // Handles addition and subtraction (lower precedence)
    private fun parseExpression(tokens: List<String>): Double {
        var result = parseTerm(tokens, 0)
        var i = result.second

        var currentTotal = result.first
        while (i < tokens.size) {
            val op = tokens[i]
            if (op == "+" || op == "-") {
                val nextTerm = parseTerm(tokens, i + 1)
                if (op == "+") {
                    currentTotal += nextTerm.first
                } else {
                    currentTotal -= nextTerm.first
                }
                i = nextTerm.second
            } else {
                break
            }
        }
        return currentTotal
    }

    // Handles multiplication and division (higher precedence)
    private fun parseTerm(tokens: List<String>, startIndex: Int): Pair<Double, Int> {
        if (startIndex >= tokens.size) return Pair(0.0, startIndex)

        var currentTotal = tokens[startIndex].toDoubleOrNull() ?: 0.0
        var i = startIndex + 1

        while (i < tokens.size) {
            val op = tokens[i]
            if (op == "*" || op == "/") {
                if (i + 1 >= tokens.size) break
                val nextNum = tokens[i + 1].toDoubleOrNull() ?: 1.0
                if (op == "*") {
                    currentTotal *= nextNum
                } else if (nextNum != 0.0) {
                    currentTotal /= nextNum
                }
                i += 2
            } else {
                break
            }
        }
        return Pair(currentTotal, i)
    }
}
