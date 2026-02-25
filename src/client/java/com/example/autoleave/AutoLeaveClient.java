package com.example.autoleave;

import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

        openMenuKey = KeyBindingHelper.registerKeyBinding(createOpenMenuKeyBinding());

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

    private static KeyBinding createOpenMenuKeyBinding() {
        final String translationKey = "key.autoleave.open_menu";
        final String category = "category.autoleave.main";

        try {
            Constructor<KeyBinding> ctorWithType = KeyBinding.class.getConstructor(
                    String.class,
                    InputUtil.Type.class,
                    int.class,
                    String.class);
            return ctorWithType.newInstance(translationKey, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, category);
        } catch (NoSuchMethodException ignored) {
            // Continue with newer/older constructor shapes for cross-version compatibility.
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create key binding using Type-based constructor", e);
        }

        try {
            Constructor<KeyBinding> ctorLegacy = KeyBinding.class.getConstructor(String.class, int.class, String.class);
            return ctorLegacy.newInstance(translationKey, GLFW.GLFW_KEY_P, category);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unsupported Minecraft key binding constructor signature", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create key binding using legacy constructor", e);
        }
    }
}
