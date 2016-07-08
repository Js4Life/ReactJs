package com.parabole.cecl.application.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.parabole.cecl.application.exceptions.AppErrorCode;
import com.parabole.cecl.application.exceptions.AppException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;

/**
 * Generic Utilities.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class AppUtils {

    public static final Config CFG = ConfigFactory.load();
    private static final SimpleDateFormat lastLoginDateFormat = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss z");

    public static String getApplicationProperty(final String key) {
        Validate.notBlank(key, "'key' cannot be null!");
        return CFG.getString(key);
    }

    public static Integer getApplicationPropertyAsInteger(final String key) {
        Validate.notBlank(key, "'key' cannot be null!");
        return Integer.parseInt(CFG.getString(key));
    }

    public static Boolean getApplicationPropertyAsBoolean(final String key) {
        Validate.notBlank(key, "'key' cannot be null!");
        return Boolean.valueOf(CFG.getString(key));
    }

    public static InputStream getClasspathFileInputStream(final String fileName) throws AppException {
        try {
            Validate.notBlank(fileName, "'fileName' cannot be null!");
            final URL url = Resources.getResource(fileName);
            final ByteSource byteSource = Resources.asByteSource(url);
            return byteSource.openBufferedStream();
        } catch (final IOException ioEx) {
            Logger.error("Could not find File: " + fileName + " in classpath", ioEx);
            throw new AppException(AppErrorCode.SYSTEM_EXCEPTION);
        }
    }

    public static Properties loadProperties(final String propertiesFileName) throws AppException {
        Validate.notBlank(propertiesFileName, "'propertiesFileName' cannot be null!");
        final Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = getClasspathFileInputStream(propertiesFileName);
            properties.load(inputStream);
        } catch (final IOException ioEx) {
            Logger.error("Could not initialize Properties File: " + propertiesFileName, ioEx);
            throw new AppException(AppErrorCode.SYSTEM_EXCEPTION);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return properties;
    }

    public static Map<String, String> createMap(final Properties properties) {
        Validate.notEmpty(properties, "'properties' cannot be null!");
        final Map<String, String> map = new HashMap<String, String>();
        for (final String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    public static String getFileContent(final String fileName) throws AppException {
        try {
            final URL url = Resources.getResource(fileName);
            final ByteSource byteSource = Resources.asByteSource(url);
            final InputStream inputStream = byteSource.openBufferedStream();
            final StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            return writer.toString();
        } catch (final IOException ioEx) {
            Logger.error("File reading error for " + fileName, ioEx);
            throw new AppException(AppErrorCode.FILE_NOT_FOUND, new String[] { fileName });
        }
    }

    public static ByteSource getFileContentAsByte(final String fileName) throws AppException, IOException {
        final URL url = Resources.getResource(fileName);
        final ByteSource byteSource = Resources.asByteSource(url);
        return byteSource;
    }

    public static Set<String> createSetFromApplicationProperty(final String key, final String delimiter) {
        final String inputString = CFG.getString(key);
        final StringTokenizer st = new StringTokenizer(inputString, "|");
        final Set<String> set = new HashSet<String>();
        while (st.hasMoreTokens()) {
            set.add(st.nextToken());
        }
        return Collections.unmodifiableSet(set);
    }

    public static String convertlastLoginDate(final Timestamp timestamp) {
        if (null == timestamp) {
            return StringUtils.EMPTY;
        } else {
            return lastLoginDateFormat.format(timestamp);
        }
    }

    public static boolean compareValue(final String master, final String argument) {
        if (StringUtils.isEmpty(argument)) {
            return false;
        } else if (master.equalsIgnoreCase(argument.trim())) {
            return true;
        }
        return false;
    }
}
