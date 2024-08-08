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

import java.io.IOException;
import java.util.ArrayList;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.scenes.AmuletScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

public class Amulet extends Item {

//	private static final String AC_END = "END THE GAME";
private static final String AC_END = Messages.get(Amulet.class, "ac_end");

	{
//		name = "Amulet of Yendor";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.AMULET;

		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Badges.checkOtilukeRescued()) {//Only allow to end the game if Otiluke rescued.
			actions.add(AC_END);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_END)) {

			showAmuletScene(false);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean doPickUp(Hero hero) {
		if (super.doPickUp(hero)) {

			if (!Statistics.amuletObtained) {
				Statistics.amuletObtained = true;
				Badges.validateVictory();
				if (!Badges.checkOtilukeRescued()) {
					showAmuletScene(true);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void showAmuletScene(boolean showText) {
		try {
			Dungeon.saveAll();
			AmuletScene.noText = !showText;
			Game.switchScene(AmuletScene.class);
		} catch (IOException e) {
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
//	public String info() {
//		return "The Amulet of Yendor is the most powerful known artifact of unknown origin. It is said that the amulet "
//				+ "is able to fulfil any wish if its owner's will-power is strong enough to \"persuade\" it to do it.";
//	}
	public String info() {
		return Messages.get(this, "desc");
	}
}
