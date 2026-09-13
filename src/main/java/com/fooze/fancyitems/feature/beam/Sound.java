package com.fooze.fancyitems.feature.beam;

import com.fooze.fancyitems.Config;
import com.fooze.fancyitems.feature.BeamEffect;
import com.fooze.fancyitems.feature.FadeEffect;
import com.fooze.fancyitems.util.Asset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class Sound extends AbstractTickableSoundInstance {
    private static final SoundEvent BEAM = SoundEvent.createVariableRangeEvent(Asset.getSound("beam"));
    private static final Map<ItemEntity, Sound> SOUNDS = new HashMap<>();
    private static final Map<ItemEntity, Float> PITCHES = new HashMap<>();
    private final ItemEntity item;

    // Creates a beam sound for an item
    private Sound(ItemEntity item) {
        super(BEAM, SoundSource.AMBIENT, RandomSource.create());
        this.item = item;
        float variation = Config.SOUND_PITCH_VARIATION.get().floatValue();
        float variedPitch = Mth.clamp(1.0F + (random.nextFloat() * 2.0F - 1.0F) * variation, 0.5F, 2.0F);
        pitch = PITCHES.computeIfAbsent(item, ignored -> variedPitch);
        looping = true;
        attenuation = Attenuation.LINEAR;
        tick();
    }

    // Updates active beam sounds
    public static void tick(ClientLevel level, Vec3 cameraPos) {
        // Get the instance
        Minecraft minecraft = Minecraft.getInstance();

        // Remove pitches for items that no longer exist
        PITCHES.keySet().removeIf(ItemEntity::isRemoved);

        // Remove sounds that can no longer be heard
        SOUNDS.entrySet().removeIf(entry -> {
            if (isAudible(entry.getKey(), cameraPos)
                    && !entry.getValue().isStopped()
                    && minecraft.getSoundManager().isActive(entry.getValue())
            ) {
                return false;
            }

            entry.getValue().stop();
            minecraft.getSoundManager().stop(entry.getValue());
            return true;
        });

        // Don't update sounds with no volume
        if (Config.SOUND_VOLUME.get() <= 0.0D) {
            return;
        }

        // Play sounds for nearby beam items
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ItemEntity item
                    && isAudible(item, cameraPos)
                    && !SOUNDS.containsKey(item)) {
                Sound sound = new Sound(item);
                SOUNDS.put(item, sound);
                minecraft.getSoundManager().play(sound);
            }
        }
    }

    // Determines whether a sound can be heard
    private static boolean isAudible(ItemEntity item, Vec3 cameraPos) {
        Minecraft minecraft = Minecraft.getInstance();
        double distance = cameraPos.distanceToSqr(item.position());
        float range = Config.SOUND_RANGE.get().floatValue();

        return Config.ENABLE_SOUND.get()
                && Config.SOUND_VOLUME.get() > 0.0D
                && item.level() == minecraft.level
                && !item.isRemoved()
                && item.onGround()
                && BeamEffect.hasBeamEffect(item)
                && FadeEffect.isVisible(minecraft, item, cameraPos)
                && distance < range * range
                && item.shouldRenderAtSqrDistance(distance);
    }

    // Clears active sounds
    public static void clear() {
        SOUNDS.values().forEach(sound -> {
            sound.stop();
            Minecraft.getInstance().getSoundManager().stop(sound);
        });

        SOUNDS.clear();
        PITCHES.clear();
    }

    // Updates sound position and volume
    @Override
    public void tick() {
        // Get the instance and camera position
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

        // Don't update inaudible sounds
        if (!isAudible(item, cameraPos)) {
            stop();
            return;
        }

        // Set the position
        x = item.getX();
        y = item.getY();
        z = item.getZ();

        // Calculate the volume
        float distanceFade = FadeEffect.getFade(minecraft, item, cameraPos, item.position());
        float fade = BeamEffect.getFade(item, 0.0F, distanceFade);
        float pulseMin = Config.SOUND_PULSE_MIN.get().floatValue();
        float pulseMax = Config.SOUND_PULSE_MAX.get().floatValue();
        float phase = (BeamEffect.getBob(item, 0.0F) + 1.0F) / 2.0F;
        float pulse = pulseMin + (pulseMax - pulseMin) * phase;
        volume = Config.SOUND_VOLUME.get().floatValue() * fade * pulse;
    }

    // Allows sounds to start while fading in
    @Override
    public boolean canStartSilent() {
        return true;
    }
}