package com.luand.dbchairfix.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class HairFixConfig {
    private static final String FILE_NAME = "dbc_hair_color_fix.properties";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_ONLY_STATE = "only_state";
    private static final String KEY_INCLUDE_SSJ4 = "include_ssj4";
    private static final String KEY_SHADE_DARKEN = "shade_darken";
    private static final String KEY_SHADE_LIGHTEN = "shade_lighten";

    private static volatile boolean loaded;
    public static volatile boolean enabled = true;
    public static volatile int onlyState = -1;
    public static volatile boolean includeSsj4 = true;
    public static volatile float shadeDarken = 0.35f;
    public static volatile float shadeLighten = 0.15f;

    private HairFixConfig() {
    }

    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (HairFixConfig.class) {
            if (loaded) {
                return;
            }
            loadInternal();
            loaded = true;
        }
    }

    private static void loadInternal() {
        File cfgDir = new File(resolveMinecraftHome(), "config");
        if (!cfgDir.exists()) {
            cfgDir.mkdirs();
        }
        File cfgFile = new File(cfgDir, FILE_NAME);

        Properties props = new Properties();
        if (cfgFile.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(cfgFile);
                props.load(in);
            } catch (IOException ignored) {
                // Keep defaults
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        } else {
            props.setProperty(KEY_ENABLED, "true");
            props.setProperty(KEY_ONLY_STATE, "-1");
            props.setProperty(KEY_INCLUDE_SSJ4, "true");
            props.setProperty(KEY_SHADE_DARKEN, "0.35");
            props.setProperty(KEY_SHADE_LIGHTEN, "0.15");
            saveProps(cfgFile, props);
        }

        enabled = parseBoolean(props.getProperty(KEY_ENABLED), true);
        onlyState = parseInt(props.getProperty(KEY_ONLY_STATE), -1);
        includeSsj4 = parseBoolean(props.getProperty(KEY_INCLUDE_SSJ4), true);
        shadeDarken = clamp(parseFloat(props.getProperty(KEY_SHADE_DARKEN), 0.35f), 0.0f, 1.0f);
        shadeLighten = clamp(parseFloat(props.getProperty(KEY_SHADE_LIGHTEN), 0.15f), 0.0f, 1.0f);
    }

    private static void saveProps(File file, Properties props) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            props.store(out, "DBC Hair Color Fix");
        } catch (IOException ignored) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static File resolveMinecraftHome() {
        try {
            Class<?> launchClass = Class.forName("net.minecraft.launchwrapper.Launch");
            Object value = launchClass.getField("minecraftHome").get(null);
            if (value instanceof File) {
                return (File) value;
            }
        } catch (Throwable ignored) {
        }
        return new File(".");
    }

    private static boolean parseBoolean(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        String v = value.trim();
        if ("true".equalsIgnoreCase(v)) {
            return true;
        }
        if ("false".equalsIgnoreCase(v)) {
            return false;
        }
        return fallback;
    }

    private static int parseInt(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static float parseFloat(String value, float fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }
}
