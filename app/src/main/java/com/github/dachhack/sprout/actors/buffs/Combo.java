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
package com.github.dachhack.sprout.actors.buffs;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.ui.BuffIndicator;
import com.github.dachhack.sprout.utils.GLog;

public class Combo extends Buff {

	private static String TXT_COMBO = "%d hit combo!";

	public int count = 0;

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	public int hit(Char enemy, int damage) {

		count++;

		if (count >= 3) {

			Badges.validateMasteryCombo(count);

			GLog.p(Messages.get(Combo.class, "combo"), count);
			postpone(2f);
			return (int) Math.min(damage, damage * (count - 2) / 5f);

		} else {

			postpone(3f);
			return 0;

		}
	}

	@Override
	public boolean act() {
		detach();
		return true;
	}

}
