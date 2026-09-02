package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.letters.C2SCreativeEssenceAlterationLetter;
import ink.iridith.be_a_doll.letters.C2SEssenceAlterationLetter;
import ink.iridith.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import ink.iridith.be_a_doll.letters.S2CDollDismountLetter;
import ink.iridith.be_a_doll.letters.S2CDollRepairedLetter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BeADoll.MOD_ID)
public class BeAPenPal {
	public static void fillPen() {
//		PayloadTypeRegistry.playS2C().register(S2CDollDismountLetter.ID, S2CDollDismountLetter.PACKET_CODEC);
//
//		PayloadTypeRegistry.playC2S().register(C2SEssenceAlterationLetter.ID, C2SEssenceAlterationLetter.PACKET_CODEC);
//		PayloadTypeRegistry.playC2S().register(C2SCreativeEssenceAlterationLetter.ID, C2SCreativeEssenceAlterationLetter.PACKET_CODEC);
//		ServerPlayNetworking.registerGlobalReceiver(C2SEssenceAlterationLetter.ID, C2SEssenceAlterationLetter::receive);
//		ServerPlayNetworking.registerGlobalReceiver(C2SCreativeEssenceAlterationLetter.ID, C2SCreativeEssenceAlterationLetter::receive);
//
//		PayloadTypeRegistry.playS2C().register(S2CDollRepairedLetter.ID, S2CDollRepairedLetter.PACKET_CODEC);
//
//		PayloadTypeRegistry.playC2S().register(C2SKeysmashConfigSyncLetter.ID, C2SKeysmashConfigSyncLetter.PACKET_CODEC);
//		ServerPlayNetworking.registerGlobalReceiver(C2SKeysmashConfigSyncLetter.ID, C2SKeysmashConfigSyncLetter::receive);
	}

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		// handlers are happening on main thread.. probly fine. never ran into problems on fabric.
		// never really thought about it. *looks around nervously for possible issues/prs approaching*
		// intellij should give me rich text for comments. hmph.
		// maybe once someone makes a real competitor to intellij that can work on making things better instead of worse again
		registrar.playToClient(
			S2CDollDismountLetter.ID,
			S2CDollDismountLetter.PACKET_CODEC,
			S2CDollDismountLetter::neodollsYoureSoLuckyIHaveSoMuchLoveInMyHeartForYouThisIsSuchAPointlessChangeToHaveToMake
		);

		registrar.playToServer(
			C2SEssenceAlterationLetter.ID,
			C2SEssenceAlterationLetter.PACKET_CODEC,
			C2SEssenceAlterationLetter::receive
		);
		registrar.playToServer(
			C2SCreativeEssenceAlterationLetter.ID,
			C2SCreativeEssenceAlterationLetter.PACKET_CODEC,
			C2SCreativeEssenceAlterationLetter::receive
		);

		registrar.playToClient(
			S2CDollRepairedLetter.ID,
			S2CDollRepairedLetter.PACKET_CODEC,
			S2CDollRepairedLetter::neodollsILoveYou
		);

		registrar.playToServer(
			C2SKeysmashConfigSyncLetter.ID,
			C2SKeysmashConfigSyncLetter.PACKET_CODEC,
			C2SKeysmashConfigSyncLetter::receive
		);
	}
}
