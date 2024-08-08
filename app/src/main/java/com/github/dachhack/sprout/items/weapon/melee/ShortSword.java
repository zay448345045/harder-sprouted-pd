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
package com.github.dachhack.sprout.items.weapon.melee;

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.quest.DarkGold;
import com.github.dachhack.sprout.items.scrolls.ScrollOfUpgrade;
import com.github.dachhack.sprout.items.weapon.missiles.Boomerang;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ShortSword extends MeleeWeapon {

//	public static final String AC_REFORGE = "REFORGE";
//
//	private static final String TXT_SELECT_WEAPON = "Select a weapon to upgrade";
//
//	private static final String TXT_REFORGED = "you reforged the short sword to upgrade your %s";
//	private static final String TXT_NOT_BOOMERANG = "you can't upgrade a boomerang this way";
public static final String AC_REFORGE = Messages.get(ShortSword.class, "ac_reforge");

	private static final String TXT_SELECT_WEAPON = Messages.get(ShortSword.class, "title");

	private static final String TXT_REFORGED = Messages.get(ShortSword.class, "reforged");
	private static final String TXT_NOT_BOOMERANG = Messages.get(ShortSword.class, "notboomerang");

	private static final float TIME_TO_REFORGE = 2f;

	private boolean equipped;
	
	private float upgradeChance = 0.5f;

	{
//		name = "short sword";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.SHORT_SWORD;

		bones = false;
	}

	public ShortSword() {
		super(1, 1f, 1f);

		STR = 11;
		MAX = 12;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (level > 0) {
			actions.add(AC_REFORGE);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action == AC_REFORGE) {

			if (hero.belongings.weapon == this) {
				equipped = true;
				hero.belongings.weapon = null;
			} else {
				equipped = false;
				detach(hero.belongings.backpack);
			}

			curUser = hero;

			GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON,
					TXT_SELECT_WEAPON);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
//	public String desc() {
//		return "It is indeed quite short, just a few inches longer, than a dagger.";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}


	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
			if (gold!=null){
			upgradeChance = (upgradeChance + (gold.quantity()*0.01f));
			}
			if (item != null && !(item instanceof Boomerang)) {
                int i=0;
				while(i<level) {
					if (i<2){
					  Sample.INSTANCE.play(Assets.SND_EVOKE);
					  ScrollOfUpgrade.upgrade(curUser);
					  evoke(curUser);
					  item.upgrade();
					} else if (Random.Float()<upgradeChance){
						if (item.level<15 || item.reinforced){
				            Sample.INSTANCE.play(Assets.SND_EVOKE);
				            ScrollOfUpgrade.upgrade(curUser);
				            evoke(curUser);
				            item.upgrade();
				            upgradeChance = Math.max(0.5f, upgradeChance-0.1f);
//						 } else {
//							 GLog.w("%s is not strong enough to recieve anymore upgrades!", item.name());
							GLog.w(Messages.get(ShortSword.class, "notenough"), item.name());
							 i=level;
						 }
				  }
				i++;
				}
							
				curUser.spendAndNext(TIME_TO_REFORGE);

				GLog.w(TXT_REFORGED, item.name());
				Badges.validateItemLevelAquired(item);

			} else {

				if (item instanceof Boomerang) {
					GLog.w(TXT_NOT_BOOMERANG);
				}

				if (equipped) {
					curUser.belongings.weapon = ShortSword.this;
				} else {
					collect(curUser.belongings.backpack);
				}
			}
		}
	};
}
