package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeASelf;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public record C2SEssenceAlterationLetter(boolean inserting) implements FabricPacket {
	public static final PacketType<C2SEssenceAlterationLetter> ID = PacketType.create(
		BeADoll.id("essence_alteration_letter"),
		buf -> new C2SEssenceAlterationLetter(buf.readBoolean())
	);

	public static void receive(C2SEssenceAlterationLetter letter, ServerPlayerEntity player) {
		PlayerScreenHandler handler = player.playerScreenHandler;
		ItemStack clickProcessedStack = BeASelf.clickSelf(handler.getCursorStack(), player, letter.inserting());
		if (clickProcessedStack != null && !player.getAbilities().creativeMode) {
			handler.setCursorStack(clickProcessedStack);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeBoolean(this.inserting);
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
