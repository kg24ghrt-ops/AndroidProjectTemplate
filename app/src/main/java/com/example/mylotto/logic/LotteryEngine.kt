package com.example.mylotto.logic

object LotteryEngine {

    sealed class ExpansionResult {
        data class Success(
            val numbers: Set<String>,
            val printableList: String,
            val multiplier: Int
        ) : ExpansionResult()

        data class Invalid(val message: String) : ExpansionResult()
    }

    fun expand(input: String, code: String): ExpansionResult {
        val lowerCode = code.lowercase().trim()
        val hasReverseModifier = lowerCode.endsWith("r")
        val baseCode = if (hasReverseModifier && lowerCode.length > 1) lowerCode.dropLast(1) else lowerCode

        // Validation logic
        val error = validateInput(input, baseCode)
        if (error != null) return ExpansionResult.Invalid(error)

        val cleanDigits = input.filter { it.isDigit() }

        // Generator Phase
        var resultSet: Set<String> = when (baseCode) {
            "b" -> generateBrake(cleanDigits)
            "f" -> generateFront(cleanDigits)
            "g" -> generateBack(cleanDigits)
            "t" -> generateRunning(cleanDigits)
            "a" -> generateAllDoubles()
            "p" -> generatePower()
            "n" -> generateNatKhat()
            "z", "x" -> generateBrother()
            "c" -> generateParity(fEven = true, sEven = true)   // စုံစုံ
            "v" -> generateParity(fEven = false, sEven = false) // မမ
            "u" -> generateParity(fEven = false, sEven = true)  // မစုံ
            "y" -> generateParity(fEven = true, sEven = false)  // စုံမ
            "k" -> generateKhway(cleanDigits, includeDoubles = false)
            "e" -> generateKhway(cleanDigits, includeDoubles = true)
            else -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
        }

        if (resultSet.isEmpty()) return ExpansionResult.Invalid("No numbers generated. Check your input.")

        // Modifier Phase (Reverse)
        if (hasReverseModifier || lowerCode == "r") {
            resultSet = applyReverse(resultSet)
        }

        val sortedList = resultSet.toList().sorted()
        return ExpansionResult.Success(resultSet, sortedList.joinToString(", "), resultSet.size)
    }

    private fun validateInput(input: String, code: String): String? {
        return when (code) {
            "b", "f", "g", "t" -> if (input.length != 1) "Requires exactly 1 digit." else null
            "k", "e" -> if (input.toSet().size < 2) "Requires at least 2 unique digits." else null
            "a", "p", "n", "z", "x", "c", "v", "u", "y" -> null // Fixed sets, no input needed
            else -> if (input.length != 2 && !code.endsWith("r")) "Enter 2 digits for Direct." else null
        }
    }

    // --- Specialized Generators ---

    private fun generatePower() = setOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")

    private fun generateNatKhat() = setOf("18", "81", "24", "42", "39", "93", "07", "70", "50", "05")

    private fun generateBrother(): Set<String> {
        val res = mutableSetOf<String>()
        for (i in 0..9) {
            val next = (i + 1) % 10
            res.add("$i$next")
            res.add("$next$i")
        }
        return res
    }

    private fun generateBrake(input: String): Set<String> {
        val target = input.toIntOrNull() ?: return emptySet()
        return (0..99).map { it.toString().padStart(2, '0') }
            .filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }.toSet()
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

    private fun generateRunning(input: String) = (0..99).map { it.toString().padStart(2, '0') }
        .filter { it.contains(input) }.toSet()

    private fun generateFront(input: String) = (0..9).map { "$input$it" }.toSet()
    private fun generateBack(input: String) = (0..9).map { "$it$input" }.toSet()
    private fun generateAllDoubles() = (0..9).map { "$it$it" }.toSet()

    private fun generateParity(fEven: Boolean, sEven: Boolean) = (0..99)
        .map { it.toString().padStart(2, '0') }
        .filter { (it[0].digitToInt() % 2 == 0) == fEven && (it[1].digitToInt() % 2 == 0) == sEven }.toSet()

    private fun applyReverse(baseSet: Set<String>): Set<String> {
        val res = mutableSetOf<String>()
        baseSet.forEach {
            res.add(it)
            res.add(it.reversed())
        }
        return res
    }
}