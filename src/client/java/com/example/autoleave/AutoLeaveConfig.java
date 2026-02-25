package com.example.autoleave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public final class AutoLeaveConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("autoleave.json");

    private boolean enabled = true;
    private float healthThreshold = 6.0F;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getHealthThreshold() {
        return healthThreshold;
    }

    public void setHealthThreshold(float healthThreshold) {
        this.healthThreshold = Math.max(1.0F, Math.min(20.0F, healthThreshold));
    }

    public static AutoLeaveConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                AutoLeaveConfig config = GSON.fromJson(Files.readString(CONFIG_PATH), AutoLeaveConfig.class);
                if (config != null) {
                    config.setHealthThreshold(config.getHealthThreshold());
                    return config;
                }
            }
        } catch (IOException ignored) {
        }

        AutoLeaveConfig config = new AutoLeaveConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            AutoLeaveMod.LOGGER.warn("Failed to save config", e);
        }
    }
}
