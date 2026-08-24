package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class DollsCantEatPlayerMixin {
	@Inject(at = @At("HEAD"), method = "canEat", cancellable = true)
	private void notIfDoll(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
		if (BeAMaid.isDoll((Player)(Object)this)) {
			cir.setReturnValue(true);
		}
	}
}
