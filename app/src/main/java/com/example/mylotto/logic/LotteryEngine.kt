package com.example.mylotto.logic

object LotteryEngine {

    private fun isEven(n: Int) = n % 2 == 0

    fun expandCode(input: String, code: String): List<String> {
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        return when (code.lowercase()) {
            "k" -> getAKhwayNumbers(input) // A-khway (Combination)
            "b" -> { 
                val target = input.toIntOrNull() ?: 0
                all2D.filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }
            }
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")
            "z", "x" -> listOf("01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67", "76", "78", "87", "89", "98", "90", "09")
            "a" -> (0..9).map { "$it$it" }
            "c" -> all2D.filter { isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) }
            "v" -> all2D.filter { !isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) }
            "u" -> all2D.filter { !isEven(it[0].digitToInt()) && isEven(it[1].digitToInt()) }
            "y" -> all2D.filter { isEven(it[0].digitToInt()) && !isEven(it[1].digitToInt()) }
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input)
            "f" -> (0..9).map { "$input$it" }
            "g" -> (0..9).map { "$it$input" }
            "t" -> all2D.filter { it.contains(input) }
            else -> listOf(input) 
        }
    }

    private fun getAKhwayNumbers(digits: String): List<String> {
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