package com.neuromuser.mmtk.mixin;

import com.mojang.nbt.tags.Tag;
import com.neuromuser.mmtk.GlobalRules;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {

	@Mutable
	@Final
	@Shadow
	protected final Minecraft mc;

	public PlayerControllerMixin(Minecraft mc) {
		this.mc = mc;
	}

	@Unique
	private boolean canPlayerBreakBlock(int x, int y, int z) {
		if (mc.thePlayer == null) return false;
		if (mc.thePlayer.gamemode != Gamemode.adventure) return true;

		ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();
		if (heldItem == null) {
			System.out.println("[MMTK-Debug] Break Denied: Hand is empty in Adventure Mode");
			return false;
		}

		int blockId = mc.currentWorld.getBlockId(x, y, z);
		Block<?> targetBlock = Blocks.getBlock(blockId);
		if (targetBlock == null) return false;

		String targetKey = targetBlock.getKey();
		System.out.println("[MMTK-Debug] Attempting to break: " + targetKey + " with " + heldItem.getDisplayName());

		// Check Global Rules
		if (GlobalRules.canDoGlobal(heldItem.getItem(), targetKey, true)) {
			System.out.println("[MMTK-Debug] Break Allowed: Global Rule found");
			return true;
		}

		// Check NBT
		if (heldItem.getData() != null && heldItem.getData().containsKey("CanBreak")) {
			ListTag list = heldItem.getData().getList("CanBreak");
			System.out.println("[MMTK-Debug] Checking " + list.tagCount() + " entries in CanBreak NBT");

			for (int i = 0; i < list.tagCount(); i++) {
				Tag<?> tag = list.tagAt(i);
				if (tag instanceof com.mojang.nbt.tags.CompoundTag) {
					String nbtKey = ((com.mojang.nbt.tags.CompoundTag) tag).getString("blockKey");
					System.out.println("[MMTK-Debug] Comparing NBT Key: '" + nbtKey + "' vs Target Key: '" + targetKey + "'");
					if (nbtKey.equals(targetKey)) {
						System.out.println("[MMTK-Debug] Break Allowed: NBT Match found");
						return true;
					}
				}
			}
		} else {
			System.out.println("[MMTK-Debug] Break Denied: Item has no CanBreak NBT data");
		}

		return false;
	}
	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_forceAdventureBreak(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		if (mc.thePlayer.getGamemode() == Gamemode.adventure) {
			// If our MMTK logic says we CAN break it, we let the method continue
			// normally, but we might need to bypass the 'heldObject == null' check
			// in the original source.
			if (canPlayerBreakBlock(x, y, z)) {
				// We do nothing here to let the original code run,
				// OR we manually trigger the destroy logic if the base game is too stubborn.
			} else {
				// If MMTK says NO, we definitely stop it.
				ci.cancel();
			}
		}
	}
	@Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopContinueDestroy(int x, int y, int z, Side side, double xHit, double yHit, CallbackInfo ci) {
		if (!canPlayerBreakBlock(x, y, z)) ci.cancel();
	}

	@Unique
	private boolean canPlayerPlaceBlockOn(int x, int y, int z, ItemStack heldItem) {
		if (mc.thePlayer == null || heldItem == null) return false;
		if (mc.thePlayer.gamemode != Gamemode.adventure) return true;

		int blockId = mc.currentWorld.getBlockId(x, y, z);
		Block<?> targetBlock = Blocks.getBlock(blockId);
		if (targetBlock == null) return false;
		String targetKey = targetBlock.getKey();

		if (GlobalRules.canDoGlobal(heldItem.getItem(), targetKey, false)) return true;

		if (heldItem.getData() != null && heldItem.getData().containsKey("CanPlaceOn")) {
			ListTag list = heldItem.getData().getList("CanPlaceOn");
			for (int i = 0; i < list.tagCount(); i++) {
				Tag<?> tag = list.tagAt(i);
				if (tag instanceof com.mojang.nbt.tags.CompoundTag) {
					if (((com.mojang.nbt.tags.CompoundTag) tag).getString("blockKey").equals(targetKey)) return true;
				}
			}
		}
		return false;
	}

	@Inject(method = "useOrPlaceItemStackOnTile", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;useItem(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;IIILnet/minecraft/core/util/helper/Side;DD)Z"), cancellable = true)
	private void mmtk_stopUseOrPlace(Player player, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (!canPlayerPlaceBlockOn(blockX, blockY, blockZ, itemstack)) cir.setReturnValue(false);
	}

	@Inject(method = "placeItemStackOnTile", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopPlaceBlock(Player player, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (!canPlayerPlaceBlockOn(blockX, blockY, blockZ, itemstack)) cir.setReturnValue(false);
	}

//	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
//	private void mmtk_stopInstantDestroy(int x, int y, int z, Side side, Player player, CallbackInfoReturnable<Boolean> cir) {
//		cir.setReturnValue(true);
////		if (!canPlayerBreakBlock(x, y, z)) {
////			cir.setReturnValue(false);
////		}
//}


}
