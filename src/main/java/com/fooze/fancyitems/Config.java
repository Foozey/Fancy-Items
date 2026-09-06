package com.fooze.fancyitems;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        // Fade Effect
        ENABLE_FADE_EFFECT = BUILDER.define("enableFadeEffect", true);

        // Starburst Effect
        BUILDER.push("starburstEffect");
        ENABLE_STARBURST = BUILDER.define("enableStarburstEffect", true);
        STARBURST_RAY_COUNT = BUILDER.defineInRange("starburstRayCount", 9, 0, 18);
        STARBURST_RAY_LENGTH = BUILDER.defineInRange("starburstRayLength", 0.5D, 0.0D, 2.0D);
        STARBURST_RAY_WIDTH = BUILDER.defineInRange("starburstRayWidth", 0.25D, 0.0D, 1.0D);
        STARBURST_LENGTH_VARIATION = BUILDER.defineInRange("starburstLengthVariation", 0.1D, 0.0D, 1.0D);
        STARBURST_LENGTH_VARIATION_COUNT = BUILDER.defineInRange("starburstLengthVariationCount", 3, 0, 6);
        STARBURST_WIDTH_VARIATION = BUILDER.defineInRange("starburstWidthVariation", 0.05D, 0.0D, 0.5D);
        STARBURST_WIDTH_VARIATION_COUNT = BUILDER.defineInRange("starburstWidthVariationCount", 2, 0, 4);
        STARBURST_PULSE_LENGTH_MIN = BUILDER.defineInRange("starburstPulseLengthMin", 0.9D, 0.0D, 2.0D);
        STARBURST_PULSE_LENGTH_MAX = BUILDER.defineInRange("starburstPulseLengthMax", 1.1D, 0.0D, 2.0D);
        STARBURST_PULSE_WIDTH_MIN = BUILDER.defineInRange("starburstPulseWidthMin", 0.33D, 0.0D, 2.0D);
        STARBURST_PULSE_WIDTH_MAX = BUILDER.defineInRange("starburstPulseWidthMax", 1.5D, 0.0D, 2.0D);
        STARBURST_PULSE_DURATION = BUILDER.defineInRange("starburstPulseDuration", 80, 0, 1200);
        STARBURST_ROTATION_DURATION = BUILDER.defineInRange("starburstRotationDuration", 400, 0, 1200);
        BUILDER.pop();

        // Beam Effect
        BUILDER.push("beamEffect");
        ENABLE_BEAM = BUILDER.define("enableBeamEffect", true);
        BEAM_PLANES = BUILDER.defineInRange("beamPlanes", 2, 0, 4);
        BEAM_HEIGHT = BUILDER.defineInRange("beamHeight", 3.0D, 0.0D, 16.0D);
        BEAM_WIDTH = BUILDER.defineInRange("beamWidth", 0.5D, 0.0D, 2.0D);
        BEAM_INTENSITY = BUILDER.defineInRange("beamIntensity", 1.0D, 0.0D, 1.0D);
        BEAM_PULSE_MIN = BUILDER.defineInRange("beamPulseMin", 1.0D, 0.0D, 2.0D);
        BEAM_PULSE_MAX = BUILDER.defineInRange("beamPulseMax", 1.5D, 0.0D, 2.0D);
        GLOW_SCALE = BUILDER.defineInRange("glowScale", 1.0D, 0.0D, 2.0D);
        GLOW_INTENSITY = BUILDER.defineInRange("glowIntensity", 0.5D, 0.0D, 1.0D);
        GLOW_PULSE_MIN = BUILDER.defineInRange("glowPulseMin", 1.0D, 0.0D, 2.0D);
        GLOW_PULSE_MAX = BUILDER.defineInRange("glowPulseMax", 1.25D, 0.0D, 2.0D);
        SPARKLES_AMOUNT = BUILDER.defineInRange("sparklesAmount", 16, 0, 64);
        SPARKLES_SCALE = BUILDER.defineInRange("sparklesScale", 0.0625D, 0.0D, 0.25D);
        SPARKLES_INTENSITY = BUILDER.defineInRange("sparklesIntensity", 1.0D, 0.0D, 1.0D);
        SPARKLES_LIFETIME = BUILDER.defineInRange("sparklesLifetime", 100, 0, 1200);
        SPARKLES_DISTANCE = BUILDER.defineInRange("sparklesDistance", 0.5D, 0.0D, 2.0D);
        SPARKLES_DRIFT_AMOUNT = BUILDER.defineInRange("sparklesDriftAmount", 0.5D, 0.0D, 2.0D);
        SPARKLES_DRIFT_FREQUENCY = BUILDER.defineInRange("sparklesDriftFrequency", 20, 0, 1200);
        SPARKLES_DRIFT_DURATION = BUILDER.defineInRange("sparklesDriftDuration", 60, 0, 1200);
        SPARKLES_RANDOMNESS = BUILDER.defineInRange("sparklesRandomness", 0.5D, 0.0D, 1.0D);
        BEAM_EFFECT_FADE_DURATION = BUILDER.defineInRange("beamEffectFadeDuration", 60, 0, 1200);
        BUILDER.pop();
    }

    // Fade Effect
    public static final ModConfigSpec.BooleanValue ENABLE_FADE_EFFECT;

    // Starburst Effect
    public static final ModConfigSpec.BooleanValue ENABLE_STARBURST;
    public static final ModConfigSpec.IntValue STARBURST_RAY_COUNT;
    public static final ModConfigSpec.DoubleValue STARBURST_RAY_LENGTH;
    public static final ModConfigSpec.DoubleValue STARBURST_RAY_WIDTH;
    public static final ModConfigSpec.DoubleValue STARBURST_LENGTH_VARIATION;
    public static final ModConfigSpec.IntValue STARBURST_LENGTH_VARIATION_COUNT;
    public static final ModConfigSpec.DoubleValue STARBURST_WIDTH_VARIATION;
    public static final ModConfigSpec.IntValue STARBURST_WIDTH_VARIATION_COUNT;
    public static final ModConfigSpec.DoubleValue STARBURST_PULSE_LENGTH_MIN;
    public static final ModConfigSpec.DoubleValue STARBURST_PULSE_LENGTH_MAX;
    public static final ModConfigSpec.DoubleValue STARBURST_PULSE_WIDTH_MIN;
    public static final ModConfigSpec.DoubleValue STARBURST_PULSE_WIDTH_MAX;
    public static final ModConfigSpec.IntValue STARBURST_PULSE_DURATION;
    public static final ModConfigSpec.IntValue STARBURST_ROTATION_DURATION;

    // Beam Effect
    public static final ModConfigSpec.BooleanValue ENABLE_BEAM;
    public static final ModConfigSpec.IntValue BEAM_PLANES;
    public static final ModConfigSpec.DoubleValue BEAM_HEIGHT;
    public static final ModConfigSpec.DoubleValue BEAM_WIDTH;
    public static final ModConfigSpec.DoubleValue BEAM_INTENSITY;
    public static final ModConfigSpec.DoubleValue BEAM_PULSE_MIN;
    public static final ModConfigSpec.DoubleValue BEAM_PULSE_MAX;
    public static final ModConfigSpec.DoubleValue GLOW_SCALE;
    public static final ModConfigSpec.DoubleValue GLOW_INTENSITY;
    public static final ModConfigSpec.DoubleValue GLOW_PULSE_MIN;
    public static final ModConfigSpec.DoubleValue GLOW_PULSE_MAX;
    public static final ModConfigSpec.IntValue SPARKLES_AMOUNT;
    public static final ModConfigSpec.DoubleValue SPARKLES_SCALE;
    public static final ModConfigSpec.DoubleValue SPARKLES_INTENSITY;
    public static final ModConfigSpec.IntValue SPARKLES_LIFETIME;
    public static final ModConfigSpec.DoubleValue SPARKLES_DISTANCE;
    public static final ModConfigSpec.DoubleValue SPARKLES_DRIFT_AMOUNT;
    public static final ModConfigSpec.IntValue SPARKLES_DRIFT_FREQUENCY;
    public static final ModConfigSpec.IntValue SPARKLES_DRIFT_DURATION;
    public static final ModConfigSpec.DoubleValue SPARKLES_RANDOMNESS;
    public static final ModConfigSpec.IntValue BEAM_EFFECT_FADE_DURATION;

    static final ModConfigSpec SPEC = BUILDER.build();
}