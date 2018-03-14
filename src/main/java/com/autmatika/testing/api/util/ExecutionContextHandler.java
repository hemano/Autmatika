package com.autmatika.testing.api.util;

import datageneration.execution.ExecutionContext;

import java.util.Map;

/**
 * <p>The class is used for handling the execution context variables during the tests running.</p>
 */

public class ExecutionContextHandler {

    //private static final Logger log = Logger.getLogger(ExecutionContextHandler.class);

    private static final String PREFIX = "EC_";

    private static final ThreadLocal<ExecutionContext> handler = new ThreadLocal<ExecutionContext>() {
        @Override
        protected ExecutionContext initialValue() {
            return new ExecutionContext(PREFIX);
        }
    };

    public static String getExecutionContextValueByKey(String key) {
        ExecutionContext executionContext  = handler.get();
        if (executionContext.getValues().containsKey(key)) {
            return executionContext.getValue(key);
        } else {
            LogManager.getLogger().warn("Requested " + key + " execution context key is absent.\n\t\tPossible reasons are:\n" +
                    "\t\t- some previous CaptureData action(s) failed;\n" +
                    "\t\t- the requested key is misspelled in the Test spreadsheet.");
            Reporter.warn("Requested " + key + " execution context key is absent.<pre>Possible reasons are:<br>" +
                    "- some previous CaptureData action(s) failed;<br>" +
                    "- the requested key is misspelled in the Test spreadsheet.");
            return key;
        }
    }

    public static void setExecutionContextValueByKey(String key, String value){
        handler.get().setValue(key, value);
    }

    public static void resetExecutionContextValues(){
        handler.get().reset();
    }

    public static Map<String, String> getAllValues(){
        return handler.get().getValues();
    }
}
