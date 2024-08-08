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
package com.github.dachhack.sprout.items.weapon.melee;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.mobs.Gullin;
import com.github.dachhack.sprout.actors.mobs.Kupua;
import com.github.dachhack.sprout.actors.mobs.MineSentinel;
import com.github.dachhack.sprout.actors.mobs.Otiluke;
import com.github.dachhack.sprout.actors.mobs.Zot;
import com.github.dachhack.sprout.actors.mobs.ZotPhase;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RoyalSpork extends MeleeWeapon {

	{
//		name = "royal spork";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ROYALSPORK;
	}

	public RoyalSpork() {
		super(1, 1f, 0.15f);
	}


	@Override
	public void proc(Char attacker, Char defender, int damage) {
		
		if (enchantment != null) {
			enchantment.proc(this, attacker, defender, damage);		
		}
	}
	
	@Override
//	public String desc() {
//		return "Is it a spoon? Is it a fork? It is neither and it is both. "
//				+"This one is gold and covered in jewels. Totally special. "
//				+"Mobs better respect!";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}
}
