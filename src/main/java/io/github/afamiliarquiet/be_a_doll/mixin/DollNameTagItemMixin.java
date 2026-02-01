package io.github.afamiliarquiet.be_a_doll.mixin;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class DollNameTagItemMixin {
	@Inject(at = @At("HEAD"), method = "useOnEntity", cancellable = true)
	private void useOnDoll(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (entity instanceof PlayerEntity doll && BeAMaid.isDoll(doll) && stack.hasCustomName()) {
			Text text = stack.getName();
			if (text != null) {
				if (!user.getWorld().isClient && entity.isAlive()) {
					BeALibrarian.relabelDoll(doll, text);
					stack.decrement(1);
				}

				cir.setReturnValue(ActionResult.SUCCESS);
			}
		}
	}
}
