package io.github.afamiliarquiet.be_a_doll.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CaredForStatusEffect extends StatusEffect {
	private final float absorptionAmount;

	public CaredForStatusEffect(StatusEffectCategory statusEffectCategory, int i, float absorptionAmount) {
		super(statusEffectCategory, i);
		this.absorptionAmount = absorptionAmount;
	}

	@Override
	public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		entity.setAbsorptionAmount(entity.getAbsorptionAmount() - Math.min(this.absorptionAmount, 4 * (amplifier + 1)));
		super.onRemoved(entity, attributes, amplifier);
	}

	@Override
	public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		entity.setAbsorptionAmount(entity.getAbsorptionAmount() + Math.min(this.absorptionAmount, 4 * (amplifier + 1)));
		super.onApplied(entity, attributes, amplifier);
	}
}
