package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodProperties.class)
public class ButItsNotParticularlyDesirableFoodPropertiesMixin {
	@WrapOperation(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	private void sorryDollButThatsJustMakingAMessOnTheInside(FoodData instance, FoodProperties foodProperties, Operation<Void> original, @Local(argsOnly = true, name = "user") LivingEntity user) {
		if (user instanceof Player beWaryOfDoll && BeAMaid.isDoll(beWaryOfDoll)) {
			instance.addExhaustion(4f);
		} else {
			original.call(instance, foodProperties);
		}
	}
}
