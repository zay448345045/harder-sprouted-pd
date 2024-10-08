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

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;

public class GoldenSkeletonKey extends Key {

	{
//		name = "golden skeleton key";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.GOLDEN_KEY;
	}

	public GoldenSkeletonKey() {
		this(0);
	}

	public GoldenSkeletonKey(int depth) {
		super();
		this.depth = depth;
	}
	
    private static final Glowing WHITE = new Glowing(0xFFFFCC);	

	@Override
	public Glowing glowing() {
		return WHITE;
	}

	@Override
//	public String info() {
//		return "The notches on this golden key are shifting and moving as if alive. "
//				+ "Maybe it can open some chest lock?";
//	}
	public String info() {
		return Messages.get(this, "desc");
	}
}
