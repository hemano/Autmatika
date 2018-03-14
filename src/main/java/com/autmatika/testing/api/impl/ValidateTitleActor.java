package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;
import com.autmatika.testing.api.util.TestParametersController;

/**
 * <p>
 * This action uses the Selenium getText method to grab data to an element by
 * Xpath, ID, CSS
 * </p>
 *
 */
public class ValidateTitleActor extends AbstractActor {

    String actualValue = "";

    @Override
    public void doIt() {
        // will be used before standard preDo() if URL is stored in EC variable
        this.value = TestParametersController.checkIfSpecialParameter(this.value);
        super.preDo();
        SeleniumHelper helper = SeleniumHelper.getInstance();
        actualValue = helper.getTitle();
        reportAndLog("Validating title to match the expected value '" + this.value + "'", "info", true);

        String newValue = this.value;
        validateTextWithRegEx(actualValue, newValue);
    }


}