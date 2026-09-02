package ink.iridith.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ink.iridith.be_a_doll.BeADecoration;
import ink.iridith.be_a_doll.letters.S2CDollDismountLetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class ShoulderAwarenessPlayerMixin extends LivingEntity {
	@Shadow
	public abstract @NotNull HumanoidArm getMainArm();

	protected ShoulderAwarenessPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getShoulderEntityLeft()Lnet/minecraft/nbt/CompoundTag;"))
	private CompoundTag checkLeftShoulder(CompoundTag original) {
		if (BeADecoration.shoulderEntityIsEmpty(this, original.isEmpty(), HumanoidArm.LEFT)) {
			return original;
		} else {
			CompoundTag narwhal = new CompoundTag();
			narwhal.putBoolean("soFullOfStuff", true);
			return narwhal;
		}
	}

	@ModifyExpressionValue(method = "setEntityOnShoulder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getShoulderEntityRight()Lnet/minecraft/nbt/CompoundTag;"))
	private CompoundTag checkRightShoulder(CompoundTag original) {
		if (BeADecoration.shoulderEntityIsEmpty(this, original.isEmpty(), HumanoidArm.RIGHT)) {
			return original;
		} else {
			CompoundTag narwhal = new CompoundTag();
			narwhal.putBoolean("soFullOfStuff", true);
			return narwhal;
		}
	}

	@Inject(method = "removeVehicle", at = @At("HEAD"))
	private void letGoOfIt(CallbackInfo ci) {
		if (this.getVehicle() instanceof ServerPlayer serverPlayerMount) {
			// the instanceof spe already kinda checks for this being on server. should be fine.
			// wait maybe not? it's loading spe on client, is that bad? nah it should be fine.
			// blame backporting if this breaks things, spe's supposed to override dismountVehicle so i can inject there
			PacketDistributor.sendToPlayer(serverPlayerMount, new S2CDollDismountLetter(List.of(this.getId())));
		}
	}
}
