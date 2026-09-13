package com.fooze.fancyitems.util;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.FancyItems;
import com.fooze.fancyitems.feature.BeamEffect;
import com.fooze.fancyitems.feature.FadeEffect;
import com.fooze.fancyitems.feature.beam.Beam;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = FancyItems.MODID, value = Dist.CLIENT)
public class Item {
    private static boolean rendered;

    // Checks whether custom item rendering is enabled
    public static boolean isEnabled() {
        return (!IrisAPI.shadersActive() || Config.FORCE_IRIS_COMPATIBILITY.get())
                && (Config.ENABLE_FADE_EFFECT.get() || Config.ENABLE_BEAM_EFFECT.get() && BeamEffect.hasVisuals());
    }

    // Checks whether an item is rendered with the custom renderer
    public static boolean isRendered() {
        return rendered;
    }

    // Renders ground items
    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        // Don't render if custom item rendering is disabled
        if (!isEnabled()) {
            return;
        }

        // Don't render in the wrong stage
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }

        // Get the instance and level
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        // Don't render if the level is null
        if (level == null) {
            return;
        }

        // Prepare rendering
        List<ItemEntity> items = new ArrayList<>();
        float ticks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        PoseStack transform = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();

        // Find visible ground items
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ItemEntity item && item.onGround()) {
                double y = 0.0D;

                if (BeamEffect.hasBeamEffect(item) && (BeamEffect.hasBeam() || BeamEffect.hasSparkles())) {
                    y = Beam.height() + 0.5F;
                }

                if (item.shouldRenderAtSqrDistance(cameraPos.distanceToSqr(item.position()))
                        && event.getFrustum().isVisible(item.getBoundingBox().inflate(0.0D, y, 0.0D))
                ) {
                    items.add(item);
                }
            }
        }

        // Render distant items first
        items.sort(Comparator.comparingDouble(
                (ItemEntity item) -> cameraPos.distanceToSqr(item.position())).reversed()
        );

        // Render each ground item
        for (ItemEntity item : items) {
            // Enable the depth mask
            RenderSystem.depthMask(true);

            // Calculate the item position and distance fade
            Vec3 itemPos = getPos(item, ticks);
            float fade = FadeEffect.getFade(minecraft, item, cameraPos, itemPos);

            // Render the beam effect if the item has one
            if (BeamEffect.hasBeamEffect(item) && BeamEffect.hasVisuals() && fade > 0.0F) {
                BeamEffect.render(level, item, itemPos, cameraPos, camera, ticks, fade, transform, buffer);
            }

            // Render the item when it's visible
            if (fade > 0.0F) {
                render(dispatcher, item, itemPos, cameraPos, ticks, fade, transform, buffer);
            }
        }
    }

    // Gets an item's interpolated position
    public static Vec3 getPos(ItemEntity item, float ticks) {
        return new Vec3(
                Mth.lerp(ticks, item.xo, item.getX()),
                Mth.lerp(ticks, item.yo, item.getY()),
                Mth.lerp(ticks, item.zo, item.getZ())
        );
    }

    // Renders a ground item with opacity
    public static void render(
            EntityRenderDispatcher renderer, ItemEntity item, Vec3 itemPos, Vec3 cameraPos,
            float ticks, float fade, PoseStack transform, MultiBufferSource.BufferSource buffer
    ) {
        try {
            rendered = true;
            MultiBufferSource itemBuffer = buffer;

            // Allow the item to be faded
            if (fade < 1.0F) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fade);
                RenderSystem.setShaderGlintAlpha(fade);
                itemBuffer = renderType -> buffer.getBuffer(getRenderType(renderType));
            }

            // Render the item
            renderer.render(
                    item,
                    itemPos.x - cameraPos.x,
                    itemPos.y - cameraPos.y,
                    itemPos.z - cameraPos.z,
                    item.getYRot(), ticks, transform, itemBuffer,
                    renderer.getPackedLightCoords(item, ticks)
            );

            buffer.endBatch();
        } finally {
            rendered = false;

            // Restore the default shader values
            if (fade < 1.0F) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderGlintAlpha(1.0F);
            }
        }
    }

    // Gets a render type that supports opacity
    private static RenderType getRenderType(RenderType renderType) {
        if (renderType == Sheets.solidBlockSheet() || renderType == Sheets.cutoutBlockSheet()) {
            return Sheets.translucentCullBlockSheet();
        }

        return renderType;
    }
}