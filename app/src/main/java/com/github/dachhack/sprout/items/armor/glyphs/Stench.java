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
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.GasesImmunity;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.armor.Armor.Glyph;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Stench extends Glyph {

//	private static final String TXT_STENCH = "%s of stench";
private static final String TXT_STENCH = Messages.get(Stench.class, "name");

	private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max(0, armor.level);

		if (Level.adjacent(attacker.pos, defender.pos)
				&& Random.Int(level + 5) >= 4) {

			Buff.prolong(defender, GasesImmunity.class, GasesImmunity.DURATION);
			GameScene.add(Blob.seed(attacker.pos, 20, ToxicGas.class));

		}

		return damage;
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_STENCH, weaponName);
	}

	@Override
	public Glowing glowing() {
		return GREEN;
	}

}
