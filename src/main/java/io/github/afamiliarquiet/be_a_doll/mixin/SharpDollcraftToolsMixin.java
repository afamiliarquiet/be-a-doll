package io.github.afamiliarquiet.be_a_doll.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageEnchantment.class)
public class SharpDollcraftToolsMixin {
	@ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
	public boolean dollToolsAreSharp(boolean original, @Local(argsOnly = true) ItemStack stack) {
		return original
			|| stack.isOf(BeACollector.CARVING_KNIFE)
			|| stack.isOf(BeACollector.FLUSH_CUTTER)
			|| stack.isOf(BeACollector.MODELING_TOOL)
			|| stack.isOf(BeACollector.WATCHMAKERS_SCREWDRIVER)
			|| stack.isOf(BeACollector.SEWING_NEEDLE);
	}
}
