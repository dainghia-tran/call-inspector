package vn.dainghia.callinspector.util

import java.util.Locale

object CountryCodeUtil {

    fun getSupportedLocales(): List<Locale> = Locale.getAvailableLocales()
        .asSequence()
        .filter { it.country.matches(Regex("[a-zA-Z]{2}")) }
        .distinctBy { it.country }
        .sortedBy { it.country }
        .toList()

    fun getFlagEmoji(countryCode: String): String {
        // Calculate the Unicode code points for the regional indicator symbols
        // 0x1F1E6 is the Unicode code point for the regional indicator symbol for 'A'
        // 0x41 is the ASCII code point for 'A'
        val firstLetterCodePoint = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetterCodePoint = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6

        // Convert the code points back to characters and combine them into a String
        return String(Character.toChars(firstLetterCodePoint)) + String(Character.toChars(secondLetterCodePoint))
    }
}