package com.autmatika.testing.api.util;

import com.autmatika.testing.api.PreProcessFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * The class is used for handling the properties passed as test parameters. Each
 * properties may be related to several context
 * </p>
 */
public class PropertiesHandler {

	private static final String PROPERTIES_FOLDER = PreProcessFiles.TEST_PROPERTIES_FILES_FOLDER_PATH;

	//Next map is needed to store the web-elements' locator ('xpath', 'css-selector' etc.)
	private static Map<String, Map<String, String>> properties = new TreeMap<String, Map<String, String>>();

	//Next map is needed to store the frames that may contain any particular web-element the framework needs to deal with.
	private static Map<String, Map<String, String>> ancestors = new TreeMap<String, Map<String, String>>();

	//Next map is needed to store the conditional event that needs to be handled when interacting with any particular web-element.
	private static Map<String, Map<String, String>> conditionalEvents = new TreeMap<String, Map<String, String>>();

	/**
	 * <p>
	 * method needed to read the excel file and:
	 * 	- get web-elements' locator and store them in static variable called 'properties'.
	 * 	- get frames that may contain web-elements and store them in static variable called 'ancestors'.
	 * 	- get conditional event that need to be executed after interacting with any particular web-element and store them in
	 * 	static variable called 'conditionalEvents'.
	 * This should be called before test
	 * execution.
	 * 
	 * @throws IOException
	 *             when properties file is not found.
	 * @throws NullPointerException
	 *             when failed to reade excel sheet
	 *             </p>
	 *
	 */
	public static void gatherProperties() throws IOException, NullPointerException {

		ExcelGetDataHelper excelGetDataHelper = new ExcelGetDataHelper();

		try {
			List<ArrayList<String>> propertiesList = excelGetDataHelper.getDataFromLocatorsSheet(PROPERTIES_FOLDER);
			int contextIndex = 0;
			int logicalNameIndex = 2;
			int parentIndex = 3;
			int descriptorIndex = 5;

			for (ArrayList<String> list: propertiesList) {
				if(properties.containsKey(list.get(contextIndex))){

					properties.get(list.get(contextIndex))
							.put(list.get(logicalNameIndex),list.get(descriptorIndex));
					ancestors.get(list.get(contextIndex))
							.put(list.get(logicalNameIndex),list.get(parentIndex));

				}else {
					Map<String, String> tempPropMap = new TreeMap<>();
					Map<String, String> tempParentMap = new TreeMap<>();

					tempPropMap.put(
							list.get(logicalNameIndex), list.get(descriptorIndex));
					tempParentMap.put(
							list.get(logicalNameIndex), list.get(parentIndex));

					properties.put(list.get(contextIndex),
							tempPropMap);
					ancestors.put(list.get(contextIndex),
							tempParentMap);
				}
			}
		} catch (IOException e1) {
			LogManager.getLogger().error("Failed to read properties file located in the " + PROPERTIES_FOLDER.toString(), e1);
			throw new IOException();
		} catch (NullPointerException e2) {
			LogManager.getLogger().error("Failed to read sheet from properties file located in the " + PROPERTIES_FOLDER.toString(), e2);
			throw new NullPointerException();
		} catch (Exception e3) {
			LogManager.getLogger().error("Failed to gather properties", e3);
			throw new NullPointerException();
		}
	}

	/**
	 *
	 * <p>
	 * 
	 * @param context
	 *            - indicates the context that seeking property is related to
	 * @param key
	 *            seeking property's name
	 * @return returns the descriptor (locator) by provided combination
	 *         context-Key. If such not found, tries to find the Key in the
	 *         Base list.
	 *         </p>
	 * 
	 */
	public static String getPropertyByContextAndKey(String context, String key) {
		if (properties.containsKey(context) && properties.get(context).containsKey(key)) {
			return properties.get(context).get(key);
		} else if (properties.containsKey("Base") && properties.get("Base").containsKey(key)) {
			return properties.get("Base").get(key);
		} else {
			LogManager.getLogger().warn("Requested " + key + " property was found neither in the " + context
					+ " nor in the Base related properties list.\n\t\tPossible reasons are:\n"
					+ "\t\t- the property is missed in properties file provided into the "
					+ PreProcessFiles.TEST_PROPERTIES_FILES_FOLDER_PATH + "/ folder path\n"
					+ "\t\t- the requested property is misspelled in the excel spreadsheet provided into the "
					+ PreProcessFiles.TEST_INPUT_FILES_FOLDER_PATH + "/ folder path.");
			return key;
		}
	}

	/**
	 *
	 * <p>
	 *
	 * @param context
	 *            - indicates the context that seeking property is related to
	 * @param key
	 *            - seeking for element ancestor
	 * @return returns the ancestor by provided combination
	 *         context-Key. If such not found, tries to find the Key in the
	 *         Base list. Else returns blank value as ancestor wasn't found
	 *         </p>
	 *
	 */
	public static String getParentByContextAndKey(String context, String key) {
		if (ancestors.containsKey(context) && ancestors.get(context).containsKey(key)) {
			return ancestors.get(context).get(key);
		} else if (ancestors.containsKey("Base") && ancestors.get("Base").containsKey(key)) {
			return ancestors.get("Base").get(key);
		} else {
			LogManager.getLogger().warn("Requested " + key + " property was found neither in the " + context
					+ " nor in the Base related properties list.\n\t\tPossible reasons are:\n"
					+ "\t\t- the property is missed in properties file provided into the "
					+ PreProcessFiles.TEST_PROPERTIES_FILES_FOLDER_PATH + "/ folder path\n"
					+ "\t\t- the requested property is misspelled in the excel spreadsheet provided into the "
					+ PreProcessFiles.TEST_INPUT_FILES_FOLDER_PATH + "/ folder path.");
			return "";
		}
	}

}
