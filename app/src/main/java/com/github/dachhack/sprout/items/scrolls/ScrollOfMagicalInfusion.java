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
package com.github.dachhack.sprout.items.scrolls;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.weapon.Weapon;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.windows.WndBag;

public class ScrollOfMagicalInfusion extends InventoryScroll {

//	private static final String TXT_INFUSE = "your %s is infused with arcane energy!";
private static final String TXT_INFUSE = Messages.get(ScrollOfMagicalInfusion.class, "infuse");

	{
//		name = "Scroll of Magical Infusion";
//		inventoryTitle = "Select an item to infuse";
		name = Messages.get(this, "name");
		inventoryTitle = Messages.get(this, "inv_title");
		mode = WndBag.Mode.UPGRADEABLE;
		consumedValue = 15;

		bones = true;
	}

	@Override
	protected void onItemSelected(Item item) {

		ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
		if (item instanceof Weapon)
			((Weapon) item).upgrade(true);
		else if (item instanceof Armor)
			((Armor) item).upgrade(true);
		else
			item.upgrade();

		GLog.p(TXT_INFUSE, item.name());

		Badges.validateItemLevelAquired(item);

		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
	}

	@Override
//	public String desc() {
//		return "This scroll will infuse a weapon or armor with powerful magical energy.\n\n"
//				+ "In addition to being upgraded, A weapon will gain a magical enchantment, or armor will be imbued with a magical glyph.\n\n"
//				+ "If the item already has an enchantment or glyph, it will be erased and replaced by the upgrade.";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}
}
