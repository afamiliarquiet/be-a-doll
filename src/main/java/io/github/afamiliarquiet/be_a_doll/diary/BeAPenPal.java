package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.letters.C2SCreativeEssenceAlterationLetter;
import io.github.afamiliarquiet.be_a_doll.letters.C2SEssenceAlterationLetter;
import io.github.afamiliarquiet.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BeAPenPal {
	public static void fillPen() {
		ServerPlayNetworking.registerGlobalReceiver(C2SEssenceAlterationLetter.ID, (letter, player, responseSender) -> C2SEssenceAlterationLetter.receive(letter, player));
		ServerPlayNetworking.registerGlobalReceiver(C2SCreativeEssenceAlterationLetter.ID, (letter, player, responseSender) -> C2SCreativeEssenceAlterationLetter.receive(letter, player));

		ServerPlayNetworking.registerGlobalReceiver(C2SKeysmashConfigSyncLetter.ID,  (letter, player, responseSender) -> C2SKeysmashConfigSyncLetter.receive(letter, player));
	}
}
