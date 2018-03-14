package com.autmatika.testing.api.util;

import com.autmatika.testing.api.PreProcessFiles;
import datageneration.keywords.KeywordManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>The class is used for handling the keywords passed as test parameters.
 */

public class KeywordsHandler {

    //private static final Logger log = Logger.getLogger(KeywordsHandler.class);

    private static KeywordManager keywordManager = null;
    private static final Path KEYWORDS_FILE_PATH = Paths.get(PreProcessFiles.METADATA_AND_KEYWORDS_FILES_FOLDER_PATH + "/keywords");

    public static void instantiate(){
        try {
            keywordManager = new KeywordManager(KEYWORDS_FILE_PATH);
        } catch (NullPointerException e1) {
            LogManager.getLogger().error("Failed to read keywords file located in the " + KEYWORDS_FILE_PATH.toString(), e1);
            Reporter.fail("Failed to read keywords file located in the " + KEYWORDS_FILE_PATH.toString()
                    + "<br>Please read the log file to get more information");
            throw new NullPointerException();
        }
    }

    public static String getValueByKeyword(String keyword) {
        try {
            if (keywordManager.isKeyword(keyword)) {
                return keywordManager.getKeyword(keyword).convertKeyword();
            } else {
                LogManager.getLogger().warn("Requested " + keyword + " keyword is absent. Please check the keywords and/or test spreadsheets.");
                return keyword;
            }
        } catch (Exception e2) {
            LogManager.getLogger().error("Failed to generate a value by provided keyword: " + keyword
                    + ". The most likely that the path to the needed .dat file is wrong. Or an issue occurred during the .dat file reading", e2);
            Reporter.fail("Failed to generate a value by provided keyword: " + keyword
                    + ". The most likely that the path to the needed .dat file is wrong. Or an issue occurred during the .dat file reading" +
                    "<br>Please read the log file to get more information");
            throw new NullPointerException();
        }
    }
}