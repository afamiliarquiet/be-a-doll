package io.github.afamiliarquiet.be_a_doll.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class FragmentedDamageLivingEntityMixin {
	// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
    @Definition(id = "hasStatusEffect", method = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z")
	@Expression("?.hasStatusEffect(?)")
	@ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean butWhatIfFragmentedToo(boolean original) {
		return original || hasEffect(BeAWitch.FRAGMENTED);
	}

	// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
    @Definition(id = "getAmplifier", method = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I")
	@Expression("?.getAmplifier() + 1")
	@ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int subtractFragmented(int original) {
		if (hasEffect(BeAWitch.FRAGMENTED)) {
			return original - (getEffect(BeAWitch.FRAGMENTED).getAmplifier() + 1);
		} else {
			return original;
		}
	}

	@WrapOperation(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"))
	private int waitCrapDontTouchThatNullNOOOOO(MobEffectInstance instance, Operation<Integer> original) {
		return instance == null ? -1 : original.call(instance);
	}

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);
}
