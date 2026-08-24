package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeAResearcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class SyntheticResistancePlayerMixin extends LivingEntity {
	protected SyntheticResistancePlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void dollsAreImmuneToDrowningAndFreezing(ServerLevel world, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (BeAMaid.isDoll((Player)(Object)this)) { // full metal alchemist..
			if (source.is(BeAResearcher.DOLL_IMMUNE)) {
				cir.setReturnValue(true);
			}
		} // full metal alchemist!
	}
}
