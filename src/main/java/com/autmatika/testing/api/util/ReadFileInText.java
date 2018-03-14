package com.autmatika.testing.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ReadFileInText reads the content of the file with text stored in resources folder
 *
 */
public class ReadFileInText {

    public static String readFile(String fileName){

        InputStream in = ReadFileInText.class.getResourceAsStream("/" + fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String currentLine = "";
        String allText = "";
        try {
            while ((currentLine = reader.readLine()) != null) {
                allText += currentLine;
            }

            return allText;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
