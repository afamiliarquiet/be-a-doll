package ink.iridith.be_a_doll.client;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.client.personal_diary.BeALocalBug;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = BeADoll.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BeADoll.MOD_ID, value = Dist.CLIENT)
public class BeALocalDoll {
//	public static final BeALocalFabricTinkerer CLIENT_CONFIG = BeALocalFabricTinkerer.createToml(FabricLoader.getInstance().getConfigDir(), "", BeADoll.MOD_ID, BeALocalFabricTinkerer.class);

	public BeALocalDoll(ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, BeALocalTinkerer.SPEC);
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		BeALocalBug.lookAtBug();

		ItemProperties.registerGeneric(
			BeADoll.id("caring"),
			((stack, world, entity, seed) ->
				entity != null && entity.getUseItem() == stack ? 1f : 0f
			)
		);

//		ClientTickEvents.START_CLIENT_TICK.register((client -> {
//			if (CLIENT_CONFIG.dirty) {
//				if (ClientPlayNetworking.canSend(C2SKeysmashConfigSyncLetter.ID)) {
//					ClientPlayNetworking.send(CLIENT_CONFIG.writtenForAFriend());
//				}
//				CLIENT_CONFIG.dirty = false;
//			}
//		}));
	}

	@SubscribeEvent
	private static void ohGoodnessMeWhatAMess(ClientTickEvent.Pre event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			if (BeALibrarian.isConfigDirty(player)) {
				// hmhmm. is this how you sniff out canSend on neo.. surely not
				if (Minecraft.getInstance().getConnection() != null) {
					PacketDistributor.sendToServer(BeALocalTinkerer.writtenForAFriend());
				}
				BeALibrarian.maidWasHere(player);
			}
		}
	}

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {

	}
}
