package ink.iridith.be_a_doll.letters;

import ink.iridith.be_a_doll.BeADoll;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

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
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void neodollsYoureSoLuckyIHaveSoMuchLoveInMyHeartForYouThisIsSuchAPointlessChangeToHaveToMake(S2CDollDismountLetter letter, IPayloadContext context) {
		letter.dismountingDollIds().forEach(id -> {
			Entity ridingDoll = context.player().level().getEntity(id);
			if (ridingDoll != null) {
				ridingDoll.removeVehicle();
			}
		});
	}
}
