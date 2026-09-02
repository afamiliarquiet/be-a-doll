package ink.iridith.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public abstract class LightweightDollPowderSnowBlockMixin {
	@ModifyExpressionValue(method = "canEntityWalkOnPowderSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z"))
	private static boolean dollsCan(boolean original, @Local(argsOnly = true) Entity entity) {
		return original || (entity instanceof Player dollRequiredAhead && BeAMaid.isDoll(dollRequiredAhead));
	}
}
