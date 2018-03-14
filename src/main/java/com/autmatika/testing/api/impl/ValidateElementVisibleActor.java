package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;
import com.autmatika.testing.api.util.TestParametersController;

/**
 * <p>
 * This action verifies if an element is visible
 * </p>
 *
 */
public class ValidateElementVisibleActor extends AbstractActor {

    String actualValue = "";

    @Override
    public void doIt() {

        this.value = TestParametersController.checkIfSpecialParameter(this.value);
        super.preDo();
        SeleniumHelper helper = SeleniumHelper.getInstance();
        helper.elementToBeVisible(this.elementLocatorByPropertyName);

        reportAndLog("The element: " + this.elementLocation + ": " + this.elementLocatorByPropertyName
                + " is visible on the page", "info", true);
    }
}