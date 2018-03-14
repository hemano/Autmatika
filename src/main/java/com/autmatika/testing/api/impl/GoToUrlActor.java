package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 *
 * <p>This action will navigate to the URL specified within the same browser
 * window.</p>
 *
 * <p>improvement added for waitForPageLoad after navigating to URL</p>
 * 
 */
public class GoToUrlActor extends AbstractActor {

	@Override
	public void doIt() {

		super.preDo();
		reportAndLog("Navigating to the URL: " + this.value, "info", true);
		SeleniumHelper helper = SeleniumHelper.getInstance();
		helper.goToURL(this.value);
		helper.waitForPageLoad(helper.getIntLongTimeOut());
	}
}
