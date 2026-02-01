package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record S2CDollRepressLetter(int entityId) implements FabricPacket {
	public static final PacketType<S2CDollRepressLetter> ID = PacketType.create(
		BeADoll.id("repress_letter"),
		buf -> new S2CDollRepressLetter(buf.readVarInt())
	);

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.entityId);
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
