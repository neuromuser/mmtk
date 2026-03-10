package com.neuromuser.mmtk.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandSource;

public class CMDCanBreak implements Command<CommandSource> {

	@Override
	public int run(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();

		if (!(source.getSender() instanceof Player)) {
			source.sendMessage("Only players can hold items.");
			return 0;
		}

		Player player = source.getSender();

		ItemStack heldItem = player.inventory.getCurrentItem();

		if (heldItem == null) {
			source.sendMessage("You must be holding an item in your hand!");
			return 0;
		}

		if (!heldItem.getItem().isDamagable()) {
			source.sendMessage("This item is not a tool or armor!");
			return 0;
		}

		CompoundTag tag = heldItem.getData();
		tag.putBoolean("Unbreakable", true);

		source.sendMessage("Your item is now Unbreakable!");

		return SINGLE_SUCCESS;
	}
}
