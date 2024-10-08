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

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.food.Meat;
import com.github.dachhack.sprout.items.potions.PotionOfMending;
import com.github.dachhack.sprout.items.weapon.enchantments.Leech;
import com.github.dachhack.sprout.sprites.BatSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Bat extends Mob {

	{
		name = Messages.get(this, "name");
		spriteClass = BatSprite.class;

		HP = HT = 80;
		baseSpeed = 2f;

		EXP = 7;
		maxLvl = 15;

		flying = true;

		loot = new PotionOfMending();
		lootChance = 0.1667f; // by default, see die()

		lootOther = new Meat();
		lootChanceOther = 0.5f; // by default, see die()
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 35);
	}

	@Override
	public int dr() {
		return 8;
	}

	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public int attackProc(Char enemy, int damage) {

		int reg = Math.min(damage, HT - HP);

		if (reg > 0) {
			HP += reg;
			sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}

		return damage;
	}

	@Override
	public void die(Object cause) {
		// sets drop chance
		lootChance = 1f / ((6 + Dungeon.limitedDrops.batHP.count));
		super.die(cause);
	}

	@Override
	protected Item createLoot() {
		Dungeon.limitedDrops.batHP.count++;
		return super.createLoot();
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Leech.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
