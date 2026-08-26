package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import io.github.afamiliarquiet.be_a_doll.item.RibbonItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class RibbonPriorityEntityMixin {
	@Inject(method = "interact", at = @At(value = "HEAD"), cancellable = true)
	private void orRibbon(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
		// i wanted to try injecting after the method already got the stack in hand for me but..
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.is(BeACollector.DOLL_RIBBON)) {
			cir.setReturnValue(((RibbonItem)handStack.getItem()).useToTryRiding(player, (Entity)(Object)this));
		}
	}
}
