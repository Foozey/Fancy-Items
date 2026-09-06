package com.fooze.fancyitems.feature.beam;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.util.Texture;
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
    // Rendering
    private static final ResourceLocation TEXTURE = Texture.misc("beam.png");
    private static final RenderType RENDER_TYPE = RenderType.entityNoOutline(TEXTURE);

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
            float phase, float bobOffset, float fade, int red, int green, int blue
    ) {
        int planes = Config.BEAM_PLANES.get();

        // Only render if there are planes
        if (planes == 0) {
            return;
        }

        // Save the item transform and sync the beam with the item bobbing animation
        transform.pushPose();
        transform.translate(0.0F, bobOffset, 0.0F);

        // Calculate the camera angle
        float cameraAngle = (float) Math.atan2(
                cameraPos.z - itemPos.z, cameraPos.x - itemPos.x
        );

        // Rotate the beam to face the camera
        transform.mulPose(Axis.YP.rotation(-cameraAngle + (float) Math.PI / (2.0F * planes)));

        // Calculate the beam dimensions
        float pulseMin = Config.BEAM_PULSE_MIN.get().floatValue();
        float pulseMax = Config.BEAM_PULSE_MAX.get().floatValue();
        float height = height();
        float width = width() * (pulseMin + (pulseMax - pulseMin) * phase);

        // Check for blocks above the beam
        BlockHitResult hit = level.clip(new ClipContext(
                itemPos.add(0.0D, bobOffset, 0.0D),
                itemPos.add(0.0D, bobOffset + height, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, item
        ));

        // Shorten the beam when a block is hit
        if (hit.getType() != BlockHitResult.Type.MISS) {
            height = Math.max(0.0F, (float) (hit.getLocation().y - itemPos.y - bobOffset));
        }

        // Prepare rendering
        BufferBuilder vertices = Tesselator.getInstance().begin(RENDER_TYPE.mode(), RENDER_TYPE.format());
        Matrix4f matrix = transform.last().pose();

        // Add the beam to the render buffer
        for (int plane = 0; plane < planes; plane++) {
            // Calculate the plane's rotation and width coordinates
            float angle = (float) Math.PI * plane / planes;
            float x = (float) Math.cos(angle) * width / 2.0F;
            float z = (float) Math.sin(angle) * width / 2.0F;

            // Add the colored outer beam plane and white inner beam plane
            add(vertices, matrix, x, z, height, red, green, blue, fade);
            add(vertices, matrix, x / 2.0F, z / 2.0F, height / 2.0F, 255, 255, 255, fade);
        }

        // Restore the item transform and draw the beam
        transform.popPose();
        RENDER_TYPE.draw(vertices.buildOrThrow());
    }

    // Adds a beam plane
    private static void add(
            VertexConsumer vertices, Matrix4f matrix,
            float x, float z, float height,
            int red, int green, int blue, float fade
    ) {
        // Calculate the beam's opacity
        int alpha = (int) (Config.BEAM_INTENSITY.get() * fade * 255.0F);

        // Bottom left
        vertices.addVertex(matrix, -x, 0.0F, -z)
                .setColor(red, green, blue, alpha)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Bottom right
        vertices.addVertex(matrix, x, 0.0F, z)
                .setColor(red, green, blue, alpha)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top right
        vertices.addVertex(matrix, x, height, z)
                .setColor(red, green, blue, 0)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);

        // Top left
        vertices.addVertex(matrix, -x, height, -z)
                .setColor(red, green, blue, 0)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}