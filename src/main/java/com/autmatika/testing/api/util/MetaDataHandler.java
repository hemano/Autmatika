package com.autmatika.testing.api.util;

import com.autmatika.testing.api.PreProcessFiles;
import datageneration.metadata.MetaData;
import datageneration.metadata.MetaDataManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * <p>The class is used for handling the metadata passed as test parameters.
 * Note: The class handles two scenarios when metadata key cannot be used:
 * - when metadata key is misspelled in the excel file where the tests are defined;
 * - when metadata file(s) is(are) missing required data;</p>
 */
public class MetaDataHandler {

    //private static final Logger log = Logger.getLogger(MetaDataHandler.class);

    private static MetaDataManager metaDataManager = null;
    private static final Path METADATA_FILE_PATH = Paths.get(PreProcessFiles.METADATA_AND_KEYWORDS_FILES_FOLDER_PATH + "/metadata");
    private static final String PREFIX = "MD_";
    private static Set<String> metadataKeysSet = null;

    public static void instantiate(){
        try {
            metaDataManager = new MetaDataManager(METADATA_FILE_PATH, PREFIX);
        } catch (NullPointerException e) {
            LogManager.getLogger().error("Failed to read metadata file located in the " + METADATA_FILE_PATH.toString(), e);
            Reporter.fail("Failed to read metadata file located in the " + METADATA_FILE_PATH.toString() + "<br>Please read the log file to get more information");
            throw new NullPointerException("Failed to read metadata file.");
        }
    }

    /**
     *
     * @param metadataKey - the metadata name that comes from test excel sheet
     * @return the value that was got by metadata name of such metadata exists. Otherwise it returns the same metadata name back
     */
    public static String getMetaDataValue(String metadataKey){
        if ((metadataKeyExists(metadataKey))) {
            return metaDataManager.getMetaData(metadataKey).getValue();
        } else {
            LogManager.getLogger().error("Requested " + metadataKey + " metadata key is absent.\n\t\tPossible reasons are:\n" +
                    "\t\t- the requested key is misspelled in the excel file where the tests are defined. File location: " + PreProcessFiles.TEST_INPUT_FILES_FOLDER_PATH +
                    "\n\t\t- metadata excel file(s) located in the " + METADATA_FILE_PATH.toString() +
                    " folder is(are) missing required data. Tag, Value and Description should be provided for each row.\n" +
                    "\t\tPlease check the excel spreadsheets.\n\t\tCannot proceed with the test execution.");
            Reporter.warn("Requested " + metadataKey + " metadata key is absent.<pre>Possible reasons are:<br>" +
                    "- the requested key is misspelled in the excel file where the tests are defined. File location: " + PreProcessFiles.TEST_INPUT_FILES_FOLDER_PATH + "<br>" +
                    "- metadata excel file(s) located in the " + METADATA_FILE_PATH.toString() +
                    " folder is(are) missing required data. Tag, Value and Description should be provided for each row.<br>" +
                    "Please check the excel spreadsheets.<br>Cannot proceed with the test execution.</pre>");
            throw new NullPointerException("Cannot retrieve the metadata.");
        }
    }

    /**
     * The method is used for checking whether the requested metadata key exists in the metadata dictionary
     *
     * @param metadataKey - requested key that needs to be checked
     * @return true if the key exists and false if does not
     */
    private static boolean metadataKeyExists(String metadataKey) {
        if (metadataKeysSet == null) {
            metadataKeysSet = new TreeSet<String>();
            String combinedKey;
            for (MetaData metadata : metaDataManager.getMetaDictionary()) {
                combinedKey = PREFIX + metadata.getFileName().split("_")[0].toUpperCase() + "_"
                        + metadata.getSheet().toUpperCase() + "_"
                        + metadata.getTag().split("_")[1].toUpperCase();
                metadataKeysSet.add(combinedKey);
            }
        }
        return metadataKeysSet.contains(metadataKey);
    }
}