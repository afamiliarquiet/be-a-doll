package ink.iridith.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class ButItsNotParticularlyDesirablePlayerMixin {
	@WrapOperation(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	private void sorryDollButThatsJustMakingAMessOnTheInside(FoodData instance, FoodProperties foodComponent, Operation<Void> original) {
		if (BeAMaid.isDoll((Player)(Object) this)) {
			instance.addExhaustion(4f);
		} else {
			original.call(instance, foodComponent);
		}
	}
}
