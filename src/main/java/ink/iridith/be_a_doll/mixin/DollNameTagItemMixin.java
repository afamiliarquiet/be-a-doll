package ink.iridith.be_a_doll.mixin;

import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeALibrarian;
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
	private void useOnDoll(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (entity instanceof Player doll && BeAMaid.isDoll(doll)) {
			Component text = stack.get(DataComponents.CUSTOM_NAME);
			if (text != null) {
				if (!user.level().isClientSide && entity.isAlive()) {
					BeALibrarian.relabelDoll(doll, text);
					stack.shrink(1);
				}

				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}
}
