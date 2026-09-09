package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.FancyItems;
import com.fooze.fancyitems.feature.beam.Beam;
import com.fooze.fancyitems.feature.beam.Glow;
import com.fooze.fancyitems.feature.beam.Sparkles;
import com.fooze.fancyitems.feature.beam.Sound;
import com.fooze.fancyitems.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FancyItems.MODID, value = Dist.CLIENT)
public class BeamEffect {
    private static final Map<Integer, Integer> ungroundedAges = new HashMap<>();

    // Checks if an item has a beam effect
    public static boolean hasBeamEffect(ItemEntity item) {
        return Config.ENABLE_BEAM_EFFECT.get()
                && !item.getItem().isEmpty()
                && Color.getColor(item.getItem()) != null;
    }

    // Checks if an item has a beam
    public static boolean hasBeam() {
        return Config.ENABLE_BEAM.get();
    }

    // Checks if an item has a glow
    public static boolean hasGlow() {
        return Config.ENABLE_GLOW.get();
    }

    // Checks if an item has sparkles
    public static boolean hasSparkles() {
        return Config.ENABLE_SPARKLES.get();
    }

    // Checks if an item has sound
    public static boolean hasSound() {
        return Config.ENABLE_SOUND.get();
    }

    // Checks if an item has any beam effect visuals
    public static boolean hasVisuals() {
        return hasBeam() || hasGlow() || hasSparkles();
    }

    // Updates the beam effect each tick
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        // Get the instance and level
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        // Don't update effects when the game is paused
        if (minecraft.isPaused()) {
            return;
        }

        // Clear ages, sparkles and sound when there's no level or no effects are enabled
        if (level == null || !isEnabled()) {
            ungroundedAges.clear();
            Sparkles.clear();
            Sound.clear();
            return;
        }

        // Remove ungrounded ages for items not on the ground or items without a beam effect
        ungroundedAges.entrySet().removeIf(entry -> {
            Entity entity = level.getEntity(entry.getKey());
            return !(entity instanceof ItemEntity item) || !item.onGround() || !hasBeamEffect(item);
        });

        // Save ungrounded ages for items on the ground that have a beam effect
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ItemEntity item && item.onGround() && hasBeamEffect(item)) {
                ungroundedAges.putIfAbsent(item.getId(), item.getAge());
            }
        }

        // Get the camera position
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

        // Update sparkles for visible beam effects
        if (hasSparkles()) {
            Sparkles.tick(level, item ->
                    item.onGround()
                    && hasBeamEffect(item)
                    && FadeEffect.isVisible(minecraft, item, cameraPos)
            );
        } else {
            Sparkles.clear();
        }

        // Update sound
        if (hasSound()) {
            Sound.tick(level, cameraPos);
        } else {
            Sound.clear();
        }
    }

    // Renders a beam effect
    public static void render(
            ClientLevel level, ItemEntity item, Vec3 itemPos, Vec3 cameraPos, Camera camera,
            float ticks, float distanceFade, PoseStack transform, MultiBufferSource.BufferSource buffer
    ) {
        // Get the fade animation
        float fade = getFade(item, ticks, distanceFade);

        // Don't render if the beam effect isn't visible
        if (fade <= 0.0F) {
            return;
        }

        // Calculate the animation values
        float bob = getBob(item, ticks);
        float phase = (bob + 1.0F) / 2.0F;
        float offset = bob * 0.1F + 0.1F;

        // Get the item's beam color
        int color = Color.getColor(item.getItem());
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        // Save the item transform
        transform.pushPose();

        // Move effect rendering to the item
        transform.translate(
                itemPos.x - cameraPos.x,
                itemPos.y - cameraPos.y,
                itemPos.z - cameraPos.z
        );

        // Draw the beam and glow
        if (hasBeam()) {
            Beam.render(level, item, itemPos, cameraPos, transform, phase, offset, fade, red, green, blue);
        }

        if (hasGlow()) {
            Glow.render(transform, phase, fade, red, green, blue);
        }

        // Draw sparkles
        if (hasSparkles()) {
            Matrix4f matrix = new Matrix4f(transform.last().pose());
            Sparkles.render(item, matrix, camera, ticks, fade, red, green, blue, buffer);
        }

        transform.popPose();
    }

    // Gets the texture style to use for the beam effect
    public static ResourceLocation getTexture(ResourceLocation fancy, ResourceLocation simple) {
        if (Config.BEAM_EFFECT_STYLE.get() == Config.BeamEffectStyle.SIMPLE) {
            return simple;
        } else {
            return fancy;
        }
    }

    // Gets the fade animation
    public static float getFade(ItemEntity item, float ticks, float distanceFade) {
        int duration = Config.BEAM_EFFECT_FADE_DURATION.get();
        int ungroundedAge = ungroundedAges.computeIfAbsent(item.getId(), id -> item.getAge());
        int groundedAge = item.getAge() - ungroundedAge;
        float fade = Math.min((groundedAge + ticks) / duration, 1.0F);
        return fade * fade * (3.0F - 2.0F * fade) * distanceFade;
    }

    // Gets the bob animation
    public static float getBob(ItemEntity item, float ticks) {
        if (IClientItemExtensions.of(item.getItem()).shouldBobAsEntity(item.getItem())) {
            return (float) Math.sin((item.getAge() + ticks) / 10.0F + item.bobOffs);
        }

        return -1.0F;
    }

    // Checks if at least one beam effect option is enabled
    private static boolean isEnabled() {
        return Config.ENABLE_BEAM_EFFECT.get() && (
                hasBeam() || hasGlow() || hasSparkles() || hasSound()
        );
    }
}