package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.ExecutionContextHandler;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * <p>Supports capturing data from an element identified by XPath, ID or CSS Selector </p>
 *
 */
public class CaptureDataActor extends AbstractActor {

    @Override
    public void doIt() {
        super.setElementLocatorByPropertyName();
        SeleniumHelper helper = SeleniumHelper.getInstance();
        reportAndLog("Capturing data from : " + this.elementLocation +": " + this.elementLocatorByPropertyName, "info", true);
        String textData = helper.getData(this.elementLocatorByPropertyName);

        /**
         * Add the textData value to the ExecutionContext list to be used further.
         * Get textData from ExecutionContext into html report
         */
        if (!this.value.equals("")) {
            if (textData.equals("")) {
                reportAndLog("Saving EC key " + this.value + " with an empty string. No application data found.", "warn", true);
            } else {
                reportAndLog("Saving EC key " + this.value + " = " + textData, "info", true);
            }
            ExecutionContextHandler.setExecutionContextValueByKey(this.value, textData);
        } else
            reportAndLog("Cannot save EC value with an empty key. Check your parameters.", "error", true);
    }
}
