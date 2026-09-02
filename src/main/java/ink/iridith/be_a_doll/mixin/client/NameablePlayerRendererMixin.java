package ink.iridith.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerRenderer.class)
public abstract class NameablePlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public NameablePlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@WrapMethod(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V")
	private void butDollsAreNoDifferent(AbstractClientPlayer player, Component text, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, float f, Operation<Void> original) {
//		DollishState dollishState = (DollishState) state;
		boolean isDoll = BeAMaid.isDoll(player);
		Component name = BeALibrarian.inspectDollLabel(player);
		boolean targeted = player == this.entityRenderDispatcher.crosshairPickEntity || player == Minecraft.getInstance().getCameraEntity();

		if (isDoll && !Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
			if (targeted) {
				if (name != null) {
					original.call(player, name, matrixStack, vertexConsumerProvider, i, f);
					return;
				} // else { defer to the grand elser }
			} else {
				return; // don't render if doll and not targeted
			}
		} // else { defer to the grand elser }

		// the grand elser
		original.call(player, text, matrixStack, vertexConsumerProvider, i, f);
	}
}
