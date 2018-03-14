package com.autmatika.testing.api.util;

import com.autmatika.GoogleDriveAPI;
import com.autmatika.ReadExcel;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * It reads the Google Sheets and return the table in required format
 *
 * @author hemano
 */
public class ExcelGetDataHelper {
    /**
     * <p>
     * Gets data from excel spreadsheet that determines in top down order which
     * test is run and what business processes belong to the test
     * </p>
     *
     * @param testSpreadsheet test spreadsheet to be read
     * @param testSheet       test sheet from test spreadsheet to be read
     * @return String[][] excel data from test spreadsheet
     * @throws IOException Signals that an I/O exception of some sort has occurred. This
     *                     class is the general class of exceptions produced by failed
     *                     or interrupted I/O operations.
     */
    public String[][] getDataFromExcelTestSheet(String testSpreadsheet, String testSheet) throws IOException {

        String resourceId = determineEffectivePropertyValue("test_spreadsheet_resource_id");
        String key = determineEffectivePropertyValue("google_api_key");


        GoogleDriveAPI googleDriveAPI = new GoogleDriveAPI(key, resourceId);
        ReadExcel<GoogleDriveAPI> readExcel = new ReadExcel<>();

        return readExcel.getExcelDataInStringArray(googleDriveAPI, "UsedRange",Arrays.asList("Info"));
    }

    /**
     * <p>
     * Reads the Excel for Business Process definitions.
     * <p>
     * First row is reserved for headers. Column A: Business Process, Column B:
     * Action, Column C: Logical Name, Column D: Element Location, Column E:
     * University
     * </p>
     *
     * @return String[][] excel business process data
     * @throws IOException ends test execution if incorrect data is read from business
     *                     process xlsx
     */
    public String[][] getDataFromExcelBusinessModuleSheet() throws IOException {

        String resourceId = determineEffectivePropertyValue("business_modules_spreadsheet_resource_id");
        String key = determineEffectivePropertyValue("google_api_key");

        GoogleDriveAPI googleDriveAPI = new GoogleDriveAPI(key, resourceId);
        ReadExcel<GoogleDriveAPI> readExcel = new ReadExcel<>();

        return readExcel.getExcelDataInStringArray(googleDriveAPI, "UsedRange",Arrays.asList("Info"));
    }

    /**
     * @param folder - folder path where properties file exists
     * @return - returns the list off arrays. Each array contains University
     * name, Property name, Descriptor (locator), Parent and Conditional Event
     * @throws IOException failed input output execution
     */
    public List<ArrayList<String>> getDataFromLocatorsSheet(String folder) throws IOException {

        String resourceId = determineEffectivePropertyValue("locators_spreadsheet_resource_id");
        String key = determineEffectivePropertyValue("google_api_key");


        GoogleDriveAPI googleDriveAPI = new GoogleDriveAPI(key, resourceId);
        ReadExcel<GoogleDriveAPI> readExcel = new ReadExcel<>();

        return readExcel.getExcelDataInStringFormat(googleDriveAPI, "Properties", "UsedRange");
    }


    /**
     * @return Map of Sheetname and Tests in them
     * @throws IOException
     */
    public static Map<String, List<String>> getMapOfSheetAndTests() throws Exception {

        Map<String, List<String>> sheetAndTestMap = new LinkedHashMap<>();

        String resourceId = determineEffectivePropertyValue("test_spreadsheet_resource_id");
        String key = determineEffectivePropertyValue("google_api_key");

        GoogleDriveAPI googleDriveAPI = new GoogleDriveAPI(key, resourceId);
        ReadExcel<GoogleDriveAPI> readExcel = new ReadExcel<>();

        Map<String, List<ArrayList<String>>> sheetsDataMap = readExcel.getMapOfSheetsAndData(googleDriveAPI, "UsedRange", Arrays.asList("Info"));

        Map<String, String> flagAndTest = new HashMap<>();

        for (Map.Entry entry : sheetsDataMap.entrySet()) {

            List<ArrayList<String>> rowsInASheet = (ArrayList) entry.getValue();

            flagAndTest = new HashMap<>();
            for (ArrayList<String> row : rowsInASheet) {

                if (!row.get(1).trim().equals(""))
                    flagAndTest.put(row.get(1), row.get(0));
            }

            ArrayList<Map.Entry> list = (ArrayList) flagAndTest.entrySet().stream().filter(r -> r.getValue().equals("Y")).collect(Collectors.toList());
            List<String> listOfTests = list.stream().map(Map.Entry::getKey).collect(Collectors.toList()).stream().map(Object::toString).collect(Collectors.toList());

            sheetAndTestMap.put(entry.getKey().toString(), listOfTests);
        }

        return sheetAndTestMap;
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
}
