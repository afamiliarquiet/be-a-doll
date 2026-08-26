package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeACurator;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class DollRetexturedGuiMixin {
	@Shadow
	private int tickCount;

	@Inject(method = "extractFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getSaturationLevel()F"))
	private void alterHungerTextures(GuiGraphicsExtractor context, Player player, int top, int right, CallbackInfo ci,
									 @Local(name = "empty") LocalRef<Identifier> emptyId,
									 @Local(name = "half") LocalRef<Identifier> fullId,
									 @Local(name = "full") LocalRef<Identifier> halfId
	) {
		// if this has an impact on fps then SUFFER
		if (BeAMaid.isDoll(player)) {
			BeADoll.Variant variant = BeALibrarian.inspectDollMaterial(player);
			emptyId.set(variant.getFoodSpriteEmpty());
			halfId.set(variant.getFoodSpriteHalf());
			fullId.set(variant.getFoodSpritFull());
		}
	}

	@Definition(id = "getTexture", method = "Lnet/minecraft/client/gui/Gui$HeartType;getSprite(ZZZ)Lnet/minecraft/resources/Identifier;")
	@Expression("?.getTexture(?, ?, ?)")
	@ModifyExpressionValue(method = "extractHeart", at = @At("MIXINEXTRAS:EXPRESSION"))
	private Identifier alterAbsorptionTexture(Identifier original, @Local(argsOnly = true, name = "type") Gui.HeartType type, @Local(argsOnly = true, name = "isHardcore") boolean isHardcore, @Local(argsOnly = true, name = "half") boolean half) {
		if (type == Gui.HeartType.ABSORBING && BeAMaid.isDoll(Minecraft.getInstance().player)) {
			if (half) {
				if (isHardcore) {
					return BeACurator.CARED_HEART_HARDCORE_HALF;
				} else {
					return BeACurator.CARED_HEART_HALF;
				}
			} else {
				if (isHardcore) {
					return BeACurator.CARED_HEART_HARDCORE_FULL;
				} else {
					return BeACurator.CARED_HEART_FULL;
				}
			}
		} else {
			return original;
		}
	}

	@Definition(id = "hasEffect", method = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z")
	@Expression("?.hasEffect(?)")
	@ModifyExpressionValue(method = "extractPlayerHealth", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean orOverflowing(boolean original, @Local(name = "player") Player player) {
		return original || player.hasEffect(BeAWitch.OVERFLOWING);
	}

	@Definition(id = "getSaturationLevel", method = "Lnet/minecraft/world/food/FoodData;getSaturationLevel()F")
	@Expression("?.getSaturationLevel() <= ?")
	@ModifyExpressionValue(method = "extractFood", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean resaturatingWave(boolean original, @Local(argsOnly = true, name = "player") Player player, @Local(name = "i") int index, @Local(name = "yo") LocalIntRef yPos) {
		if (player.hasEffect(BeAWitch.OVERFLOWING) && BeAMaid.isDoll(player)) {
			// if i was a super optimizer i could put the ticks % 15 outside the for loop.
			if (index == this.tickCount % 25) {
				yPos.set(yPos.get() - 2);
			}

			return false; // basically cancels the if statement for random bobs
		} else {
			return original;
		}
	}
}
