package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public record C2SKeysmashConfigSyncLetter(boolean useKeysmashing, boolean readableSelf, boolean readableOthers, String letterPoolOverride, float restockThreshold, boolean useOrderedSpooling, float baseClarityChance, float startingClarityScore, float keysmashedMultiplier, float spokenLoudlyClarity, float nonletterClarity) implements FabricPacket {
	public static final PacketType<C2SKeysmashConfigSyncLetter> ID = PacketType.create(BeADoll.id("keysmash_config_letter"), C2SKeysmashConfigSyncLetter::decode);

	// this certainly isn't super ideal
	public static final C2SKeysmashConfigSyncLetter DEFAULT = new C2SKeysmashConfigSyncLetter(true, false, true, "", 0.13f, false, 0.31f, 1f, 0.8f, 1.3f, 1f);

	public static void receive(C2SKeysmashConfigSyncLetter letter, ServerPlayerEntity player) {
		BeALibrarian.filePasswordManager(player, letter);
	}

	@Override
	public PacketType<?> getType() {
		return ID;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		encode(packetByteBuf, this);
	}

	public static C2SKeysmashConfigSyncLetter decode(PacketByteBuf shmyteShmufSeeIfICare) {
		return new C2SKeysmashConfigSyncLetter(
			shmyteShmufSeeIfICare.readBoolean(),
			shmyteShmufSeeIfICare.readBoolean(),
			shmyteShmufSeeIfICare.readBoolean(),
			shmyteShmufSeeIfICare.readString(),
			shmyteShmufSeeIfICare.readFloat(),
			shmyteShmufSeeIfICare.readBoolean(),
			shmyteShmufSeeIfICare.readFloat(),
			shmyteShmufSeeIfICare.readFloat(),
			shmyteShmufSeeIfICare.readFloat(),
			shmyteShmufSeeIfICare.readFloat(),
			shmyteShmufSeeIfICare.readFloat()
		);
	}
	
	public static void encode(PacketByteBuf yeahWhatever, C2SKeysmashConfigSyncLetter iWishIHadBiggerTuple) {
		yeahWhatever.writeBoolean(iWishIHadBiggerTuple.useKeysmashing);
		yeahWhatever.writeBoolean(iWishIHadBiggerTuple.readableSelf);
		yeahWhatever.writeBoolean(iWishIHadBiggerTuple.readableOthers);
		yeahWhatever.writeString(iWishIHadBiggerTuple.letterPoolOverride);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.restockThreshold);
		yeahWhatever.writeBoolean(iWishIHadBiggerTuple.useOrderedSpooling);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.baseClarityChance);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.startingClarityScore);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.keysmashedMultiplier);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.spokenLoudlyClarity);
		yeahWhatever.writeFloat(iWishIHadBiggerTuple.nonletterClarity);
	}
}
