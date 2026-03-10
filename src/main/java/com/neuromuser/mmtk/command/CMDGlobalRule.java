package com.neuromuser.mmtk.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.neuromuser.mmtk.GlobalRules;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.helpers.BlockInput;

public class CMDGlobalRule implements Command<CommandSource> {
	private final boolean isBreak;

	public CMDGlobalRule(boolean isBreak) {
		this.isBreak = isBreak;
	}
	@Override
	public int run(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		if (!(source.getSender() instanceof Player)) return 0;

		BlockInput blockInput = context.getArgument("block_id", BlockInput.class);
		boolean state = context.getArgument("state", Boolean.class);

		Block<?> block = blockInput.getBlock();
		if (block == null) return 0;

		Player player = source.getSender();
		ItemStack heldItem = player.inventory.getCurrentItem();
		if (heldItem == null) return 0;

		GlobalRules.setGlobalRule(heldItem.getItem(), block.getKey(), isBreak, state);
		source.sendMessage("Global rule updated!");

		return 1;
	}}
