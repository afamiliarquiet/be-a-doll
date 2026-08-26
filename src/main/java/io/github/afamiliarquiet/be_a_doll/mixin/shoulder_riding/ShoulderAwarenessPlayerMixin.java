package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.afamiliarquiet.be_a_doll.BeADecoration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ShoulderAwarenessPlayerMixin extends LivingEntity {

	protected ShoulderAwarenessPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Definition(id = "getShoulderEntityLeft", method = "Lnet/minecraft/server/level/ServerPlayer;getShoulderEntityLeft()Lnet/minecraft/nbt/CompoundTag;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z")
	@Expression("this.getShoulderEntityLeft().isEmpty()")
	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkLeftShoulder(boolean original) {
		return BeADecoration.shoulderEntityIsEmpty(this, original, HumanoidArm.LEFT);
	}

	@Definition(id = "getShoulderEntityRight", method = "Lnet/minecraft/server/level/ServerPlayer;getShoulderEntityRight()Lnet/minecraft/nbt/CompoundTag;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z")
	@Expression("this.getShoulderEntityRight().isEmpty()")
	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkRightShoulder(boolean original) {
		return BeADecoration.shoulderEntityIsEmpty(this, original, HumanoidArm.RIGHT);
	}
}
