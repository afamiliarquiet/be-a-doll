package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeASelf;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.InventoryMenu;

public record C2SEssenceAlterationLetter(boolean inserting) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<C2SEssenceAlterationLetter> ID = new CustomPacketPayload.Type<>(BeADoll.id("essence_alteration_letter"));

	public static final StreamCodec<ByteBuf, C2SEssenceAlterationLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		C2SEssenceAlterationLetter::inserting,
		C2SEssenceAlterationLetter::new
	);

	public static void receive(C2SEssenceAlterationLetter letter, ServerPlayNetworking.Context context) {
		InventoryMenu handler = context.player().inventoryMenu;
		ItemStack clickProcessedStack = BeASelf.clickSelf(handler.getCarried(), context.player(), letter.inserting());
		if (clickProcessedStack != null && !context.player().hasInfiniteMaterials()) {
			handler.setCarried(clickProcessedStack);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
