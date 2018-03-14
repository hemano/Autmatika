package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * <p> Closes browser </p>
 *
 */
public class CloseBrowserActor extends AbstractActor {
	
	@Override
	public void doIt() {
		SeleniumHelper helper = SeleniumHelper.getInstance();
		reportAndLog("Closing current browser", "info", true);
		helper.closeBrowser();
	}

}
