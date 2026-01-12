package com.example.mylotto.logic

/**
 * Myanmar 2D Lottery Engine
 * Architecture: Input Validation -> Base Set Generation -> Modifier Transformation -> Canonicalization
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
     * Entry point for expansion logic.
     */
    fun expand(input: String, code: String): ExpansionResult {
        val lowerCode = code.lowercase().trim()
        
        // 1. Identify Modifiers (Suffixes)
        // Check if 'r' is present at the end of the code string
        val hasReverseModifier = lowerCode.endsWith("r")
        val baseGeneratorCode = if (hasReverseModifier && lowerCode.length > 1) {
            lowerCode.dropLast(1)
        } else {
            lowerCode
        }

        // 2. Strict Input Validation
        val validationError = validateInput(input, baseGeneratorCode)
        if (validationError != null) return ExpansionResult.Invalid(validationError)

        val cleanDigits = input.filter { it.isDigit() }

        // 3. Base Generation Phase
        var resultSet: Set<String> = when (baseGeneratorCode) {
            "b" -> generateBrake(cleanDigits)
            "f" -> generateFront(cleanDigits)
            "g" -> generateBack(cleanDigits)
            "t" -> generateRunning(cleanDigits)
            "a" -> generateAllDoubles()
            "p" -> generatePower()
            "n" -> generateNatKhat()
            "z", "x" -> generateBrother()
            "c" -> generateParity(firstEven = true, secondEven = true)   // Sone-Sone
            "v" -> generateParity(firstEven = false, secondEven = false) // Ma-Ma
            "u" -> generateParity(firstEven = false, secondEven = true)  // Ma-Sone
            "y" -> generateParity(firstEven = true, secondEven = false)  // Sone-Ma
            "k" -> generateKhway(cleanDigits, includeDoubles = false)
            "e" -> generateKhway(cleanDigits, includeDoubles = true)
            "d" -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
            // 'r' is handled as a modifier, but if used alone with 2 digits, treat as Direct + Reverse
            "r" -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
            else -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
        }

        if (resultSet.isEmpty()) {
            return ExpansionResult.Invalid("Please enter a valid digit for this category.")
        }

        // 4. Modifier Phase (Apply Reverse)
        // Apply if code is 'r' or ends with 'r' (like 'br', 'kr', 'dr')
        if (hasReverseModifier || lowerCode == "r") {
            resultSet = applyReverse(resultSet)
        }

        // 5. Canonicalization (Deduplication + Sorting)
        val sortedList = resultSet.toList().sorted()
        
        return ExpansionResult.Success(
            numbers = resultSet,
            printableList = sortedList.joinToString(", "),
            multiplier = resultSet.size
        )
    }

    private fun validateInput(input: String, code: String): String? {
        val digitsOnly = input.all { it.isDigit() }
        if (!digitsOnly && input.isNotEmpty()) return "Input must contain digits only."

        return when (code) {
            "d", "r" -> if (input.length != 2) "Requires exactly 2 digits." else null
            "b" -> if (input.length != 1) "Brake requires exactly 1 digit." else null
            "f", "g", "t" -> if (input.length != 1) "Requires exactly 1 digit (0-9)." else null
            "k", "e" -> {
                val distinctCount = input.toSet().size
                if (distinctCount < 2) "Requires at least 2 different digits." else null
            }
            // Fixed sets that require no input
            "a", "p", "n", "z", "x", "c", "v", "u", "y" -> null
            else -> if (input.isEmpty()) "Input field cannot be empty." else null
        }
    }

    // --- GENERATORS ---

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

    /**
     * Matches any 2D number containing the input digit in any position.
     */
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

    // --- MODIFIERS ---

    private fun applyReverse(baseSet: Set<String>): Set<String> {
        val res = mutableSetOf<String>()
        baseSet.forEach {
            res.add(it)
            res.add(it.reversed())
        }
        return res
    }
}