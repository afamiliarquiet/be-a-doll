package ink.iridith.be_a_doll.mixin;

import com.mojang.authlib.GameProfile;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ThrowNameSelfServerPlayerMixin extends Player {

	public ThrowNameSelfServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
	private void hehe(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		// if a doll looks straight up and throws a name tag, they can write on their own tag!
		if (this.getXRot() <= -88.5f && stack.is(Items.NAME_TAG) && BeAMaid.isDoll(this)) {
			Component text = stack.get(DataComponents.CUSTOM_NAME);
			if (text != null) {
				if (this.isAlive()) {
					BeALibrarian.relabelDoll(this, text);
					stack.shrink(1);
				}

				cir.setReturnValue(null);
			}
		}
	}
}
