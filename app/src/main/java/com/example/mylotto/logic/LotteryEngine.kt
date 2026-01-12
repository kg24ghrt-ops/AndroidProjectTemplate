package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    fun expand(input: String, code: String): ExpansionResult {
        // Pool of all numbers from 00 to 99
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // BRAKE (b): Sum of digits mod 10
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }

            // 1. A-KHWAY (k): Round bets EXCLUDING doubles
            // Input "12" -> 12, 21
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

            // 2. A-KHWAY-PUU (e): k-set PLUS the double digits for those specific digits
            // Input "12" -> 11, 12, 21, 22
            "e" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                for (d1 in digits) {
                    for (d2 in digits) {
                        res.add("$d1$d2")
                    }
                }
                res.distinct().sorted()
            }

            // --- FIXED SET GENERATORS (Generate 25 numbers each) ---

            // စုံစုံ (Sone-Sone): Both digits are Even (0, 2, 4, 6, 8)
            "c" -> all2D.filter { it[0].digitToInt() % 2 == 0 && it[1].digitToInt() % 2 == 0 }

            // မမ (Ma-Ma): Both digits are Odd (1, 3, 5, 7, 9)
            "v" -> all2D.filter { it[0].digitToInt() % 2 != 0 && it[1].digitToInt() % 2 != 0 }

            // မစုံ (Ma-Sone): First is Odd, Second is Even
            "u" -> all2D.filter { it[0].digitToInt() % 2 != 0 && it[1].digitToInt() % 2 == 0 }

            // စုံမ (Sone-Ma): First is Even, Second is Odd
            "y" -> all2D.filter { it[0].digitToInt() % 2 == 0 && it[1].digitToInt() % 2 != 0 }

            // TWINS / အပူး (a): All 10 double numbers (00, 11, ..., 99)
            "a" -> (0..9).map { "$it$it" }

            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}