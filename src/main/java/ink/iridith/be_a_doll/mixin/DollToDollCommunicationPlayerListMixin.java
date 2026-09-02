package ink.iridith.be_a_doll.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import ink.iridith.be_a_doll.BeADollthing;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import ink.iridith.be_a_doll.letters.IntraLibraryMessageCacheLetter;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerList.class)
public class DollToDollCommunicationPlayerListMixin {
	@Shadow
	@Final
	private MinecraftServer server;

	@Inject(at = @At("HEAD"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
	private void papersPlease(PlayerChatMessage message, Predicate<ServerPlayer> shouldSendFiltered, @Nullable ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
		// this is, in my head, a helpful measure with large player counts.
		// (also a late redecoration. i guess i could shave off one decorate with another mixin,
		//  but i don't think i really need to worry that much about optimizing.)
		BeADollthing.prepareMessageSending(message, sender, this.server);
	}

	@ModifyArg(index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
	private OutgoingChatMessage distributeFliers(OutgoingChatMessage message, @Local(argsOnly = true) PlayerChatMessage signedMessage, @Local(ordinal = 0, argsOnly = true) ServerPlayer sender, @Local(ordinal = 1) ServerPlayer target) {
		IntraLibraryMessageCacheLetter documents = BeALibrarian.checkDocuments(sender);
		C2SKeysmashConfigSyncLetter passwords = BeALibrarian.checkFilesForPasswordManager(target);
		if (documents != null && documents.senderSmashesKeys()) {
			if (target != sender && (BeAMaid.isDoll(target) && passwords.readableOthers() || target.position().closerThan(sender.position(), 13)) || target == sender && documents.senderSeesClearly()) {
				return documents.dolledMessage();
			} else {
				return documents.keysmashedMessage();
			}
		} else {
			return message;
		}
	}

	@Inject(at = @At("RETURN"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
	private void timesUp(PlayerChatMessage message, Predicate<ServerPlayer> shouldSendFiltered, @Nullable ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
		if (sender != null) {
			BeALibrarian.shredDocuments(sender);
		}
	}
}
