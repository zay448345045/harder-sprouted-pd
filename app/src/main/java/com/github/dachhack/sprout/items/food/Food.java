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
package com.github.dachhack.sprout.items.food;

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.buffs.Hunger;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.SpellSprite;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.scrolls.ScrollOfRecharging;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Food extends Item {

	private static final float TIME_TO_EAT = 3f;

//	public static final String AC_EAT = "EAT";
public static final String AC_EAT = Messages.get(Food.class, "ac_eat");

	public float energy = Hunger.HUNGRY;
//	public String message = "That food tasted delicious!";
public String message = Messages.get(this, "eat_msg");

	public int hornValue = 3;

	{
		stackable = true;
//		name = "ration of food";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.RATION;

		bones = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_EAT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_EAT)) {

			detach(hero.belongings.backpack);

			hero.buff(Hunger.class).satisfy(energy);
			GLog.i(message);
            int healEnergy = Math.max(7, Math.round(energy/40));
			switch (hero.heroClass) {
			case WARRIOR:
				if (hero.HP < hero.HT) {
					hero.HP = Math.min(hero.HP + Random.Int(3, healEnergy), hero.HT);
					hero.sprite.emitter()
							.burst(Speck.factory(Speck.HEALING), 1);
				}
				break;
			case MAGE:
				hero.belongings.charge(false);
				ScrollOfRecharging.charge(hero);
				if (hero.HP < hero.HT) {
					hero.HP = Math.min((hero.HP + Random.Int(1, 3)), hero.HT);
					hero.sprite.emitter()
							.burst(Speck.factory(Speck.HEALING), 1);
				}
				break;
			case ROGUE:
				if (hero.HP < hero.HT) {
					hero.HP = Math.min((hero.HP + Random.Int(1, 3)), hero.HT);
					hero.sprite.emitter()
							.burst(Speck.factory(Speck.HEALING), 1);
				}
			case HUNTRESS:
				if (hero.HP < hero.HT) {
					hero.HP = Math.min((hero.HP + Random.Int(1, 3)), hero.HT);
					hero.sprite.emitter()
							.burst(Speck.factory(Speck.HEALING), 1);
				}
				break;
			}

			hero.sprite.operate(hero.pos);
			hero.busy();
			SpellSprite.show(hero, SpellSprite.FOOD);
			Sample.INSTANCE.play(Assets.SND_EAT);

			hero.spend(TIME_TO_EAT);

			Statistics.foodEaten++;
			Badges.validateFoodEaten();

		} else {

			super.execute(hero, action);

		}
	}

//	@Override
//	public String info() {
//		return "Nothing fancy here: dried meat, "
//				+ "some biscuits - things like that.";
//	}
@Override
public String info() {
	return Messages.get(this, "desc");
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
	public int price() {
		return 10 * quantity;
	}
}
