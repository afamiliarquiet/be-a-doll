package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class ButItsNotParticularlyDesirablePlayerEntityMixin {
	@WrapOperation(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"))
	private void sorryDollButThatsJustMakingAMessOnTheInside(HungerManager instance, Item item, ItemStack stack, Operation<Void> original) {
		if (BeAMaid.isDoll((PlayerEntity)(Object) this)) {
			instance.addExhaustion(4f);
		} else {
			original.call(instance, item, stack);
		}
	}
}
