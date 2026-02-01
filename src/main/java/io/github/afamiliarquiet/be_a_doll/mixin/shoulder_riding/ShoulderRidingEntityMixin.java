package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import io.github.afamiliarquiet.be_a_doll.BeADecoration;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ShoulderRidingEntityMixin {
	@Shadow
	public abstract EntityDimensions getDimensions(EntityPose pose);

	@Shadow
	public abstract EntityPose getPose();

	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", cancellable = true)
	private void getDollSlot(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
		EntityDimensions dimensions = this.getDimensions(this.getPose());

		// intellij please give up on this
		//noinspection ConstantValue
		if ((Object)this instanceof PlayerEntity playerMount && passenger instanceof PlayerEntity doll && BeAMaid.isDoll(doll)) {
			// todo: use proper scale factor
			Vec3d dollSlot = BeADecoration.getDollAttachmentPos(playerMount, doll, dimensions, 1.f);

			if (dollSlot != null) {
				positionUpdater.accept(passenger, dollSlot.x, dollSlot.y, dollSlot.z);
				ci.cancel();
			}
		}
	}

	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "canAddPassenger", cancellable = true)
	private void twoDolls(Entity passenger, CallbackInfoReturnable<Boolean> cir) {
		// oh i think i get it now. there's probably a special exception if it's just (object)this instanceof (seemsimpossible)
		//noinspection ConstantValue
		if ((Object)this instanceof PlayerEntity playerMount && passenger instanceof PlayerEntity doll) {
			cir.setReturnValue(BeADecoration.canAddPassenger(playerMount, doll));
		}
	}

	// `this` is the mount
	@Inject(at = @At("HEAD"), method = "updatePassengerForDismount", cancellable = true)
	private void placeAtFeetPlease(LivingEntity passenger, CallbackInfoReturnable<Vec3d> cir) {
		// ...
		//noinspection ConstantValue
		if ((Object)this instanceof PlayerEntity playerMount && passenger instanceof PlayerEntity doll) {
			cir.setReturnValue(BeADecoration.updatePassengerForDismount(playerMount, doll));
		}
	}

	// todone - verify that this isn't necessary? can i just ride players now without fighting minecraft?
	// yeah seems so. i guess 1.21.8 or somewhereabouts decided players riding players is illegal.
//	// `this` is the doll
//	@ModifyExpressionValue(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z"))
//	private boolean forDirtsSakeJustLetMeRideOtherPlayers(boolean original, @Local(argsOnly = true) Entity mount) {
//		// i've seen you do this before intellij. and you've gotta stop it
//		//noinspection ConstantValue
//		if (mount instanceof PlayerEntity && (Object)this instanceof PlayerEntity doll && BeAMaid.isDoll(doll)) {
//			return true;
//		} else {
//			return original;
//		}
//	}
}
