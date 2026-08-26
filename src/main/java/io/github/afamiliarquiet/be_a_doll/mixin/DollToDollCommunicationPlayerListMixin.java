package io.github.afamiliarquiet.be_a_doll.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.be_a_doll.BeADollthing;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import io.github.afamiliarquiet.be_a_doll.letters.IntraLibraryMessageCacheLetter;
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
	private void papersPlease(PlayerChatMessage message, Predicate<ServerPlayer> isFiltered, @Nullable ServerPlayer senderPlayer, ChatType.Bound chatType, CallbackInfo ci) {
		// this is, in my head, a helpful measure with large player counts.
		// (also a late redecoration. i guess i could shave off one decorate with another mixin,
		//  but i don't think i really need to worry that much about optimizing.)
		BeADollthing.prepareMessageSending(message, senderPlayer, this.server);
	}

	@ModifyArg(index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
	private OutgoingChatMessage distributeFliers(OutgoingChatMessage message, @Local(argsOnly = true, name = "senderPlayer") ServerPlayer senderPlayer, @Local(name = "player") ServerPlayer player) {
		IntraLibraryMessageCacheLetter documents = BeALibrarian.checkDocuments(senderPlayer);
		C2SKeysmashConfigSyncLetter passwords = BeALibrarian.checkFilesForPasswordManager(player);
		if (documents != null && documents.senderSmashesKeys()) {
			if (player != senderPlayer && (BeAMaid.isDoll(player) && passwords.readableOthers() || player.position().closerThan(senderPlayer.position(), 13)) || player == senderPlayer && documents.senderSeesClearly()) {
				return documents.dolledMessage();
			} else {
				return documents.keysmashedMessage();
			}
		} else {
			return message;
		}
	}

	@Inject(at = @At("RETURN"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
	private void timesUp(PlayerChatMessage message, Predicate<ServerPlayer> isFiltered, @Nullable ServerPlayer senderPlayer, ChatType.Bound chatType, CallbackInfo ci) {
		if (senderPlayer != null) {
			BeALibrarian.shredDocuments(senderPlayer);
		}
	}
}
