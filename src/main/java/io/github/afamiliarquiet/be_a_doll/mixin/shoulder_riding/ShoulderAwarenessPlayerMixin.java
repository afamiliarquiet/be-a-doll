package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.afamiliarquiet.be_a_doll.BeADecoration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class ShoulderAwarenessPlayerMixin extends LivingEntity {
	@Shadow
	public abstract HumanoidArm getMainArm();

	protected ShoulderAwarenessPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
    @Definition(id = "getShoulderEntityLeft", method = "Lnet/minecraft/world/entity/player/Player;getShoulderEntityLeft()Lnet/minecraft/nbt/CompoundTag;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z")
	@Expression("this.getShoulderEntityLeft().isEmpty()")
	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkLeftShoulder(boolean original) {
		return BeADecoration.shoulderEntityIsEmpty(this, original, HumanoidArm.LEFT);
	}

	// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
    @Definition(id = "getShoulderEntityRight", method = "Lnet/minecraft/world/entity/player/Player;getShoulderEntityRight()Lnet/minecraft/nbt/CompoundTag;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z")
	@Expression("this.getShoulderEntityRight().isEmpty()")
	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkRightShoulder(boolean original) {
		return BeADecoration.shoulderEntityIsEmpty(this, original, HumanoidArm.RIGHT);
	}
}
