package com.autmatika.testing.api;

import com.autmatika.testing.api.client.StartAction;
import com.autmatika.testing.api.util.*;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import org.hamcrest.Matchers;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jcabi.matchers.RegexMatchers.matchesPattern;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>
 * This abstract class contains all of the properties for each doable.
 * </p>
 *
 */
public abstract class AbstractActor implements IActor, SauceOnDemandSessionIdProvider {

    private String lastBusinessModule = "";
    private String lastSkipBusinessModule = "";

    // every row in the xlsx will require context to be specified
    // from the test xlsx;
    protected String run;
    protected String test;
    protected String testOnFail;
    protected String businessModule;
    protected String value;
    protected String iteration;

    // logical Name from Business Process xlsx = element Location
    protected String elementLocation;
    protected String stepId;
    protected String actorOnFail;
    protected String context;
    protected String uiAction;
    protected String parameters;
    protected String elementLocatorByPropertyName;
    protected String testTagValue;

    // Reserved to determine when a test starts and ends
    protected String testStatus;

    private AbstractActor abstractActor;

    private int doableIndex = 0;

    protected String sessionId;

    // determines Pass Fail status of each test
    protected boolean executionStatus = true;

    //  Setters and Getters

    // following getters and setters are part of the java beans contract as the
    // context must be explicitly be declared here and the xml

    public String getContext() {
        return context;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestTagValue(String testTagValue) {
        this.testTagValue = testTagValue;
    }

    public String getTestTagValue() { return testTagValue; }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getUiAction() {
        return uiAction;
    }

    public void setUiAction(String uiAction) {
        this.uiAction = uiAction;
    }

    public String getTestOnFail() {
        return testOnFail;
    }

    public void setTestOnFail(String testOnFail) {
        this.testOnFail = testOnFail;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getActorOnFail() {
        return actorOnFail;
    }

    public void setActorOnFail(String actorOnFail) {
        this.actorOnFail = actorOnFail;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getOnFail() {
        return actorOnFail;
    }

    public void setOnFail(String onFail) {
        this.actorOnFail = onFail;
    }

    public String getBusinessModule() {
        return businessModule;
    }

    public void setBusinessModule(String businessModule) {
        this.businessModule = businessModule;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getElementLocation() {
        return elementLocation;
    }

    public void setElementLocation(String elementLocation) {
        this.elementLocation = elementLocation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * This list of Doables that make up this IActor.
     */
    protected List<IActor> actors = new ArrayList<IActor>();

    /**
     * Accessors
     *
     * @return actors UI actions
     **/
    public List<IActor> getActors() {
        return actors;
    }

    public void setActors(List<IActor> actors) {
        this.actors = actors;
    }

    /** API methods **/

    /**
     * <p> Default implementation of doIt() is to recursively call doIt() on all
     * actors that compose this IActor. </p>
     * <p>
     * <p> -Reporting for saucelabs and extentreports test execution results (pass or fail) </p>
     * <p> -Exceptions are cataloged into the log, saucelabs report, and extentreports </p>
     */
    public void doIt() {
        // instantiate the report

        // loop through all the actors
        for (; doableIndex < actors.size(); doableIndex++) {

            // create a local instance of abstract doable
            abstractActor = (AbstractActor) actors.get(doableIndex);

            // create new test for the report when testStatus = Start
            if (abstractActor.getTestStatus().equalsIgnoreCase("Start")) {

                // Initialize the Driver object
                //System.setProperty("test_name", abstractActor.getTest());

                // create the test for report and set the name with getTest()
                Reporter.addTest(abstractActor.getTest());
                Reporter.logForEveryTest();
                LogManager.getLogger().info("Executing Test: " + abstractActor.getTest() + ", Iteration: "
                        + abstractActor.getIteration());

                // SauceLabs Session ID
                this.sessionId = (((RemoteWebDriver) SeleniumHelper.getInstance().getInitialDriver()).getSessionId()).toString();
                StartAction.setExecutionEnvironmentInfo();
            }

            try {
                // -------------Execute the step--------------------//
                String businessModuleName = abstractActor.getBusinessModule();
                String context = abstractActor.getContext();

                String newBusinessModule = context + " " + businessModuleName;
                LogManager.getLogger().info("Current Business Process Running :- " + newBusinessModule);

                //Create a node in report when new business process start
                if (!(lastBusinessModule.equals(newBusinessModule))) {
                    Reporter.node("Business Module: " + businessModuleName, businessModuleName + " execution has started");
                    lastBusinessModule = newBusinessModule;
                    lastSkipBusinessModule = lastBusinessModule;
                }
                Reporter.log("<pre>Performing the " + abstractActor.getUiAction() + " action</pre>");

                actors.get(doableIndex).doIt();
                // -------------End Execute the step--------------------//

            } catch (AssertionError e) { // catch the validation exception and
                // report the failure
                reportFailAndLog("FAILURE: Validation does not match application value = " + abstractActor.getValue()
                        + "\n" + e.getLocalizedMessage());
                executionStatus = false;
                // catches the all common selenium exceptions and mark the
                // failure on report; must list ALL exceptions here

                //--------------Exception Reference-------------------//
                // Reference https://seleniumhq.github.io/selenium/docs/api/py/_modules/selenium/common/exceptions.html
                // Reference http://toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/
                //----------------------------------------------------//
            }catch (StaleElementReferenceException e){
                try {
                    reportAndLog("Stale element reference exception occurred." +
                            " Trying to work with the element again.", "warn", true);
                    actors.get(doableIndex).doIt();
                } catch (Exception e1) {
                    handleWebElementException(e1);
                }
            }
            catch (ElementNotVisibleException | TimeoutException | NoSuchElementException  | ElementNotSelectableException e) {
                handleWebElementException(e);

                // catch NoAlertPresent and UnhandledAlert Exceptions
            } catch (NoAlertPresentException | UnhandledAlertException ex) {
                String alertMsg = SeleniumHelper.getInstance().getDriver().switchTo().alert().getText();
                Reporter.failNoScreenshot("Unexpected alert occurred with the following message:" +
                        "<pre><textarea>" + alertMsg + "</textarea></pre><br />Attempting to close the alert.");
                SeleniumHelper.getInstance().getDriver().switchTo().alert().accept();
                ex.printStackTrace();
                executionStatus = false;
                // skip all the atomic steps till the end of current test
                skipLogic();

            }// catch other Exception
            catch (Exception e) {
                Reporter.failNoScreenshot("Unexpected error occurred:" +
                        "<pre><textarea>" + e.getLocalizedMessage() + "</textarea></pre>");
                e.printStackTrace();
                executionStatus = false;
                // skip all the atomic steps till the end of current test
                skipLogic();
                SeleniumHelper.closeInstance();
            }

                // Handle the activities at the end of every tests
            if (abstractActor.getTestStatus().equalsIgnoreCase("End")) {
                // set the test info to the report

                if (executionStatus) {
                    LogManager.getLogger().info("Test " + abstractActor.getTest() + " PASSED");
                } else {
                    LogManager.getLogger().info("Test " + abstractActor.getTest() + " FAILED");
                }

                SeleniumHelper.closeInstance();

                if (determineEffectivePropertyValue("driver_id").contains("SAUCE")) {

                    // create a node in report for the screen cast as SauceLabs Information
                    Reporter.addLinkToReport(Reporter.getScreencastLinkFromSauce(sessionId));

                    if (executionStatus) {
                        Reporter.updateSauceJob("pass", this.sessionId);
                        LogManager.getLogger().info("Writting Pass test status to SauceJob");
                    } else {
                        Reporter.updateSauceJob("fail", this.sessionId);
                        LogManager.getLogger().info("Writting Fail test status to SauceJob");
                    }
                }

                Reporter.flush();

                // write Execution Context variables into file
                try {
                    Reporter.writeToFile();
                } catch (IOException e) {
                    LogManager.getLogger().error("Failed to write Execution Context variables into file!");
                }
                // Reset the Execution Context
                ExecutionContextHandler.resetExecutionContextValues();
                // Reset exectuionStatus
                executionStatus = true;
            }
        }

        postDo();
    }

    /**
     * Skips all the further atomic actions till the end of the test case and
     * set the doableIndex to the next test
     */
    private void skipLogic() {

        if (abstractActor.getTestOnFail().equalsIgnoreCase("Exit")) {
            for (; doableIndex < actors.size(); doableIndex++) {
                abstractActor = (AbstractActor) actors.get(doableIndex);

                String businessModuleName = abstractActor.getBusinessModule();
                String context = abstractActor.getContext();

                String skipBusinessModule = context + " " + businessModuleName;
                LogManager.getLogger().info("lastBusinessModule value :- " + lastBusinessModule + " lastSkipBusinessModule value :- " + lastSkipBusinessModule);

                //If business process execute and failed then it create a skip node for remaining business process
                if (!lastSkipBusinessModule.equalsIgnoreCase(skipBusinessModule)) {
                    Reporter.skip("Skipping Business Process : " + businessModuleName);
                    lastSkipBusinessModule = skipBusinessModule;
                }

                if (((AbstractActor) actors.get(doableIndex)).getTestStatus().equalsIgnoreCase("End")) {
                    break;
                }
            }
        } else if (abstractActor.getTestOnFail().equalsIgnoreCase("Continue")) {
            for (; doableIndex < actors.size(); doableIndex++) {
                abstractActor = (AbstractActor) actors.get(doableIndex);

                String businessModuleName = abstractActor.getBusinessModule();
                String context = abstractActor.getContext();
                String skipBusinessModule = context + " " + businessModuleName;

				/*
                    If Business process written Continue on fail then it execute next business process and if next business process
					fail and created skip node for the remaining business process
				 */

                if (!lastSkipBusinessModule.equalsIgnoreCase(skipBusinessModule)) {
                    Reporter.skip("Skipping Business Process : " + businessModuleName);
                    lastSkipBusinessModule = skipBusinessModule;
                }

                if (((AbstractActor) actors.get(doableIndex)).getTestStatus().equalsIgnoreCase("Test in Process") ||
                        ((AbstractActor) actors.get(doableIndex)).getTestStatus().equalsIgnoreCase("End")) {
                    break;
                }
            }
        }
    }

    /**
     * <p>
     * Executes before every doable class with the exception of
     * CaptureDataActor class. Provides a wait for element to be displayed
     * defined by a long timeout in SeleniumHelper. Then evaluates user input if
     * it is a metadata, keyword, or execution context variable.
     * </p>
     */
    public void preDo() {
        if (!this.elementLocation.equals("")) {
            setElementLocatorByPropertyName();
            handleFrames();
            SeleniumHelper.getInstance().waitForElementToBeDisplayed(this.elementLocatorByPropertyName);
        }
        String inputTestParameter = this.value;
        this.value = TestParametersController.checkIfSpecialParameter(this.value);
        reportAndLog("[input test parameter] '" + inputTestParameter + "' -> '" + this.value + "' [output value]", "info", true);
    }


    /**
     * <p>Reserved method for use after ALL test have been executed.</p>
     */
    public void postDo() {
        Reporter.flush();
    }

    /**
     * <p>
     * This is specific for CaptureDataActor.class to exclude parameter type
     * warning message set this.elementLocatorByPropertyName in accordance with
     * provided property and context this is performed before
     * this.elementLocation is used in IActor classes (actions)
     * </p>
     */
    public void setElementLocatorByPropertyName() {
        if (!this.elementLocation.equals("")) {
            this.elementLocatorByPropertyName = PropertiesHandler.getPropertyByContextAndKey(this.context,
                    this.elementLocation);

            if (PageLocatorMatcher.isECVariableInXpath(this.elementLocatorByPropertyName)) {
                this.elementLocatorByPropertyName = PageLocatorMatcher.updateXpath(this.elementLocatorByPropertyName);
            }
        } else {
            this.elementLocatorByPropertyName = "";
        }
    }

    /**
     *
     *
     * @param context
     * @param element    - value defined in 'Parent' column
     *
     */
    private String getParents(String context, String element) {
        reportAndLog("Gathering parent value for " + element, "debug", true);
        String parents = PropertiesHandler.getParentByContextAndKey(context, element);
        if (!parents.isEmpty()) {
            reportAndLog("The found parent value for " + element + " is '" + "' " + parents, "info", true);
            return parents;
        } else {
            reportAndLog("The " + element + " does not have a parent value", "debug", true);
            return "";
        }
    }

    /**
     * <p>
     *
     * @param frameName - iFrame defined as string to be switched to
     *                  </p>
     */
    private void switchToFrame(String frameName) {
        SeleniumHelper helper = SeleniumHelper.getInstance();
        String frame = PropertiesHandler.getPropertyByContextAndKey(this.context, frameName);
        helper.elementToBePresent(frame);
        WebElement frameElement = helper.getDriver().findElement(helper.getElementBy());
        reportAndLog("Switching to frame " + frameName, "info", true);
        try {
            helper.getDriver().switchTo().frame(frameElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The method is used to determine whether web-element is housed in any frame and to switch into if needed
     */
    private void handleFrames() {
        String parents = getParents(this.context, this.elementLocation);
        if (!parents.equals(SeleniumHelper.getInstance().getActiveFrame())) {
            SeleniumHelper.getInstance().getDriver().switchTo().defaultContent();
            String[] frames = {};
            if (!parents.isEmpty()) {
                frames = parents.split(">");
            }

            for (int i = 0; i < frames.length; i++) {
                switchToFrame(frames[i]);
            }
            SeleniumHelper.getInstance().setActiveFrame(parents);
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    /**
     * It returns the property value specified in either environment variable or
     * configuration.properties It gives priority to the property specified in
     * Java environment variable For e.g. -Ddriver_id=FIREFOX
     *
     * @param key a key from properties file
     * @return the value by passes key
     */
    private static String determineEffectivePropertyValue(String key) {

        PropertiesHelper propertiesHelper = new PropertiesHelper();

        if (null != System.getProperty(key)) {
            return System.getProperty(key);
        } else {
            return propertiesHelper.getProperties().getProperty(key);
        }
    }

    /**
     * <p>
     * Writes to log with specified logLevel. Writes to report if writeToReport
     * as information type. is true.
     * </p>
     *
     * @param log           message that will be shown in log and/or report
     * @param logLevel      indicates the log level: info, warn or error
     * @param writeToReport indicates if it's needed the message to be shown in report
     */
    protected void reportAndLog(String log, String logLevel, boolean writeToReport) {
        switch (logLevel) {
            case "info":
                LogManager.getLogger().info(log);
                if (writeToReport) {
                    Reporter.log(log);
                }
                break;
            case "warn":
                LogManager.getLogger().warn(log);
                if (writeToReport) {
                    Reporter.warn(log);
                }
                break;
            case "error":
                LogManager.getLogger().error(log);
                break;
            case "debug":
                LogManager.getLogger().debug(log);
                break;
            case "pass":
                LogManager.getLogger().info(log);
                if (writeToReport) {
                    Reporter.passMessage(log);
                }
                break;
            default:
                LogManager.getLogger().info("Check spelling in method for logLevel");
                break;
        }
    }

    /**
     * Record in report with failure as type and log with error level.
     *
     * @param log information to be logged in report and logger
     */
    public void reportFailAndLog(String log) {
        Reporter.fail(log);
        LogManager.getLogger().error(log);
    }

    /**
     * used in catch blocks where any web-element related exceptions are caught
     * @param e the exception object
     */
    protected void handleWebElementException(Exception e){
        reportFailAndLog("FAILURE:" + "\n" +
                "Test: " + abstractActor.getTest() + "\n" +
                "Business Process: " + abstractActor.getBusinessModule() + "\n" +
                "Action: " + abstractActor.getUiAction() + "\n" +
                "Element Location / Logical Name: " + abstractActor.getElementLocation() + "\n" +
                "Business Process Step: " + abstractActor.getStepId() + "\n" +
                e.getLocalizedMessage());

        e.printStackTrace();
        executionStatus = false;
        // skip all the atomic steps till the end of current test
        skipLogic();
    }

    protected void validateTextWithRegEx(String actualValue, String newValue) {
        // Capability to accept Regular Expression, Substring and exact string
        // Usages:
        // RE=^Starting.*Ending$
        // CONTAINS=substring
        // exact string
        // CI=some string
        if (this.value.toUpperCase().trim().startsWith("RE=")) {
            newValue = newValue.substring("RE=".length());
            assertThat(actualValue.trim(), matchesPattern(newValue ));
            reportAndLog("Actual value '" + actualValue + "' matches the pattern " + "'" +newValue +"'", "pass", true);
        } else if (this.value.toUpperCase().startsWith("CONTAINS=")) {
            newValue = newValue.substring("CONTAINS=".length());
            assertThat(actualValue.trim(), Matchers.containsString(newValue));
            reportAndLog("Actual value '" + actualValue + "' contains the string " + "'" +newValue+"'", "pass", true);
        } else if (this.value.toUpperCase().startsWith("CASE=")) {
            newValue = newValue.substring("CASE=".length());
            assertThat(actualValue.trim(), Matchers.equalTo(newValue));
            reportAndLog("Actual value '" + actualValue + "' equals to the case sensitive string " + "'" +newValue +"'", "pass", true);
        } else if (this.value.toUpperCase().contains("STARTS-WITH=")) {
            newValue = newValue.substring("STARTS-WITH=".length());
            assertThat(actualValue.trim(), Matchers.startsWith(newValue));
            reportAndLog("Actual value '" + actualValue + "' starts with case sensitive string " + "'" +newValue +"'", "pass", true);
        }else {
            assertThat(actualValue.trim(), Matchers.equalToIgnoringWhiteSpace(this.value));
            reportAndLog("Actual value '" + actualValue + "' equals to the case insensitive string " + "'" +newValue +"'", "pass", true);
        }
    }
}