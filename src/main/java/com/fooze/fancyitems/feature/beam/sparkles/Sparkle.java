package com.fooze.fancyitems.feature.beam.sparkles;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.beam.Beam;
import com.fooze.fancyitems.feature.beam.Sparkles;

public class Sparkle {
    // Randomized properties
    public final float scale = Sparkles.random();
    public final float intensity = Sparkles.random();
    public final int lifetime = Math.max(1, Math.round(Config.SPARKLES_LIFETIME.get() * Sparkles.random()));
    public final float distance = Sparkles.random();
    public final float driftAmount = Config.SPARKLES_DRIFT_AMOUNT.get().floatValue() * Sparkles.random();
    public final int driftFrequency = Math.max(1, Math.round(Config.SPARKLES_DRIFT_FREQUENCY.get() * Sparkles.random()));
    public final int driftDuration = Math.max(1, Math.round(Config.SPARKLES_DRIFT_DURATION.get() * Sparkles.random()));

    // Position and animation
    private final float originX = (Sparkles.RANDOM.nextFloat() - 0.5F) * Beam.width();
    private final float originZ = (Sparkles.RANDOM.nextFloat() - 0.5F) * Beam.width();
    public int previousAge;
    public int currentAge;
    public float previousX = originX;
    public float currentX = originX;
    public float previousZ = originZ;
    public float currentZ = originZ;
    private int driftTimer = driftFrequency;
    private int driftProgress = driftDuration;
    private float driftOriginX = originX;
    private float driftOriginZ = originZ;
    private float driftTargetX = originX;
    private float driftTargetZ = originZ;

    // Updates the sparkle
    public boolean tick() {
        previousAge = currentAge;
        previousX = currentX;
        previousZ = currentZ;

        // Remove sparkle after its lifetime
        if (++currentAge >= lifetime) {
            return true;
        }

        // Change drift target after the previous drift finishes
        if (++driftTimer >= driftFrequency && driftProgress >= driftDuration) {
            float angle = Sparkles.RANDOM.nextFloat() * (float) Math.PI * 2.0F;
            float distance = Sparkles.RANDOM.nextFloat() * driftAmount;
            driftOriginX = currentX;
            driftOriginZ = currentZ;
            driftTargetX = originX + (float) Math.cos(angle) * distance;
            driftTargetZ = originZ + (float) Math.sin(angle) * distance;
            driftTimer = 0;
            driftProgress = 0;
        }

        // Smoothly move towards the drift target
        if (driftProgress < driftDuration) {
            float progress = (float) ++driftProgress / driftDuration;
            progress = progress * progress * (3.0F - 2.0F * progress);
            currentX = driftOriginX + (driftTargetX - driftOriginX) * progress;
            currentZ = driftOriginZ + (driftTargetZ - driftOriginZ) * progress;
        }

        return false;
    }
}