package org.rients.com.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {

    public static Properties getProperties(String fileName) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props;
    }

    
    public static void saveProperties(String file, Map<String, String> fundProperties) {
        // load first the existing, then add the new values
        Properties properties = getPropertiesFromClasspath(file);
        Set<String> set = fundProperties.keySet();
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = fundProperties.get(key);
            properties.setProperty(key, value);
        }
        try {
            String path = properties.get("path").toString();
            properties.remove("path");
            properties.store(new FileOutputStream(path), "Java properties test");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    public static void saveProperties(String file, Properties properties) {
        try {
            String path = properties.get("path").toString();
            properties.remove("path");
            properties.store(new FileOutputStream(path), "Java properties test");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /** Constructor. */
    public static Properties getPropertiesFromClasspath(String filename) {
        Properties properties = new Properties();
        try {
            URL url = PropertiesUtils.class.getResource("/" + filename);
            String path = "";
            if (url != null)  {
                
                path = url.getPath();
                properties.load(PropertiesUtils.class.getResourceAsStream("/" + filename));
                properties.setProperty("path", path);
            } else {
            	path = "E:/trading/fund_properties/" + filename;
            	properties.setProperty("path", path);
            }
        } catch (IOException e) {
            System.err.println("Unable to load " + filename + ".");
        }
        return properties;
    }
    
    public static boolean hasKey(Properties props, String key) {
        Enumeration<?> e = props.keys();
        key = replace(key);
        while (e.hasMoreElements()) {
            String keyFromP = (String) e.nextElement();
            if (key.equals(keyFromP) ) {
                return true;
            }
        }
        return false;
    }


    public static String getValue(Properties props, String key) {
        String value = "";
        key = replace(key);
        Enumeration<?> e = props.keys();
        while (e.hasMoreElements()) {
            String keyFromP = (String) e.nextElement();
            if (key.equals(keyFromP) ) {
                String valueTemp = (String) props.get(keyFromP);
                value = valueTemp.substring(valueTemp.indexOf("=") + 1).trim();
                break;
            }
        }
        return value;
    }
    
    private static String replace(String input) {
        return input.replaceAll(" ", "_");
    }
}
