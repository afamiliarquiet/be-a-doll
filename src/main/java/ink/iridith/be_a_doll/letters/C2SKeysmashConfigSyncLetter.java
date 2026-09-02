package ink.iridith.be_a_doll.letters;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SKeysmashConfigSyncLetter(boolean useKeysmashing, boolean readableSelf, boolean readableOthers, String letterPoolOverride, float restockThreshold, boolean useOrderedSpooling, float baseClarityChance, float startingClarityScore, float keysmashedMultiplier, float spokenLoudlyClarity, float nonletterClarity) implements CustomPacketPayload {
	public static final Type<C2SKeysmashConfigSyncLetter> ID = new Type<>(BeADoll.id("keysmash_config_letter"));

	public static final StreamCodec<ByteBuf, C2SKeysmashConfigSyncLetter> PACKET_CODEC = StreamCodec.of(C2SKeysmashConfigSyncLetter::encode, C2SKeysmashConfigSyncLetter::decode);

	// this certainly isn't super ideal
	public static final C2SKeysmashConfigSyncLetter DEFAULT = new C2SKeysmashConfigSyncLetter(true, false, true, "", 0.13f, false, 0.31f, 1f, 0.8f, 1.3f, 1f);

	public static void receive(C2SKeysmashConfigSyncLetter letter, IPayloadContext context) {
		BeALibrarian.filePasswordManager(context.player(), letter);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return ID;
	}
	
	public static C2SKeysmashConfigSyncLetter decode(ByteBuf shmyteShmufSeeIfICare) {
		return new C2SKeysmashConfigSyncLetter(
			ByteBufCodecs.BOOL.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.BOOL.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.BOOL.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.STRING_UTF8.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.BOOL.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare),
			ByteBufCodecs.FLOAT.decode(shmyteShmufSeeIfICare)
		);
	}
	
	public static void encode(ByteBuf yeahWhatever, C2SKeysmashConfigSyncLetter iWishIHadBiggerTuple) {
		ByteBufCodecs.BOOL.encode(yeahWhatever, iWishIHadBiggerTuple.useKeysmashing);
		ByteBufCodecs.BOOL.encode(yeahWhatever, iWishIHadBiggerTuple.readableSelf);
		ByteBufCodecs.BOOL.encode(yeahWhatever, iWishIHadBiggerTuple.readableOthers);
		ByteBufCodecs.STRING_UTF8.encode(yeahWhatever, iWishIHadBiggerTuple.letterPoolOverride);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.restockThreshold);
		ByteBufCodecs.BOOL.encode(yeahWhatever, iWishIHadBiggerTuple.useOrderedSpooling);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.baseClarityChance);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.startingClarityScore);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.keysmashedMultiplier);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.spokenLoudlyClarity);
		ByteBufCodecs.FLOAT.encode(yeahWhatever, iWishIHadBiggerTuple.nonletterClarity);
	}
}
