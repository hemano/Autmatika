package com.autmatika.testing.api.util;

import com.autmatika.testing.api.PreProcessFiles;
import com.autmatika.testing.api.util.webdriver.WebDriverThread;
import com.google.common.base.Function;
import com.isomorphic.webdriver.ByScLocator;
import org.hamcrest.Matcher;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;

/**
 * A classic singleton helper to perform the plethora of Selenium-specific
 * operations.
 */
public class SeleniumHelper {

    // Note intLongTimeOut should not exceed 90 seconds because SauceLabs will
    // exit at this time threshold if no action occurs
    private int intLongTimeOut = 30;

    public int getIntLongTimeOut() {
        return intLongTimeOut;
    }

    private EventFiringWebDriver driver;
    private WebDriver e_driver;
    private int pollingTimeOut = 250;
    private String fileUploadPath = PreProcessFiles.TEST_FILES_FOLDER_PATH + "/fileToUpload.txt";

    // variable to work with element in frame(s)
    private String activeFrame = "";

    public void setActiveFrame(String activeFrame) {
        this.activeFrame = activeFrame;
    }

    public String getActiveFrame() {
        return activeFrame;
    }

    private final int numberOfAttemptToClick = 2;
    private final int secondsToWaitAtConditionalEvent = 10;

    //
    private By elementBy = null;

    public By getElementBy() {
        return elementBy;
    }

    private static final ThreadLocal<SeleniumHelper> instance = new ThreadLocal<SeleniumHelper>() {
        @Override
        protected SeleniumHelper initialValue() {
            return new SeleniumHelper();
        }
    };

    // constructor
    private SeleniumHelper() {

        e_driver = instantiateDriver();

        driver = new EventFiringWebDriver(e_driver);

        WebEventListener eventListener = new WebEventListener();
        driver.register(eventListener);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    public static SeleniumHelper getInstance() {
        return instance.get();
    }

    private WebDriver instantiateDriver() {
        try {
            return new WebDriverThread().getDriver();
        } catch (Exception e) {
            throw new WebDriverException("Could not start the Driver", e);
        }
    }

    /**
     * Close the Selenium Helper instance
     */
    public static void closeInstance() {
        instance.get().closeBrowser();
        instance.remove();
    }

    /**
     * @return the web diver object that has been initially created by DriveFactory.
     * This is not the EventFiring driver
     * Use it only to get remote driver session.
     */
    public WebDriver getInitialDriver() {
        return e_driver;
    }

    /**
     * Returns the driver instance
     *
     * @return driver
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Load a URL in browser
     *
     * @param targetURL: the URL to be used in the browser
     */
    public void goToURL(String targetURL) {
        driver.get(targetURL);
    }

    /**
     * Close browser
     */
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Waits for a page to load completely
     *
     * @param timeoutSeconds: the integer value that specifies the timeout
     */
    public void waitForPageLoad(int timeoutSeconds) {
        Wait<WebDriver> wait = new WebDriverWait(driver, timeoutSeconds, 500).ignoring(WebDriverException.class);
        wait.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                        .equals("complete");
            }
        });
    }

    /**
     * Action to switch to specified window index. Index = 1 is supposed to be
     * the index of main window
     *
     * @param index - index of a window that it is needed to switch to.
     */
    public void switchToWindowByIndex(int index) {
        ArrayList<String> windows;
        if (index > 1) {
            waitWhileExpectedWindowsAppear(5, index);
            windows = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(windows.get(index - 1));
        } else {
            try {
                waitWhileExpectedWindowsLeft(5, 1);
            } catch (Exception e) {
                LogManager.getLogger().warn("Seems like several windows are still opened." +
                        " This may mean that some windows don't close themselves automatically." +
                        " Trying to close them...");
                windows = new ArrayList<String>(driver.getWindowHandles());
                for (int i = windows.size(); i > 1; i--) {
                    driver.switchTo().window(windows.get(i - 1)).close();
                    waitWhileExpectedWindowsLeft(5, i - 1);
                }
            }
            windows = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(windows.get(0));
        }
    }

    /**
     * Action to wait while browser opens all expected windows. Used when
     * expected number of windows is greater than 1
     *
     * @param timeoutSeconds  - number of SECONDS to wait for expected windows to appears
     * @param numberOfWindows - indicates the minimal number of windows to wait for
     */
    private void waitWhileExpectedWindowsAppear(int timeoutSeconds, int numberOfWindows) {
        Matcher<?> matcher = is(greaterThanOrEqualTo(numberOfWindows));
        new FluentWait<WebDriver>(driver).withTimeout(timeoutSeconds, TimeUnit.SECONDS).pollingEvery(1000, TimeUnit.MILLISECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    public Boolean apply(WebDriver d) {
                        return (matcher.matches(driver.getWindowHandles().size()));
                    }
                });
    }

    /**
     * Action to wait while browser close extra windows and leaves the main one.
     * Used when expected number of windows is 1
     *
     * @param timeoutSeconds  - number of SECONDS to wait for expected windows to disappears
     * @param numberOfWindows - indicates the number of windows be left
     */
    private void waitWhileExpectedWindowsLeft(int timeoutSeconds, int numberOfWindows) {
        Matcher<?> matcher = is(lessThanOrEqualTo(numberOfWindows));
        new FluentWait<WebDriver>(driver).withTimeout(timeoutSeconds, TimeUnit.SECONDS).pollingEvery(1000, TimeUnit.MILLISECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    public Boolean apply(WebDriver d) {
                        return (matcher.matches(driver.getWindowHandles().size()));
                    }
                });
    }

    /**
     * Method to define type of locators to be used in IActor classes
     *
     * @param locator
     * @return By-type value
     */
    private By initElementByLocator(String locator) {
        if (PageLocatorMatcher.isXpath(locator)) {
            return By.xpath(locator.substring(6));
        } else if (PageLocatorMatcher.isId(locator)) {
            return By.id(locator.substring(3));
        } else if (PageLocatorMatcher.isCss(locator)) {
            return By.cssSelector(locator.substring(4));
        } else if (PageLocatorMatcher.isName(locator)) {
            return By.name(locator.substring(5));
        } else if (PageLocatorMatcher.isTagname(locator)) {
            return By.tagName(locator.substring(8));
        } else if (PageLocatorMatcher.isClass(locator)) {
            return By.className(locator.substring(10));
        } else if (PageLocatorMatcher.isLink(locator)) {
            return By.linkText(locator.substring(5));
        } else if (locator.startsWith("scLocator")) {
            return ByScLocator.scLocator(locator);
        } else {
            Reporter.fail("Cannot initialize " + locator + " as an accepted type of value. Property item cannot be found!");
            return By.linkText(locator);
        }
    }

    /**
     * Action to get text data from the input text field / text area
     *
     * @param locator locator type to be used to locate the input text field / text area
     * @return text value from the
     */
    public String getData(String locator) {
        this.elementBy = initElementByLocator(locator);
        WebElement webelement = driver.findElement(this.elementBy);
        String data = webelement.getText().trim();
        if (data.isEmpty()) {
            try {
                data = webelement.getAttribute("value").trim();
            } catch (Exception e) {
                data = "";
            }
        }
        return data;
    }

    /**
     * Action to select radio button identified by locator based on
     * boolean expression is true or false
     *
     * @param locator:   locator type to be used to locate the radio button element
     * @param mustCheck: true or false
     */
    public void selectRadioButton(String locator, boolean mustCheck) {
        this.elementBy = initElementByLocator(locator);
        WebElement webelement = driver.findElement(this.elementBy);
        boolean clickButton = !webelement.isSelected() & mustCheck;
        if (clickButton) {
            webelement.click();
        }
    }

    /**
     * Method returns the value of the field
     *
     * @param locator: locator type to be used to locate the radio button element
     * @return String webelement text
     */
    public String getTextValueFromField(String locator) {
        this.elementBy = initElementByLocator(locator);
        WebElement webelement = driver.findElement(this.elementBy);
        String data = webelement.getText().trim();
        if (data.isEmpty()) {
            try {
                LogManager.getLogger().info("Getting text from value attribute");
                data = webelement.getAttribute("value").trim();
            } catch (Exception e) {
                data = "";
            }
        }
        return data;
    }

    /**
     * Method to return the page title
     * @return String webelement text
     */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Action to check / uncheck the checkbox on a web page
     *
     * @param locator:         locator type to be used to locate the radio button element
     * @param shouldBeChecked: true / false
     */
    public void checkUncheckCheckbox(String locator, boolean shouldBeChecked) {
        this.elementBy = initElementByLocator(locator);
        WebElement checkbox = driver.findElement(this.elementBy);
        boolean doClick = (!checkbox.isSelected() & shouldBeChecked) || (checkbox.isSelected() & !shouldBeChecked);
        if (doClick) {
            checkbox.click();
        }
    }

    /**
     * Action to select a value from a dropdown
     *
     * @param locator: locator type to be used to locate the dropdown element
     * @param value:   value to be selected from dropdown
     */
    public void selectValueFromDropDown(String locator, String value) {
        this.elementBy = initElementByLocator(locator);
        if (!value.equals("")) {
            elementToBeEnable(this.elementBy);
            WebElement webelement = driver.findElement(this.elementBy);
            Select dropdown = new Select(webelement);
            waitForOptions(locator);
            try {
                selectDropDownOption(dropdown, value);
            } catch (StaleElementReferenceException e1) {
                webelement = driver.findElement(this.elementBy);
                dropdown = new Select(webelement);
                waitForOptions(locator);
                selectDropDownOption(dropdown, value);
            }
        }
    }

    /**
     * This method select the value from the dropdown
     *
     * @param dropdown : to pass the WebElement into dropdown
     * @param value    : pass the value to select from dropdown
     */
    private void selectDropDownOption(Select dropdown, String value) {
        try {
            dropdown.selectByVisibleText(value);
        } catch (NoSuchElementException n) {
            value = value.replaceAll(String.valueOf((char) 160), String.valueOf((char) 32));
            dropdown.selectByVisibleText(value);
        }
        WebDriverWait wait = new WebDriverWait(driver, intLongTimeOut);
        wait.until(ExpectedConditions.attributeContains(dropdown.getFirstSelectedOption(), "text", value));
    }

    /**
     * This method gets auto selected value from a dropdown
     *
     * @param locator: locator type to be used to locate the dropdown  element
     * @return a string indicating a value that has been selected
     */
    public String autoSelectFromDropdown(String locator) {
        this.elementBy = initElementByLocator(locator);
        elementToBeEnable(this.elementBy);
        WebElement selectElement = driver.findElement(this.elementBy);
        Select select = new Select(selectElement);
        waitForOptions(locator);

        int value;
        Random random = new Random();
        List<WebElement> allOptions = select.getOptions();

        if (allOptions.get(0).getText().toLowerCase().contains("none")) {
            value = 1 + random.nextInt(allOptions.size() - 1);
        } else {
            value = random.nextInt(allOptions.size() - 1);
        }

        select.selectByIndex(value);
        return allOptions.get(value).getText();
    }

    /**
     * This method wait for the value to populate in the dropdown
     *
     * @param locator :- selector to find the element
     */
    private void waitForOptions(String locator) {
        this.elementBy = initElementByLocator(locator);
        try {
            LogManager.getLogger().info("Waiting for options to be available...");
            WebElement webelement = driver.findElement(this.elementBy);
            Select dropdown = new Select(webelement);
            new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS).pollingEvery(10, TimeUnit.MILLISECONDS)
                    .until(new Function<WebDriver, Boolean>() {
                        public Boolean apply(WebDriver d) {
                            return (dropdown.getOptions().size() >= 2);
                        }
                    });
        } catch (StaleElementReferenceException s) {
            LogManager.getLogger().info("Seems like the web page is being updated. Waiting...");
            WebElement webelement = driver.findElement(this.elementBy);
            Select dropdown = new Select(webelement);
            new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS).pollingEvery(10, TimeUnit.MILLISECONDS)
                    .until(new Function<WebDriver, Boolean>() {
                        public Boolean apply(WebDriver d) {
                            return (dropdown.getOptions().size() >= 2);
                        }
                    });
        }
    }

    /**
     * Wait for the element to be enable on web page
     *
     * @param locator: locator to wait to make it enable
     */
    public void elementToBeEnable(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, intLongTimeOut);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for the element to be displayed on the page
     *
     * @param locator: locator to check it is displayed on the webpage
     */
    public boolean waitForElementToBeDisplayed(String locator) {
        this.elementBy = initElementByLocator(locator);
        return new FluentWait<WebDriver>(driver).withTimeout(intLongTimeOut, TimeUnit.SECONDS)
                .pollingEvery(pollingTimeOut, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(this.elementBy))
                .isDisplayed();
    }

    public void elementToBePresent(String locator) {
        this.elementBy = initElementByLocator(locator);
        WebDriverWait wait = new WebDriverWait(driver, intLongTimeOut);
        wait.until(ExpectedConditions.presenceOfElementLocated(this.elementBy));
    }

    public boolean elementToBeVisible(String locator) {
        return waitForElementToBeDisplayed(locator);
    }

    /**
     * Action to click an element on a web page
     *
     * @param locator: locator type to be used to locate the button element
     */
    public void clickElement(String locator, String expectedValueToAppearAfterClick) {
        this.elementBy = initElementByLocator(locator);
        WebElement webelement = driver.findElement(this.elementBy);
        webelement.click();
    }


    /**
     * The method is used to wait until popup appears;
     */
    private void waitForPopupToAppear() {

        new FluentWait<WebDriver>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .ignoring(NoAlertPresentException.class)
                .pollingEvery(1, TimeUnit.SECONDS)
                .until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Action to imitate key press from keyboard
     *
     * @param fieldValue: value to be entered in appropriate field using metadata values
     */
    public void keyboardPress(String fieldValue) {
        if (!fieldValue.equals("")) {
            try {
                WebElement keyItem = driver.findElement(this.elementBy);
                keyItem.sendKeys(Keys.valueOf(fieldValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogManager.getLogger().info("No value defined for 'PressKey' action. Check your parameters");
        }
    }


    /**
     * Action to set text into a field on a web page
     *
     * @param locator:    locator type to be used to locate the button element
     * @param fieldValue: value to be entered in appropriate field
     */
    public void enterField(String locator, String fieldValue) {
        this.elementBy = initElementByLocator(locator);
        if (!fieldValue.equals("")) {
            WebElement textField = driver.findElement(this.elementBy);
            textField.clear();
            textField.sendKeys(fieldValue);
        }
    }

    /**
     * Action to accept alert / error message on the web page
     *
     * @return String webelement text with information provided in the Popup Window
     */
    public String acceptAlertMessage() {
        Alert jsalert = driver.switchTo().alert();
        String alertMsg = jsalert.getText();
        if (alertMsg.contains("Account Alert")) {
            Reporter.log("Logging " + alertMsg + " popup appeared");
            jsalert.accept();
        } else {
            Reporter.log("Logging " + alertMsg + " popup appeared");
            jsalert.accept();
        }
        return alertMsg;
    }

    /**
     * Action to upload a file
     *
     * @param locator: locator type to be used to locate the element for uploading a file
     */
    public void fileUpload(String locator) {
        this.elementBy = initElementByLocator(locator);
        WebElement webelement = driver.findElement(this.elementBy);
        webelement.sendKeys(fileUploadPath);
    }

    /**
     * Provides the ability to use the browser's navigation capabilities.
     *
     * @param operation: browser operation performed can be FORWARD, BACK, or REFRESH case-insensitive
     */
    public void pageNavigation(String operation) {
        String browserOperation = operation.toUpperCase();
        switch (browserOperation) {
            case "FORWARD":
                LogManager.getLogger().info("Browser FORWARD operation executing.");
                driver.navigate().forward();
                break;
            case "BACK":
                LogManager.getLogger().info("Browser BACK operation executing.");
                driver.navigate().back();
                break;
            case "REFRESH":
                LogManager.getLogger().info("Browser REFRESH operation executing.");
                driver.navigate().refresh();
                break;
            default:
                LogManager.getLogger().info("No navigation operation performed.  Check spelling for page navigation parameter.  Only 'Forward', 'Back', and 'Refresh' are supported.");
                break;
        }

    }
}
