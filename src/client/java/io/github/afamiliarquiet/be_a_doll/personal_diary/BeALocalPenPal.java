package io.github.afamiliarquiet.be_a_doll.personal_diary;

import io.github.afamiliarquiet.be_a_doll.BeALocalDoll;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.item.DollcraftItem;
import io.github.afamiliarquiet.be_a_doll.letters.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class BeALocalPenPal {
	public static void fillPen() {
		ClientPlayNetworking.registerGlobalReceiver(S2CDollDismountLetter.ID, ((letter, player, responseSender) -> {
			letter.dismountingDollIds().forEach(id -> {
				Entity ridingDoll = player.getWorld().getEntityById(id);
				if (ridingDoll != null) {
					ridingDoll.dismountVehicle();
				}
			});
		}));

		ClientPlayNetworking.registerGlobalReceiver(S2CDollRepairedLetter.ID, (letter, player, responseSender) -> {
			Entity repairedEntity = player.getWorld().getEntityById(letter.entityId());
			if (repairedEntity instanceof PlayerEntity letThereBeDoll) {
				DollcraftItem.spawnRepairParticles(letThereBeDoll, letter.material(), 16);
			}
		});

		// i couldnt find a builtin method for attachment syncing in 1.20.1
		// so it seems like we need to handle attachment syncing ourselves
		ClientPlayNetworking.registerGlobalReceiver(S2CDollVariantLetter.ID, (letter, player, responseSender) -> {
			if(player.getWorld().getEntityById(letter.entityId()) instanceof PlayerEntity doll) {
				if(letter.variant().isDollish()) {
					BeALibrarian.reshapeDoll(doll, letter.variant());
				} else {
					BeALibrarian.repress(doll);
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(S2CDollLabelLetter.ID, (letter, player, responseSender) -> {
			if(player.getWorld().getEntityById(letter.entityId()) instanceof PlayerEntity doll) {
				BeALibrarian.relabelDoll(doll, letter.name().orElse(null));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(S2CDollRepressLetter.ID, (letter, player, responseSender) -> {
			if (player.getWorld().getEntityById(letter.entityId()) instanceof PlayerEntity repressed) {
				BeALibrarian.repress(repressed);
			}
		});

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) ->
			ClientPlayNetworking.send(BeALocalDoll.CLIENT_CONFIG.writtenForAFriend())));
	}
}
