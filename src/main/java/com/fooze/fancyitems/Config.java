package com.fooze.fancyitems;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_STARBURST = BUILDER
            .comment("Whether to enable the starburst effect for items with a rarity or colored name")
            .define("enableStarburst", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}