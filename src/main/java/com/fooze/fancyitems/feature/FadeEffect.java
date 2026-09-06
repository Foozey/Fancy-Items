package com.fooze.fancyitems.feature;

import com.fooze.fancyitems.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class FadeEffect {
    // Checks whether an item is within its render distance
    public static boolean isRendered(Minecraft minecraft, ItemEntity item, Vec3 cameraPosition) {
        if (!Config.ENABLE_FADE_EFFECT.get()) {
            return true;
        }

        float renderDistance = getRenderDistance(minecraft, item);
        return cameraPosition.distanceToSqr(item.position()) < renderDistance * renderDistance;
    }

    // Gets an item's opacity based on its distance from the camera
    public static float getFade(Minecraft minecraft, ItemEntity item, Vec3 cameraPosition, Vec3 itemPosition) {
        if (!Config.ENABLE_FADE_EFFECT.get()) {
            return 1.0F;
        }

        float fadeEnd = getRenderDistance(minecraft, item);
        float fadeStart = Math.max(0.0F, fadeEnd - 16.0F);
        float distance = (float) cameraPosition.distanceTo(itemPosition);
        float fade = Mth.clamp((fadeEnd - distance) / (fadeEnd - fadeStart), 0.0F, 1.0F);
        return fade * fade * (3.0F - 2.0F * fade);
    }

    // Gets the distance where the item stops rendering
    private static float getRenderDistance(Minecraft minecraft, ItemEntity item) {
        double renderDistanceScaling = Mth.clamp(minecraft.options.getEffectiveRenderDistance() / 8.0D, 1.0D, 2.5D);
        double entityDistanceScaling = minecraft.options.entityDistanceScaling().get();
        double entityDistanceScale = renderDistanceScaling * entityDistanceScaling;
        return (float) (item.getBoundingBox().getSize() * 64.0D * entityDistanceScale);
    }
}