package io.github.afamiliarquiet.be_a_doll.mixin;

import com.mojang.authlib.GameProfile;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ThrowNameSelfServerPlayerEntityMixin extends PlayerEntity {

	public ThrowNameSelfServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
	private void hehe(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		// if a doll looks straight up and throws a name tag, they can write on their own tag!
		if (this.getPitch() <= -88.5f && stack.isOf(Items.NAME_TAG) && BeAMaid.isDoll(this) && stack.hasCustomName()) {
			Text text = stack.getName();
			if (text != null) {
				if (this.isAlive()) {
					BeALibrarian.relabelDoll(this, text);
					stack.decrement(1);
				}

				cir.setReturnValue(null);
			}
		}
	}
}
