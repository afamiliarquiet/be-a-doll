package io.github.afamiliarquiet.be_a_doll.personal_diary;

import io.github.afamiliarquiet.be_a_doll.BeALocalDoll;
import io.github.afamiliarquiet.be_a_doll.item.DollcraftItem;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollDismountLetter;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollRepairedLetter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BeALocalPenPal {
	public static void fillPen() {
		ClientPlayNetworking.registerGlobalReceiver(S2CDollDismountLetter.ID, ((letter, context) -> {
			letter.dismountingDollIds().forEach(id -> {
				Entity ridingDoll = context.player().level().getEntity(id);
				if (ridingDoll != null) {
					ridingDoll.removeVehicle();
				}
			});
		}));

		ClientPlayNetworking.registerGlobalReceiver(S2CDollRepairedLetter.ID, (letter, context) -> {
			Entity repairedEntity = context.player().level().getEntity(letter.entityId());
			if (repairedEntity instanceof Player letThereBeDoll) {
				DollcraftItem.spawnRepairParticles(letThereBeDoll, letter.material(), 16);
			}
		});

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) ->
			ClientPlayNetworking.send(BeALocalDoll.CLIENT_CONFIG.writtenForAFriend())));
	}
}
