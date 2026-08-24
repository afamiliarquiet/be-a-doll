package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.effect.FragmentedStatusEffect;
import io.github.afamiliarquiet.be_a_doll.effect.OverflowingStatusEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.AbsorptionMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;

public class BeAWitch {
	public static final Holder<MobEffect> CARED_FOR = Registry.registerForHolder(
		BuiltInRegistries.MOB_EFFECT,
		BeADoll.id("cared_for"),
		new AbsorptionMobEffect(MobEffectCategory.BENEFICIAL, 0xb0b8dd)
			.addAttributeModifier(Attributes.MAX_ABSORPTION, BeADoll.id("effect.cared_for"), 14.0, AttributeModifier.Operation.ADD_VALUE)
	);

	public static final Holder<MobEffect> FRAGMENTED = Registry.registerForHolder(
		BuiltInRegistries.MOB_EFFECT,
		BeADoll.id("fragmented"),
		new FragmentedStatusEffect(MobEffectCategory.HARMFUL, 0xccb7c3, BeABug.FRAGMENTED)
	);

	public static final Holder<MobEffect> OVERFLOWING = Registry.registerForHolder(
		BuiltInRegistries.MOB_EFFECT,
		BeADoll.id("overflowing"),
		new OverflowingStatusEffect(MobEffectCategory.BENEFICIAL, 0x93a4ea)
	);

	public static void putOnHat() {

	}

	// this essence ain't big enough for the two of us... *antimatter tumbleweed rolls past*
	public static void annihilate(LivingEntity entity, MobEffectInstance overflowingInstance, MobEffectInstance fragmentedInstance) {
		if (overflowingInstance != null && fragmentedInstance != null) { // my enemy-y-y-y-y
			int combatAdjustedOverflowDuration = overflowingInstance.getDuration() * 3;
			int fragmentedDuration = fragmentedInstance.getDuration();

			entity.removeEffect(OVERFLOWING);
			entity.removeEffect(FRAGMENTED);

			int overflowRemainder = (combatAdjustedOverflowDuration - fragmentedDuration) / 3;
			int fragmentedRemainder = fragmentedDuration - combatAdjustedOverflowDuration;

			if (overflowRemainder > 0) {
				entity.addEffect(new MobEffectInstance(OVERFLOWING, overflowRemainder, overflowingInstance.getAmplifier()));
			}
			if (fragmentedRemainder > 0) {
				entity.addEffect(new MobEffectInstance(FRAGMENTED, fragmentedRemainder, fragmentedInstance.getAmplifier()));
			}
		}
	}
}
