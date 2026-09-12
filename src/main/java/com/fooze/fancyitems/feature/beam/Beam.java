package com.fooze.fancyitems.feature.beam;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.BeamEffect;
import com.fooze.fancyitems.util.Asset;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class Beam {
    // Textures
    private static final ResourceLocation FANCY_TEXTURE = Asset.getTexture("beam_fancy.png");
    private static final ResourceLocation SIMPLE_TEXTURE = Asset.getTexture("beam_simple.png");

    // Returns the beam height in blocks
    public static float height() {
        return Config.BEAM_HEIGHT.get().floatValue();
    }

    // Returns the beam width in blocks
    public static float width() {
        return Config.BEAM_WIDTH.get().floatValue();
    }

    // Renders a beam for an item with a beam effect
    public static void render(
            ClientLevel level, ItemEntity item, Vec3 itemPos, Vec3 cameraPos, PoseStack transform,
            float phase, float offset, float fade, int red, int green, int blue
    ) {
        int planes = Config.BEAM_PLANES.get();

        // Save the item transform and sync the beam with the item bobbing animation
        transform.pushPose();
        transform.translate(0.0F, offset, 0.0F);

        // Calculate the camera angle and rotate the beam to face the camera
        float cameraAngle = (float) Math.atan2(cameraPos.z - itemPos.z, cameraPos.x - itemPos.x);
        transform.mulPose(Axis.YP.rotation(-cameraAngle + (float) Math.PI / (2.0F * planes)));

        // Calculate the beam dimensions
        float pulseMin = Config.BEAM_PULSE_MIN.get().floatValue();
        float pulseMax = Config.BEAM_PULSE_MAX.get().floatValue();
        float height = height();
        float width = width() * (pulseMin + (pulseMax - pulseMin) * phase);

        // Check for blocks above the beam
        BlockHitResult hit = level.clip(new ClipContext(
                itemPos.add(0.0D, offset, 0.0D),
                itemPos.add(0.0D, offset + height, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, item
        ));

        // Shorten the beam when a block is hit
        if (hit.getType() != BlockHitResult.Type.MISS) {
            height = Math.max(0.0F, (float) (hit.getLocation().y - itemPos.y - offset));
        }

        // Prepare rendering
        ResourceLocation texture = BeamEffect.getTexture(FANCY_TEXTURE, SIMPLE_TEXTURE);
        RenderType renderType = BeamEffect.getRenderType(texture);
        BufferBuilder vertices = Tesselator.getInstance().begin(renderType.mode(), renderType.format());
        Matrix4f matrix = transform.last().pose();

        // Add the beam to the render buffer
        for (int plane = 0; plane < planes; plane++) {
            // Calculate the plane's rotation, width coordinates, and item height
            float angle = (float) Math.PI * plane / planes;
            float x = (float) Math.cos(angle) * width / 2.0F;
            float z = (float) Math.sin(angle) * width / 2.0F;
            float itemHeight = Math.min(item.getBbHeight(), height);

            // Add the colored outer beam plane
            add(vertices, matrix, x, z, height, itemHeight, red, green, blue, fade);

            // Add the white inner beam plane
            add(vertices, matrix, x / 2.0F, z / 2.0F, height / 2.0F,
                    Math.min(itemHeight, height / 2.0F), 255, 255, 255, fade);
        }

        // Restore the item transform and draw the beam
        transform.popPose();
        renderType.draw(vertices.buildOrThrow());
    }

    // Adds a beam plane
    private static void add(
            VertexConsumer vertices, Matrix4f matrix,
            float x, float z, float height, float itemHeight,
            int red, int green, int blue, float fade
    ) {
        // Calculate the beam's opacity and item's top UV
        int alpha = (int) (Config.BEAM_INTENSITY.get() * fade * 255.0F);
        float topUv = 1.0F - itemHeight / height;

        // Fade the beam in from bottom of the item, then out at the top of the beam
        addPlane(vertices, matrix, x, z, 0.0F, itemHeight, red, green, blue, 0, alpha, 1.0F, topUv);
        addPlane(vertices, matrix, x, z, itemHeight, height, red, green, blue, alpha, 0, topUv, 0.0F);
    }

    // Adds a section of a beam plane
    private static void addPlane(
            VertexConsumer vertices, Matrix4f matrix,
            float x, float z, float bottom, float top,
            int red, int green, int blue, int bottomAlpha, int topAlpha,
            float bottomUv, float topUv
    ) {
        // Bottom left
        vertices.addVertex(matrix, -x, bottom, -z)
                .setColor(red, green, blue, bottomAlpha)
                .setUv(0.0F, bottomUv)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Bottom right
        vertices.addVertex(matrix, x, bottom, z)
                .setColor(red, green, blue, bottomAlpha)
                .setUv(1.0F, bottomUv)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top right
        vertices.addVertex(matrix, x, top, z)
                .setColor(red, green, blue, topAlpha)
                .setUv(1.0F, topUv)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top left
        vertices.addVertex(matrix, -x, top, -z)
                .setColor(red, green, blue, topAlpha)
                .setUv(0.0F, topUv)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}