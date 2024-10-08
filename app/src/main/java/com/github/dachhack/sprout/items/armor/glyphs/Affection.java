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
package com.github.dachhack.sprout.items.armor.glyphs;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Charm;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.armor.Armor.Glyph;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Affection extends Glyph {

	private static final String TXT_AFFECTION = Messages.get(Affection.class, "name");

	private static ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF4488);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		int level = (int) GameMath.gate(0, armor.level, 6);

		if (Level.adjacent(attacker.pos, defender.pos)
				&& Random.Int(level / 2 + 5) >= 4) {

			int duration = Random.IntRange(3, 7);

			Buff.affect(attacker, Charm.class, Charm.durationFactor(attacker)
					* duration).object = defender.id();
			attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART),
					0.2f, 5);

			//duration *= Random.Float(0.5f, 1);

			//Buff.affect(defender, Charm.class, Charm.durationFactor(defender)
			//		* duration).object = attacker.id();
			//defender.sprite.centerEmitter().start(Speck.factory(Speck.HEART),
			//		0.2f, 5);
		}

		return damage;
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_AFFECTION, weaponName);
	}

	@Override
	public Glowing glowing() {
		return PINK;
	}
}
