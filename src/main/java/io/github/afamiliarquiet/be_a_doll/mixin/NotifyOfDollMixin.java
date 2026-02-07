package io.github.afamiliarquiet.be_a_doll.mixin;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollLabelLetter;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollVariantLetter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EntityTrackerEntry.class)
public class NotifyOfDollMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "startTracking", at = @At("RETURN"))
	public void notifyPlayerIfDoll(ServerPlayerEntity player, CallbackInfo ci) {
		// fabric's START_TRACKING event runs before the client is aware of the entity,
		// meaning that entity-id-based synchronization wont work there
		// so we have to do it here
		if(this.entity instanceof PlayerEntity potentialDoll && BeAMaid.isDoll(potentialDoll)) {
			S2CDollVariantLetter variantLetter = new S2CDollVariantLetter(potentialDoll.getId(), BeALibrarian.inspectDollMaterial(potentialDoll));
			S2CDollLabelLetter nameLetter = new S2CDollLabelLetter(potentialDoll.getId(), Optional.ofNullable(BeALibrarian.inspectDollLabel(potentialDoll)));
			ServerPlayNetworking.send(player, variantLetter);
			ServerPlayNetworking.send(player, nameLetter);
		}
	}
}
