package ink.iridith.be_a_doll.mixin.synthetic_treats;

import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SyntheticResistanceLivingEntityMixin {
	@Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
	private void ifDollThenNo(CallbackInfoReturnable<Boolean> cir) {
		//noinspection ConstantValue
		if ((Object)this instanceof Player couldThisBeDoll && BeAMaid.isDoll(couldThisBeDoll)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void ifDollThenNoHunger(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		//noinspection ConstantValue
		if ((Object)this instanceof Player couldThisBeDoll && BeAMaid.isDoll(couldThisBeDoll)) {
			if (effect.is(MobEffects.HUNGER)) {
				cir.setReturnValue(false);
			}
		}
	}
}
