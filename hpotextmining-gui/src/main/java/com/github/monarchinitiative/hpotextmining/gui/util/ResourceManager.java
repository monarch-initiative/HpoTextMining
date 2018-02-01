package com.github.monarchinitiative.hpotextmining.gui.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class ResourceManager {

    public static final String APP_SETTINGS_FILENAME = "AppSettings.json";

    public static final String APP_SETTINGS_DIRNAME = ".hpo-text-mining";

    private static final Logger LOGGER = LogManager.getLogger();

    private final File appDir;


    public ResourceManager() {
        this(resolveAppDir());
    }


    public ResourceManager(File appDir) {
        LOGGER.info("Creating ResourceManager");
        this.appDir = appDir;
        createDirIfNotExists(appDir);
    }


    public File getAppDir() {
        return appDir;
    }


    private static void createDirIfNotExists(File path) {
        if (path.exists()) {
            if (path.isFile()) {
                LOGGER.warn("Refusing to create directory '{}' since it exists and it is a file", path);
            }
        } else {
            if (!path.mkdirs()) {
                LOGGER.warn("Unable to create directory {}", path);
            }
        }
    }


    /**
     * Get {@link CurrentPlatform} representing platform on which the code is running at the moment.
     *
     * @param osName String with content of <code>os.name</code> system property
     * @return {@link CurrentPlatform} representing platform on which we're running
     */
    public static CurrentPlatform figureOutPlatform(final String osName) {
        String lower = osName.toLowerCase();
        if (lower.contains("nix") || lower.contains("nux") || lower.contains("aix")) {
            return CurrentPlatform.LINUX;
        } else if (lower.contains("win")) {
            return CurrentPlatform.WINDOWS;
        } else if (lower.contains("mac")) {
            return CurrentPlatform.OSX;
        } else {
            return CurrentPlatform.UNKNOWN;
        }
    }


    /**
     * Get directory with the app resources (Jannovar cache, app setup, etc..)
     *
     * @return {@link Optional} with
     */
    public static File resolveAppDir() {
        final String osName = System.getProperty("os.name");
        final CurrentPlatform platform = figureOutPlatform(osName);
        final File userHome = getUserHomeDir();

        switch (platform) {
            case LINUX:
            case OSX:
                return new File(userHome, APP_SETTINGS_DIRNAME);
            case WINDOWS:
            case UNKNOWN:
                return new File(userHome, APP_SETTINGS_DIRNAME);
            default:
                throw new RuntimeException("Shouldn't get here!");

        }
    }


    public static File getUserHomeDir() {
        return new File(System.getProperty("user.home"));
    }
}
