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
package com.github.dachhack.sprout.items.armor;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;

public class ScaleArmor extends Armor {

	{
//		name = "scale armor";
//		image = ItemSpriteSheet.ARMOR_SCALE;
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ARMOR_SCALE;
	}

	public ScaleArmor() {
		super(4);
	}

//	@Override
//	public String desc() {
//		return "The metal scales sewn onto a leather vest create a flexible, yet protective armor.";
@Override
public String desc() {
	return Messages.get(this, "desc");
}
}
