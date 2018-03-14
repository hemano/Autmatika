package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 * Uploads a file from local machine
 *
 */
public class UploadFileActor extends AbstractActor {

	@Override
	public void doIt() {
		super.preDo();
		SeleniumHelper helper = SeleniumHelper.getInstance();
		reportAndLog("Uploading file \""  + "\" to " + this.elementLocatorByPropertyName, "info", true);
        helper.fileUpload(this.elementLocatorByPropertyName);
	}
}