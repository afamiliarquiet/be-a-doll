package ink.iridith.be_a_doll.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ink.iridith.be_a_doll.diary.BeAWitch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class FragmentedDamageLivingEntityMixin {
	@ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
	private boolean butWhatIfFragmentedToo(boolean original) {
		return original || hasEffect(BeAWitch.FRAGMENTED);
	}

//	@ModifyExpressionValue(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getAmplifier()I"))
//	private int subtractFragmented(int original) {
//		if (hasStatusEffect(BeAWitch.FRAGMENTED)) {
//			return original - (getStatusEffect(BeAWitch.FRAGMENTED).getAmplifier() + 1);
//		} else {
//			return original;
//		}
//	}

	@WrapOperation(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"))
	private int waitCrapDontTouchThatNullNOOOOO(MobEffectInstance instance, Operation<Integer> original) {
//		return instance == null ? -1 : original.call(instance);
		int safeResistanceAmplifier = instance == null ? -1 : original.call(instance);
		if (hasEffect(BeAWitch.FRAGMENTED)) {
			return safeResistanceAmplifier - (getEffect(BeAWitch.FRAGMENTED).getAmplifier() + 1);
		} else {
			return safeResistanceAmplifier;
		}
	}

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);
}
