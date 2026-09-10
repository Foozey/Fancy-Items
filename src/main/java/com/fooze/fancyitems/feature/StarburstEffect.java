package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.util.Color;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class StarburstEffect {
    // Renders a starburst effect behind inventory items
    public static void render(GuiGraphics transform, ItemStack item) {
        // Don't render if the effect is disabled or there's no item
        if (!Config.ENABLE_STARBURST_EFFECT.get() || item.isEmpty()) {
            return;
        }

        // Get the item color
        Integer color = Color.getColor(item, Config.STARBURST_EFFECT_ALLOW_COMMON.get());

        // Don't render if there's no color
        if (color == null) {
            return;
        }

        // Get the starburst properties
        int rayCount = Config.STARBURST_RAY_COUNT.get();
        float rayLength = Config.STARBURST_RAY_LENGTH.get().floatValue();
        float rayWidth = Config.STARBURST_RAY_WIDTH.get().floatValue();
        float intensity = Config.STARBURST_INTENSITY.get().floatValue();
        float lengthVariation = Config.STARBURST_LENGTH_VARIATION.get().floatValue();
        int lengthVariationCount = Config.STARBURST_LENGTH_VARIATION_COUNT.get();
        float widthVariation = Config.STARBURST_WIDTH_VARIATION.get().floatValue();
        int widthVariationCount = Config.STARBURST_WIDTH_VARIATION_COUNT.get();
        float pulseLengthMin = Config.STARBURST_PULSE_LENGTH_MIN.get().floatValue();
        float pulseLengthMax = Config.STARBURST_PULSE_LENGTH_MAX.get().floatValue();
        float pulseWidthMin = Config.STARBURST_PULSE_WIDTH_MIN.get().floatValue();
        float pulseWidthMax = Config.STARBURST_PULSE_WIDTH_MAX.get().floatValue();
        int pulseDuration = Config.STARBURST_PULSE_DURATION.get();
        int rotationDuration = Config.STARBURST_ROTATION_DURATION.get();
        boolean animationOffset = Config.STARBURST_ANIMATION_OFFSET.get();

        // Get the animation time and offset
        float time = Util.getMillis() / 50.0F;
        int offset = 0;

        // Only offset each item's animation if the option is enabled
        if (animationOffset) {
            offset = Math.floorMod(ItemStack.hashItemAndComponents(item),
                    Math.max(pulseDuration, rotationDuration));
        }

        // Calculate the animations
        float phase = ((float) Math.sin((time + offset) * Math.PI * 2.0F / pulseDuration) + 1.0F) / 2.0F;
        float pulseLength = pulseLengthMin + (pulseLengthMax - pulseLengthMin) * phase;
        float pulseWidth = pulseWidthMin + (pulseWidthMax - pulseWidthMin) * phase;
        float rotation = (float) ((time + offset) * Math.PI * 2.0D / rotationDuration);

        // Prepare rendering
        VertexConsumer rays = transform.bufferSource().getBuffer(RenderType.dragonRays());

        // Draw rays in a pulsing, rotating circle with varying length and width
        for (int ray = 0; ray < rayCount; ray++) {
            // Calculate ray length, width, and angle
            float length = pulseLength * (rayLength + (ray % lengthVariationCount) * lengthVariation);
            float width = pulseWidth * (rayWidth + (ray % widthVariationCount) * widthVariation);
            float angle = (float) (ray * (Math.PI * 2.0D / rayCount)) + rotation;

            // Draw the ray
            transform.pose().pushPose();
            transform.pose().mulPose(Axis.ZP.rotation(angle));
            Matrix4f matrix = transform.pose().last().pose();
            addRay(rays, matrix, length, width, color, intensity);
            transform.pose().popPose();
        }
    }

    // Draws a triangular ray
    private static void addRay(
            VertexConsumer vertices, Matrix4f matrix,
            float length, float width, int color, float intensity
    ) {
        // Get the RGB color channels
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = (int) (intensity * 255.0F);

        // Add a triangle that fades outward from the center
        vertices.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(red, green, blue, alpha);
        vertices.addVertex(matrix, width, length, 0.0F).setColor(red, green, blue, 0);
        vertices.addVertex(matrix, -width, length, 0.0F).setColor(red, green, blue, 0);
    }
}