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
package com.github.dachhack.sprout.items.weapon.missiles;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.sprites.MissileSprite;

public class Boomerang extends MissileWeapon {

	{
//		name = "boomerang";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.BOOMERANG;

		STR = 10;

		MIN = 1;
		MAX = 4;

		stackable = false;
		unique = true;
		scaling = 2;
		upgradeable = true;

		bones = false;
	}

	@Override
	public boolean isUpgradable() {
		return true;
	}

	@Override
	public Item upgrade() {
		return upgrade(false);
	}

	@Override
	public Item upgrade(boolean enchant) {
		super.upgrade(enchant);

		updateQuickslot();

		return this;
	}

	@Override
	public Item degrade() {
		return super.degrade();
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		super.proc(attacker, defender, damage);
		if (attacker instanceof Hero && ((Hero) attacker).rangedWeapon == this) {
			circleBack(defender.pos, (Hero) attacker);
		}
	}

	@Override
	public int min() {
		return 1 + level;
	}

	@Override
	public int max() {
		return 6 + level*2;
	}

	@Override
	protected void miss(int cell) {
		circleBack(cell, curUser);
	}

	private void circleBack(int from, Hero owner) {

		((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class))
				.reset(from, curUser.pos, curItem, null);

		if (throwEquiped) {
			owner.belongings.weapon = this;
			owner.spend(-TIME_TO_EQUIP);
			Dungeon.quickslot.replaceSimilar(this);
			updateQuickslot();
		} else if (!collect(curUser.belongings.backpack)) {
			Dungeon.level.drop(this, owner.pos).sprite.drop();
		}
	}

	private boolean throwEquiped;

	@Override
	public void cast(Hero user, int dst) {
		throwEquiped = isEquipped(user);
		super.cast(user, dst);
	}

	@Override
	public String desc() {
//		String info = "Thrown to the enemy this flat curved wooden missile will return to the hands of its thrower.";
		String info = Messages.get(this, "desc");
		switch (imbue) {
		case LIGHT:
//			info += "\n\nIt was balanced to be lighter. ";
			info += Messages.get(this, "lighter");
			break;
		case HEAVY:
//			info += "\n\nIt was balanced to be heavier. ";
			info += Messages.get(this, "heavier");
			break;
		case NONE:
		}
		if(reinforced){
//			info += "\n\nIt is reinforced. ";
			info += Messages.get(this, "reinforced");
		}
		return info;
	}
}
