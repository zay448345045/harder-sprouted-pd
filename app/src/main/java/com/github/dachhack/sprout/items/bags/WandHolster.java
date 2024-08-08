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
package com.github.dachhack.sprout.items.bags;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.wands.Wand;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;

public class WandHolster extends Bag {

	{
//		name = "wand holster";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.HOLSTER;

		size = 12;
	}

	@Override
	public boolean grab(Item item) {
		return item instanceof Wand;
	}

	@Override
	public boolean collect(Bag container) {
		if (super.collect(container)) {
			if (owner != null) {
				for (Item item : items) {
					((Wand) item).charge(owner);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach() {
		for (Item item : items) {
			((Wand) item).stopCharging();
		}
	}

	@Override
	public int price() {
		return 50;
	}

	@Override
//	public String info() {
//		return "This slim holster is made from some exotic animal, and is designed to compactly carry up to "
//				+ size
//				+ " wands.\n\n"
//				+ "The size seems a bit excessive, who would ever have that many wands?";
//	}
	public String info() {
		return Messages.get(this, "desc");
	}
}
