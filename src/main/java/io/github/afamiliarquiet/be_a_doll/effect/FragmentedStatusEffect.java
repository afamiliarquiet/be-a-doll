package io.github.afamiliarquiet.be_a_doll.effect;

import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleEffect;

// this could theoretically be what overflow extends. but i feel like the duelling vibe works better without doing that
public class FragmentedStatusEffect extends StatusEffect {
	public FragmentedStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	public FragmentedStatusEffect(StatusEffectCategory category, int color, ParticleEffect particleEffect) {
		super(category, color);
	}

	@Override
	public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		BeAWitch.annihilate(entity, entity.getStatusEffect(BeAWitch.OVERFLOWING.value()), entity.getStatusEffect(BeAWitch.FRAGMENTED.value()));
		super.onApplied(entity, attributes, amplifier);
	}
}
