package com.example.mylotto.logic

object LotteryEngine {

    // Parity Definitions
    private fun isEven(n: Int) = n % 2 == 0

    // Full logic for letter codes from the research paper
    fun expandCode(input: String, code: String): List<String> {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        return when (code.lowercase()) {
            "b" -> { // Brake (Sum of digits)
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94") // Power
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76") // Nat Khat
            "z", "x" -> listOf("01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67", "76", "78", "87", "89", "98", "90", "09") // Brother (Nyi Ko)
            "a" -> (0..9).map { "$it$it" } // Twins (A-puu)
            "c" -> all2D.filter { isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) } // Sone-Sone (Even-Even)
            "v" -> all2D.filter { !isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) } // Ma-Ma (Odd-Odd)
            "u" -> all2D.filter { !isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) } // Ma-Sone (Odd-Even)
            "y" -> all2D.filter { isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) } // Sone-Ma (Even-Odd)
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input) // Reverse
            "f" -> (0..9).map { "$input$it" } // Front (Hteik)
            "g" -> (0..9).map { "$it$input" } // Back (Nauk)
            "t" -> all2D.filter { it.contains(input) } // Running (Pat-thee)
            else -> listOf(input) 
        }
    }
}