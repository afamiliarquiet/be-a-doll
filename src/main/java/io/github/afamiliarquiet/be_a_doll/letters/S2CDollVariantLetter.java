package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record S2CDollVariantLetter(int entityId, BeADoll.Variant variant) implements FabricPacket {
	public static final PacketType<S2CDollVariantLetter> ID = PacketType.create(
		BeADoll.id("variant_letter"),
		buf -> new S2CDollVariantLetter(buf.readVarInt(), BeADoll.Variant.byIndex(buf.readVarInt()))
	);

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeVarInt(this.variant.getIndex());
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}
}
