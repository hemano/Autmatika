package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * This action will imitate keyboard click using metadata value as a parameter.
 */
public class PressKeyActor extends AbstractActor {
    @Override
    public void doIt() {
        super.preDo();
        reportAndLog("Pressing key " + this.value + " from keyboard ", "info", true);
        SeleniumHelper helper = SeleniumHelper.getInstance();
        helper.waitForPageLoad(helper.getIntLongTimeOut());
        helper.keyboardPress(this.value);
    }
}
