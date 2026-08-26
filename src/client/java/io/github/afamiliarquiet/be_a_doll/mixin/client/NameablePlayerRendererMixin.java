package io.github.afamiliarquiet.be_a_doll.mixin.client;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.DollishState;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class NameablePlayerRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {
	public NameablePlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(at = @At("RETURN"), method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V")
	private void alsoCheckDollness(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		if (entity instanceof Player player) { // 26.1 port.. todo - do i need to respect mannequins? idk
			DollishState dollishState = (DollishState) state;
			dollishState.be_a_doll$setDoll(BeAMaid.isDoll(player));
			// no need for now
//		dollishState.be_a_doll$setVariant(BeALibrarian.inspectDollMaterial(player));
			dollishState.be_a_doll$setDollName(BeALibrarian.inspectDollLabel(player));
			dollishState.be_a_doll$setTargeted(player == this.entityRenderDispatcher.crosshairPickEntity || player == Minecraft.getInstance().getCameraEntity());

			if (dollishState.be_a_doll$isDoll() && !Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
				if (dollishState.be_a_doll$isTargeted()) {
					if (dollishState.be_a_doll$getDollName() != null) {
						state.nameTag = dollishState.be_a_doll$getDollName();
					} // else { defer to the grand elser }
				} else {
					state.nameTag = null;
					// don't render if doll and not targeted
				}
			} // else { defer to the grand elser }
//		if (dollishState.be_a_doll$isDoll() && state.squaredDistanceToCamera < 4096.0 && player == this.dispatcher.targetedEntity || player == MinecraftClient.getInstance().getCameraEntity()) {
//			// todone - add f3 override for moderation + doll name from nametag
//			dollishState.be_a_doll$setDollName(BeALibrarian.inspectDollLabel(player));
//			if (dollishState.be_a_doll$getDollName() == null) { // notodo - remove this probably? but... it seems like attachments are broken
//				dollishState.be_a_doll$setDollName(this.getDisplayName(player));
//			}
//		} else {
//			dollishState.be_a_doll$setDollName(null);
//		}
		}
	}

//	@WrapMethod(method = "renderNameTag(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
//	private void butDollsAreNoDifferent(AvatarRenderState state, Component text, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, Operation<Void> original) {
//		DollishState dollishState = (DollishState) state;
//		if (dollishState.be_a_doll$isDoll() && !Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
//			if (dollishState.be_a_doll$isTargeted()) {
//				if (dollishState.be_a_doll$getDollName() != null) {
//					original.call(state, dollishState.be_a_doll$getDollName(), matrixStack, vertexConsumerProvider, i);
//					return;
//				} // else { defer to the grand elser }
//			} else {
//				return; // don't render if doll and not targeted
//			}
//		} // else { defer to the grand elser }
//
//		// the grand elser
//		original.call(state, text, matrixStack, vertexConsumerProvider, i);
//	}
}
