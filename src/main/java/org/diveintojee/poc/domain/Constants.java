/**
 *
 */
package org.diveintojee.poc.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author louis.gueye@gmail.com
 */
public class Constants {
    public static final String MESSAGES_BUNDLE_NAME = "messages";
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final List<String> SUPPORTED_LOCALES = Arrays.asList(Locale.FRENCH.getLanguage(),
            DEFAULT_LOCALE.getLanguage());
}
