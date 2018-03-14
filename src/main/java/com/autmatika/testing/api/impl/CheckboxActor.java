package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 *
 * <p> Verify and Set the checkbox state action: Supports clicking element by XPath, CSS selector or ID </p>
 *
 */
public class CheckboxActor extends AbstractActor {

    @Override
    public void doIt() {
        super.preDo();
        boolean isChecked = Boolean.parseBoolean(this.value);
        reportAndLog("Setting the checkbox field with value: " + this.value + ": " + this.elementLocatorByPropertyName, "info", true);
        SeleniumHelper helper = SeleniumHelper.getInstance();
        helper.checkUncheckCheckbox(this.elementLocatorByPropertyName, isChecked);
    }
}
