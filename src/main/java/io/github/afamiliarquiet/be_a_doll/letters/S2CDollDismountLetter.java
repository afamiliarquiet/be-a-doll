package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

import java.util.List;

// i'll move this into its own file if i make any more. but i don't think i will
public record S2CDollDismountLetter(List<Integer> dismountingDollIds) implements CustomPacketPayload {
	public static final Type<S2CDollDismountLetter> ID = new Type<>(BeADoll.id("doll_dismount_letter"));

	public static final StreamCodec<ByteBuf, S2CDollDismountLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
		S2CDollDismountLetter::dismountingDollIds,
		S2CDollDismountLetter::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
