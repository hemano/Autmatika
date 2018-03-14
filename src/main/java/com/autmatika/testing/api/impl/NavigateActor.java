package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;


/**
 * 
 *<p> Supports browser navigation - Forward, Back, and Refresh Page </p>
 *
 */
public class NavigateActor extends AbstractActor {

    @Override
    public void doIt() {
        super.preDo();
        reportAndLog("Attempting browser navigation opperation: " + this.value, "info", true);
        SeleniumHelper helper = SeleniumHelper.getInstance();
        helper.pageNavigation(this.value);
    }
}
