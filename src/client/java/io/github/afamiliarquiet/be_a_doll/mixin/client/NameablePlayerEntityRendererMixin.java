package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.pehkui.api.ScaleData;

@Mixin(PlayerEntityRenderer.class)
public abstract class NameablePlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public NameablePlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@WrapMethod(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	private void butDollsAreNoDifferent(AbstractClientPlayerEntity player, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Operation<Void> original) {
//		DollishState dollishState = (DollishState) state;
		boolean isDoll = BeAMaid.isDoll(player);
		Text name = BeALibrarian.inspectDollLabel(player);
		boolean targeted = player == this.dispatcher.targetedEntity || player == MinecraftClient.getInstance().getCameraEntity();

		if(isDoll) {
			// pehkui also scales down the nametag
			// this undoes that
			ScaleData dollScale = BeALibrarian.DOLL_SCALE_TYPE.getScaleData(player);
			matrixStack.push();
			float scale = 1.f / dollScale.getScale();
			matrixStack.scale(scale, scale, scale);
			matrixStack.translate(0, -player.getNameLabelHeight(), 0);
		}

		if (isDoll && !MinecraftClient.getInstance().options.debugEnabled) {
			if (targeted) {
				if (name != null) {
					original.call(player, name, matrixStack, vertexConsumerProvider, i);
					matrixStack.pop();
					return;
				} // else { defer to the grand elser }
			} else {
				return; // don't render if doll and not targeted
			}
		} // else { defer to the grand elser }

		// the grand elser
		original.call(player, text, matrixStack, vertexConsumerProvider, i);
		if(isDoll)
			matrixStack.pop();
	}
}
