package utils

import java.text.Normalizer
import java.util.regex.Pattern

object TextModerator {

    // Simple banned list for a STEM Lab environment
    private val BANNED_WORDS = setOf(
        "crap", "idiot", "stupid", "fuck", "trash", "bitch", "damn", "hell", "suck", "ugly","heck","shit"
    )

    // Character mapping for common substitutions (leetspeak)
    private val TRANSFORMATION_MAP = mapOf(
        '0' to 'o', '1' to 'i', '!' to 'i', 'l' to 'i', '3' to 'e',
        '4' to 'a', '@' to 'a', '5' to 's', '$' to 's', '7' to 't',
        '+' to 't', '8' to 'b', '9' to 'g', '(' to 'c'
    )

    //check if string contain banned words
    fun hasProfanity(input: String): Boolean {
        if (input.isBlank()) return false
        val words = input.split(Regex("\\s+"))
        return words.any { rawWord ->
            BANNED_WORDS.contains(normalize(rawWord))
        }
    }

    fun sanitize(input: String): String {
        if (input.isBlank()) return input

        // We split by whitespace but keep track of punctuation
        val words = input.split(Regex("\\s+"))
        var sanitizedInput = input

        for (rawWord in words) {
            val normalized = normalize(rawWord)
            
            if (BANNED_WORDS.contains(normalized)) {
                val replacement = "*".repeat(rawWord.length)
                val pattern = Pattern.compile("\\b" + Pattern.quote(rawWord) + "\\b", Pattern.CASE_INSENSITIVE)
                sanitizedInput = pattern.matcher(sanitizedInput).replaceAll(replacement)
            }
        }

        return sanitizedInput
    }

    private fun normalize(word: String): String {
        var normalized = word.lowercase()

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
        normalized = normalized.replace(Regex("\\p{M}"), "")

        val builder = StringBuilder()
        for (char in normalized) {
            builder.append(TRANSFORMATION_MAP[char] ?: char)
        }
        normalized = builder.toString()


        return normalized.filter { it in 'a'..'z' }
    }
}
