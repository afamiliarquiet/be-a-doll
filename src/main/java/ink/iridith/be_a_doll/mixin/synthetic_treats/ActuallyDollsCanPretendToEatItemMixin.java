package ink.iridith.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ActuallyDollsCanPretendToEatItemMixin {
	@ModifyExpressionValue(method = "getUseDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodProperties;eatDurationTicks()I"))
	private int slowlyNibbling(int original, @Local(argsOnly = true, ordinal = 0) ItemStack stack, @Local(argsOnly = true, ordinal = 0) LivingEntity user, @Local(ordinal = 0) FoodProperties food) {
		// hm. actually this is maybe better food detection than checking foodcomponent on 1.21.8?
		// i forget if non-foods can have nutrition
		// then again, this interferes with any special dollfoods that could be made.
		if (food.nutrition() > 0 && user instanceof Player player && BeAMaid.isDoll(player)) {
			// note the magic number 3
			return original * 3;
		} else {
			return original;
		}
	}
}
