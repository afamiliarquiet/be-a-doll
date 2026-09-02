package ink.iridith.be_a_doll;

import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import ink.iridith.be_a_doll.letters.IntraLibraryMessageCacheLetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeADollthing {
	// throwing this in here because papi and styledchat are modCompileOnly and will detonate classnotfound in mixin
	// i should probably go learn about classloading.
	// the original issue was that styledchat always has an override on chat messages
	// then i think the issue with StyledChatUtils:modifyForSending() would be that maybeFormatFor always ignores unsigned content
	public static void prepareMessageSending(PlayerChatMessage message, ServerPlayer sender, MinecraftServer server) {
		if (sender != null) {
//			boolean hackTheStyles = FabricLoader.getInstance().isModLoaded("styledchat");
			String keysmashed = syntheticKeysmashing(message.signedContent(), sender);
			Component keysmashedContent;
			Component dolledContent;

//			if (hackTheStyles) {
//				keysmashedContent = StyledChatUtils.formatFor(sender.createCommandSourceStack(), keysmashed);
//				dolledContent = StyledChatUtils.formatFor(sender.createCommandSourceStack(), message.signedContent())
//					.copy().withStyle(style -> style.withColor(0xbca1a0).withItalic(true));
//			} else {
				keysmashedContent = server.getChatDecorator().decorate(sender, Component.nullToEmpty(
					syntheticKeysmashing(message.signedContent(), sender)
				));
				dolledContent = server.getChatDecorator().decorate(sender,
					Component.literal(message.signedContent())
						.withStyle(style -> style.withColor(0xbca1a0).withItalic(true))
				);
//			}

			PlayerChatMessage keysmashedMessage = message.withUnsignedContent(keysmashedContent);
			PlayerChatMessage dolledMessage = message.withUnsignedContent(dolledContent);

//			if (hackTheStyles) {
//				ExtSignedMessage.setArg(keysmashedMessage, "base_input", keysmashedContent);
//				ExtSignedMessage.setArg(dolledMessage, "base_input", dolledContent);
//				ExtSignedMessage.setArg(keysmashedMessage, "override", StyledChatStyles.getChat(sender, keysmashedContent));
//				ExtSignedMessage.setArg(dolledMessage, "override", StyledChatStyles.getChat(sender, dolledContent));
//			}

			C2SKeysmashConfigSyncLetter passwordManager = BeALibrarian.checkFilesForPasswordManager(sender);
			BeALibrarian.filePaperwork(sender, new IntraLibraryMessageCacheLetter(
				BeAMaid.isDoll(sender) && passwordManager.useKeysmashing(),
				passwordManager.readableSelf(),
				OutgoingChatMessage.create(keysmashedMessage),
				OutgoingChatMessage.create(dolledMessage)
			));
		}
	}

	public static @NotNull String syntheticKeysmashing(@NotNull String originalMessage) {
		return syntheticKeysmashing(originalMessage, C2SKeysmashConfigSyncLetter.DEFAULT);
	}

	public static @NotNull String syntheticKeysmashing(@NotNull String originalMessage, @NotNull Player keysmasher) {
		return syntheticKeysmashing(originalMessage, BeALibrarian.checkFilesForPasswordManager(keysmasher));
	}

	public static @NotNull String syntheticKeysmashing(@NotNull String originalMessage, @NotNull C2SKeysmashConfigSyncLetter penpalsWishes) {
		// if you want to try doll-to-doll communication later, try a mixin at PlayerManager#824 or so
		// this is entirely limited to whatever lowercase can detect, and subject to what My Keyboard looks like.
		// thats just how it is
		// wait. What if the player's keysmash config is synced to server,
		// then server can choose whether to keysmashify or send pure to players depending on doll or not.
		// this would work. this is Doll Power
		if (!originalMessage.isEmpty() && originalMessage.charAt(0) == '\\' || !penpalsWishes.useKeysmashing()) {
			return originalMessage;
		}

		String material = !penpalsWishes.letterPoolOverride().isEmpty() ? penpalsWishes.letterPoolOverride() : "asdfjkl;";
		List<Character> spool = new ArrayList<>(material.length());
		Random random = new Random();
		StringBuilder smashed = new StringBuilder();
		double clarity = penpalsWishes.startingClarityScore();

		for (int i = 0; i < originalMessage.length(); i++) {
			if (spool.size() <= material.length() * penpalsWishes.restockThreshold() || spool.isEmpty()) {
				spool.clear();
				for (int j = 0; j < material.length(); j++) {
					spool.add(material.charAt(j));
				}
			}

			char current = originalMessage.charAt(i);
			if (Character.isLowerCase(current)) {
				smashed.append(spool.remove(penpalsWishes.useOrderedSpooling() ? 0 : random.nextInt(spool.size())));
				clarity *= penpalsWishes.keysmashedMultiplier();
			} else if (Character.isUpperCase(current)) {
				smashed.append(Character.toLowerCase(current));
				clarity += penpalsWishes.spokenLoudlyClarity();
			} else {
				if (random.nextDouble() < penpalsWishes.baseClarityChance() + (clarity / (1 + smashed.length()))) { // not normal text? good luck
					smashed.append(current);
					clarity += penpalsWishes.nonletterClarity();
				}
				if (penpalsWishes.useOrderedSpooling()) {
					spool.clear(); // trigger restock
				}
			}
		}

		if (smashed.isEmpty()) { // if it was non-letters and bad luck. a (i don't think this is possible anymore?)
			smashed.append('a');
		}
		return smashed.toString();
	}
}
