package com.fooze.fancyitems.mixin;

import com.fooze.fancyitems.feature.BeamEffect;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    // Hides item shadows during beam effects
    @Inject(method = "getShadowRadius", at = @At("RETURN"), cancellable = true)
    private void fancyitems$hideShadow(Entity entity, CallbackInfoReturnable<Float> callback) {
        if (entity instanceof ItemEntity itemEntity && BeamEffect.hasBeam(itemEntity)) {
            callback.setReturnValue(0.0F);
        }
    }
}