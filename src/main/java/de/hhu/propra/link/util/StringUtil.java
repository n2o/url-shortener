package de.hhu.propra.link.util;

import com.github.slugify.Slugify;

import java.util.Locale;

public class StringUtil {

    /**
     * Slugify uses the locale dependent toLowerCase() method.
     * We overwrite that with the default locale-independent one.
     */
    private static final Slugify SLUGIFY = new Slugify().withLowerCase(false);

    /**
     * 'Slugify' the given string - meaning converting it to a URL-friendly format
     *
     * @param string the string to be slugified
     * @return the slugified string
     */
    public static String slugify(String string) {
        return SLUGIFY.slugify(string).toLowerCase(Locale.ROOT);
    }

    /**
     * Strip everything after the last dot, if that dot is not first character.
     *
     * @param s the string
     * @return the stripped string
     */
    public static String stripAfterLastDot(String s) {
        int lastDot = s.lastIndexOf('.');
        if (lastDot > 0) {
            s = s.substring(0, lastDot);
        }
        return s;
    }

    /**
     * Strip the string of vowels and dots.
     *
     * @param s the string
     * @return the string without vowels and dots
     */
    public static String unvowelize(String s) {
        StringBuilder unvowelized = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!isLowerCaseEnglishVowel(c) && c != '.') {
                unvowelized.append(c);
            }
        }
        return unvowelized.toString();
    }

    /**
     * Check if character is a lower case vowel of the english language.
     *
     * @param letter The letter to check
     * @return true if letter is a lower case vowel of the english language, false otherwise
     */
    public static boolean isLowerCaseEnglishVowel(char letter) {
        switch (letter) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            default:
                return false;
        }
    }

    /**
     * Truncate a string to the given maximum length.
     *
     * @param s         string to be truncated
     * @param maxLength maximum length
     * @return truncated string
     */
    public static String truncate(String s, int maxLength) {
        return s.substring(0, Math.min(s.length(), maxLength));
    }
}
