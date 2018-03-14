package com.autmatika.testing.api.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 * Created by hemantojha on 12/04/17.
 */
public class WebEventListener implements WebDriverEventListener {

    //private static final Logger logger = Logger.getLogger(WebEventListener.class);

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    public void beforeNavigateTo(String url, WebDriver driver) {
        LogManager.getLogger().debug("Before navigating to: '" + url + "'");
    }

    public void afterNavigateTo(String url, WebDriver driver) {
        LogManager.getLogger().debug("Navigated to:'" + url + "'");
    }

    public void beforeChangeValueOf(WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Earlier Value of the :" + element.toString() + " -> " + element.getText());
    }

    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Later Value of the :" + element.toString() + " changed to -> " + element.getText());
    }

    public void beforeClickOn(WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Trying to click on: " + element.toString());
    }

    public void afterClickOn(WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Clicked on: " + element.toString());
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    public void beforeNavigateBack(WebDriver driver) {
    }

    public void afterNavigateBack(WebDriver driver) {
    }

    public void beforeNavigateForward(WebDriver driver) {
    }

    public void afterNavigateForward(WebDriver driver) {
    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {
    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {
    }

    public void onException(Throwable error, WebDriver driver) {
        LogManager.getLogger().debug("Exception occured: " + error);
    }

    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Trying to find Element By : " + by.toString());
    }

    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        LogManager.getLogger().debug("Found Element By : " + by.toString());
    }

    /*
     * non overridden methods of WebListener class
     */
    public void beforeScript(String script, WebDriver driver) {
    }

    public void afterScript(String script, WebDriver driver) {
    }

}