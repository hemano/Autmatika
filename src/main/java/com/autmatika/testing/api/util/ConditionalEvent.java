package com.autmatika.testing.api.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The class is used to store the conditional types that are used in SeleniumHelper.class
 * <p>
 *
 */
public class ConditionalEvent {

    private final Set<String> conditionalEvents = new HashSet<>(
            Arrays.asList(
                    "WaitForElementToAppear".toLowerCase(),
                    "WaitForJavaScriptToUpdate".toLowerCase(),
                    "WaitForElementToContainText".toLowerCase(),
                    "ToUpperCase".toLowerCase(),
                    "AcceptAlert".toLowerCase()
            )
    );

    public class Event {
        public String key;
        public String value;


        private Event(String key, String value) {
            this.key = key.toLowerCase();
            this.value = value;
        }
    }

    /**
     * @param key the passed key that is checked for matching one of the listed conditional event keys
     *
     * @return the conditional event
     */
    /**
     *
     * @param key the passed key that is checked for matching one of the listed conditional event keys
     * @param value the value tied with a key
     * @return event object
     */
    public Event getConditionalEvent(String key, String value) {
        if (conditionalEvents.contains(key.toLowerCase())) {
            return new Event(key, value);
        }
        return null;
    }
}
