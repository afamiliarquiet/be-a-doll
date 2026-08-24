package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeASelf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// me sowing: haha fsdf;lkj yeah!!! thanks creative inventory screen now i know how to do an evil and strange slot!!
// me reaping: well this fdj;lksing sucks. why are you being evil to me too.
public record C2SCreativeEssenceAlterationLetter(boolean inserting, ItemStack statedCursorStack) implements CustomPacketPayload {
	public static final Type<C2SCreativeEssenceAlterationLetter> ID = new Type<>(BeADoll.id("creative_essence_alteration_letter"));

	public static final StreamCodec<RegistryFriendlyByteBuf, C2SCreativeEssenceAlterationLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		C2SCreativeEssenceAlterationLetter::inserting,
		ItemStack.OPTIONAL_STREAM_CODEC,
		C2SCreativeEssenceAlterationLetter::statedCursorStack,
		C2SCreativeEssenceAlterationLetter::new
	);

	public static void receive(C2SCreativeEssenceAlterationLetter letter, ServerPlayNetworking.Context context) {
		if (!context.player().hasInfiniteMaterials()) {
			return; // how dare you. this is for creative only
		}
		// fire this off still for the tf effects n such to happen on server
		BeASelf.clickSelf(letter.statedCursorStack(), context.player(), letter.inserting());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
