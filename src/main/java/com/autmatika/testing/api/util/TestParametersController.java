package com.autmatika.testing.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * <p>This class provides an ability to check whether the passed from excel sheet parameter is
 * a keyword, a metadata or an execution context key and convert such appropriately.</p>
 * <br>NOTES:
 * <br>Keyword:
 * <pre>
 *     - acceptable single keyword format is <b>KW_AUTO_keyword name|modifier if needed</b>.Keyword name should be a word in uppercase.
 *     - each keyword may be combined with pre-pending and/or post-pending text. Format: <b>pre-pending text &lt;KEYWORD&gt; post-pending text</b>.
 *     Pre-pending and post-pending texts are optional and may contain <i>upper- and lowercase letters, digits, '_', '-', '@', '.'</i>
 *     - each input value will be considered as a keyword if it contains 'KW_'.
 *     - input value may contain several keywords.
 * </pre>
 *
 *  Metadata:
 * <pre>
 *     - acceptable metadata format is <b>MD_FILE_SHEET_KEY</b>. File name, sheet and key should be a word in uppercase.
 *     - each input value will be considered as a metadata key if it starts with 'MD_'.
 * </pre>
 *
 *  Execution Context:
 * <pre>
 *     - acceptable execution context format is <b>EC_part1..._partN</b>. Execution context key should contain 'EC' prefix and any number of words in uppercase separated by '_'.
 *     - each input value will be considered as an execution context key if it starts with 'EC_'.
 * </pre>
 *
 */
public class TestParametersController {
    //private static final Logger log = Logger.getLogger(TestParametersController.class);

    private static final Pattern ACCEPTABLE_METADATA_PATTERN = Pattern.compile("^MD_([A-Z]+_){2}[A-Z]+$");
    private static final Pattern ACCEPTABLE_KEYWORD_PATTERN = Pattern.compile("^([A-Za-z\\d-_.]*<?KW_AUTO_[A-Z]+(\\b|[|])[~:)(/A-Za-z._#?-]*>?[/A-Za-z\\d@._-]*)+$");
    private static final Pattern ACCEPTABLE_EXECUTION_CONTEXT_PATTERN = Pattern.compile("^EC_([A-Z]+_*)+[A-Z]+$");
    private static final Pattern GENERAL_METADATA_PATTERN = Pattern.compile("^MD_.+$");
    private static final Pattern GENERAL_KEYWORD_PATTERN = Pattern.compile("^.*KW_.+$");
    private static final Pattern GENERAL_EXECUTION_CONTEXT_PATTERN = Pattern.compile("^EC_.+$");

    private static final String KEYWORD_NAME_PREFIX = "KW_AUTO_";
    private static final String KEYWORD_NAME_TO_SKIP = "KW_AUTO_SELECT";


    /**
     * The method checks whether passed parameter is any of keyword, metadata or executions context key.
     *
     * @param parameter parameter that comes from excel sheet
     * @return converted value by provided parameter. Otherwise returns the unmodified parameter that was passed from excel sheet.
     */
    public static String checkIfSpecialParameter(String parameter) {
        if (isMetaData(parameter)) {
            return MetaDataHandler.getMetaDataValue(parameter);
        } else if (isKeyword(parameter)) {
            if (parameter.equals(KEYWORD_NAME_TO_SKIP)){
                return parameter;
            }
            String[] splitArray = parameter.split("[<>]");
            StringBuilder resultingValue = new StringBuilder();
            for (String element : splitArray) {
                if (element.startsWith(KEYWORD_NAME_PREFIX)) {
                    resultingValue.append(KeywordsHandler.getValueByKeyword(element.substring(3)));
                } else {
                    resultingValue.append(element);
                }
            }
            return resultingValue.toString();
        } else if (isExecutionContextKey(parameter)) {
            return ExecutionContextHandler.getExecutionContextValueByKey(parameter);
        } else {
            return parameter;
        }
    }


    /**
     * The method performs two-levels verification of passed value to be a metadata key.
     * First level checkup is based on determining whether the passed value starts with 'MD_' prefix.
     * If the result is true, then on the second level it is possible to check whether the passed parameter is well-formatted and acceptable to be handled further.
     *
     * @param value value that needs to be checked
     * @return true if the parameter is acceptable metadata
     */
    private static boolean isMetaData(String value){
        Matcher acceptableMetaDataMatcher = ACCEPTABLE_METADATA_PATTERN.matcher(value);
        Matcher possibleMetaDataMatcher = GENERAL_METADATA_PATTERN.matcher(value);
        if(possibleMetaDataMatcher.matches()){
            if (acceptableMetaDataMatcher.matches()){
                return true;
            }
            else {
                String messageToLog = "\n\t\tAcceptable metadata format is MD_FILE_SHEET_KEY.\n" +
                        "\t\tFile name, sheet and key should be a word in uppercase.";
                String messageToReport = "<pre>Acceptable metadata format is 'MD_FILE_SHEET_KEY'.<br>" +
                        "File name, sheet and key should be a word in uppercase.</pre>";
                LogManager.getLogger().error("Passed \"" + value + "\" parameter looks like a metadata key but it's format is not acceptable." + messageToLog);
                Reporter.log("Passed \"" + value + "\" parameter looks like a metadata key but it's format is not acceptable." + messageToReport);
                return false;
            }
        }
        return false;
    }

    /**
     * The method performs two-levels verification of passed value to be a keyword.
     * First level checkup is based on determining whether the passed value contains the 'KW_'.
     * If the result is true, then on the second level it is possible to check whether the passed parameter is well-formatted and acceptable to be handled further.
     *
     * @param value value that needs to be checked
     * @return true if the parameter is acceptable keyword
     */
    private static boolean isKeyword(String value){
        Matcher acceptableKeywordMatcher = ACCEPTABLE_KEYWORD_PATTERN.matcher(value);
        Matcher possibleKeywordMatcher = GENERAL_KEYWORD_PATTERN.matcher(value);
        if(possibleKeywordMatcher.matches()){
            if (acceptableKeywordMatcher.matches()){
                return true;
            }
            else {
                String messageToLog = "\n\t\tAcceptable single keyword format is 'KW_AUTO_keyword name|modifier if needed'. Keyword name should be a word in uppercase.\n" +
                        "\t\tEach keyword may be combined with pre-pending and/or post-pending text. Format: 'pre-pending text<KEYWORD>post-pending text'.\n" +
                        "\t\tPre-pending and post-pending texts are optional and may contain upper- and lowercase letters, digits, '_', '-', '@', '.'\n" +
                        "\t\tInput value may contain several keywords.";
                String messageToReport = "<pre>Acceptable single keyword format is 'KW_AUTO_keyword name|modifier if needed'. Keyword name should be a word in uppercase.<br>" +
                        "Each keyword may be combined with pre-pending and/or post-pending text. Format: 'pre-pending text&lt;KEYWORD&gt;post-pending text'.<br>" +
                        "Pre-pending and post-pending texts are optional and may contain upper- and lowercase letters, digits, '_', '-', '@', '.'<br>" +
                        "Input value may contain several keywords.</pre>";
                LogManager.getLogger().error("Passed \"" + value + "\" parameter looks like a keyword but it's format is not acceptable." + messageToLog);
                Reporter.log("Passed \"" + value + "\" parameter looks like a keyword but it's format is not acceptable." + messageToReport);
                return false;
            }
        }
        return false;
    }

    /**
     * The method performs two-levels verification of passed value to be an execution context key.
     * First level checkup is based on determining whether the passed value starts with 'EC_' prefix.
     * If the result is true, then on the second level it is possible to check whether the passed parameter is well-formatted and acceptable to be handled further.
     *
     * @param value value that needs to be checked
     * @return true if the parameter is acceptable execution context key
     */
    private static boolean isExecutionContextKey(String value){
        Matcher acceptableExecutionContextMatcher = ACCEPTABLE_EXECUTION_CONTEXT_PATTERN.matcher(value);
        Matcher possibleExecutionContextMatcher = GENERAL_EXECUTION_CONTEXT_PATTERN.matcher(value);
        if(possibleExecutionContextMatcher.matches()){
            if (acceptableExecutionContextMatcher.matches()){
                return true;
            }
            else {
                String messageToLog = "\n\t\tAcceptable execution context format is EC_PART-1..._PART-N.\n" +
                        "\t\tExecution context key should contain 'EC' prefix and any number of words in uppercase separated by '_'.";
                String messageToReport = "<pre>Acceptable execution context format is 'EC_PART-1..._PART-N'.<br>" +
                        "Execution context key should contain 'EC' prefix and any number of words in uppercase separated by '_'.</pre>";
                LogManager.getLogger().error("Passed \"" + value + "\" parameter looks like a execution context key but it's format is not acceptable." + messageToLog);
                Reporter.log("Passed \"" + value + "\" parameter looks like a execution context key but it's format is not acceptable." + messageToReport);
                return false;
            }
        }
        return false;
    }


}