package io.github.afamiliarquiet.be_a_doll.mixin;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollLabelLetter;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollVariantLetter;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class NotifyOfDollMixin {
	@Inject(method = "onPlayerConnect", at = @At("RETURN"))
	public void notifyPlayerIfDoll(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		// fabric's JOIN event runs before the client is aware of the entity,
		// meaning that entity-id-based synchronization wont work there
		// so we have to do it here
		if(BeAMaid.isDoll(player)) {
			S2CDollVariantLetter variantLetter = new S2CDollVariantLetter(player.getId(), BeALibrarian.inspectDollMaterial(player));
			S2CDollLabelLetter nameLetter = new S2CDollLabelLetter(player.getId(), Optional.ofNullable(BeALibrarian.inspectDollLabel(player)));
			PlayerLookup.tracking(player)
				.forEach(other -> {
					ServerPlayNetworking.send(other, variantLetter);
					ServerPlayNetworking.send(other, nameLetter);
				});
			ServerPlayNetworking.send(player, variantLetter);
			ServerPlayNetworking.send(player, nameLetter);
		}
	}
}
