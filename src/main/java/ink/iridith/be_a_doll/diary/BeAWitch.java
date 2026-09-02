package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.effect.CaredForEffect;
import ink.iridith.be_a_doll.effect.FragmentedStatusEffect;
import ink.iridith.be_a_doll.effect.OverflowingStatusEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeAWitch {
	private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, BeADoll.MOD_ID);

	public static final Holder<MobEffect> CARED_FOR = MOB_EFFECTS.register(
		"cared_for",
		() -> new CaredForEffect(MobEffectCategory.BENEFICIAL, 0xb0b8dd)
			.addAttributeModifier(Attributes.MAX_ABSORPTION, BeADoll.id("effect.cared_for"), 14.0, AttributeModifier.Operation.ADD_VALUE)
	);

	public static final Holder<MobEffect> FRAGMENTED = MOB_EFFECTS.register(
		"fragmented",
		() -> new FragmentedStatusEffect(MobEffectCategory.HARMFUL, 0xccb7c3)
	);

	public static final Holder<MobEffect> OVERFLOWING = MOB_EFFECTS.register(
		"overflowing",
		() -> new OverflowingStatusEffect(MobEffectCategory.BENEFICIAL, 0x93a4ea)
	);

	public static void putOnHat(IEventBus modBus) {
		MOB_EFFECTS.register(modBus);
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
