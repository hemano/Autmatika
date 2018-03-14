package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;
import com.autmatika.testing.api.util.TestParametersController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * <p>
 * This action uses the Selenium getText method to grab data to an element by
 * Xpath, ID, CSS
 * </p>
 *
 */
public class ValidateTextActor extends AbstractActor {

    String actualValue = "";

    @Override
    public void doIt() {
        // will be used before standard preDo() if URL is stored in EC variable
        this.value = TestParametersController.checkIfSpecialParameter(this.value);
        if (this.value.startsWith("http")) {
            actualValue = SeleniumHelper.getInstance().getDriver().getCurrentUrl();
            reportAndLog("Validating URL to match : " + this.value, "info", true);
            assertThat(actualValue.toLowerCase(), containsString(this.value.toLowerCase()));
            reportAndLog("Actual URL '" + actualValue + "' matches the expected one.", "pass", true);
            return;
        }

        super.preDo();
        SeleniumHelper helper = SeleniumHelper.getInstance();
        actualValue = helper.getTextValueFromField(this.elementLocatorByPropertyName);
        reportAndLog("Validating data from : " + this.elementLocation + ": " + this.elementLocatorByPropertyName
                + " to match the expected value '" + this.value + "'", "info", true);

        String newValue = this.value;
        validateTextWithRegEx(actualValue, newValue);
    }
}