package ink.iridith.be_a_doll.letters;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.item.DollcraftItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

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
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void neodollsILoveYou(S2CDollRepairedLetter letter, IPayloadContext context) {
		Entity repairedEntity = context.player().level().getEntity(letter.entityId());
		if (repairedEntity instanceof Player letThereBeDoll) {
			DollcraftItem.spawnRepairParticles(letThereBeDoll, letter.material(), 16);
		}
	}
}
