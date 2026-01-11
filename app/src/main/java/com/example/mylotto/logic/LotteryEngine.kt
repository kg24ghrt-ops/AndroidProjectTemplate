package com.example.mylotto.logic

object LotteryEngine {

    private fun isEven(n: Int) = n % 2 == 0

    fun expandCode(input: String, code: String): List<String> {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        return when (code.lowercase()) {
            // Operational & Positional
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input) // Ar (Reverse)
            "f" -> (0..9).map { "$input$it" } // Hteik-see (Front)
            "g" -> (0..9).map { "$it$input" } // Nauk-peik (Tail)
            "t" -> all2D.filter { it.contains(input) } // Pat-thee (Running)
            
            // Sum & Pattern Classifications
            "b" -> { // Brake (Sum of digits)
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }
            "a" -> (0..9).map { "$it$it" } // A-puu (Twins)
            
            // Astrological & Cultural
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94") // Power
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76") // Nat Khat
            
            // Sequence (Brother Numbers)
            "z", "x" -> listOf("01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67", "76", "78", "87", "89", "98", "90", "09") // Nyi-Ko
            
            // Parity (Even/Odd)
            "c" -> all2D.filter { isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) } // Sone-Sone (Even-Even)
            "v" -> all2D.filter { !isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) } // Ma-Ma (Odd-Odd)
            "u" -> all2D.filter { !isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) } // Ma-Sone (Odd-Even)
            "y" -> all2D.filter { isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) } // Sone-Ma (Even-Odd)
            
            // Combinations
            "k" -> getAKhway(input) // A-khway (Full combinations of input digits)
            "e" -> getAKhway(input).filter { it[0] == it[1] } // A-khway-puu (Twins from set)
            
            "d" -> listOf(input) // Direct (Du-ae)
            else -> listOf(input) 
        }
    }

    private fun getAKhway(digits: String): List<String> {
        val uniqueDigits = digits.toSet()
        val result = mutableListOf<String>()
        for (d1 in uniqueDigits) {
            for (d2 in uniqueDigits) {
                result.add("$d1$d2")
            }
        }
        return result.sorted()
    }
}