package io.github.afamiliarquiet.be_a_doll.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class AnnihilateDollEffectsMixin {
	// dont send annihilated effects to the client
	// fixes the effect lingering on 0 seconds left until rejoin
	@WrapWithCondition(method = "onStatusEffectApplied", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
	public boolean anihilateEffects(ServerPlayNetworkHandler instance, Packet<?> packet, @Local(argsOnly = true) StatusEffectInstance effect) {
		LivingEntity doll = (LivingEntity) (Object) this;
		if(effect.getEffectType() == BeAWitch.OVERFLOWING.value() || effect.getEffectType() == BeAWitch.FRAGMENTED.value()) {
			return doll.getStatusEffect(effect.getEffectType()) == effect;
		}
		return true;
	}
}
