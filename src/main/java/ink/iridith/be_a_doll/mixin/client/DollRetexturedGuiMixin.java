package ink.iridith.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeACurator;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.diary.BeAWitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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

	@Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getSaturationLevel()F"))
	private void alterHungerTextures(GuiGraphics context, Player player, int top, int right, CallbackInfo ci,
                                     @Local(name = "identifier", ordinal = 0) LocalRef<ResourceLocation> emptyId,
                                     @Local(name = "identifier2", ordinal = 1) LocalRef<ResourceLocation> fullId,
                                     @Local(name = "identifier3", ordinal = 2) LocalRef<ResourceLocation> halfId
	) {
		// if this has an impact on fps then SUFFER
		if (BeAMaid.isDoll(player)) {
			BeADoll.Variant variant = BeALibrarian.inspectDollMaterial(player);
			emptyId.set(variant.getFoodSpriteEmpty());
			halfId.set(variant.getFoodSpriteHalf());
			fullId.set(variant.getFoodSpritFull());
		}
	}

	@ModifyExpressionValue(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui$HeartType;getSprite(ZZZ)Lnet/minecraft/resources/ResourceLocation;"))
	private ResourceLocation alterAbsorptionTexture(ResourceLocation original, @Local(argsOnly = true) Gui.HeartType heartType, @Local(argsOnly = true, ordinal = 0) boolean hardcore, @Local(argsOnly = true, ordinal = 2) boolean half) {
		if (heartType == Gui.HeartType.ABSORBING && BeAMaid.isDoll(Minecraft.getInstance().player)) {
			if (half) {
				if (hardcore) {
					return BeACurator.CARED_HEART_HARDCORE_HALF;
				} else {
					return BeACurator.CARED_HEART_HALF;
				}
			} else {
				if (hardcore) {
					return BeACurator.CARED_HEART_HARDCORE_FULL;
				} else {
					return BeACurator.CARED_HEART_FULL;
				}
			}
		} else {
			return original;
		}
	}

	// todone - this might also kill me. did it?
	// nah seems ok
	@ModifyExpressionValue(method = "renderHealthLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"))
	private boolean orOverflowing(boolean original, @Local(name = "playerEntity", ordinal = 0) Player player) {
		return original || player.hasEffect(BeAWitch.OVERFLOWING);
	}

	@ModifyExpressionValue(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getSaturationLevel()F"))
	private float resaturatingWave(float original, @Local(argsOnly = true) Player player, @Local(name="j", ordinal = 3) int index, @Local(name="k", ordinal = 4) LocalIntRef yPos) {
		if (player.hasEffect(BeAWitch.OVERFLOWING) && BeAMaid.isDoll(player)) {
			// if i was a super optimizer i could put the ticks % 15 outside the for loop.
			if (index == this.tickCount % 25) {
				yPos.set(yPos.get() - 2);
			}

			return 1f; // basically cancels the if statement for random bobs
		} else {
			return original;
		}
	}
}
