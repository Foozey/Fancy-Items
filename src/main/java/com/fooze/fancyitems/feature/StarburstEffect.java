package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.joml.Matrix4f;

public class StarburstEffect {
    private static final int RAY_COUNT = 9; // Number of rays
    private static final int RAY_COUNT_LENGTH = 3; // Number of rays to add length variation to before repeating
    private static final int RAY_COUNT_WIDTH = 2; // Number of rays to add width variation to before repeating
    private static final float RAY_LENGTH = 0.5F; // Length of rays before variation and pulsing
    private static final float RAY_WIDTH = 0.25F; // Width of rays before variation and pulsing
    private static final float LENGTH_VARIATION = 0.1F; // Amount of variation in ray length
    private static final float WIDTH_VARIATION = 0.05F; // Amount of variation in ray width
    private static final float PULSE_LENGTH_MIN = 0.9F; // Minimum ray length during pulsing
    private static final float PULSE_LENGTH_MAX = 1.1F; // Maximum ray length during pulsing
    private static final float PULSE_WIDTH_MIN = 0.33F; // Minimum ray width during pulsing
    private static final float PULSE_WIDTH_MAX = 1.5F; // Maximum ray width during pulsing
    private static final float PULSE_SPEED = 0.075F; // Speed of the pulsing animation
    private static final float ROTATION_SPEED = 0.025F; // Speed of the rotation
    private static final float ANIMATION_OFFSET = 0.1F; // Offset to desynchronize each item

    private StarburstEffect() {
    }

    // Renders a starburst effect behind an inventory item
    public static void render(GuiGraphics graphics, ItemStack stack, int seed) {
        Minecraft minecraft = Minecraft.getInstance();

        // Don't render if the config option is disabled, there's no item, or the screen is invalid
        if (!Config.ENABLE_STARBURST.get() || stack.isEmpty() || minecraft.screen != null
                && !(minecraft.screen instanceof AbstractContainerScreen<?>)) {
            return;
        }

        // Calculate the phase of the animation (0.0 to 1.0)
        float time = getTime(minecraft);
        float phase = ((float) Math.sin(time * PULSE_SPEED + seed * ANIMATION_OFFSET) + 1.0F) / 2.0F;

        // Calculate the pulse length and width based on the phase of the animation
        float pulseLength = PULSE_LENGTH_MIN + (PULSE_LENGTH_MAX - PULSE_LENGTH_MIN) * phase;
        float pulseWidth = PULSE_WIDTH_MIN + (PULSE_WIDTH_MAX - PULSE_WIDTH_MIN) * phase;

        // Set the color to the item's name or rarity
        Integer color = getColor(stack);

        // Only render if there's a color
        if (color == null) {
            return;
        }

        // Draw rays with the dragon ray render type
        VertexConsumer rays = graphics.bufferSource().getBuffer(RenderType.dragonRays());

        // Draw rays in a rotating circle with varying length and width
        for (int ray = 0; ray < RAY_COUNT; ray++) {
            float angle = (float) (ray * (Math.PI * 2.0D / RAY_COUNT) + time * ROTATION_SPEED);
            float length = pulseLength * (RAY_LENGTH + (ray % RAY_COUNT_LENGTH) * LENGTH_VARIATION);
            float width = pulseWidth * (RAY_WIDTH + (ray % RAY_COUNT_WIDTH) * WIDTH_VARIATION);

            graphics.pose().pushPose();
            graphics.pose().mulPose(Axis.ZP.rotation(angle));
            addRay(rays, graphics.pose().last().pose(), length, width, color);
            graphics.pose().popPose();
        }
    }

    // Gets the time used for the animation
    private static float getTime(Minecraft minecraft) {
        // If a player exists, align the animation with the current tick and partial tick timing
        if (minecraft.player != null) {
            return minecraft.player.tickCount + minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        }

        // Continue animating when no player exists, such as menus
        return System.currentTimeMillis() / 50.0F;
    }

    // Draws a triangular ray
    private static void addRay(VertexConsumer vertices, Matrix4f transform, float length, float width, int color) {
        // Split RGB color into vertex color channels
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        // Add a triangle that fades outward from the center
        vertices.addVertex(transform, 0.0F, 0.0F, 0.0F).setColor(red, green, blue, 255);
        vertices.addVertex(transform, width, length, 0.0F).setColor(red, green, blue, 0);
        vertices.addVertex(transform, -width, length, 0.0F).setColor(red, green, blue, 0);
    }

    // Gets the color of the starburst based on the item's name or rarity
    private static Integer getColor(ItemStack stack) {
        Integer nameColor = findColor(stack.getHoverName());

        // Use the item's name color if it's not white
        if (nameColor != null && !nameColor.equals(ChatFormatting.WHITE.getColor())) {
            return nameColor;
        }

        // Don't render the starburst for common items
        if (stack.getRarity() == Rarity.COMMON) {
            return null;
        }

        // Use the item's rarity color if the name color is white
        return stack.getRarity().color().getColor();
    }

    // Finds the color of a component's text
    private static Integer findColor(Component component) {
        Style style = component.getStyle();
        TextColor color = style.getColor();

        // Check if the component has a color
        if (color != null) {
            return color.getValue();
        }

        // Check if the component has any children that have a color
        for (Component child : component.getSiblings()) {
            Integer childColor = findColor(child);

            if (childColor != null) {
                return childColor;
            }
        }

        // If no color is found, return null
        return null;
    }
}