package com.fooze.fancyitems.util;

import com.fooze.fancyitems.FancyItems;
import net.minecraft.resources.ResourceLocation;

public class Texture {
    // Returns a resource location for a misc texture
    public static ResourceLocation misc(String texture) {
        return ResourceLocation.fromNamespaceAndPath(FancyItems.MODID, "textures/misc/" + texture);
    }
}