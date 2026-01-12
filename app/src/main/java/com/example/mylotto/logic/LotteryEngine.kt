package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    fun expand(input: String, code: String): ExpansionResult {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // BRAKE (b): Sum of Digit A + Digit B mod 10
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }

            // 1. A-KHWAY (k) - NO DOUBLE DIGITS
            // Input "123" -> 12, 13, 21, 23, 31, 32 (Multiplier: 6)
            "k" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                for (d1 in digits) {
                    for (d2 in digits) {
                        if (d1 != d2) { // Logic fix: Skip doubles
                            res.add("$d1$d2")
                        }
                    }
                }
                res.sorted()
            }

            // 2. A-KHWAY-PUU (e) - ONLY DOUBLE DIGITS
            // Input "123" -> 11, 22, 33 (Multiplier: 3)
            "e" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                digits.map { "$it$it" }.sorted()
            }

            // TWINS / အပူး (a): The standard set of all 10 doubles
            "a" -> (0..9).map { "$it$it" }

            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}