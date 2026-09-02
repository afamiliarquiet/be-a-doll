package ink.iridith.be_a_doll.letters;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.BeASelf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SEssenceAlterationLetter(boolean inserting) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<C2SEssenceAlterationLetter> ID = new CustomPacketPayload.Type<>(BeADoll.id("essence_alteration_letter"));

	public static final StreamCodec<ByteBuf, C2SEssenceAlterationLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		C2SEssenceAlterationLetter::inserting,
		C2SEssenceAlterationLetter::new
	);

	public static void receive(C2SEssenceAlterationLetter letter, IPayloadContext context) {
		InventoryMenu handler = context.player().inventoryMenu;
		ItemStack clickProcessedStack = BeASelf.clickSelf(handler.getCarried(), context.player(), letter.inserting());
		if (clickProcessedStack != null && !context.player().hasInfiniteMaterials()) {
			handler.setCarried(clickProcessedStack);
		}
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
