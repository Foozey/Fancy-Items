package com.fooze.fancyitems;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public enum BeamEffectStyle {
        FANCY, SIMPLE
    }

    static {
        // Fade Effect
        ENABLE_FADE_EFFECT = BUILDER.define("enableFadeEffect", true);

        // Starburst Effect
        BUILDER.push("starburstEffect");
        ENABLE_STARBURST_EFFECT = BUILDER.define("enableStarburstEffect", true);
        STARBURST_RAY_COUNT = BUILDER.defineInRange("starburstRayCount", 9, 1, 18);
        STARBURST_RAY_LENGTH = BUILDER.defineInRange("starburstRayLength", 0.5D, 0.1D, 2.0D);
        STARBURST_RAY_WIDTH = BUILDER.defineInRange("starburstRayWidth", 0.25D, 0.1D, 1.0D);
        STARBURST_LENGTH_VARIATION = BUILDER.defineInRange("starburstLengthVariation", 0.1D, 0.01D, 1.0D);
        STARBURST_LENGTH_VARIATION_COUNT = BUILDER.defineInRange("starburstLengthVariationCount", 3, 1, 6);
        STARBURST_WIDTH_VARIATION = BUILDER.defineInRange("starburstWidthVariation", 0.05D, 0.01D, 0.5D);
        STARBURST_WIDTH_VARIATION_COUNT = BUILDER.defineInRange("starburstWidthVariationCount", 2, 1, 4);
        STARBURST_PULSE_LENGTH_MIN = BUILDER.defineInRange("starburstPulseLengthMin", 0.9D, 0.1D, 2.0D);
        STARBURST_PULSE_LENGTH_MAX = BUILDER.defineInRange("starburstPulseLengthMax", 1.1D, 0.1D, 2.0D);
        STARBURST_PULSE_WIDTH_MIN = BUILDER.defineInRange("starburstPulseWidthMin", 0.33D, 0.1D, 2.0D);
        STARBURST_PULSE_WIDTH_MAX = BUILDER.defineInRange("starburstPulseWidthMax", 1.5D, 0.1D, 2.0D);
        STARBURST_PULSE_DURATION = BUILDER.defineInRange("starburstPulseDuration", 80, 1, 1200);
        STARBURST_ROTATION_DURATION = BUILDER.defineInRange("starburstRotationDuration", 400, 1, 1200);
        STARBURST_ANIMATION_OFFSET = BUILDER.define("starburstAnimationOffset", true);
        BUILDER.pop();

        // Beam Effect
        BUILDER.push("beamEffect");
        ENABLE_BEAM_EFFECT = BUILDER.define("enableBeamEffect", true);
        BEAM_EFFECT_STYLE = BUILDER.defineEnum("beamEffectStyle", BeamEffectStyle.FANCY);
        BEAM_EFFECT_FADE_DURATION = BUILDER.defineInRange("beamEffectFadeDuration", 60, 1, 1200);

        // Beam
        BUILDER.push("beam");
        ENABLE_BEAM = BUILDER.define("enableBeam", true);
        BEAM_PLANES = BUILDER.defineInRange("beamPlanes", 2, 1, 4);
        BEAM_HEIGHT = BUILDER.defineInRange("beamHeight", 3.0D, 0.1D, 16.0D);
        BEAM_WIDTH = BUILDER.defineInRange("beamWidth", 0.5D, 0.1D, 2.0D);
        BEAM_INTENSITY = BUILDER.defineInRange("beamIntensity", 1.0D, 0.1D, 1.0D);
        BEAM_PULSE_MIN = BUILDER.defineInRange("beamPulseMin", 1.0D, 0.1D, 2.0D);
        BEAM_PULSE_MAX = BUILDER.defineInRange("beamPulseMax", 1.5D, 0.1D, 2.0D);
        BUILDER.pop();

        // Glow
        BUILDER.push("glow");
        ENABLE_GLOW = BUILDER.define("enableGlow", true);
        GLOW_SCALE = BUILDER.defineInRange("glowScale", 1.0D, 0.1D, 3.0D);
        GLOW_INTENSITY = BUILDER.defineInRange("glowIntensity", 0.5D, 0.1D, 1.0D);
        GLOW_PULSE_MIN = BUILDER.defineInRange("glowPulseMin", 1.0D, 0.1D, 2.0D);
        GLOW_PULSE_MAX = BUILDER.defineInRange("glowPulseMax", 1.25D, 0.1D, 2.0D);
        BUILDER.pop();

        // Sparkles
        BUILDER.push("sparkles");
        ENABLE_SPARKLES = BUILDER.define("enableSparkles", true);
        SPARKLES_AMOUNT = BUILDER.defineInRange("sparklesAmount", 16, 1, 64);
        SPARKLES_SCALE = BUILDER.defineInRange("sparklesScale", 0.0625D, 0.01D, 0.25D);
        SPARKLES_INTENSITY = BUILDER.defineInRange("sparklesIntensity", 1.0D, 0.1D, 1.0D);
        SPARKLES_LIFETIME = BUILDER.defineInRange("sparklesLifetime", 100, 1, 1200);
        SPARKLES_DISTANCE = BUILDER.defineInRange("sparklesDistance", 0.5D, 0.1D, 2.0D);
        SPARKLES_DRIFT_AMOUNT = BUILDER.defineInRange("sparklesDriftAmount", 0.25D, 0.1D, 2.0D);
        SPARKLES_DRIFT_FREQUENCY = BUILDER.defineInRange("sparklesDriftFrequency", 60, 1, 1200);
        SPARKLES_RANDOMNESS = BUILDER.defineInRange("sparklesRandomness", 0.5D, 0.1D, 1.0D);
        BUILDER.pop();

        // Sound
        BUILDER.push("sound");
        ENABLE_SOUND = BUILDER.define("enableSound", true);
        SOUND_VOLUME = BUILDER.defineInRange("soundVolume", 0.5D, 0.0D, 1.0D);
        SOUND_PITCH_VARIATION = BUILDER.defineInRange("soundPitchVariation", 0.1D, 0.0D, 1.0D);
        SOUND_PULSE_MIN = BUILDER.defineInRange("soundPulseMin", 0.5D, 0.1D, 2.0D);
        SOUND_PULSE_MAX = BUILDER.defineInRange("soundPulseMax", 1.0D, 0.1D, 2.0D);
        BUILDER.pop();
        BUILDER.pop();
    }

    // Fade Effect
    public static final ModConfigSpec.BooleanValue ENABLE_FADE_EFFECT;

    // Starburst Effect
    public static final ModConfigSpec.BooleanValue ENABLE_STARBURST_EFFECT;
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
    public static final ModConfigSpec.BooleanValue STARBURST_ANIMATION_OFFSET;

    // Beam Effect
    public static final ModConfigSpec.BooleanValue ENABLE_BEAM_EFFECT;
    public static final ModConfigSpec.EnumValue<BeamEffectStyle> BEAM_EFFECT_STYLE;
    public static final ModConfigSpec.IntValue BEAM_EFFECT_FADE_DURATION;

    // Beam
    public static final ModConfigSpec.BooleanValue ENABLE_BEAM;
    public static final ModConfigSpec.IntValue BEAM_PLANES;
    public static final ModConfigSpec.DoubleValue BEAM_HEIGHT;
    public static final ModConfigSpec.DoubleValue BEAM_WIDTH;
    public static final ModConfigSpec.DoubleValue BEAM_INTENSITY;
    public static final ModConfigSpec.DoubleValue BEAM_PULSE_MIN;
    public static final ModConfigSpec.DoubleValue BEAM_PULSE_MAX;

    // Glow
    public static final ModConfigSpec.BooleanValue ENABLE_GLOW;
    public static final ModConfigSpec.DoubleValue GLOW_SCALE;
    public static final ModConfigSpec.DoubleValue GLOW_INTENSITY;
    public static final ModConfigSpec.DoubleValue GLOW_PULSE_MIN;
    public static final ModConfigSpec.DoubleValue GLOW_PULSE_MAX;

    // Sparkles
    public static final ModConfigSpec.BooleanValue ENABLE_SPARKLES;
    public static final ModConfigSpec.IntValue SPARKLES_AMOUNT;
    public static final ModConfigSpec.DoubleValue SPARKLES_SCALE;
    public static final ModConfigSpec.DoubleValue SPARKLES_INTENSITY;
    public static final ModConfigSpec.IntValue SPARKLES_LIFETIME;
    public static final ModConfigSpec.DoubleValue SPARKLES_DISTANCE;
    public static final ModConfigSpec.DoubleValue SPARKLES_DRIFT_AMOUNT;
    public static final ModConfigSpec.IntValue SPARKLES_DRIFT_FREQUENCY;
    public static final ModConfigSpec.DoubleValue SPARKLES_RANDOMNESS;

    // Sound
    public static final ModConfigSpec.BooleanValue ENABLE_SOUND;
    public static final ModConfigSpec.DoubleValue SOUND_VOLUME;
    public static final ModConfigSpec.DoubleValue SOUND_PITCH_VARIATION;
    public static final ModConfigSpec.DoubleValue SOUND_PULSE_MIN;
    public static final ModConfigSpec.DoubleValue SOUND_PULSE_MAX;

    static final ModConfigSpec SPEC = BUILDER.build();
}