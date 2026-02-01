package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeASelf;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

// me sowing: haha fsdf;lkj yeah!!! thanks creative inventory screen now i know how to do an evil and strange slot!!
// me reaping: well this fdj;lksing sucks. why are you being evil to me too.
public record C2SCreativeEssenceAlterationLetter(boolean inserting, ItemStack statedCursorStack) implements FabricPacket {
	public static final PacketType<C2SCreativeEssenceAlterationLetter> ID = PacketType.create(
		BeADoll.id("creative_essence_alteration_letter"),
		buf -> new C2SCreativeEssenceAlterationLetter(buf.readBoolean(), buf.readItemStack())
	);

	public static void receive(C2SCreativeEssenceAlterationLetter letter, ServerPlayerEntity player) {
		if (!player.getAbilities().creativeMode) {
			return; // how dare you. this is for creative only
		}
		// fire this off still for the tf effects n such to happen on server
		BeASelf.clickSelf(letter.statedCursorStack(),player, letter.inserting());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeBoolean(this.inserting);
		packetByteBuf.writeItemStack(this.statedCursorStack);
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
