package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record C2SKeysmashConfigSyncLetter(boolean useKeysmashing, boolean readableSelf, boolean readableOthers, String letterPoolOverride, float restockThreshold, boolean useOrderedSpooling, float baseClarityChance, float startingClarityScore, float keysmashedMultiplier, float spokenLoudlyClarity, float nonletterClarity) implements CustomPacketPayload {
	public static final Type<C2SKeysmashConfigSyncLetter> ID = new Type<>(BeADoll.id("keysmash_config_letter"));

	public static final StreamCodec<ByteBuf, C2SKeysmashConfigSyncLetter> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, C2SKeysmashConfigSyncLetter::useKeysmashing,
		ByteBufCodecs.BOOL, C2SKeysmashConfigSyncLetter::readableSelf,
		ByteBufCodecs.BOOL, C2SKeysmashConfigSyncLetter::readableOthers,
		ByteBufCodecs.STRING_UTF8, C2SKeysmashConfigSyncLetter::letterPoolOverride,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::restockThreshold,
		ByteBufCodecs.BOOL, C2SKeysmashConfigSyncLetter::useOrderedSpooling,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::baseClarityChance,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::startingClarityScore,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::keysmashedMultiplier,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::spokenLoudlyClarity,
		ByteBufCodecs.FLOAT, C2SKeysmashConfigSyncLetter::nonletterClarity,
		C2SKeysmashConfigSyncLetter::new
	);

	// this certainly isn't super ideal
	public static final C2SKeysmashConfigSyncLetter DEFAULT = new C2SKeysmashConfigSyncLetter(true, false, true, "", 0.13f, false, 0.31f, 1f, 0.8f, 1.3f, 1f);

	public static void receive(C2SKeysmashConfigSyncLetter letter, ServerPlayNetworking.Context context) {
		BeALibrarian.filePasswordManager(context.player(), letter);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
