package io.github.afamiliarquiet.be_a_doll.letters;

import net.minecraft.network.chat.OutgoingChatMessage;

public record IntraLibraryMessageCacheLetter(boolean senderSmashesKeys, boolean senderSeesClearly, OutgoingChatMessage keysmashedMessage, OutgoingChatMessage dolledMessage) {
}
