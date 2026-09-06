package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.FancyItems;
import com.fooze.fancyitems.feature.beam.Beam;
import com.fooze.fancyitems.feature.beam.Glow;
import com.fooze.fancyitems.feature.beam.Sparkles;
import com.fooze.fancyitems.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.multiplayer.ClientLevel;
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

    // Checks if an item uses a loot beam
    public static boolean hasBeam(ItemEntity item) {
        return Config.ENABLE_BEAM.get()
                && !item.getItem().isEmpty()
                && Color.getColor(item.getItem()) != null;
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

        // Clear sparkles and ungrounded ages when no level is loaded
        if (level == null) {
            ungroundedAges.clear();
            Sparkles.clear();
            return;
        }

        // Remove ungrounded ages for items not on the ground or without a beam effect
        ungroundedAges.entrySet().removeIf(entry -> {
            Entity entity = level.getEntity(entry.getKey());
            return !(entity instanceof ItemEntity item) || !item.onGround() || !hasBeam(item);
        });

        // Get the camera position
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

        // Update sparkles for visible beam effects
        Sparkles.tick(level, item -> item.onGround()
                && hasBeam(item)
                && FadeEffect.isRendered(minecraft, item, cameraPos));
    }

    // Renders a beam effect
    public static void render(
            ClientLevel level, ItemEntity item, Vec3 itemPos, Vec3 cameraPos, Camera camera,
            float ticks, float distanceFade, PoseStack transform, MultiBufferSource.BufferSource buffer
    ) {
        // Calculate the fade animation
        int fadeDuration = Config.BEAM_EFFECT_FADE_DURATION.get();
        int ungroundedAge = ungroundedAges.computeIfAbsent(item.getId(), id -> item.getAge());
        int groundAge = item.getAge() - ungroundedAge;
        float fade = fadeDuration <= 0 ? 1.0F : Math.min((groundAge + ticks) / fadeDuration, 1.0F);
        fade = fade * fade * (3.0F - 2.0F * fade) * distanceFade;

        // Don't render if the beam effect isn't visible
        if (fade <= 0.0F) {
            return;
        }

        // Set the default bobbing value
        float bob = -1.0F;

        // Calculate the item bobbing animation
        if (IClientItemExtensions.of(item.getItem()).shouldBobAsEntity(item.getItem())) {
            bob = (float) Math.sin((item.getAge() + ticks) / 10.0F + item.bobOffs);
        }

        // Calculate the animation values
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

        // Draw the beam effect
        Matrix4f matrix = new Matrix4f(transform.last().pose());
        Beam.render(level, item, itemPos, cameraPos, transform, phase, offset, fade, red, green, blue);
        Glow.render(transform, phase, fade, red, green, blue);
        Sparkles.render(item, matrix, camera, ticks, fade, red, green, blue, buffer);
        transform.popPose();
    }
}