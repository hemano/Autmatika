package com.autmatika.testing.api.util.webdriver;

import com.autmatika.testing.api.PreProcessFiles;
import com.autmatika.testing.api.util.PropertiesHelper;
import com.autmatika.testing.api.util.Reporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hemantojha
 *         <p>
 *         DriverType implements DriverSetup interface
 *         <p>
 *         It has enums for all the types of Driver Types
 *         This is what is passed from System Property variable as
 *         driver_id=SAUCE_FIREFOX
 *         </p>
 */
public enum DriverType implements DriverSetup {

    FIREFOX {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities =
                    DesiredCapabilities.firefox();

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            try {

                configureGecko();

                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                        "image/jpeg, application/pdf, application/octet-stream, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                profile.setPreference("pdfjs.disabled", true);

                capabilities.setCapability(FirefoxDriver.PROFILE, profile);

                return new FirefoxDriver(capabilities);
            } catch (Exception e) {
                throw new WebDriverException("Unable to launch the browser", e);
            }
        }
    },
    CHROME {
        public DesiredCapabilities getDesiredCapabilities() {
            //downloads folder to automatically save the downloaded files
            File folder = new File("downloads");
            folder.mkdir();

            configureChrome();

            ChromeOptions options = new ChromeOptions();
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);

            options.setExperimentalOption("prefs", prefs);
            options.addArguments("--test-type");
            options.addArguments("--start-maximized");
//            options.addArguments("--kiosk");
            options.addArguments("--disable-save-password-bubble");


            DesiredCapabilities capabilities = new DesiredCapabilities().chrome();

            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

            capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
            HashMap<String, Object> chromePreferences = new HashMap<>();
            chromePreferences.put("profile.password_manager_enabled", "false");
            chromePreferences.put("credentials_enable_service", "false");
            chromePreferences.put("profile.default_content_settings.popups", 0);
            chromePreferences.put("download.default_directory", folder.getAbsolutePath());

            capabilities.setCapability("chrome.prefs", chromePreferences);

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new ChromeDriver(capabilities);
        }
    },

    SAUCE_FIREFOX {
        public DesiredCapabilities getDesiredCapabilities() {

            //Creating a profile
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "image/jpeg, application/pdf, application/octet-stream, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            profile.setPreference("pdfjs.disabled", true);
            //To skip the insecured password behaviour
            profile.setPreference("security.insecure_password.ui.enabled", false);
            profile.setPreference("security.insecure_field_warning.contextual.enabled", false);


            //Create Desired Capability Instance
            DesiredCapabilities capabilities = DesiredCapabilities.firefox();

            //configure capability with firefox version
            String ff_version = determineEffectivePropertyValue("ff_version");

            if (null == ff_version) {
                capabilities.setCapability("version", "47.0");
            } else {
                capabilities.setCapability("version", ff_version);
            }

            //configure capability with platform type
            String platform = determineEffectivePropertyValue("platform");

            if (null == platform) {
                capabilities.setCapability("platform", "Windows XP");
            } else {
                platform = platform.replace("_"," ");
                capabilities.setCapability("platform", platform);
            }

            //configure capability for setting up Test Case name for Sauce Jobs
            //String testName = System.getProperty("test_name");
            String testName = Reporter.getCurrentTestName();
            capabilities.setCapability("name", testName);

            capabilities.setCapability(FirefoxDriver.PROFILE, profile);
            capabilities.setCapability("screenResolution", "1920x1080");
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            final String URL = getSauceHubUrl();

            try {
                RemoteWebDriver driver = new RemoteWebDriver(new URL(URL), capabilities);
                driver.setFileDetector(new LocalFileDetector());
                return driver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    },
    SAUCE_CHROME {
        public DesiredCapabilities getDesiredCapabilities() {
            //downloads folder to automatically save the downloaded files
            File folder = new File("downloads");
            folder.mkdir();

            DesiredCapabilities capabilities = new DesiredCapabilities().chrome();

            ChromeOptions options = new ChromeOptions();
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);

            options.setExperimentalOption("prefs", prefs);
            options.addArguments("--test-type");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-save-password-bubble");

            capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
            HashMap<String, Object> chromePreferences = new HashMap<>();
            chromePreferences.put("profile.password_manager_enabled", "false");
            chromePreferences.put("credentials_enable_service", "false");
            chromePreferences.put("profile.default_content_settings.popups", 0);
            chromePreferences.put("download.default_directory", folder.getAbsolutePath());
            capabilities.setCapability("chrome.prefs", chromePreferences);

            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);

            //configure capability with chrome version
            String ch_version = determineEffectivePropertyValue("ch_version");
            if (null == ch_version) {
                capabilities.setCapability("version", "58");
            } else {
                capabilities.setCapability("version", ch_version);
            }

            //configure capability with platform type
            String platform = determineEffectivePropertyValue("platform");
            if (null == platform) {
                capabilities.setCapability("platform", "Windows XP");
            } else {
                platform = platform.replace("_"," ");
                capabilities.setCapability("platform", platform);
            }

            //configure capability to set the job name with Test Case name
            //String testName = System.getProperty("test_name");
            String testName = Reporter.getCurrentTestName();
            capabilities.setCapability("name", testName);
            capabilities.setCapability("screenResolution", "1920x1080");

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            final String URL = getSauceHubUrl();

            try {
                RemoteWebDriver driver = new RemoteWebDriver(new URL(URL), capabilities);
                driver.setFileDetector(new LocalFileDetector());
                return driver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    },
    SAUCE_IE {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();

            capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION,
                    true);
            capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,
                    true);
            capabilities.setCapability("requireWindowFocus",
                    true);
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability("ignoreZoomSetting", true);
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

            //configure capability with chrome version
            String ie_version = determineEffectivePropertyValue("ie_version");
            if (null == ie_version) {
                capabilities.setCapability("version", "11");
            } else {
                capabilities.setCapability("version", ie_version);
            }

            //configure capability with platform type
            String platform = determineEffectivePropertyValue("platform");
            if (null == platform) {
                capabilities.setCapability("platform", "Windows XP");
            } else {
                platform = platform.replace("_"," ");
                capabilities.setCapability("platform", platform);
            }

            //configure capability to set the job name with Test Case name
            //String testName = System.getProperty("test_name");
            String testName = Reporter.getCurrentTestName();
            capabilities.setCapability("name", testName);
            capabilities.setCapability("screenResolution", "1920x1080");

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            final String URL = getSauceHubUrl();

            try {
                RemoteWebDriver driver = new RemoteWebDriver(new URL(URL), capabilities);
                driver.setFileDetector(new LocalFileDetector());
                return driver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;

        }
    },
    FIREFOX_LOCAL {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities =
                    DesiredCapabilities.firefox();
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            FirefoxProfile profile = new FirefoxProfile();

            try {

                configureGecko();

                String ext = "extensions.firebug.";
                String ext1 = "extensions.firepath.";

                profile.setPreference(ext + "currentVersion", "2.0.16");
                profile.setPreference(ext1 + "currentVersion", "0.9.7");
                profile.setPreference(ext + "allPagesActivation", "on");
                profile.setPreference(ext + "defaultPanelName", "net");
                profile.setPreference(ext + "net.enableSites", true);

                WebDriver firefox = new FirefoxDriver(profile);
                firefox.manage().deleteAllCookies();
                return firefox;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new FirefoxDriver(capabilities);
        }
    },
    IE {
        public DesiredCapabilities getDesiredCapabilities() {

            configureIE();

            DesiredCapabilities capabilities =
                    new DesiredCapabilities().internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION,
                    true);
            capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,
                    true);
            capabilities.setCapability("requireWindowFocus",
                    true);

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new InternetExplorerDriver(capabilities);
        }
    },

    SAFARI {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities =
                    DesiredCapabilities.safari();
            capabilities.setCapability("safari.cleansession",
                    true);

            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new SafariDriver(capabilities);
        }
    };

    private static String getSauceHubUrl() {

        String USERNAME = determineEffectivePropertyValue("sauce.username");
        String ACCESS_KEY = determineEffectivePropertyValue("sauce.password");

        return "http://" + USERNAME + ":" + ACCESS_KEY + "@ondemand.saucelabs.com:80/wd/hub";
    }

    /**
     * It configures the Gecko driver
     */
    private static void configureGecko() {

        String ff_version = determineEffectivePropertyValue("ff_version");
        if (null != ff_version && Double.parseDouble(ff_version) < 48) {
            System.setProperty("webdriver.firefox.marionette", "false");
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            String geckoPath = null;
            if (os.indexOf("mac") >= 0) {
                geckoPath = PreProcessFiles.ROOT_FOLDER_PATH + "/vendors/gecko/mac/geckodriver";
            } else if (os.indexOf("win") >= 0) {
                geckoPath = PreProcessFiles.ROOT_FOLDER_PATH + "/vendors/gecko/win/geckodriver.exe";
            } else {
                throw new IllegalArgumentException("Operating System : " + os + " is not supported");
            }
            System.setProperty("webdriver.gecko.driver", geckoPath);
        }
    }

    /**
     * It configures the Chrome driver
     */
    private static void configureChrome() {
        String os = System.getProperty("os.name").toLowerCase();
        String chromePath = null;
        if (os.indexOf("mac") >= 0) {
            chromePath = PreProcessFiles.ROOT_FOLDER_PATH + "/vendors/chrome/mac/chromedriver";
        } else if (os.indexOf("win") >= 0) {
            chromePath = PreProcessFiles.ROOT_FOLDER_PATH + "/vendors/chrome/win/chromedriver.exe";
        } else {
            throw new IllegalArgumentException("Operating System : " + os + " is not supported");
        }
        System.setProperty("webdriver.chrome.driver", chromePath);
    }

    /**
     * It configures the Internet Explorer driver
     */
    private static void configureIE() {
        String os = System.getProperty("os.name").toLowerCase();
        String ieDriverPath = null;
        if (os.indexOf("mac") >= 0) {
            throw new IllegalArgumentException("Internet Explorer not available on Mac");
        } else if (os.indexOf("win") >= 0) {
            ieDriverPath = PreProcessFiles.ROOT_FOLDER_PATH + "/vendors/ie/IEDriverServer.exe";
        } else {
            throw new IllegalArgumentException("Operating System : " + os + " is not supported");
        }
        System.setProperty("webdriver.ie.driver", ieDriverPath);
    }


    /**
     * It returns the property value specified in either environment variable or configuration.properties
     * It gives priority to the property specified in Java environment variable For e.g. -Ddriver_id=FIREFOX
     *
     * @param key
     * @return
     */
    private static String determineEffectivePropertyValue(String key) {

        PropertiesHelper propertiesHelper = new PropertiesHelper();

        if (null != System.getProperty(key)) {
            return System.getProperty(key);
        } else {
            return propertiesHelper.getProperties().getProperty(key);
        }
    }
}