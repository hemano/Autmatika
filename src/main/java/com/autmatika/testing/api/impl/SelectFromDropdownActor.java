package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * <p> Click action: Supports clicking element by XPath or ID </p>
 *
 */
public class SelectFromDropdownActor extends AbstractActor {

	@Override
	public void doIt() {
        super.preDo();
        SeleniumHelper helper = SeleniumHelper.getInstance();
        if (this.value.equals("KW_AUTO_SELECT")) {
            reportAndLog("Starting random selection from dropdown.", "info", true);
            String autoSelectedValue = helper.autoSelectFromDropdown(this.elementLocatorByPropertyName);
            reportAndLog("Selected \"" + autoSelectedValue + "\" value from " + this.elementLocation + ": " + this.elementLocatorByPropertyName, "info", true);
        } else {
            reportAndLog("Selecting \"" + this.value + "\" value from " + this.elementLocation + ": " + this.elementLocatorByPropertyName, "info", true);
            helper.selectValueFromDropDown(this.elementLocatorByPropertyName, this.value);
        }
    }
}