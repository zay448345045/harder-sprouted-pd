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
package com.github.dachhack.sprout.items.potions;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.utils.GLog;

public class PotionOfMight extends Potion {

	{
//		name = "Potion of Might";
		name = Messages.get(this, "name");

		bones = true;
	}

	@Override
	public void apply(Hero hero) {
		setKnown();

		hero.STR++;
		hero.HT += 5;
		hero.HP += 5;
//		hero.sprite.showStatus(CharSprite.POSITIVE, "+1 str, +5 ht");
//		GLog.p("Newfound strength surges through your body.");
		hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1"));
		GLog.p(Messages.get(this, "msg_2"));

		Badges.validateStrengthAttained();
	}

	@Override
//	public String desc() {
//		return "This powerful liquid will course through your muscles, permanently "
//				+ "increasing your strength by one point and health by five points.";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public int price() {
		return isKnown() ? 200 * quantity : super.price();
	}
}
