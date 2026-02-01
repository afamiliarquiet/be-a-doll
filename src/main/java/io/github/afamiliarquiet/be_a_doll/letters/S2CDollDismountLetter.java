package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

// i'll move this into its own file if i make any more. but i don't think i will
public record S2CDollDismountLetter(List<Integer> dismountingDollIds) implements FabricPacket {
	public static final PacketType<S2CDollDismountLetter> ID = PacketType.create(
		BeADoll.id("doll_dismount_letter"),
		buf -> new S2CDollDismountLetter(buf.readIntList())
	);

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeIntList(new IntArrayList(this.dismountingDollIds));
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
