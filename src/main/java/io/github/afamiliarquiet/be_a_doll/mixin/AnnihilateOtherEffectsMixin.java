package io.github.afamiliarquiet.be_a_doll.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class AnnihilateOtherEffectsMixin {
	// dont send annihilated effects to the client
	// fixes the effect lingering on 0 seconds left until rejoin
	@WrapWithCondition(method = "onStatusEffectApplied", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;sendEffectToControllingPlayer(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
	public boolean anihilateEffects(LivingEntity instance, StatusEffectInstance effect) {
		if(effect.getEffectType() == BeAWitch.OVERFLOWING.value() || effect.getEffectType() == BeAWitch.FRAGMENTED.value()) {
			return instance.getStatusEffect(effect.getEffectType()) == effect;
		}
		return true;
	}
}
