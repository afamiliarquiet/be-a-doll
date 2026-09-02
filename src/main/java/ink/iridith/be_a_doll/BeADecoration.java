package ink.iridith.be_a_doll;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeADecoration {
	// here's where you become a shoulder decoration!
	// this is a pile of stuff for mixins to use, but having 'em here makes em reloadable for easier dev

	// todone - lots to do but firstly dismounting. the mount player never sees things dismount for some reason
	// todone - let the mount force a doll off (probably with ribbon? but maybe not. maybe pose can also do it, like crawl)
	// todone - should probably force a dismount on gamemode change n such too.. blegh. later problem
	// notodo - copy the boat yaw clamping stuffs?
	// todone - adjust for pose (crouching, crawling/swimming/gliding, sleeping, spin attack maybe, dying??)
	// todone - make riptide not hit shouldered dolls
	// todone - make sure only one doll fits
	// todone - actually. you have two shoulders, let two dolls fit
	// todone - now also should probably check on parrot shoulder slots for compat there
	// notodone - wait. two shoulders AND a head? it's possible...
	// notodo - this is extreme but.. you know how the happy ghast has seats on all 4 sides? player head is same shape..
	// todone - width/scale check?
	// todone - check to see if dolls follow with teleport, and if not try to fix. (this was a problem for spectating) (seems fine?)
	// todone - make locator bar not shiver in terror

	public static int getParrotCount(Player playerMount) {
		int parrots = 0;
		if (!playerMount.getShoulderEntityLeft().isEmpty()) parrots++;
		if (!playerMount.getShoulderEntityRight().isEmpty()) parrots++;
		return parrots;
	}

	private static HumanoidArm getArm(Player playerMount, Player doll, int parrotCount) {
		int passengerIndex = playerMount.getPassengers().indexOf(doll);
		HumanoidArm armToSitOn = playerMount.getMainArm().getOpposite();
		if (passengerIndex == 1) {// second passenger
			armToSitOn = armToSitOn.getOpposite();
		}

		if (parrotCount == 1) {
			// force seat if parrot has the other. if there's two parrots and a doll is riding then things are already broken
			if (!playerMount.getShoulderEntityLeft().isEmpty())
				armToSitOn = HumanoidArm.RIGHT; // if left is occupied, has to be right
			if (!playerMount.getShoulderEntityRight().isEmpty())
				armToSitOn = HumanoidArm.LEFT; // if right is occupied, has to be right
		}
		return armToSitOn;
	}

	// null return indicates mixin should not intervene
	public static @Nullable Vec3 getDollAttachmentPos(Player playerMount, Player doll, EntityDimensions dimensions, float scaleFactor) {
		int parrotCount = getParrotCount(playerMount);
		if (parrotCount > 1) {
			return null; // secret third slot. head. this shouldn't happen, but if it does, head.
		}

		HumanoidArm armToSitOn = getArm(playerMount, doll, parrotCount);
		Vec3 attachmentPos = new Vec3((armToSitOn == HumanoidArm.LEFT ? 1 : -1) * 0.3625 * scaleFactor, dimensions.height(), 0);

		switch (playerMount.getPose()) {
			case CROUCHING:
				attachmentPos = attachmentPos.add(new Vec3(0, -0.025, 0).scale(scaleFactor));
			case STANDING:
				attachmentPos = attachmentPos.add(new Vec3(0, - 0.425, 0.1).scale(scaleFactor));
				break;
			case SLEEPING:
				attachmentPos = attachmentPos.add(new Vec3(0, -0.35, 0).scale(scaleFactor));
				// preemptively un-rotate and then rotate to sleeping direction
				Direction sleepingDirection = playerMount.getBedOrientation();
				if (sleepingDirection != null) { // should always be true but i'm not the assertive type
					attachmentPos = attachmentPos.yRot(playerMount.yBodyRot * (float) (Math.PI / 180.0));
					attachmentPos = attachmentPos.yRot((sleepingDirection.toYRot() + 180) * (float) (Math.PI / 180.0));
				}
				break;
			case SWIMMING:
				attachmentPos = attachmentPos.add(new Vec3(0, 0.15, -1).scale(scaleFactor));
			case SPIN_ATTACK:
			case FALL_FLYING:
				attachmentPos = attachmentPos.add(new Vec3(0, -0.5, 1.5).scale(scaleFactor));
				attachmentPos = attachmentPos.xRot(-playerMount.getXRot() * (float) (Math.PI / 180.0));
				break;
		}

		return attachmentPos.yRot(-playerMount.yBodyRot * (float) (Math.PI / 180.0));
	}

	public static boolean canAddPassenger(Player playerMount, Player doll) {
		int freeSlots = 2;
		freeSlots -= getParrotCount(playerMount);
		freeSlots -= playerMount.getPassengers().size();
		return freeSlots > 0 && doll.getScale() / playerMount.getScale() <= 0.31;
	}

	public static Vec3 updatePassengerForDismount(Player playerMount, Player doll) {
		return new Vec3(doll.getX(), playerMount.getBoundingBox().minY, doll.getZ());
	}

	public static boolean shoulderEntityIsEmpty(LivingEntity playerMountTrustMe, boolean parrotsEmpty, HumanoidArm testArm) {
		int likelyDollCount = playerMountTrustMe.countPlayerPassengers();
		boolean dollsEmpty = true;

		if (likelyDollCount > 1) {
			dollsEmpty = false;
		} else if (likelyDollCount == 1) {
			// pain
			dollsEmpty = testArm != playerMountTrustMe.getMainArm();
		}

		return parrotsEmpty && dollsEmpty;
	}
}
