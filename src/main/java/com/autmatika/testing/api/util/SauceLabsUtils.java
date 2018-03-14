package com.autmatika.testing.api.util;

import com.saucelabs.ci.sauceconnect.AbstractSauceTunnelManager;
import com.saucelabs.ci.sauceconnect.SauceConnectFourManager;
import com.saucelabs.ci.sauceconnect.SauceTunnelManager;

import java.io.IOException;

/**
 * Created by hemantojha on 09/07/17.
 */
public class SauceLabsUtils {

    private static SauceTunnelManager sauceConnectFourManager;
    private static String sauceUsername;


    public static void
    startSauceConnect() {
        try {
            PropertiesHelper propertiesHelper = new PropertiesHelper();

            String sauceUsername = propertiesHelper.getProperties().getProperty("sauce.username");
            String sauceAccessKey = propertiesHelper.getProperties().getProperty("sauce.password");

            boolean tunnelFlag = Reporter.getSauceTunnels(sauceUsername, sauceAccessKey);

            if (!tunnelFlag) {

                int port = 4445;
                boolean quietMode = false;
                sauceConnectFourManager = new SauceConnectFourManager(quietMode);
                ((AbstractSauceTunnelManager)sauceConnectFourManager).getTunnelIdentifier(null,sauceUsername);
                sauceConnectFourManager.openConnection(
                        sauceUsername, sauceAccessKey, port, null, null, null, !quietMode, null);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void stopTunnel() {
        ((AbstractSauceTunnelManager)sauceConnectFourManager).getTunnelIdentifier(null , sauceUsername);
        sauceConnectFourManager.closeTunnelsForPlan(sauceUsername, null, null);
    }
}
