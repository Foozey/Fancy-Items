package com.fooze.fancyitems.mixin;

import com.fooze.fancyitems.feature.StarburstEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void fancyitems$render(
            LivingEntity entity, Level level, ItemStack stack,
            int x, int y, int seed, int guiOffset, CallbackInfo callback) {
        StarburstEffect.render((GuiGraphics) (Object) this, stack, seed);
    }
}