package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public abstract class LightweightDollPowderSnowBlockMixin {
	// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
// TODO(Ravel): remapper for com.llamalad7.mixinextras.expression.Expression is not implemented
    @Definition(id = "isIn", method = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z")
	@Expression("?.isIn(?)")
	@ModifyExpressionValue(method = "canEntityWalkOnPowderSnow", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean dollsCan(boolean original, @Local(argsOnly = true) Entity entity) {
		return original || (entity instanceof Player dollRequiredAhead && BeAMaid.isDoll(dollRequiredAhead));
	}
}
