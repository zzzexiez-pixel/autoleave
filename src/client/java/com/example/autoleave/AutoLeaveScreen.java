package com.example.autoleave;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoLeaveScreen extends Screen {
    private final AutoLeaveConfig config;
    private TextFieldWidget healthField;
    private ButtonWidget toggleButton;

    protected AutoLeaveScreen(AutoLeaveConfig config) {
        super(Text.literal("AutoLeave Settings"));
        this.config = config;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.toggleButton = ButtonWidget.builder(getToggleLabel(), button -> {
            this.config.setEnabled(!this.config.isEnabled());
            button.setMessage(getToggleLabel());
            this.config.save();
        }).dimensions(centerX - 100, this.height / 2 - 35, 200, 20).build();
        this.addDrawableChild(this.toggleButton);

        this.healthField = new TextFieldWidget(this.textRenderer, centerX - 100, this.height / 2, 200, 20,
                Text.literal("HP threshold"));
        this.healthField.setText(String.valueOf(this.config.getHealthThreshold()));
        this.healthField.setChangedListener(this::onHealthChanged);
        this.addDrawableChild(this.healthField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(centerX - 100, this.height / 2 + 35, 200, 20).build());
    }

    private Text getToggleLabel() {
        return Text.literal("AutoLeave: " + (this.config.isEnabled() ? "ON" : "OFF"));
    }

    private void onHealthChanged(String input) {
        try {
            float threshold = Float.parseFloat(input);
            this.config.setHealthThreshold(threshold);
            this.config.save();
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60,
                0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Set HP to auto disconnect"),
                this.width / 2, this.height / 2 - 47, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
