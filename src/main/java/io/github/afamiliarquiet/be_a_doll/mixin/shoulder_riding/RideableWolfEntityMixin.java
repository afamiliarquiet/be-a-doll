package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class RideableWolfEntityMixin extends TameableEntity {
	protected RideableWolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	// this is classic WolfEntity stuff right here they really made something special out of that interactMob
	// copying the solution i used for familiar magic
	@Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
	private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (player.getStackInHand(hand).isOf(BeACollector.DOLL_RIBBON)) {
			cir.setReturnValue(super.interactMob(player, hand));
		}
	}
}
