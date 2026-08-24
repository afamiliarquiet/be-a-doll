package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemStack.class)
public abstract class AwardWinningFoodActorItemStackMixin implements DataComponentHolder {
	@ModifyArg(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/Consumable;shouldEmitParticlesAndSounds(I)Z"), index = 0)
	private int spitOutAsMuchAsPossible(int remainingUseTicks, @Local(argsOnly = true, ordinal = 0) LivingEntity user) {
		if (get(DataComponents.FOOD) != null && user instanceof Player dollODoll && BeAMaid.isDoll(dollODoll)) {
			// preserve the % 4 but otherwise compress to typical
			// note the magic number 3
			int compensated = remainingUseTicks / 3;
			compensated -= compensated % 4;
			compensated += remainingUseTicks % 4;
			return compensated;
		} else {
			return remainingUseTicks;
		}
	}
}
