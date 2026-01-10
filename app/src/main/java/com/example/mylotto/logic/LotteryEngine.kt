package com.example.mylotto.logic

object LotteryEngine {

    // 1. Brake System (b): Sum of digits % 10
    fun getBrakeNumbers(brakeValue: Int): List<String> {
        return (0..99).map { it.toString().padStart(2, '0') }
            .filter { 
                val sum = it[0].digitToInt() + it[1].digitToInt()
                sum % 10 == brakeValue 
            }
    }

    // 2. Power Numbers (p): (0,5), (1,6), (2,7), (3,8), (4,9)
    private val powerPairs = mapOf('0' to '5', '5' to '0', '1' to '6', '6' to '1', '2' to '7', '7' to '2', '3' to '8', '8' to '3', '4' to '9', '9' to '4')
    
    fun getPowerNumbers(): List<String> = listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")

    // 3. Nat Khat (n): (1,8), (2,4), (3,9), (5,0), (6,7)
    fun getNatKhatNumbers(): List<String> = listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")

    // 4. Operational Logic
    fun expandEntry(input: String, category: String): List<String> {
        return when (category) {
            "b" -> getBrakeNumbers(input.toIntOrNull() ?: 0)
            "r" -> { // Reverse
                if (input.length == 2) listOf(input, input.reversed()) else listOf(input)
            }
            "f" -> (0..9).map { "$input$it" } // Hteik (Front)
            "g" -> (0..9).map { "$it$input" } // Nauk (Back)
            "t" -> { // Pat-thee (Running)
                val set = mutableSetOf<String>()
                for (i in 0..9) {
                    set.add("$input$i")
                    set.add("$i$input")
                }
                set.toList()
            }
            "p" -> getPowerNumbers()
            "n" -> getNatKhatNumbers()
            "a" -> (0..9).map { "$it$it" } // Twins
            else -> listOf(input) // Default: store as is
        }
    }
}