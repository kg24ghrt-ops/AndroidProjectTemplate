package com.example.mylotto.logic

object LotteryEngine {

    data class ExpansionResult(
        val numbers: Set<String>,
        val printableList: String,
        val multiplier: Int
    )

    /**
     * The main entry point for expanding bets.
     */
    fun expand(input: String, code: String): ExpansionResult {
        // Step 1: Validation
        val cleanInput = input.filter { it.isDigit() }
        if (cleanInput.isEmpty() && !isFixedGenerator(code)) {
            return ExpansionResult(emptySet(), "", 0)
        }

        // Step 2: Generation (Sets created from 00-99)
        var resultSet: Set<String> = when (code.lowercase()) {
            "b" -> generateBrake(cleanInput)
            "f" -> generateFront(cleanInput)
            "g" -> generateBack(cleanInput)
            "t" -> generateRunning(cleanInput)
            "a" -> generateAllDoubles()
            "p" -> generatePower()
            "n" -> generateNatKhat()
            "z", "x" -> generateBrother()
            "c" -> generateParity(evenFirst = true, evenSecond = true)  // Sone-Sone
            "v" -> generateParity(evenFirst = false, evenSecond = false) // Ma-Ma
            "u" -> generateParity(evenFirst = false, evenSecond = true)  // Ma-Sone
            "y" -> generateParity(evenFirst = true, evenSecond = false)  // Sone-Ma
            "k" -> generateKhway(cleanInput, includeDoubles = false)
            "e" -> generateKhway(cleanInput, includeDoubles = true)
            else -> setOf(cleanInput.padStart(2, '0')).filter { it.length == 2 }.toSet()
        }

        // Step 3: Modifiers (Transformation phase)
        // Note: r (Reverse) can be triggered by code or specific UI flags
        if (code.lowercase() == "r") {
            resultSet = applyReverse(resultSet)
        }

        // Step 4: Canonicalization
        val sortedList = resultSet.toList().sorted()
        
        return ExpansionResult(
            numbers = resultSet,
            printableList = sortedList.joinToString(", "),
            multiplier = resultSet.size
        )
    }

    // --- GENERATORS ---

    private fun generateBrake(input: String): Set<String> {
        val target = input.firstOrNull()?.digitToInt() ?: return emptySet()
        return (0..99).map { it.toString().padStart(2, '0') }
            .filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }.toSet()
    }

    private fun generateFront(input: String): Set<String> {
        val digit = input.firstOrNull() ?: return emptySet()
        return (0..9).map { "$digit$it" }.toSet()
    }

    private fun generateBack(input: String): Set<String> {
        val digit = input.firstOrNull() ?: return emptySet()
        return (0..9).map { "$it$digit" }.toSet()
    }

    private fun generateRunning(input: String): Set<String> {
        val digit = input.firstOrNull() ?: return emptySet()
        return (0..99).map { it.toString().padStart(2, '0') }
            .filter { it.contains(digit) }.toSet()
    }

    private fun generateKhway(input: String, includeDoubles: Boolean): Set<String> {
        val digits = input.map { it.toString() }.distinct()
        val res = mutableSetOf<String>()
        for (d1 in digits) {
            for (d2 in digits) {
                if (includeDoubles || d1 != d2) res.add("$d1$d2")
            }
        }
        return res
    }

    private fun generatePower() = setOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")

    private fun generateNatKhat() = setOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")

    private fun generateBrother() = setOf(
        "01", "12", "23", "34", "45", "56", "67", "78", "89", "90",
        "10", "21", "32", "43", "54", "65", "76", "87", "98", "09"
    )

    private fun generateParity(evenFirst: Boolean, evenSecond: Boolean): Set<String> {
        return (0..99).map { it.toString().padStart(2, '0') }.filter { num ->
            val firstEven = num[0].digitToInt() % 2 == 0
            val secondEven = num[1].digitToInt() % 2 == 0
            (firstEven == evenFirst) && (secondEven == evenSecond)
        }.toSet()
    }

    private fun generateAllDoubles() = (0..9).map { "$it$it" }.toSet()

    // --- MODIFIERS ---

    private fun applyReverse(baseSet: Set<String>): Set<String> {
        val reversed = mutableSetOf<String>()
        baseSet.forEach {
            reversed.add(it)
            reversed.add(it.reversed())
        }
        return reversed
    }

    private fun isFixedGenerator(code: String) = code.lowercase() in listOf("a", "p", "n", "z", "x", "c", "v", "u", "y")
}