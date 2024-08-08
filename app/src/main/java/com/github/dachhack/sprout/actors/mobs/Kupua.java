/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.github.dachhack.sprout.actors.mobs;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.CorruptGas;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.items.StoneOre;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.KupuaSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Kupua extends Mob {
	
	{
		name = Messages.get(this, "name");
		spriteClass = KupuaSprite.class;

		HP = HT = 1800;

		EXP = 10;
		maxLvl = 999;

		loot = new StoneOre();
		lootChance = 0.9f;

		scalesWithHeroLevel = true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(200, 300);
	}

	@Override
	public int dr() {
		return 50;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (dmg > HT/8){
			GameScene.add(Blob.seed(pos, 30, CorruptGas.class));
		}
		
		super.damage(dmg, src);
	}
	
	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(CorruptGas.class);
		RESISTANCES.add(Death.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
