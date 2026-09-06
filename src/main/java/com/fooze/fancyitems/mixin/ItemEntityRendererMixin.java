package com.fooze.fancyitems.mixin;

import com.fooze.fancyitems.util.Item;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    // Defers item rendering until the custom item renderer is drawn
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void fancyitems$deferRender(
            ItemEntity itemEntity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, CallbackInfo callback
    ) {
        if (!Item.isRendered() && itemEntity.onGround()) {
            callback.cancel();
        }
    }
}