package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ActuallyDollsCanPretendToEatItemMixin {
	// todo: find way to impl in 1.20.1
//	@ModifyExpressionValue(method = "getMaxUseTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/FoodComponent;getEatTicks()I"))
//	private int slowlyNibbling(int original, @Local(argsOnly = true, ordinal = 0) ItemStack stack, @Local(argsOnly = true, ordinal = 0) LivingEntity user, @Local(ordinal = 0) FoodComponent food) {
//		// hm. actually this is maybe better food detection than checking foodcomponent on 1.21.8?
//		// i forget if non-foods can have nutrition
//		// then again, this interferes with any special dollfoods that could be made.
//		if (food.nutrition() > 0 && user instanceof PlayerEntity player && BeAMaid.isDoll(player)) {
//			// note the magic number 3
//			return original * 3;
//		} else {
//			return original;
//		}
//	}
}
