package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.letters.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.*;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class BeALibrarian {
	// how does this librarian differ from the maid? this one handles the forbidden (experimental) knowledge!
	public static final AttachmentType<BeADoll.Variant> DOLL_VARIANT = AttachmentRegistry
		.<BeADoll.Variant>builder()
		.initializer(() -> BeADoll.Variant.DEFAULT)
		.persistent(BeADoll.Variant.CODEC)
		.buildAndRegister(BeADoll.id("doll_variant"));

	public static final AttachmentType<Text> DOLL_NAME = AttachmentRegistry
		.<Text>builder()
		.persistent(Codecs.TEXT)
		.buildAndRegister(BeADoll.id("doll_name"));

	// yep. keeping the letter, envelope and all. no sync needed. certainly no persistence.
	public static final AttachmentType<C2SKeysmashConfigSyncLetter> KEYSMASH_CONFIG = AttachmentRegistry
		.<C2SKeysmashConfigSyncLetter>builder()
		.initializer(() -> C2SKeysmashConfigSyncLetter.DEFAULT)
		.buildAndRegister(BeADoll.id("keysmash_config"));

	public static final AttachmentType<IntraLibraryMessageCacheLetter> MESSAGE_CACHE = AttachmentRegistry.create(
		BeADoll.id("message_cache")
	);

	public static final ScaleModifier DOLL_SCALE = new TypedScaleModifier(() -> BeALibrarian.DOLL_SCALE_TYPE);
	public static final ScaleType DOLL_SCALE_TYPE = ScaleType.Builder.create()
		.addDependentModifier(DOLL_SCALE)
		.affectsDimensions()
		.defaultPersistence(true)
		.build();

	public static void lookForABook() {
		ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, BeADoll.id("doll_modifier"), DOLL_SCALE);
		ScaleRegistries.register(ScaleRegistries.SCALE_TYPES, BeADoll.id("doll"), DOLL_SCALE_TYPE);

		ScaleTypes.WIDTH.getDefaultBaseValueModifiers().add(DOLL_SCALE);
		ScaleTypes.HEIGHT.getDefaultBaseValueModifiers().add(DOLL_SCALE);
	}


	/**
	 * finds the doll's variant from attachment, or gets default if none found.<br/>
	 * you should check {@link io.github.afamiliarquiet.be_a_doll.BeAMaid#isDoll(PlayerEntity)} as the authority
	 * on whether a PlayerEntity is a player or a doll. remember this, quiet. i made javadoc for you.
	 * @return the doll's variant, or the default doll type (which is NOT a normal player and is still a doll type)
	 */
	public static @NotNull BeADoll.Variant inspectDollMaterial(@NotNull PlayerEntity doll) {
		return doll.getAttachedOrCreate(DOLL_VARIANT);
	}

	// okay so it looks bad now that i'm not using the above one at all. it's interesting yeah
	// listen okay i need isDoll to be authoritative because it relies on the attributes
	// and i don't ever want a doll to lose their attributes
	// so if they lose their attributes then they must not be a doll anymore, ergo a doll has not lost their attributes
	// being lopsided is no good so this avoids that
	public static @NotNull BeADoll.Variant inspectSupposedPlayer(@NotNull PlayerEntity supposedPlayer) {
		return BeAMaid.isDoll(supposedPlayer) ? inspectDollMaterial(supposedPlayer) : BeADoll.Variant.REPRESSED;
	}

	// yeah we're just washing off the experimental api smell here
	public static void reshapeDoll(@NotNull PlayerEntity doll, @NotNull BeADoll.Variant variant) {
		doll.setAttached(DOLL_VARIANT, variant);
		if(!doll.getWorld().isClient) {
			S2CDollVariantLetter letter = new S2CDollVariantLetter(doll.getId(), variant);
			PlayerLookup.tracking(doll)
				.forEach(player -> ServerPlayNetworking.send(player, letter));
			ServerPlayNetworking.send((ServerPlayerEntity) doll, letter);
		}
		// special compat treat for clockwork dolls
//		if (FabricLoader.getInstance().isModLoaded("occmy")) {
//			if (variant == BeADoll.Variant.CLOCKWORK) {
//				doll.setAttached(OccEntities.ENJOINED, Unit.INSTANCE);
//			} else {
//				doll.removeAttached(OccEntities.ENJOINED);
//			}
//		}
	}

	public static @Nullable Text inspectDollLabel(@NotNull PlayerEntity doll) {
		return doll.getAttached(DOLL_NAME);
	}

	public static void relabelDoll(@NotNull PlayerEntity doll, @Nullable Text name) {
		doll.setAttached(DOLL_NAME, name);
		// manually sync attachments
		if(!doll.getWorld().isClient) {
			S2CDollLabelLetter letter = new S2CDollLabelLetter(doll.getId(), Optional.ofNullable(name));
			PlayerLookup.tracking(doll)
				.forEach(player -> ServerPlayNetworking.send(player, letter));
			ServerPlayNetworking.send((ServerPlayerEntity) doll, letter);
		}
	}

	public static void repress(@NotNull PlayerEntity player) {
		// clean up special compat treat
//		if (FabricLoader.getInstance().isModLoaded("occmy")) {
//			if (inspectDollMaterial(player) == BeADoll.Variant.CLOCKWORK) {
//				player.removeAttached(OccEntities.ENJOINED);
//			}
//		}

		player.removeAttached(DOLL_VARIANT);
		player.removeAttached(DOLL_NAME);
		// manually sync attachments
		if(!player.getWorld().isClient) {
			S2CDollRepressLetter letter = new S2CDollRepressLetter(player.getId());
			PlayerLookup.tracking(player)
				.forEach(receiver -> ServerPlayNetworking.send(receiver, letter));
			ServerPlayNetworking.send((ServerPlayerEntity) player, letter);
		}
	}

	public static void filePasswordManager(@NotNull PlayerEntity player, C2SKeysmashConfigSyncLetter letter) {
		player.setAttached(KEYSMASH_CONFIG, letter);
	}

	public static @NotNull C2SKeysmashConfigSyncLetter checkFilesForPasswordManager(@NotNull PlayerEntity player) {
		return player.getAttachedOrCreate(KEYSMASH_CONFIG);
	}

	public static void filePaperwork(@NotNull PlayerEntity player, IntraLibraryMessageCacheLetter letter) {
		player.setAttached(MESSAGE_CACHE, letter);
	}

	public static @Nullable IntraLibraryMessageCacheLetter checkDocuments(@NotNull PlayerEntity player) {
		return player.getAttached(MESSAGE_CACHE);
	}

	public static void shredDocuments(@NotNull PlayerEntity player) {
		player.removeAttached(MESSAGE_CACHE);
	}
}
