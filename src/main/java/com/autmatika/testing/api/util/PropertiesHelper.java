package com.autmatika.testing.api.util;

import com.autmatika.testing.api.PreProcessFiles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>This function uses a Java util, ResourceBundle, to identify file name located in src.
 * This returns a String value identified by the key in the property file </p>
 * 
 */
public class PropertiesHelper {

	Properties properties;

	public Properties getProperties() {

		properties = new Properties();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(PreProcessFiles.TEST_RESOURCES_FOLDER_PATH + "/configuration.properties");
			properties.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}


	/**
	 * <p>It returns the property value specified in either environment variable or configuration.properties
	 * It gives priority to the property specified in Java environment variable For e.g. -Ddriver_id=FIREFOX
	 * @param key used to search for property
	 * @return </p>
	 */
	public static String determineEffectivePropertyValue(String key) {

		PropertiesHelper propertiesHelper = new PropertiesHelper();

		if (null != System.getProperty(key)) {
			return System.getProperty(key);
		} else {
			return propertiesHelper.getProperties().getProperty(key);
		}
	}
}
