package io.github.afamiliarquiet.be_a_doll.item;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats.FoxTrustInvoker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class RibbonItem extends Item {
	public RibbonItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Player doll && BeAMaid.isDoll(doll)) {
			if (doll.startRiding(user, false)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return InteractionResult.SUCCESS;
			}
		} else {
			InteractionResult tried = useToTryRiding(stack, user, entity, hand);
			if (tried.consumesAction()) {
				return tried;
			}
		}

		return super.interactLivingEntity(stack, user, entity, hand);
	}

	public InteractionResult useToTryRiding(ItemStack stack, Player user, Entity entity, InteractionHand hand) {
		if (BeAMaid.isDoll(user)) {
			// ohh.. so the user was the doll!
			boolean shouldRide = false;
			if (entity instanceof OwnableEntity tameable) {
				EntityReference<LivingEntity> ownerRef = tameable.getOwnerReference();
				if (ownerRef != null && ownerRef.matches(user) && entity.getBbWidth() > user.getBbWidth()) {
					shouldRide = true;
				}
			} else if (entity instanceof Fox foxesAreSoCool && ((FoxTrustInvoker)foxesAreSoCool).invokeTrusts(user)) {
				shouldRide = true;
			}

			if (shouldRide && user.startRiding(entity)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		// yeah no lol. did you not see the C2SDollDismountLetter i had to make? client's gotta hear about this
		if (/*!user.getWorld().isClient && */!user.getPassengers().isEmpty() && user.isSecondaryUseActive()) {
//			user.removeAllPassengers();
			Entity doll = user.getPassengers().getLast();
			BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.NONE);
			Vec3 pos;
			// fear my mega if statement of doom! it could be worse. i'm just being a little bit silly with it.
			if (!world.isClientSide()
				&& blockHitResult.getType() == HitResult.Type.BLOCK
				&& doll instanceof ServerPlayer serverPlayerEntity
				&& (pos = getDollPlacementPos(blockHitResult, doll)) != null
			) {
				serverPlayerEntity.teleport(new TeleportTransition(serverPlayerEntity.level(), pos, Vec3.ZERO, user.getYRot() + 180, user.getXRot() * -1, TeleportTransition.DO_NOTHING));
			} else {
				doll.stopRiding();
			}
			user.playSound(BeABirdwatcher.RAVEN_CRY, 1f, 1f);
			return InteractionResult.SUCCESS;
		} else {
			return super.use(world, user, hand);
		}
	}

	public static @Nullable Vec3 getDollPlacementPos(BlockHitResult blockHitResult, Entity doll) {
		Vec3 pos = blockHitResult.getLocation();
		EntityDimensions dollStanding = doll.getDimensions(Pose.STANDING);

		if (blockHitResult.getDirection().getAxis() == Direction.Axis.Y) {
			if (blockHitResult.getDirection() == Direction.DOWN) {
				pos = pos.add(0, -dollStanding.height(), 0);
			}

			// just because i'm feeling extra nice, i'll give you a horizontal aim assist too.
			Vec3 firstCheck = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.X);
			if (firstCheck != null) {
				pos = firstCheck;
			} else {
				pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Z);
			}
		} else {
			Vector3f sideVec = blockHitResult.getDirection().step();
			pos = pos.add(sideVec.x * dollStanding.width() / 2, 0, sideVec.z * dollStanding.width() / 2);

			// adjust for being low or high enough to clip into a possible adjacent block
			pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Y);
		}

		// check for any other collisions, if collide then. bad aim, sorry, just gonna drop.
		// i'll maybe update the math later.
		if (pos == null || doll.level().getBlockCollisions(doll, dollStanding.makeBoundingBox(pos)).iterator().hasNext()) {
			return null;
		} else {
			return pos;
		}
	}

	// todo - hey quiet if you come back to use this again, maybe do it more like Entity.adjustMovementForCollisions
	//  you've really got a lot of block collision checking going on here
	//  anyway this partial aim assist only helps in the case of one collision.
	//  redoing this like entity.amfc would probably be the best way to fix that. assuming amfc is.. what this is.
	private static @Nullable Vec3 checkForCollisionsOnAxis(Entity doll, EntityDimensions dollStanding, @Nullable Vec3 pos, Direction.Axis axis) {
		if (pos == null || !doll.level().getBlockCollisions(doll, dollStanding.makeBoundingBox(pos)).iterator().hasNext()) {
			return pos;
		} else {
			double xyz = pos.get(axis);
			double positiveEdgeCrumb = (xyz + (axis.isVertical() ? dollStanding.height() : dollStanding.width() / 2)) - Math.ceil(xyz);
			double negativeEdgeCrumb = Math.floor(xyz) - (xyz - (axis.isVertical() ? 0 : dollStanding.width() / 2));
			if (positiveEdgeCrumb < 0.5 && positiveEdgeCrumb > 0 && !doll.level().getBlockCollisions(doll, dollStanding.makeBoundingBox(pos.with(axis, xyz - positiveEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll down a bit works, try now
				return pos.with(axis, xyz - positiveEdgeCrumb);
			} else if (negativeEdgeCrumb < 0.5 && negativeEdgeCrumb > 0 && !doll.level().getBlockCollisions(doll, dollStanding.makeBoundingBox(pos.with(axis, xyz + negativeEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll up a bit works, try now
				return pos.with(axis, xyz + negativeEdgeCrumb);
			} else {
				// well. if there's gonna be collision, just drop
				return null;
			}
		}
	}
}
