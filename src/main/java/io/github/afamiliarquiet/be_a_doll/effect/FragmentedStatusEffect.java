package io.github.afamiliarquiet.be_a_doll.effect;

import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.particles.ParticleOptions;

// this could theoretically be what overflow extends. but i feel like the duelling vibe works better without doing that
public class FragmentedStatusEffect extends MobEffect {
	public FragmentedStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public FragmentedStatusEffect(MobEffectCategory category, int color, ParticleOptions particleEffect) {
		super(category, color, particleEffect);
	}

	@Override
	public void onEffectStarted(LivingEntity entity, int amplifier) {
		BeAWitch.annihilate(entity, entity.getEffect(BeAWitch.OVERFLOWING), entity.getEffect(BeAWitch.FRAGMENTED));
		super.onEffectStarted(entity, amplifier);
	}
}
