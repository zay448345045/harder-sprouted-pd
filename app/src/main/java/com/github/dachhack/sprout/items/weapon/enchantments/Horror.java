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
package com.github.dachhack.sprout.items.weapon.enchantments;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.actors.buffs.Vertigo;
import com.github.dachhack.sprout.items.weapon.Weapon;
import com.github.dachhack.sprout.items.weapon.melee.relic.RelicMeleeWeapon;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Horror extends Weapon.Enchantment {

//	private static final String TXT_ELDRITCH = "Eldritch %s";
private static final String TXT_ELDRITCH = Messages.get(Horror.class, "name");

	private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x222222);

	@Override
	public boolean proc(RelicMeleeWeapon weapon, Char attacker, Char defender, int damage) {
		return false;
	}
	
	@Override
	public boolean proc(Weapon weapon, Char attacker, Char defender, int damage) {
		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		int level = Math.max(0, weapon.level);

		if (Random.Int(level + 5) >= 4) {

			if (defender == Dungeon.hero) {
				Buff.affect(defender, Vertigo.class, Vertigo.duration(defender));
			} else {
				Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker
						.id();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public Glowing glowing() {
		return GREY;
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_ELDRITCH, weaponName);
	}

}
