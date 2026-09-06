package com.fooze.fancyitems.feature.beam.sparkles;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.beam.Sparkles;

import java.util.ArrayList;
import java.util.List;

public class Emitter {
    public final List<Sparkle> sparkles = new ArrayList<>();
    public long lastUpdate;

    // Returns the number of sparkles to be emitted
    public int amount() {
        return Math.max(0, Math.round(Config.SPARKLES_AMOUNT.get() * Sparkles.random()));
    }
}