package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeACurator;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class DollRetexturedInGameHudMixin {
	@Shadow
	private int ticks;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getSaturationLevel()F"))
	private void alterHungerTextures(DrawContext context, CallbackInfo ci, @Share("variant") LocalRef<BeADoll.Variant> variant, @Local PlayerEntity player) {
		// if this has an impact on fps then SUFFER
		if (BeAMaid.isDoll(player)) {
			variant.set(BeALibrarian.inspectDollMaterial(player));
		}
	}

	@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3))
	private void alterEmptyHungerTexture(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original, @Share("variant") LocalRef<BeADoll.Variant> variant) {
		if(variant.get() != null)
			instance.drawTexture(variant.get().getFoodSpriteEmpty(), x, y, 0, 0, 9, 9, 9, 9);
		else original.call(instance, texture, x, y, u, v, width, height);
	}

	@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 4))
	private void alterHalfHungerTexture(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original, @Share("variant") LocalRef<BeADoll.Variant> variant) {
		if(variant.get() != null)
			instance.drawTexture(variant.get().getFoodSpriteHalf(), x, y, 0, 0, 9, 9, 9, 9);
		else original.call(instance, texture, x, y, u, v, width, height);
	}

	@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 5))
	private void alterFullHungerTexture(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original, @Share("variant") LocalRef<BeADoll.Variant> variant) {
		if(variant.get() != null)
			instance.drawTexture(variant.get().getFoodSpritFull(), x, y, 0, 0, 9, 9, 9, 9);
		else original.call(instance, texture, x, y, u, v, width, height);
	}

	@Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
	private void alterAbsorptionTexture(DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean half, CallbackInfo ci, @Local(argsOnly = true) InGameHud.HeartType heartType, @Local(argsOnly = true, ordinal = 0) boolean hardcore) {
		Identifier heartTexture = null;
		if (heartType == InGameHud.HeartType.ABSORBING && BeAMaid.isDoll(MinecraftClient.getInstance().player)) {
			if (half) {
				if (hardcore) {
					heartTexture = BeACurator.CARED_HEART_HARDCORE_HALF;
				} else {
					heartTexture =  BeACurator.CARED_HEART_HALF;
				}
			} else {
				if (hardcore) {
					heartTexture =  BeACurator.CARED_HEART_HARDCORE_FULL;
				} else {
					heartTexture =  BeACurator.CARED_HEART_FULL;
				}
			}
		}
		if (heartTexture != null) {
			context.drawTexture(heartTexture, x, y, 0, 0, 9, 9, 9, 9);
			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
	private boolean orOverflowing(boolean original, @Local(name = "playerEntity", ordinal = 0) PlayerEntity player) {
		return original || player.hasStatusEffect(BeAWitch.OVERFLOWING.value());
	}

	@ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getSaturationLevel()F"))
	private float resaturatingWave(float original, @Local PlayerEntity player, @Local(name="y", ordinal = 15) int index, @Local(name="z", ordinal = 16) LocalIntRef yPos) {
		if (player.hasStatusEffect(BeAWitch.OVERFLOWING.value()) && BeAMaid.isDoll(player)) {
			// if i was a super optimizer i could put the ticks % 15 outside the for loop.
			if (index == this.ticks % 25) {
				yPos.set(yPos.get() - 2);
			}

			return 1f; // basically cancels the if statement for random bobs
		} else {
			return original;
		}
	}
}
