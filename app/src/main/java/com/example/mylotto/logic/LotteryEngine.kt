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
        // 1. STRICT INPUT VALIDATION
        val validationError = validateInput(input, code)
        if (validationError != null) return ExpansionResult.Invalid(validationError)

        val cleanDigits = input.filter { it.isDigit() }

        // 2. GENERATOR PHASE
        // Identify if 'r' is present as a modifier suffix (e.g., "5br")
        val isReverseModifier = code.lowercase().endsWith("r")
        val baseCode = if (isReverseModifier && code.length > 1) {
            code.lowercase().dropLast(1)
        } else {
            code.lowercase()
        }

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
            else -> if (cleanDigits.length == 2) setOf(cleanDigits) else emptySet()
        }

        if (resultSet.isEmpty() && baseCode !in listOf("a", "p", "n", "z", "x", "c", "v", "u", "y")) {
            return ExpansionResult.Invalid("No numbers generated from input")
        }

        // 3. MODIFIER PHASE (Composability)
        if (isReverseModifier || code.lowercase() == "r") {
            resultSet = applyReverse(resultSet)
        }

        // 4. CANONICALIZATION
        val sortedList = resultSet.toList().sorted()
        return ExpansionResult.Success(
            numbers = resultSet,
            printableList = sortedList.joinToString(", "),
            multiplier = resultSet.size
        )
    }

    private fun validateInput(input: String, code: String): String? {
        val baseCode = code.lowercase().replace("r", "")
        val digitsOnly = input.all { it.isDigit() }

        if (input.isEmpty() && baseCode !in listOf("a", "p", "n", "z", "x", "c", "v", "u", "y")) {
            return "Input cannot be empty"
        }
        if (!digitsOnly) return "Input must contain digits only"
        
        return when (baseCode) {
            "b" -> if (input.length != 1) "Brake must be exactly 1 digit" else null
            "f", "g", "t" -> if (input.length != 1) "Generator requires 1 digit" else null
            "k", "e" -> if (input.length < 2) "Khway requires at least 2 digits" else null
            "" -> if (code.lowercase() == "r" && input.length != 2) "Direct reverse requires 2 digits" else null
            else -> null
        }
    }

    // --- DYNAMIC GENERATORS ---

    private fun generateBrake(input: String): Set<String> {
        val target = input.toInt()
        return (0..99).map { it.toString().padStart(2, '0') }
            .filter { (it[0].digitToInt() + it[1].digitToInt()) % 10 == target }.toSet()
    }

    private fun generateBrother(): Set<String> {
        val res = mutableSetOf<String>()
        for (i in 0..9) {
            val next = (i + 1) % 10
            res.add("$i$next")
            res.add("$next$i")
        }
        return res
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
    private fun generatePower() = setOf("05", "50", "16", "61", "27", "72", "38", "83", "49", "94")
    private fun generateNatKhat() = setOf("18", "81", "24", "42", "39", "93", "05", "50", "67", "76")
    private fun generateAllDoubles() = (0..9).map { "$it$it" }.toSet()
    private fun generateParity(firstEven: Boolean, secondEven: Boolean) = (0..99)
        .map { it.toString().padStart(2, '0') }
        .filter { (it[0].digitToInt() % 2 == 0) == firstEven && (it[1].digitToInt() % 2 == 0) == secondEven }.toSet()

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