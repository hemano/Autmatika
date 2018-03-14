package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;
import org.openqa.selenium.NoAlertPresentException;

/**
 * Clicks on java script alert message to accept the notification
 *
 */
public class JSAlertActor extends AbstractActor {

	@Override
	public void doIt() {
		super.preDo();
		SeleniumHelper helper = SeleniumHelper.getInstance();
		try {
			helper.acceptAlertMessage();
			reportAndLog("Alert message found and accepted", "info", true);
		}
		catch (NoAlertPresentException Ex){
			reportAndLog("No alert message found", "warn", true);
		}
	}
}
