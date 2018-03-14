package com.autmatika.testing.api.util.webdriver;

import com.autmatika.testing.api.util.PropertiesHelper;
import com.google.common.base.Throwables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author hemantojha
 *
 *         <p>
 *         This enables us to create webDriver instance for a thread
 *         </p>
 *
 */
public class WebDriverThread {
	//private final Logger logger = Logger.getLogger(WebDriverThread.class);

	private WebDriver driver;
	private DriverType driverType;
	private DriverType selectedDriverType;

	// setting the default DriverType as FIREFOX_LOCAL
	private final DriverType defaultDriverType = DriverType.FIREFOX;

	private String driver_id;

	// setting the other variables
	private final String operatingSystem = System.getProperty("os.name").toUpperCase();
	private final String systemArchitecture = System.getProperty("os.arch").toUpperCase();
	private final boolean useRemoteWebDriver = Boolean.getBoolean("remoteDriver");

	/**
	 * <p>
	 * Create a desired capabilities instance It instantiate the web driver by
	 * determining the effective driver type.
	 * </p>
	 * 
	 * @return driver
	 * @throws Exception
	 *             The class Exception and its subclasses are a form of
	 *             Throwable that indicates conditions that a reasonable
	 *             application might want to catch.
	 * 
	 *             The class Exception and any subclasses that are not also
	 *             subclasses of RuntimeException are checked exceptions.
	 *             Checked exceptions need to be declared in a method or
	 *             constructor's throws clause if they can be thrown by the
	 *             execution of the method or constructor and propagate outside
	 *             the method or constructor boundary.
	 * 
	 */
	public WebDriver getDriver() throws Exception {

		if (null == driver) {
			selectedDriverType = determineEffectiveDriverType();

			DesiredCapabilities desiredCapabilities = selectedDriverType.getDesiredCapabilities();

			instantiateWebDriver(desiredCapabilities);
		}
		return driver;
	}

	/**
	 * <p>
	 * Returns the most appropriate driver type It allows to read the driver_id
	 * from the environment variables
	 * </p>
	 * 
	 * @return DriverType
	 */
	private DriverType determineEffectiveDriverType() {
		driverType = defaultDriverType;
		try {

			// get the browser id from Command Line
			if (null != System.getProperty("driver_id")) {
				driver_id = System.getProperty("driver_id");
				driverType = DriverType.valueOf(driver_id);
			} else {
				PropertiesHelper propertiesHelper = new PropertiesHelper();
				driver_id = propertiesHelper.getProperties().get("driver_id").toString();
				driverType = DriverType.valueOf(driver_id);
			}

		} catch (IllegalArgumentException ignore) {
			Throwables.propagate(
					new IllegalArgumentException("Unknown DriverType specified... defaulting to " + driverType));
		} catch (NullPointerException ignore) {
			Throwables.propagate(
					new IllegalArgumentException("Unknown DriverType specified... defaulting to " + driverType));
		}

		return driverType;
	}

	/**
	 * <p>
	 * Instantiate the webDriver using desiredCapabilities object.
	 * </p>
	 * 
	 * @param desiredCapabilities
	 * @throws MalformedURLException
	 */
	private void instantiateWebDriver(DesiredCapabilities desiredCapabilities) throws MalformedURLException {

		System.out.println(String.format("Current Operating System : {%s}", operatingSystem));
		System.out.println(String.format("Current System Architecture : {%s}", systemArchitecture));
		System.out.println(String.format("Current Browser : {%s}\n", driverType));

		if (useRemoteWebDriver) {
			URL seleniumGridURL = new URL(System.getProperty("gridURL"));
			System.out.println("seleniumGridURL = " + seleniumGridURL);
			String desiredBrowserVersion = System.getProperty("desiredBrowserVersion");
			String desiredPlatform = System.getProperty("desiredPlatform");

			if ((null != desiredPlatform) && desiredPlatform.isEmpty()) {
				desiredCapabilities.setPlatform(Platform.valueOf(desiredPlatform));
			}

			if (null != desiredBrowserVersion && desiredBrowserVersion.isEmpty()) {
				desiredCapabilities.setVersion(desiredBrowserVersion);
			}
			driver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);

		} else {
			driver = selectedDriverType.getWebDriverObject(desiredCapabilities);
		}
	}
}
