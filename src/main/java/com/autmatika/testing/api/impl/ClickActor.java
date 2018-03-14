package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * <p> Click action: Supports clicking element by XPath or ID </p>
 *
 */
public class ClickActor extends AbstractActor {

	@Override
	public void doIt() {
        super.preDo();
        reportAndLog("Clicking on " + this.elementLocation + ": " + this.elementLocatorByPropertyName, "info", true);
        SeleniumHelper helper = SeleniumHelper.getInstance();
        helper.clickElement(this.elementLocatorByPropertyName, this.value);
    }
}
