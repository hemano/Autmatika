package com.autmatika.testing.api.util.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

/**
 * @author hemantojha
 *         <p>
 *         DriverSetup Interface make sure that every DiveryType has desired
 *         capabilities defined and return the new instance of webDriver
 *         </p>
 */
public interface DriverSetup {

	/**
	 * <p>
	 * It returns the instance of new webDriver and accepts desiredCapabilites
	 * object
	 * </p>
	 *
	 *
	 * @param desiredCapabilities
	 *            - Describes a series of key/value pairs that encapsulate
	 *            aspects of a browser. Basically, the DesiredCapabilities help
	 *            to set properties for the WebDriver. A typical use case would
	 *            be to set the path for the FirefoxDriver if your local
	 *            installation doesn't correspond to the default settings.
	 * @return - the instance of desired capabilities
	 * @throws MalformedURLException
	 *             Thrown to indicate that a malformed URL has occurred. Either
	 *             no legal protocol could be found in a specification string or
	 *             the string could not be parsed.
	 */
	WebDriver getWebDriverObject(DesiredCapabilities desiredCapabilities) throws MalformedURLException;

	DesiredCapabilities getDesiredCapabilities();

}