package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public abstract class LightweightDollPowderSnowBlockMixin {
	@Definition(id = "is", method = "Lnet/minecraft/world/entity/Entity;is(Lnet/minecraft/tags/TagKey;)Z")
	@Expression("?.is(?)")
	@ModifyExpressionValue(method = "canEntityWalkOnPowderSnow", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean dollsCan(boolean original, @Local(argsOnly = true, name = "entity") Entity entity) {
		return original || (entity instanceof Player dollRequiredAhead && BeAMaid.isDoll(dollRequiredAhead));
	}
}
