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
import com.github.dachhack.sprout.actors.buffs.Hunger;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.armor.Armor.Glyph;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.github.dachhack.sprout.ui.BuffIndicator;
import com.watabou.utils.Random;

public class Metabolism extends Glyph {

//	private static final String TXT_METABOLISM = "%s of metabolism";
private static final String TXT_METABOLISM = Messages.get(Metabolism.class, "name");

	private static ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max(0, armor.level);
		if (Random.Int(level / 2 + 5) >= 4) {

			int healing = Math.min(defender.HT - defender.HP,
					Random.Int(1, defender.HT / 5));

			if (healing > 0) {

				Hunger hunger = defender.buff(Hunger.class);

				if (hunger != null && !hunger.isStarving()) {

					//hunger.satisfy(-Hunger.STARVING / 10);
					BuffIndicator.refreshHero();

					defender.HP += healing;
					defender.sprite.emitter().burst(
							Speck.factory(Speck.HEALING), 1);
					defender.sprite.showStatus(CharSprite.POSITIVE,
							Integer.toString(healing));
				}
			}

		}

		return damage;
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_METABOLISM, weaponName);
	}

	@Override
	public Glowing glowing() {
		return RED;
	}
}
