package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.util.Color;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class StarburstEffect {
    // Renders a starburst effect behind inventory items
    public static void render(GuiGraphics transform, ItemStack stack, int seed) {
        // Get the instance and screen
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        // Don't render if the effect is disabled, there's no item, or the screen is invalid
        if (!Config.ENABLE_STARBURST.get() || stack.isEmpty() || screen != null
                && !(screen instanceof AbstractContainerScreen<?>)) {
            return;
        }

        // Get the ray properties
        int rayCount = Config.STARBURST_RAY_COUNT.get();
        float rayLength = Config.STARBURST_RAY_LENGTH.get().floatValue();
        float rayWidth = Config.STARBURST_RAY_WIDTH.get().floatValue();
        float lengthVariation = Config.STARBURST_LENGTH_VARIATION.get().floatValue();
        int lengthVariationCount = Math.max(Config.STARBURST_LENGTH_VARIATION_COUNT.get(), 1);
        float widthVariation = Config.STARBURST_WIDTH_VARIATION.get().floatValue();
        int widthVariationCount = Math.max(Config.STARBURST_WIDTH_VARIATION_COUNT.get(), 1);

        // Calculate the animation values
        int pulseDuration = Config.STARBURST_PULSE_DURATION.get();
        int rotationDuration = Config.STARBURST_ROTATION_DURATION.get();
        float phase = 0.0F;
        float time = getTime(minecraft);
        int offset = Math.floorMod(seed + stack.hashCode(), Math.max(Math.max(pulseDuration, rotationDuration), 1));
        float pulseLength = 1.0F;
        float pulseWidth = 1.0F;

        // Only calculate the animation phase if there's a pulse duration
        if (pulseDuration > 0) {
            phase = ((float) Math.sin((time + offset) * Math.PI * 2.0F / pulseDuration) + 1.0F) / 2.0F;
        }

        // Only calculate the pulse length and width if there's a pulse duration
        if (pulseDuration > 0) {
            float pulseLengthMin = Config.STARBURST_PULSE_LENGTH_MIN.get().floatValue();
            float pulseLengthMax = Config.STARBURST_PULSE_LENGTH_MAX.get().floatValue();
            float pulseWidthMin = Config.STARBURST_PULSE_WIDTH_MIN.get().floatValue();
            float pulseWidthMax = Config.STARBURST_PULSE_WIDTH_MAX.get().floatValue();
            pulseLength = pulseLengthMin + (pulseLengthMax - pulseLengthMin) * phase;
            pulseWidth = pulseWidthMin + (pulseWidthMax - pulseWidthMin) * phase;
        }

        // Prepare rendering
        VertexConsumer rays = transform.bufferSource().getBuffer(RenderType.dragonRays());
        Integer color = Color.getColor(stack);

        // Only render if there's a color
        if (color == null) {
            return;
        }

        // Draw rays in a rotating circle with varying length and width
        for (int ray = 0; ray < rayCount; ray++) {
            // Calculate ray angle
            float angle = (float) (ray * (Math.PI * 2.0D / rayCount));

            // Only rotate if there's rotation duration
            if (rotationDuration > 0) {
                angle += (float) ((time + offset) * Math.PI * 2.0D / rotationDuration);
            }

            // Calculate ray length and width
            float length = pulseLength * (rayLength + (ray % lengthVariationCount) * lengthVariation);
            float width = pulseWidth * (rayWidth + (ray % widthVariationCount) * widthVariation);

            // Draw the ray
            transform.pose().pushPose();
            transform.pose().mulPose(Axis.ZP.rotation(angle));
            Matrix4f matrix = transform.pose().last().pose();
            addRay(rays, matrix, length, width, color);
            transform.pose().popPose();
        }
    }

    // Gets the time used for the animation
    private static float getTime(Minecraft minecraft) {
        // If a player exists, align the animation with the current tick
        if (minecraft.player != null) {
            return minecraft.player.tickCount + minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        }

        // Continue animating when no player exists, such as menus
        return System.currentTimeMillis() / 50.0F;
    }

    // Draws a triangular ray
    private static void addRay(VertexConsumer vertices, Matrix4f matrix, float length, float width, int color) {
        // Get the RGB color channels
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        // Add a triangle that fades outward from the center
        vertices.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(red, green, blue, 255);
        vertices.addVertex(matrix, width, length, 0.0F).setColor(red, green, blue, 0);
        vertices.addVertex(matrix, -width, length, 0.0F).setColor(red, green, blue, 0);
    }
}