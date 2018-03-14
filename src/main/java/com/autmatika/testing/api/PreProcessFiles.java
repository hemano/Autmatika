package com.autmatika.testing.api;

import com.autmatika.testing.api.util.BeansXmlGenerator;
import com.autmatika.testing.api.util.LogManager;
import com.autmatika.testing.api.util.PropertiesHandler;
import com.autmatika.testing.api.util.PropertiesHelper;

import java.io.IOException;

public class PreProcessFiles {

    public static String TEST_INPUT_FILES_FOLDER_PATH = null;
    public static String METADATA_AND_KEYWORDS_FILES_FOLDER_PATH = null;
    public static String TEST_PROPERTIES_FILES_FOLDER_PATH = null;
    public static String TEST_RESOURCES_FOLDER_PATH = null;
    public static String ROOT_FOLDER_PATH = null;
    public static String TEST_EXECUTION_FILE_NAME = "Test.xlsx";
    public static String TEST_EXECUTION_SHEET_NAME = "Test";
    public static String IMAGES_FOLDER_PATH = null;
    public static String TEST_FILES_FOLDER_PATH = null;

    PropertiesHelper propertiesHelper;

    public PreProcessFiles() {
        this.propertiesHelper = new PropertiesHelper();
    }

    /**
     * The method is used for providing the right paths to the NoahClient's and the Noah's resources folders
     *
     * @param args - argument that come from command line
     */
    private void initPaths(String[] args) {
        String rootFolder = System.getProperty("user.dir").replace("\\", "/");

        String className = this.getClass().getName().replace('.', '/');
        String classJar =
                this.getClass().getResource("/" + className + ".class").toString();

        if (classJar.startsWith("jar:")) {
            TEST_INPUT_FILES_FOLDER_PATH = rootFolder + "/input";
            METADATA_AND_KEYWORDS_FILES_FOLDER_PATH = rootFolder + "/resources/keywords.metadata";
            TEST_PROPERTIES_FILES_FOLDER_PATH = rootFolder + "/resources/test.properties";
            TEST_RESOURCES_FOLDER_PATH = rootFolder + "/resources";
            ROOT_FOLDER_PATH = rootFolder;
            IMAGES_FOLDER_PATH = rootFolder + "/images";
            TEST_FILES_FOLDER_PATH = rootFolder + "/testFilesToUpload";
        }else {
            TEST_INPUT_FILES_FOLDER_PATH = rootFolder + "/src/main/resources/input";
            METADATA_AND_KEYWORDS_FILES_FOLDER_PATH = rootFolder + "/src/main/resources/keywords.metadata";
            TEST_PROPERTIES_FILES_FOLDER_PATH = rootFolder + "/src/main/resources/test.properties";
            TEST_RESOURCES_FOLDER_PATH = rootFolder + "/src/main/resources";
            ROOT_FOLDER_PATH = rootFolder;
            IMAGES_FOLDER_PATH = rootFolder + "/images";
            TEST_FILES_FOLDER_PATH = rootFolder + "/testFilesToUpload";
        }
    }


    /**
     * Determines if arguments have been provided to run tests locally from the
     * project level rather than expectedly the NoahClient. The files are
     * expected to be located locally from the current directory under a folder
     * named, "input." Default values to run the test execution are Test.xlsx
     * and sheet name "Test". The end user is expected to configure their test
     * execution through the batch file in the NoahClient.
     *
     * @param args criteria specified by the end user in the NoahClient project
     * @return returns true if all test configurations are correct and normal test execution can proceed
     * @throws IOException will throw exception ending the test execution if invalid information is available
     */
    public boolean preProcessTestConfiguration(String[] args) throws IOException {

        try {
            initPaths(args);
            PropertiesHandler.gatherProperties();

            boolean generateBeansXmlFile = true;
            if (args.length > 0) {
                if (args.length > 1) {
                    TEST_EXECUTION_FILE_NAME = args[0];
                    TEST_EXECUTION_SHEET_NAME = args[1];
                } else {
                    LogManager.getLogger().warn("You must specify a string for the test xlsx file name such as Test.xlsx and the name of the test sheet");
                    generateBeansXmlFile = false;
                }
            }

            if (generateBeansXmlFile) {
                new BeansXmlGenerator().xmlGenerate(TEST_EXECUTION_FILE_NAME, TEST_EXECUTION_SHEET_NAME);
            } else {
                return false;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}