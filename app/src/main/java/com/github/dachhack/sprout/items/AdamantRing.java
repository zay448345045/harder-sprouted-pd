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
package com.github.dachhack.sprout.items;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;

public class AdamantRing extends Item {

		{
//		name = "adamantite ring";
			name = Messages.get(this, "name");
		image = ItemSpriteSheet.RING_ADAMANT;

		unique = true;
	}

	

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	
	@Override
//	public String info() {
//		return "An old ring missing its jewel. It's made of a dull grey ore.";
//	}
//
	public String info() {
		return Messages.get(this, "desc");
	}
}
