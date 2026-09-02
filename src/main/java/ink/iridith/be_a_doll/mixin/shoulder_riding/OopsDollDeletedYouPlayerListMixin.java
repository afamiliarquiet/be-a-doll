package ink.iridith.be_a_doll.mixin.shoulder_riding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class OopsDollDeletedYouPlayerListMixin {
	@ModifyExpressionValue(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasExactlyOnePlayerPassenger()Z"))
	private boolean waitWaitStopDontRemoveThatPlayer(boolean original, @Local(argsOnly = true) ServerPlayer disconnected, @Local Entity entity) {
		return original && entity.getPassengersAndSelf().noneMatch(current -> current != null && current.isAlwaysTicking() && current != disconnected);
	}
}
