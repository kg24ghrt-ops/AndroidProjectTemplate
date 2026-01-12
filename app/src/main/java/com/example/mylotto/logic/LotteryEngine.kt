package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    /**
     * Expands a shorthand input into a full list of 2D numbers based on Myanmar 2D rules.
     * * @param input The digits entered (e.g., "123" or "5")
     * @param code The category code (e.g., "k", "b", "e")
     */
    fun expand(input: String, code: String): ExpansionResult {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // 1. BRAKE (b) - Based on your Comprehensive Table
            // (Sum of Digit A + Digit B) mod 10
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { 
                    val sum = it[0].digitToInt() + it[1].digitToInt()
                    sum % 10 == target 
                }
            }

            // 2. A-KHWAY (k) - Full Combination Set
            // e.g., "12" -> 11, 12, 21, 22
            "k" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                if (digits.isEmpty()) listOf(input)
                else {
                    val res = mutableListOf<String>()
                    for (d1 in digits) for (d2 in digits) res.add("$d1$d2")
                    res.sorted()
                }
            }

            // 3. A-KHWAY-PUU (e) - Doubles within a digit set
            // e.g., "123" -> 11, 22, 33
            "e" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                digits.map { "$it$it" }.sorted()
            }

            // 4. POWER (p) - Standard pairs: 05, 16, 27, 38, 49
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")

            // 5. NATKHAT (n) - Standard pairs: 18, 24, 39, 50, 67
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")

            // 6. BROTHER (z) - Consecutive numbers (e.g., 01, 12, 89, 90)
            "z" -> listOf(
                "01", "12", "23", "34", "45", "56", "67", "78", "89", "90",
                "10", "21", "32", "43", "54", "65", "76", "87", "98", "09"
            )

            // 7. TWINS (a) - All double numbers
            "a" -> (0..9).map { "$it$it" }

            // 8. REVERSE (r) - R-Pats (e.g., "12" -> 12, 21)
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input)

            // 9. RUNNING / TEE (t) - All numbers containing the input digit
            "t" -> if (input.length == 1) all2D.filter { it.contains(input) } else listOf(input)

            // 10. DIRECT (d) - Single number entry
            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}