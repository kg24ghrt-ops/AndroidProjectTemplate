package com.example.mylotto.logic

/**
 * Myanmar 2D Lottery Engine
 * Handles canonical generation, set modification, and strict input validation.
 */
object LotteryEngine {

    sealed class ExpansionResult {
        data class Success(
            val numbers: Set<String>,
            val printableList: String,
            val multiplier: Int
        ) : ExpansionResult()

        data class Invalid(val message: String) : ExpansionResult()
    }

    /**
     * Expands shorthand codes into 2D number sets.
     * Logic Flow: Validation -> Generation -> Modification -> Canonicalization
     */
    fun expand(input: String, code: String): ExpansionResult {
        val lowerCode = code.lowercase()
        
        // 1. Identify Modifiers (Suffixes like 'r')
        val isReverseModifier = lowerCode.endsWith("r")
        val baseCode = if (isReverseModifier && lowerCode.length > 1) {
            lowerCode.dropLast(1)
        } else {
            lowerCode
        }

        // 2. Strict Input Validation
        val validationError = validateInput(input, baseCode)
        if (validationError != null) return ExpansionResult.Invalid(validationError)

        val cleanDigits = input.filter { it.isDigit() }

        // 3. Generator Phase
        var resultSet: Set<String> = when (baseCode) {
            "b" -> generateBrake(cleanDigits)
            "f" -> generateFront(cleanDigits)
            "g" -> generateBack(cleanDigits)
            "t" -> generateRunning(cleanDigits)
            "a" -> generateAllDoubles()
            "p" -> generatePower()
            "n" -> generateNatKhat()
            "z", "x" -> generateBrother()
            "c" -> generateParity(firstEven = true, secondEven = true)
            "v" -> generateParity(firstEven = false, secondEven = false)
            "u" -> generateParity(firstEven = false, secondEven = true)
            "y" -> generateParity(firstEven = true, secondEven = false)
            "k" -> generateKhway(cleanDigits, includeDoubles = false)
            "e" -> generateKhway(cleanDigits, includeDoubles = true)
            "r" -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
            "d" -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
            else -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
        }

        // Final safety check for generators that failed to produce output
        if (resultSet.isEmpty()) {
            return ExpansionResult.Invalid("Calculation error: No numbers generated.")
        }

        // 4. Modifier Phase (Apply Reverse if applicable)
        if (isReverseModifier || lowerCode == "r") {
            resultSet = applyReverse(resultSet)
        }

        // 5. Canonicalization (Deduplication and Sorting)
        val sortedList = resultSet.toList().sorted()
        
        return ExpansionResult.Success(
            numbers = resultSet,
            printableList = sortedList.joinToString(", "),
            multiplier = resultSet.size
        )
    }

    private fun validateInput(input: String, code: String): String? {
        val digitsOnly = input.all { it.isDigit() }
        if (!digitsOnly) return "Input must contain digits only"

        return when (code) {
            "d" -> if (input.length != 2) "Direct requires exactly 2 digits" else null
            "b" -> if (input.length != 1) "Brake requires exactly 1 digit" else null
            "f", "g", "t" -> if (input.length != 1) "Requires exactly 1 digit" else null
            "k", "e" -> {
                val distinct = input.filter { it.isDigit() }.toSet()
                if (distinct.size < 2) "Requires at least 2 distinct digits" else null
            }
            "r" -> if (input.length != 2) "Reverse requires 2 digits" else null
            // Generators that do not require input
            "a", "p", "n", "z", "x", "c", "v", "u", "y" -> null 
            else -> if (input.length < 1) "Please enter a digit" else null
        }
    }

    // --- Dynamic Generators ---

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

    private fun generateBrother(): Set<String> {
        val res = mutableSetOf<String>()
        for (i in 0..9) {
            val next = (i + 1) % 10
            res.add("$i$next")
            res.add("$next$i")
        }
        return res
    }

    private fun generateParity(firstEven: Boolean, secondEven: Boolean) = (0..99)
        .map { it.toString().padStart(2, '0') }
        .filter { (it[0].digitToInt() % 2 == 0) == firstEven && (it[1].digitToInt() % 2 == 0) == secondEven }.toSet()

    private fun generatePower() = setOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")
    private fun generateNatKhat() = setOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")
    private fun generateAllDoubles() = (0..9).map { "$it$it" }.toSet()

    // --- Modifiers ---

    private fun applyReverse(baseSet: Set<String>): Set<String> {
        val res = mutableSetOf<String>()
        baseSet.forEach {
            res.add(it)
            res.add(it.reversed())
        }
        return res
    }
}