package ink.iridith.be_a_doll.mixin.shoulder_riding;

import com.mojang.authlib.GameProfile;
import ink.iridith.be_a_doll.letters.S2CDollDismountLetter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class DollsMustDropServerPlayerMixin extends Player {

	public DollsMustDropServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;removeEntitiesOnShoulder()V"))
	private void untieDolls(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
		// todo - can prob move this to neo's event for onChangeGameType. will i? prob not. what're they gonna do, turn me into a human? aw fuck waIT. fuuck what happened to my body. this sucks. now i've got the frickin dysphoria doctors dont want you to know about that they were showing me in the ads in 2011
		List<Entity> passengers = this.getPassengers();
		if (!passengers.isEmpty()) {
			PacketDistributor.sendToPlayer((ServerPlayer) (Object) this, new S2CDollDismountLetter(passengers.stream().map(Entity::getId).toList()));
			this.ejectPassengers();
		}
	}
}
