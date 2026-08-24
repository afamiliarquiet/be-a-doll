package io.github.afamiliarquiet.be_a_doll.mixin.shoulder_riding;

import com.mojang.authlib.GameProfile;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollDismountLetter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class DollsMustDropServerPlayerMixin extends Player {
	public DollsMustDropServerPlayerMixin(Level world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;removeEntitiesOnShoulder()V"))
	private void untieDolls(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
		List<Entity> passengers = this.getPassengers();
		if (!passengers.isEmpty()) {
			ServerPlayNetworking.send((ServerPlayer) (Object) this, new S2CDollDismountLetter(passengers.stream().map(Entity::getId).toList()));
			this.ejectPassengers();
		}
	}

	@Inject(method = "removeVehicle", at = @At("HEAD"))
	private void letGoOfIt(CallbackInfo ci) {
		if (this.getVehicle() instanceof ServerPlayer serverPlayerMount) {
			ServerPlayNetworking.send(serverPlayerMount, new S2CDollDismountLetter(List.of(this.getId())));
		}
	}
}
