package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * <p> This action uses the Selenium sendKeys method to enter text to an element by Xpath or ID </p>
 *
 */
public class SetTextActor extends AbstractActor {

	@Override
	public void doIt() {

		super.preDo();
		reportAndLog("Setting \"" + this.value + "\" to " + this.elementLocation + ": " + this.elementLocatorByPropertyName, "info", true);
		SeleniumHelper helper = SeleniumHelper.getInstance();
		helper.enterField(this.elementLocatorByPropertyName, this.value);
	}
}
