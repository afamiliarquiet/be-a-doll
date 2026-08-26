package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ActuallyDollsCanPretendToEatItemMixin {
    @Definition(id = "getConsumeTicks", method = "Lnet/minecraft/world/item/component/Consumable;consumeTicks()I")
	@Expression("?.getConsumeTicks()")
	@ModifyExpressionValue(method = "getUseDuration", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int slowlyNibbling(int original, @Local(argsOnly = true, name = "itemStack") ItemStack itemStack, @Local(argsOnly = true, name = "user") LivingEntity user) {
		if (itemStack.get(DataComponents.FOOD) != null && user instanceof Player player && BeAMaid.isDoll(player)) {
			// note the magic number 3
			return original * 3;
		} else {
			return original;
		}
	}
}
