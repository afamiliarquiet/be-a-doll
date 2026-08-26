package io.github.afamiliarquiet.be_a_doll.mixin;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class DollNameTagItemMixin {
	@Inject(at = @At("HEAD"), method = "interactLivingEntity", cancellable = true)
	private void useOnDoll(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type, CallbackInfoReturnable<InteractionResult> cir) {
		if (target instanceof Player doll && BeAMaid.isDoll(doll)) {
			Component text = itemStack.get(DataComponents.CUSTOM_NAME);
			if (text != null) {
				if (!player.level().isClientSide() && target.isAlive()) {
					BeALibrarian.relabelDoll(doll, text);
					itemStack.shrink(1);
				}

				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}
}
