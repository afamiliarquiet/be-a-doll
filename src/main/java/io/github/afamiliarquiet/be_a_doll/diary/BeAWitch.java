package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.effect.CaredForStatusEffect;
import io.github.afamiliarquiet.be_a_doll.effect.FragmentedStatusEffect;
import io.github.afamiliarquiet.be_a_doll.effect.OverflowingStatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class BeAWitch {
	public static final RegistryEntry<StatusEffect> CARED_FOR = Registry.registerReference(
		Registries.STATUS_EFFECT,
		BeADoll.id("cared_for"),
		new CaredForStatusEffect(StatusEffectCategory.BENEFICIAL, 0xb0b8dd, 14.0f)
	);

	public static final RegistryEntry<StatusEffect> FRAGMENTED = Registry.registerReference(
		Registries.STATUS_EFFECT,
		BeADoll.id("fragmented"),
		new FragmentedStatusEffect(StatusEffectCategory.HARMFUL, 0xccb7c3, BeABug.FRAGMENTED)
	);

	public static final RegistryEntry<StatusEffect> OVERFLOWING = Registry.registerReference(
		Registries.STATUS_EFFECT,
		BeADoll.id("overflowing"),
		new OverflowingStatusEffect(StatusEffectCategory.BENEFICIAL, 0x93a4ea)
	);

	public static void putOnHat() {

	}

	// this essence ain't big enough for the two of us... *antimatter tumbleweed rolls past*
	public static void annihilate(LivingEntity entity, StatusEffectInstance overflowingInstance, StatusEffectInstance fragmentedInstance) {
		if (overflowingInstance != null && fragmentedInstance != null) { // my enemy-y-y-y-y
			int combatAdjustedOverflowDuration = overflowingInstance.getDuration() * 3;
			int fragmentedDuration = fragmentedInstance.getDuration();

			entity.removeStatusEffect(OVERFLOWING.value());
			entity.removeStatusEffect(FRAGMENTED.value());

			int overflowRemainder = (combatAdjustedOverflowDuration - fragmentedDuration) / 3;
			int fragmentedRemainder = fragmentedDuration - combatAdjustedOverflowDuration;

			if (overflowRemainder > 0) {
				entity.addStatusEffect(new StatusEffectInstance(OVERFLOWING.value(), overflowRemainder, overflowingInstance.getAmplifier()));
			}
			if (fragmentedRemainder > 0) {
				entity.addStatusEffect(new StatusEffectInstance(FRAGMENTED.value(), fragmentedRemainder, fragmentedInstance.getAmplifier()));
			}
		}
	}
}
