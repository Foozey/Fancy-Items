package com.fooze.fancyitems.feature.beam;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.BeamEffect;
import com.fooze.fancyitems.feature.beam.sparkles.Emitter;
import com.fooze.fancyitems.feature.beam.sparkles.Sparkle;
import com.fooze.fancyitems.util.Asset;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class Sparkles {
    // Textures
    private static final ResourceLocation FANCY_TEXTURE = Asset.getTexture("sparkle_fancy.png");
    private static final ResourceLocation SIMPLE_TEXTURE = Asset.getTexture("sparkle_simple.png");

    // Util
    private static final Map<UUID, Emitter> EMITTERS = new HashMap<>();
    public static final Random RANDOM = new Random();
    private static long update;

    // Returns a random multiplier centered around 1.0F
    public static float random() {
        return 1.0F + (RANDOM.nextFloat() * 2.0F - 1.0F) * Config.SPARKLES_RANDOMNESS.get().floatValue();
    }

    // Updates sparkles for all active beam effects
    public static void tick(ClientLevel level, Predicate<ItemEntity> shouldRender) {
        long currentUpdate = ++update;

        for (Entity entity : level.entitiesForRendering()) {
            // Only update for items with beam effects
            if (!(entity instanceof ItemEntity item) || !shouldRender.test(item)) {
                continue;
            }

            // Update the sparkle emitter
            Emitter emitter = EMITTERS.computeIfAbsent(item.getUUID(), ignored -> new Emitter());
            emitter.lastUpdate = currentUpdate;
            emitter.sparkles.removeIf(Sparkle::tick);

            // Emit sparkles until the amount is reached
            while (emitter.sparkles.size() < emitter.amount()) {
                emitter.sparkles.add(new Sparkle());
            }
        }

        // Remove sparkles for items without beam effects
        EMITTERS.values().removeIf(emitter -> emitter.lastUpdate != currentUpdate);
    }

    // Clears all sparkles
    public static void clear() {
        EMITTERS.clear();
    }

    // Renders sparkles for an item with a beam effect
    public static void render(
            ItemEntity item, Matrix4f matrix, Camera camera, float ticks, float fade,
            int red, int green, int blue, MultiBufferSource.BufferSource buffer
    ) {
        Emitter emitter = EMITTERS.get(item.getUUID());

        // Only render for items with sparkles
        if (emitter == null) {
            return;
        }

        // Prepare rendering
        ResourceLocation texture = BeamEffect.getTexture(FANCY_TEXTURE, SIMPLE_TEXTURE);
        RenderType renderType = BeamEffect.getRenderType(texture);
        VertexConsumer vertices = buffer.getBuffer(renderType);
        Vector3f left = camera.getLeftVector();
        Vector3f up = camera.getUpVector();

        // Add the sparkles to the render buffer
        for (Sparkle sparkle : emitter.sparkles) {
            add(vertices, matrix, left, up, sparkle, ticks, fade, red, green, blue);
        }

        // Draw the sparkles
        buffer.endBatch(renderType);
    }

    // Adds a camera-facing sparkle
    private static void add(
            VertexConsumer vertices, Matrix4f matrix, Vector3f left, Vector3f up,
            Sparkle sparkle, float ticks, float fade, int red, int green, int blue
    ) {
        // Calculate the sparkle's animation progress
        float age = sparkle.previousAge + (sparkle.currentAge - sparkle.previousAge) * ticks;
        float progress = age / sparkle.lifetime;

        // Calculate the sparkle's size
        float scale = Config.SPARKLES_SCALE.get().floatValue();
        float size = scale * sparkle.scale * (1.0F - progress * 0.5F) / 2.0F;
        float leftX = left.x() * size;
        float leftY = left.y() * size;
        float leftZ = left.z() * size;
        float upX = up.x() * size;
        float upY = up.y() * size;
        float upZ = up.z() * size;

        // Calculate the sparkle's opacity
        float intensity = Config.SPARKLES_INTENSITY.get().floatValue();
        fade = fade * Math.min(progress / 0.5F, 1.0F) * (1.0F - progress);
        int alpha = (int) (intensity * sparkle.intensity * 255.0F * fade);

        // Calculate the sparkle's position
        float distance = Config.SPARKLES_DISTANCE.get().floatValue();
        float x = sparkle.previousX + (sparkle.currentX - sparkle.previousX) * ticks;
        float y = 0.01F + progress * Beam.height() * distance * sparkle.distance;
        float z = sparkle.previousZ + (sparkle.currentZ - sparkle.previousZ) * ticks;

        // Bottom right
        vertices.addVertex(matrix, x - leftX - upX, y - leftY - upY, z - leftZ - upZ)
                .setColor(red, green, blue, alpha)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Bottom left
        vertices.addVertex(matrix, x + leftX - upX, y + leftY - upY, z + leftZ - upZ)
                .setColor(red, green, blue, alpha)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top left
        vertices.addVertex(matrix, x + leftX + upX, y + leftY + upY, z + leftZ + upZ)
                .setColor(red, green, blue, alpha)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top right
        vertices.addVertex(matrix, x - leftX + upX, y - leftY + upY, z - leftZ + upZ)
                .setColor(red, green, blue, alpha)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}