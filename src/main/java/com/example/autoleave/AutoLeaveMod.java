package com.example.autoleave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class AutoLeaveMod implements ModInitializer {
    public static final String MOD_ID = "autoleave";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AutoLeave initialized");
    }
}
