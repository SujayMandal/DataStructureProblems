package com.ca.framework.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.security.PasswordDecryptionUtil;

public class CommonUtils {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	
    public static Object[][] getResultSet(List<Object[]> list) {
        return list.toArray(new Object[list.size()][]);
    }

    public static List<Object[]> readCSVData(String location) {
        BufferedReader br = null;
        String line = "";
        List<Object[]> list = new ArrayList<Object[]>();
        try {
            br = new BufferedReader(new FileReader(new File(location)));
            while ((line = br.readLine()) != null) {
                Object[] s = line.split(",");
                list.add(s);
            }
        } catch (FileNotFoundException e) {
        	LOGGER.error("FileNotFoundException: {}",e);
        } catch (IOException e) {
        	LOGGER.error("IOException: {}",e);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                	LOGGER.error("IOException: {}",e);
                }
        }
        return list;
    }
}
