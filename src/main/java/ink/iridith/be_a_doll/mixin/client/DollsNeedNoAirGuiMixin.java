package ink.iridith.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class DollsNeedNoAirGuiMixin {
	// silly little way to dodge calling isDoll multiple times. pretty unnecessary
	@Unique
	private boolean be_a_doll$secureBankDepositBox = false;

	// hmmmmmmmm. mixing in to neomethod, interesting.
	// todone - did this kill me?
	// looks like you're good
//	@WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAir()I"))
	@WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAirSupply()I"))
	private int considerItToBeAtItsMaximumOrMaxedSoToSpeak(Player instance, Operation<Integer> original) {
		be_a_doll$secureBankDepositBox = BeAMaid.isDoll(instance);
		return be_a_doll$secureBankDepositBox ? instance.getMaxAirSupply() : original.call(instance);
	}

//	@ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSubmergedIn(Lnet/minecraft/tags/TagKey;)Z"))
	@ModifyExpressionValue(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean letsSupposeThatIAmADollAndYouAreAWaterAndYouWantToKillMe(boolean original) {
		return original && !be_a_doll$secureBankDepositBox; // i would simply dodge. or maybe not, backporting has made this less robust
	}
}
