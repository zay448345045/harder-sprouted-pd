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
import com.github.dachhack.sprout.actors.buffs.Amok;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.npcs.Imp;
import com.github.dachhack.sprout.items.KindOfWeapon;
import com.github.dachhack.sprout.items.food.Food;
import com.github.dachhack.sprout.items.food.Meat;
import com.github.dachhack.sprout.items.weapon.melee.Knuckles;
import com.github.dachhack.sprout.sprites.MonkSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Monk extends Mob {

	public static final String TXT_DISARM = "%s has knocked the %s from your hands!";

	{
		name = Messages.get(this, "name");
		spriteClass = MonkSprite.class;

		HP = HT = 150;
		defenseSkill = 30+ adjustForDepth(1);

		EXP = 11;
		maxLvl = 21;

		loot = new Food();
		lootChance = 0.1f;
		
		lootOther = new Meat();
		lootChanceOther = 0.1f; // by default, see die()
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(25, 65);
	}

	@Override
	protected float attackDelay() {
		return 0.5f;
	}

	@Override
	public int dr() {
		return 2+ adjustForDepth(0);
	}

	@Override
                          public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public void die(Object cause) {
		Imp.Quest.process(this);

		super.die(cause);
	}

	@Override
	public int attackProc(Char enemy, int damage) {

		if (Random.Int(12) == 0 && enemy == Dungeon.hero) {

			Hero hero = Dungeon.hero;
			KindOfWeapon weapon = hero.belongings.weapon;

			if (weapon != null && !(weapon instanceof Knuckles)
					&& !weapon.cursed) {
				hero.belongings.weapon = null;
				Dungeon.level.drop(weapon, hero.pos).sprite.drop();
				GLog.w( Messages.get(Monk.class, "arm"), name, weapon.name());
				weapon.updateQuickslot();
			}
		}

		return damage;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Amok.class);
		IMMUNITIES.add(Terror.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
