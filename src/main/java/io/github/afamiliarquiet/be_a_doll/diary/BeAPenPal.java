package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.letters.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.Optional;

public class BeAPenPal {
	public static void fillPen() {
		ServerPlayNetworking.registerGlobalReceiver(C2SEssenceAlterationLetter.ID, (letter, player, responseSender) -> C2SEssenceAlterationLetter.receive(letter, player));
		ServerPlayNetworking.registerGlobalReceiver(C2SCreativeEssenceAlterationLetter.ID, (letter, player, responseSender) -> C2SCreativeEssenceAlterationLetter.receive(letter, player));

		ServerPlayNetworking.registerGlobalReceiver(C2SKeysmashConfigSyncLetter.ID,  (letter, player, responseSender) -> C2SKeysmashConfigSyncLetter.receive(letter, player));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			sender.sendPacket(new S2CDollVariantLetter(
				handler.player.getId(),
				BeALibrarian.inspectSupposedPlayer(handler.player)
			));
			sender.sendPacket(new S2CDollLabelLetter(
				handler.player.getId(),
				Optional.ofNullable(BeALibrarian.inspectDollLabel(handler.player))
			));
		});
	}
}
