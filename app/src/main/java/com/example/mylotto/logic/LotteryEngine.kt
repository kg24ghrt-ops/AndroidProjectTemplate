package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    fun expand(input: String, code: String): ExpansionResult {
        // Pool of all 100 numbers for logic that requires filtering
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // BRAKE (b): Sum of Digit A + Digit B mod 10
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }

            // A-KHWAY (k) အခွေ: Full round bet from digit set
            "k" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                for (d1 in digits) {
                    for (d2 in digits) {
                        res.add("$d1$d2")
                    }
                }
                res.sorted()
            }

            // A-KHWAY-PUU (e) အခွေပူး: ONLY the "အပူး" (doubles) from the digit set
            "e" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                digits.map { "$it$it" }.sorted()
            }

            // TWINS အပူး (a): The fixed set of all 10 doubles
            "a" -> (0..9).map { "$it$it" }

            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}