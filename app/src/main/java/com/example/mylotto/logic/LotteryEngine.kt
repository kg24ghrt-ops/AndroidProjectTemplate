package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    fun expand(input: String, code: String): ExpansionResult {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // BRAKE (b): Based on your classification table (Sum mod 10)
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }

            // 1. A-KHWAY (k): Round bets from digit set EXCLUDING doubles
            // Input "123" -> 12, 13, 21, 23, 31, 32
            "k" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                for (d1 in digits) {
                    for (d2 in digits) {
                        if (d1 != d2) res.add("$d1$d2") 
                    }
                }
                res.sorted()
            }

            // 2. A-KHWAY-PUU (e): Take the 'k' set and ADD double digits for those digits
            // Input "123" -> (12, 13, 21, 23, 31, 32) + (11, 22, 33)
            "e" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                
                for (d1 in digits) {
                    for (d2 in digits) {
                        // This generates EVERYTHING (both k-set and the doubles)
                        res.add("$d1$d2")
                    }
                }
                res.distinct().sorted()
            }

            // TWINS / အပူး (a): The fixed set of all 10 doubles
            "a" -> (0..9).map { "$it$it" }

            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}