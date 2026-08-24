package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class SuffocationResistanceLivingEntityMixin extends Entity {
	public SuffocationResistanceLivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyReturnValue(method = "isInWall", at = @At("RETURN"))
	private boolean notIfMyRideIsOkay(boolean original) {
		if (this.getVehicle() instanceof Player myRide) {
			return original && myRide.isInWall();
		} else {
			return original;
		}
	}
}
