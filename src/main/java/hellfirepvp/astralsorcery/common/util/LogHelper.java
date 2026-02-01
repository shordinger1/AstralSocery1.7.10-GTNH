/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Logging utility for Astral Sorcery mod
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hellfirepvp.astralsorcery.common.lib.Constants;

/**
 * Helper class for logging with mod-specific prefix
 */
public final class LogHelper {

    private static final Logger LOGGER = LogManager.getLogger(Constants.MODNAME);
    private static final String PREFIX = "[" + Constants.MODNAME + "] ";

    private LogHelper() {} // Prevent instantiation

    /**
     * Log an info message
     */
    public static void info(String message) {
        LOGGER.info(PREFIX + message);
    }

    /**
     * Log an info message with format
     */
    public static void info(String format, Object... args) {
        LOGGER.info(PREFIX + String.format(format, args));
    }

    /**
     * Log a warning message
     */
    public static void warn(String message) {
        LOGGER.warn(PREFIX + message);
    }

    /**
     * Log a warning message with format
     */
    public static void warn(String format, Object... args) {
        LOGGER.warn(PREFIX + String.format(format, args));
    }

    /**
     * Log an error message
     */
    public static void error(String message) {
        LOGGER.error(PREFIX + message);
    }

    /**
     * Log an error message with format
     */
    public static void error(String format, Object... args) {
        LOGGER.error(PREFIX + String.format(format, args));
    }

    /**
     * Log an error message with exception
     */
    public static void error(String message, Throwable throwable) {
        LOGGER.error(PREFIX + message, throwable);
    }

    /**
     * Log a debug message (only if debug mode is enabled)
     */
    public static void debug(String message) {
        if (Constants.IS_DEBUG) {
            LOGGER.debug(PREFIX + "[DEBUG] " + message);
        }
    }

    /**
     * Log a debug message with format (only if debug mode is enabled)
     */
    public static void debug(String format, Object... args) {
        if (Constants.IS_DEBUG) {
            LOGGER.debug(PREFIX + "[DEBUG] " + String.format(format, args));
        }
    }

    /**
     * Log a debug message with exception (only if debug mode is enabled)
     */
    public static void debug(String message, Throwable throwable) {
        if (Constants.IS_DEBUG) {
            LOGGER.debug(PREFIX + "[DEBUG] " + message, throwable);
        }
    }

    /**
     * Log a trace message for development
     */
    public static void trace(String message) {
        if (Constants.IS_DEVMODE) {
            LOGGER.trace(PREFIX + "[TRACE] " + message);
        }
    }

    /**
     * Get the raw logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Log an exception with stack trace
     */
    public static void throwing(String message, Throwable throwable) {
        LOGGER.error(PREFIX + "THROWING: " + message, throwable);
    }

    /**
     * Log method entry (for debug)
     */
    public static void entry(String methodName) {
        debug("ENTER: " + methodName);
    }

    /**
     * Log method exit (for debug)
     */
    public static void exit(String methodName) {
        debug("EXIT: " + methodName);
    }

    /**
     * Log method exit with result (for debug)
     */
    public static void exit(String methodName, Object result) {
        debug("EXIT: " + methodName + " = " + result);
    }
}
