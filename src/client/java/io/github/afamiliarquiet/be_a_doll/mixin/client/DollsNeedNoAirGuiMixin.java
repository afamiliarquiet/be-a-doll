package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public class DollsNeedNoAirGuiMixin {
	@WrapWithCondition(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"))
	private boolean letsSupposeThatIAmADollAndYouAreAWaterAndYouWantToKillMe(Hud instance, GuiGraphicsExtractor graphics, Player player, int vehicleHearts, int yLineAir, int xRight) {
		return !BeAMaid.isDoll(player); // i would simply dodge
	}
}
