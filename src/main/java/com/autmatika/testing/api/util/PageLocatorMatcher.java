package com.autmatika.testing.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Class contains static methods that check whether passed string locator matches any of CSS, XPATH or ID pattern </p>
 */
public class PageLocatorMatcher {

    //TODO improve the patterns if needed
    private static final Pattern XPATH_PATTERN = Pattern.compile("^xpath=.+$");
    private static final Pattern ID_PATTERN = Pattern.compile("^id=.+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^name=.+$");
    private static final Pattern TAGNAME_PATTERN = Pattern.compile("^tagname=.+$");
    private static final Pattern LINK_PATTERN = Pattern.compile("^link=.+$");
    private static final Pattern CLASS_PATTERN = Pattern.compile("^class=.+$");
    private static final Pattern CSS_PATTERN = Pattern.compile("^css=.+$");
    private static final Pattern IMAGES_PATTERN = Pattern.compile("^.*\\.(png|PNG)$");
    private static final Pattern EC_VARIABLE_PATTERN =
            Pattern.compile("^xpath=.+(" +
                    "(((text\\(\\))|\\.)\\s*=\\s*'EC_[A-Z_]+')" +
                    "|(contains\\(((text\\(\\))|\\.),\\s*'EC_[A-Z_]+'\\))" +
                    "|(@value\\s*=\\s*'EC_[A-Z_]+')).+$");

    public static boolean isXpath(String locator){
        Matcher xpathMatcher = XPATH_PATTERN.matcher(locator);
        return xpathMatcher.matches();
    }

    public static  boolean isId(String locator){
        Matcher idMatcher = ID_PATTERN.matcher(locator);
        return idMatcher.matches();
    }

    public static boolean isCss(String locator){
        Matcher cssMatcher = CSS_PATTERN.matcher(locator);
        return cssMatcher.matches();
    }

    public static boolean isName(String locator){
        Matcher nameMatcher = NAME_PATTERN.matcher(locator);
        return nameMatcher.matches();
    }

    public static boolean isTagname(String locator){
        Matcher tagnameMatcher = TAGNAME_PATTERN.matcher(locator);
        return tagnameMatcher.matches();
    }

    public static boolean isLink(String locator){
        Matcher linkMatcher = LINK_PATTERN.matcher(locator);
        return linkMatcher.matches();
    }

    public static boolean isClass(String locator){
        Matcher classMatcher = CLASS_PATTERN.matcher(locator);
        return classMatcher.matches();
    }

    public static boolean isImage(String elementPatternPath){
        Matcher imageMatcher = IMAGES_PATTERN.matcher(elementPatternPath);
        return imageMatcher.matches();
    }

    public static boolean isECVariableInXpath(String locator){
        Matcher ecVariableMatcher = EC_VARIABLE_PATTERN.matcher(locator);
        return ecVariableMatcher.matches();
    }

    /**
     * Action to update Xpath with EC variable during the execution.
     * @param locator:
     *            locator type to be used to locate an element
     */
    public static String updateXpath(String locator){
        Matcher ec = Pattern.compile("EC_([A-Z]+_*)+[A-Z]+").matcher(locator);
        List<String> listMatches = new ArrayList<String>();
        while (ec.find()) {
            listMatches.add(ec.group());
        }
        if(!listMatches.isEmpty()){
            String ecVariableKey = listMatches.get(0);
            String ecVariableValue = ExecutionContextHandler.getExecutionContextValueByKey(ecVariableKey);
            return locator.replaceAll(ecVariableKey, ecVariableValue);
        }
        return null;
    }
}