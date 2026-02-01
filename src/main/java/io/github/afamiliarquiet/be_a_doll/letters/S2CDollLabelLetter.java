package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.Optional;

public record S2CDollLabelLetter(int entityId, Optional<Text> name) implements FabricPacket {
	public static final PacketType<S2CDollLabelLetter> ID = PacketType.create(
		BeADoll.id("label_letter"),
		buf -> new S2CDollLabelLetter(buf.readVarInt(), buf.readOptional(PacketByteBuf::readText))
	);

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeOptional(this.name, PacketByteBuf::writeText);
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
