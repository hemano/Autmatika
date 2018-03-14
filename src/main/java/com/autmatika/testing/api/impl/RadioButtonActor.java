package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 *  <p>Supports  finding and selecting radio-button element by CSS Selector, ID and XPath </p>
 */

public class RadioButtonActor extends AbstractActor {
	
	@Override
	public void doIt() {
		super.preDo();
		boolean selectOrNot = Boolean.parseBoolean(this.value.toLowerCase());
		reportAndLog("Setting the radio button field with value: " + this.value + ": " + this.elementLocatorByPropertyName, "info", true);
		SeleniumHelper helper = SeleniumHelper.getInstance();
		helper.selectRadioButton(this.elementLocatorByPropertyName, selectOrNot);
	}
}
