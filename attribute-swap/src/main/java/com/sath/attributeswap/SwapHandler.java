package com.sath.attributeswap;

import com.sath.attributeswap.mixin.ClientPlayerInteractionManagerInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

/**
 * The swap itself. Everything happens inside one client tick, so the
 * "change slot, attack, change slot back" packets reach the server together.
 * The server updates held-item attributes and resets the attack cooldown on
 * its own tick, so it still sees the original item as held for those, while
 * the attack itself is done with the swapped-in item.
 */
public final class SwapHandler {
	private SwapHandler() {
	}

	/**
	 * Called at the start of every left-click attack.
	 *
	 * @return true if the swap handled the click and vanilla should do nothing
	 */
	public static boolean handleAttack(MinecraftClient client) {
		SwapConfig config = SwapConfig.get();
		ClientPlayerEntity player = client.player;
		ClientPlayerInteractionManager interactionManager = client.interactionManager;
		if (!config.enabled || player == null || interactionManager == null || player.isSpectator() || player.isUsingItem()) {
			return false;
		}

		int spearSlot = config.spearSlot - 1;
		int otherSlot = config.otherSlot - 1;
		if (spearSlot == otherSlot) {
			return false;
		}

		int held = player.getInventory().getSelectedSlot();
		if (held == spearSlot && config.maceSwap) {
			return hitWithOtherItem(client, player, interactionManager, spearSlot, otherSlot);
		}
		if (held == otherSlot && config.lungeSwap) {
			return jabWithSpear(player, interactionManager, spearSlot, otherSlot);
		}
		return false;
	}

	/**
	 * Holding the spear: swap to the other item (mace), hit the entity under the
	 * crosshair, swap back. Clicking air or a block is left to vanilla, so a
	 * spear with Lunge still lunges.
	 */
	private static boolean hitWithOtherItem(MinecraftClient client, ClientPlayerEntity player,
			ClientPlayerInteractionManager interactionManager, int spearSlot, int otherSlot) {
		if (!(client.crosshairTarget instanceof EntityHitResult entityHit)) {
			return false;
		}
		PlayerInventory inventory = player.getInventory();
		if (inventory.getStack(otherSlot).isEmpty()) {
			return false;
		}

		inventory.setSelectedSlot(otherSlot);
		// attackEntity sends the slot change before the attack packet.
		interactionManager.attackEntity(player, entityHit.getEntity());
		player.swingHand(Hand.MAIN_HAND);
		inventory.setSelectedSlot(spearSlot);
		((ClientPlayerInteractionManagerInvoker) interactionManager).attributeswap$syncSelectedSlot();
		return true;
	}

	/**
	 * Holding the other item: swap to the spear, do the spear jab (which
	 * triggers Lunge), swap back.
	 */
	private static boolean jabWithSpear(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager,
			int spearSlot, int otherSlot) {
		PlayerInventory inventory = player.getInventory();
		ItemStack spear = inventory.getStack(spearSlot);
		PiercingWeaponComponent piercing = spear.streamAll(PiercingWeaponComponent.class).findFirst().orElse(null);
		if (piercing == null) {
			// Nothing spear-like in the spear slot, so just attack normally.
			return false;
		}
		if (player.isBelowMinimumAttackCharge(spear, 0)) {
			// Vanilla would refuse this jab too; eat the click instead of hitting with the other item.
			return true;
		}

		ClientPlayerInteractionManagerInvoker sync = (ClientPlayerInteractionManagerInvoker) interactionManager;
		inventory.setSelectedSlot(spearSlot);
		sync.attributeswap$syncSelectedSlot();
		interactionManager.attackWithPiercingWeapon(piercing);
		player.swingHand(Hand.MAIN_HAND);
		inventory.setSelectedSlot(otherSlot);
		sync.attributeswap$syncSelectedSlot();
		return true;
	}
}
