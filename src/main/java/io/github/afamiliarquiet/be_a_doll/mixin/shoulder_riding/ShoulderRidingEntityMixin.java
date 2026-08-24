package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeADecoration;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ShoulderRidingEntityMixin {
	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "getPassengerAttachmentPoint(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityDimensions;F)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
	private void getDollSlot(Entity passenger, EntityDimensions dimensions, float scaleFactor, CallbackInfoReturnable<Vec3> cir) {
		// intellij please give up on this
		//noinspection ConstantValue
		if ((Object)this instanceof Player playerMount && passenger instanceof Player doll && BeAMaid.isDoll(doll)) {
			Vec3 dollSlot = BeADecoration.getDollAttachmentPos(playerMount, doll, dimensions, scaleFactor);

			if (dollSlot != null) {
				cir.setReturnValue(dollSlot);
			}
		}
	}

	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "canAddPassenger", cancellable = true)
	private void twoDolls(Entity passenger, CallbackInfoReturnable<Boolean> cir) {
		// oh i think i get it now. there's probably a special exception if it's just (object)this instanceof (seemsimpossible)
		//noinspection ConstantValue
		if ((Object)this instanceof Player playerMount && passenger instanceof Player doll) {
			cir.setReturnValue(BeADecoration.canAddPassenger(playerMount, doll));
		}
	}

	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "getDismountLocationForPassenger", cancellable = true)
	private void placeAtFeetPlease(LivingEntity passenger, CallbackInfoReturnable<Vec3> cir) {
		// ...
		//noinspection ConstantValue
		if ((Object)this instanceof Player playerMount && passenger instanceof Player doll) {
			cir.setReturnValue(BeADecoration.updatePassengerForDismount(playerMount, doll));
		}
	}

	// `this` is the doll
	@ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
	private boolean forDirtsSakeJustLetMeRideOtherPlayers(boolean original, @Local(argsOnly = true) Entity mount) {
		// i've seen you do this before intellij. and you've gotta stop it
		//noinspection ConstantValue
		if (mount instanceof Player && (Object)this instanceof Player doll && BeAMaid.isDoll(doll)) {
			return true;
		} else {
			return original;
		}
	}
}
