package com.autmatika.testing.api.util;

/**
 * The class is used to set the conditional event type that needs to be processed when doable action is running.
 *
 */
public class ConditionalEventHandler {

    //private static final Logger log = Logger.getLogger(ConditionalEventHandler.class);

    /**
     * set the conditional event's value
     * @param conditionalEventParameter
     * @return configured conditional event
     */
    public static ConditionalEvent.Event getConditionalEvent(String conditionalEventParameter) {
        if (conditionalEventParameter.isEmpty()) {
            return null;
        }

        String[] conditionalEventKeyAndValue = conditionalEventParameter.replaceAll("\\s", "").split("\\|");

        ConditionalEvent.Event conditionalEvent;

        if (conditionalEventKeyAndValue.length > 1) {
            conditionalEvent = new ConditionalEvent().getConditionalEvent(conditionalEventKeyAndValue[0], conditionalEventKeyAndValue[1]);
        }
        else {
            conditionalEvent = new ConditionalEvent().getConditionalEvent(conditionalEventKeyAndValue[0], "");
        }

        if (conditionalEvent == null) {
            LogManager.getLogger().warn("Conditional event key '" + conditionalEventKeyAndValue[0] + "' is not found. " +
                    "Please, use a valid key that goes alone or stands before the '|' sign.");
            Reporter.warn("Conditional event key '" + conditionalEventKeyAndValue[0] + "' is not found." +
                    " Please, use a valid key that goes alone or stands before the '|' sign.");
            return null;
        }

        return conditionalEvent;
    }
}