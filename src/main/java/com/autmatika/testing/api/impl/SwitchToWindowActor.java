package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.util.SeleniumHelper;

/**
 *
 * <p> Switching to the window with appropriate index. Used when few windows are open in browser.
 * If needed it may be used in order to switch to any extra window and then back to the main window.
 * Index = 1 is supposed to be the index of the main window
 * MAXIMUM acceptable window index is 9 </p>
 *
 */
public class SwitchToWindowActor extends AbstractActor {

    @Override
    public void doIt() {
        super.preDo();
        if (this.value.length() > 0) {
            int index = Integer.parseInt(this.value.substring(0, 1));

            reportAndLog("Switching to the window with index = " + index, "info", true);
            SeleniumHelper helper = SeleniumHelper.getInstance();
            helper.switchToWindowByIndex(index);
        }
        else{
        	reportAndLog("REQUIRED 'WINDOW INDEX' PARAMETER IS MISSED", "warn", true);
        }
    }
}