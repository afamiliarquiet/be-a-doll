package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CDollRepairedLetter(int entityId, ItemStack material) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<S2CDollRepairedLetter> ID = new CustomPacketPayload.Type<>(BeADoll.id("doll_repaired_letter"));

	public static final StreamCodec<RegistryFriendlyByteBuf, S2CDollRepairedLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT,
		S2CDollRepairedLetter::entityId,
		ItemStack.STREAM_CODEC,
		S2CDollRepairedLetter::material,
		S2CDollRepairedLetter::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
