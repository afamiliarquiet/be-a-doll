package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class DollReadableNameLivingEntityRendererMixin {
    @Definition(id = "getCameraEntity", method = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;")
	@Expression("? != ?.getCameraEntity()")
	@ModifyExpressionValue(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean orDoll(boolean original) {
		return original || BeAMaid.isDoll(Minecraft.getInstance().player);
	}
}
