package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class DollsNeedNoAirGuiMixin {
	@WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderAirBubbles(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;III)V"))
	private boolean letsSupposeThatIAmADollAndYouAreAWaterAndYouWantToKillMe(Gui instance, GuiGraphics context, Player player, int heartCount, int top, int left) {
		return !BeAMaid.isDoll(player); // i would simply dodge
	}
}
