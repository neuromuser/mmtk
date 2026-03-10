package com.neuromuser.mmtk.mixin;

import com.mojang.nbt.tags.Tag;
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
		if (heldItem != null && heldItem.getData().containsKey("CanBreak")) {

			int blockId = mc.currentWorld.getBlockId(x, y, z);
			Block<?> targetBlock = Blocks.getBlock(blockId);

			if (targetBlock != null) {
				String targetKey = targetBlock.getKey();
				ListTag list = heldItem.getData().getList("CanBreak");

				for (int i = 0; i < list.tagCount(); i++) {
					Tag<?> tag = list.tagAt(i);

					if (tag instanceof com.mojang.nbt.tags.CompoundTag) {
						com.mojang.nbt.tags.CompoundTag entry = (com.mojang.nbt.tags.CompoundTag) tag;
						if (entry.getString("blockKey").equals(targetKey)) {
							return true;
						}
					}
					else if (tag instanceof com.mojang.nbt.tags.StringTag) {
						String legacyKey = ((com.mojang.nbt.tags.StringTag) tag).getValue();
						if (legacyKey.equals(targetKey)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopStartDestroy(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		if (!canPlayerBreakBlock(x, y, z)) {
			ci.cancel();
		}
	}

	@Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopContinueDestroy(int x, int y, int z, Side side, double xHit, double yHit, CallbackInfo ci) {
		if (!canPlayerBreakBlock(x, y, z)) {
			ci.cancel();
		}
	}

// --- CAN PLACE ON LOGIC ---

	@Unique
	private boolean canPlayerPlaceBlockOn(int x, int y, int z, ItemStack heldItem) {
		if (mc.thePlayer == null) return false;

		if (mc.thePlayer.gamemode != Gamemode.adventure) return true;

		if (heldItem != null && heldItem.getData() != null && heldItem.getData().containsKey("CanPlaceOn")) {
			int blockId = mc.currentWorld.getBlockId(x, y, z);
			Block<?> targetBlock = Blocks.getBlock(blockId);

			if (targetBlock != null) {
				String targetKey = targetBlock.getKey();
				ListTag list = heldItem.getData().getList("CanPlaceOn");

				for (int i = 0; i < list.tagCount(); i++) {
					Tag<?> tag = list.tagAt(i);

					if (tag instanceof com.mojang.nbt.tags.CompoundTag) {
						com.mojang.nbt.tags.CompoundTag entry = (com.mojang.nbt.tags.CompoundTag) tag;
						if (entry.getString("blockKey").equals(targetKey)) {
							return true;
						}
					}
					else if (tag instanceof com.mojang.nbt.tags.StringTag) {
						String legacyKey = ((com.mojang.nbt.tags.StringTag) tag).getValue();
						if (legacyKey.equals(targetKey)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// Intercept standard player right-clicks (covers 99% of block placement)
	@Inject(
		method = "useOrPlaceItemStackOnTile",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;useItem(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;IIILnet/minecraft/core/util/helper/Side;DD)Z"),
		cancellable = true
	)
	private void mmtk_stopUseOrPlace(Player player, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (!canPlayerPlaceBlockOn(blockX, blockY, blockZ, itemstack)) {
			cir.setReturnValue(false);
		}
	}

	// Keep this as a fallback for any edge-cases or modded block placers that directly call the secondary method
	@Inject(method = "placeItemStackOnTile", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopPlaceBlock(Player player, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (!canPlayerPlaceBlockOn(blockX, blockY, blockZ, itemstack)) {
			cir.setReturnValue(false);
		}
	}
}
