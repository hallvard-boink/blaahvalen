package com.hallvardlaerum.skalTilHavaara;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMester {

    /**
     * Bruk groupInteger 0 hvis hele yttrykket skal brukes.
     * @param testString
     * @param patternString
     * @param groupInteger
     * @return
     */
    public static String hentUtMedRegEx(String testString, String patternString, Integer groupInteger) {
        Matcher matcher = Pattern.compile(patternString).matcher(testString);
        if (matcher.find()) {
            if (groupInteger==0) {
                return matcher.group();
            } else {
                return matcher.group(groupInteger);
            }
        } else {
            return null;
        }
    }

}
