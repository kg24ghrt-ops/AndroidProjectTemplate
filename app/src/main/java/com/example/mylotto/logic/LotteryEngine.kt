package com.example.mylotto.logic

/**
 * Advanced 2D Lottery Engine
 * Handles dynamic expansion of all Myanmar 2D subculture classifications.
 */
object LotteryEngine {

    /**
     * Data class to package the results for the Activity/Voucher.
     */
    data class ExpansionResult(
        val printableList: String, // Every single number combination
        val multiplier: Int         // Total count for money calculation
    )

    /**
     * The core expansion engine.
     * Takes user input and code, returns all matching 2D numbers.
     */
    fun expand(input: String, code: String): ExpansionResult {
        // Generate the universe of 2D (00-99)
        val all2D = (0..99).map { it.toString().padStart(2, '0') }
        
        val resultList: List<String> = when (code.lowercase()) {
            // 1. DYNAMIC COMBINATION: အခွေ (A-Khway)
            // Takes any string like "12345" and cross-multiplies every digit.
            "k" -> {
                val digits = input.filter { it.isDigit() }.map { it.toString() }.distinct()
                val combinations = mutableListOf<String>()
                for (d1 in digits) {
                    for (d2 in digits) {
                        combinations.add("$d1$d2")
                    }
                }
                combinations.sorted()
            }

            // 2. DYNAMIC BRAKE: ဘရိတ် (Sum of digits ends in input)
            // Works for any single digit 0-9.
            "b" -> {
                val target = input.toIntOrNull() ?: 0
                all2D.filter { num ->
                    val sum = num[0].digitToInt() + num[1].digitToInt()
                    sum % 10 == target
                }
            }

            // 3. REVERSE / AR: အာ
            // Takes "12" and gives "12, 21". Works for any 2-digit input.
            "r" -> if (input.length == 2) listOf(input, input.reversed()).distinct() else listOf(input)

            // 4. POWER: ပါဝါ (Fixed Astrological Set)
            "p" -> listOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")

            // 5. NAT KHAT: နက္ခတ် (Fixed Astrological Set)
            "n" -> listOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")

            // 6. BROTHER: ညီကို (Sequential Numbers)
            "z", "x" -> listOf("01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67", "76", "78", "87", "89", "98", "90", "09")

            // 7. TWINS: အပူး (Double numbers)
            "a" -> (0..9).map { "$it$it" }

            // 8. POSITIONALS: ထိပ် / ပိတ် / ပတ် (Front, Tail, Running)
            "f" -> (0..9).map { "$input$it" } // Starts with
            "g" -> (0..9).map { "$it$input" } // Ends with
            "t" -> all2D.filter { it.contains(input) } // Contains digit

            // 9. PARITY: စုံ / မ (Even-Odd status of tens and units)
            "c" -> all2D.filter { it[0].digitToInt() % 2 == 0 && it[1].digitToInt() % 2 == 0 } // Even-Even
            "v" -> all2D.filter { it[0].digitToInt() % 2 != 0 && it[1].digitToInt() % 2 != 0 } // Odd-Odd
            "u" -> all2D.filter { it[0].digitToInt() % 2 != 0 && it[1].digitToInt() % 2 == 0 } // Odd-Even
            "y" -> all2D.filter { it[0].digitToInt() % 2 == 0 && it[1].digitToInt() % 2 != 0 } // Even-Odd

            // 10. DIRECT: ဒွဲ / တိုက်ရိုက်
            else -> listOf(input)
        }

        return ExpansionResult(
            printableList = resultList.joinToString(", "),
            multiplier = resultList.size
        )
    }
}