package com.example.autoleave;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class AutoLeaveClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;
    private static AutoLeaveConfig config;
    private static boolean disconnected;

    @Override
    public void onInitializeClient() {
        config = AutoLeaveConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoleave.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.autoleave.main"));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        while (openMenuKey.wasPressed()) {
            client.setScreen(new AutoLeaveScreen(config));
        }

        if (client.player == null || client.getNetworkHandler() == null) {
            disconnected = false;
            return;
        }

        if (!config.isEnabled()) {
            disconnected = false;
            return;
        }

        if (disconnected) {
            return;
        }

        float health = client.player.getHealth() + client.player.getAbsorptionAmount();
        if (health <= config.getHealthThreshold()) {
            disconnected = true;
            client.getNetworkHandler().getConnection().disconnect(
                    Text.literal("[AutoLeave] HP became too low (" + health + " <= " + config.getHealthThreshold() + ")"));
        }
    }
}
