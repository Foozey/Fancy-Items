package com.fooze.fancyitems.feature.beam;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.BeamEffect;
import com.fooze.fancyitems.util.Asset;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class Glow {
    // Textures
    private static final ResourceLocation FANCY_TEXTURE = Asset.getTexture("glow_fancy.png");
    private static final ResourceLocation SIMPLE_TEXTURE = Asset.getTexture("glow_simple.png");

    // Renders a glow for an item with a beam effect
    public static void render(PoseStack transform, float phase, float fade, int red, int green, int blue) {
        // Calculate the glow scale
        float pulseMin = Config.GLOW_PULSE_MIN.get().floatValue();
        float pulseMax = Config.GLOW_PULSE_MAX.get().floatValue();
        float scale = Config.GLOW_SCALE.get().floatValue() * (pulseMin + (pulseMax - pulseMin) * phase);

        // Prepare rendering
        ResourceLocation texture = BeamEffect.getTexture(FANCY_TEXTURE, SIMPLE_TEXTURE);
        RenderType renderType = BeamEffect.getRenderType(texture);
        BufferBuilder vertices = Tesselator.getInstance().begin(renderType.mode(), renderType.format());

        // Draw the glow
        Matrix4f matrix = transform.last().pose();
        add(vertices, matrix, scale, fade, red, green, blue);
        renderType.draw(vertices.buildOrThrow());
    }

    // Adds a ground glow
    private static void add(
            VertexConsumer vertices, Matrix4f matrix,
            float scale, float fade, int red, int green, int blue
    ) {
        // Calculate the glow's opacity
        int alpha = (int) (Config.GLOW_INTENSITY.get() * fade * 255.0F);

        // Top left
        vertices.addVertex(matrix, -scale, 0.01F, -scale)
                .setColor(red, green, blue, alpha)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Bottom left
        vertices.addVertex(matrix, -scale, 0.01F, scale)
                .setColor(red, green, blue, alpha)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Bottom right
        vertices.addVertex(matrix, scale, 0.01F, scale)
                .setColor(red, green, blue, 0)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top right
        vertices.addVertex(matrix, scale, 0.01F, -scale)
                .setColor(red, green, blue, 0)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}