package ink.iridith.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class DollReadableNameLivingEntityRendererMixin {
	@ModifyExpressionValue(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
	private Entity orDoll(Entity original) {
		// should effectively make the entity != camera entity when doll, allowing nametag rendering
		return BeAMaid.isDoll(Minecraft.getInstance().player) ? null : original;
	}
}
