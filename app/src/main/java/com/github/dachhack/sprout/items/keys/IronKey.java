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
package com.github.dachhack.sprout.items.keys;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.items.bags.Bag;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.Utils;

public class IronKey extends Key {

//	private static final String TXT_FROM_DEPTH = "iron key from depth %d";
private static final String TXT_FROM_DEPTH = Messages.get(IronKey.class, "depth");

	public static int curDepthQuantity = 0;

	{
//		name = "iron key";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.IRON_KEY;
	}

	public IronKey() {
		this(0);
	}

	public IronKey(int depth) {
		super();
		this.depth = depth;
	}

	@Override
	public boolean collect(Bag bag) {
		boolean result = super.collect(bag);
		if (result && depth == Dungeon.depth && Dungeon.hero != null) {
			Dungeon.hero.belongings.countIronKeys();
		}
		return result;
	}

	@Override
	public void onDetach() {
		if (depth == Dungeon.depth) {
			Dungeon.hero.belongings.countIronKeys();
		}
	}

	@Override
	public String toString() {
		return Utils.format(TXT_FROM_DEPTH, depth);
	}

	@Override
//	public String info() {
//		return "The notches on this ancient iron key are well worn; its leather lanyard "
//				+ "is battered by age. What door might it open?";
//	}
	public String info() {
		return Messages.get(this, "desc");
	}
}
