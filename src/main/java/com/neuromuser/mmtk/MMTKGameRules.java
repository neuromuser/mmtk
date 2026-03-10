package com.neuromuser.mmtk;

import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.world.World;

public class MMTKGameRules {
	public static GameRule<Boolean> FALL_DAMAGE;

	public static void register() {
		FALL_DAMAGE = GameRules.register(new GameRuleBoolean("mmtk_fallDamage", true));
	}

	public static boolean isFallDamageEnabled(World world) {
		return world.getGameRuleValue(FALL_DAMAGE);
	}
}
