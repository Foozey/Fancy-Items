package com.fooze.fancyitems.util;

import com.fooze.fancyitems.FancyItems;
import net.minecraft.resources.ResourceLocation;

public class Asset {
    // Returns a resource location for a texture
    public static ResourceLocation getTexture(String texture) {
        return ResourceLocation.fromNamespaceAndPath(FancyItems.MODID, "textures/" + texture);
    }

    // Returns a resource location for a sound
    public static ResourceLocation getSound(String sound) {
        return ResourceLocation.fromNamespaceAndPath(FancyItems.MODID, sound);
    }
}