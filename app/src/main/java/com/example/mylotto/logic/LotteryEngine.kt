package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val printableList: String,
        val multiplier: Int
    )

    fun expand(input: String, code: String): ExpansionResult {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            "k" -> { // Dynamic A-Khway (e.g. 12345)
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val res = mutableListOf<String>()
                for (d1 in digits) for (d2 in digits) res.add("$d1$d2")
                res.sorted()
            }
            "b" -> { // Brake
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")
            "a" -> (0..9).map { "$it$it" }
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input)
            "t" -> all2D.filter { it.contains(input) }
            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}