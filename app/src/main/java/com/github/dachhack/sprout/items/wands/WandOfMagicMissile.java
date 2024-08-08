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
package com.github.dachhack.sprout.items.wands;

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Strength;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.quest.DarkGold;
import com.github.dachhack.sprout.items.scrolls.ScrollOfUpgrade;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.github.dachhack.sprout.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WandOfMagicMissile extends Wand {

//	public static final String AC_DISENCHANT = "DISENCHANT";
//
//	private static final String TXT_SELECT_WAND = "Select a wand to upgrade";
//
//	private static final String TXT_DISENCHANTED = "you disenchanted the Wand of Magic Missile and used its essence to upgrade your %s";
public static final String AC_DISENCHANT = Messages.get(WandOfMagicMissile.class, "ac_dis");

	private static final String TXT_SELECT_WAND = Messages.get(WandOfMagicMissile.class, "title");

	private static final String TXT_DISENCHANTED = Messages.get(WandOfMagicMissile.class, "dised");

	private static final float TIME_TO_DISENCHANT = 2f;

	private boolean disenchantEquipped;
	
	private float upgradeChance = 0.5f;

	{
//		name = "Wand of Magic Missile";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE;

		bones = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (level > 0) {
			actions.add(AC_DISENCHANT);
		}
		return actions;
	}

	@Override
	public int magicMax(int lvl) {
		return 6 + lvl*3;
	}

	@Override
	public int magicMin(int lvl) {
		return 2 + lvl*2;
	}

	@Override
	protected void onZap(int cell) {

		Char ch = Actor.findChar(cell);
		if (ch != null) {

			int level = level();
            int damage= magicDamageRoll();
            if (Dungeon.hero.buff(Strength.class) != null){ damage *= (int) 4f; Buff.detach(Dungeon.hero, Strength.class);}
			ch.damage(damage, this);
            processSoulMark(ch, chargesPerCast());

			ch.sprite.burst(0xFF99CCFF, level / 2 + 2);

			if (ch == curUser && !ch.isAlive()) {
				Dungeon.fail(Utils.format(ResultDescriptions.ITEM, name));
//				GLog.n("You killed yourself with your own Wand of Magic Missile...");
				GLog.n(Messages.get(this,"killed_miss"));
			}
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_DISENCHANT)) {

			if (hero.belongings.weapon == this) {
				disenchantEquipped = true;
				hero.belongings.weapon = null;
				updateQuickslot();
			} else {
				disenchantEquipped = false;
				detach(hero.belongings.backpack);
			}

			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.WAND,
					TXT_SELECT_WAND);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	protected boolean isKnown() {
		return true;
	}

	@Override
	public void setKnown() {
	}

	@Override
	protected int initialCharges() {
		return 3;
	}

	@Override
	public String desc() {
//		return "This wand launches missiles of pure magical energy, dealing moderate damage to a target creature." +
//				"\n\n" + statsDesc();
		return Messages.get(this, "desc", 3 + level(), 6 + level() * 2);
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		attacker.belongings.charge(false);//Grants 1 charge for each wand in the hero's inventory
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {

				Sample.INSTANCE.play(Assets.SND_EVOKE);
				ScrollOfUpgrade.upgrade(curUser);
				evoke(curUser);

				GLog.w(TXT_DISENCHANTED, item.name());

				Dungeon.quickslot.clearItem(WandOfMagicMissile.this);
				WandOfMagicMissile.this.updateQuickslot();
				
				DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
				if (gold!=null){
				upgradeChance = (upgradeChance + (gold.quantity()*0.01f));
				}

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
							 } else {
//								 GLog.w("%s is not strong enough to recieve anymore upgrades!", item.name());
								GLog.w(Messages.get(WandOfMagicMissile.class, "notenough", item.name()));
								 i=level;
							 }
					  }
					i++;
					}
				
				item.upgrade();
				curUser.spendAndNext(TIME_TO_DISENCHANT);

				Badges.validateItemLevelAquired(item);

			} else {
				if (disenchantEquipped) {
					curUser.belongings.weapon = WandOfMagicMissile.this;
					WandOfMagicMissile.this.updateQuickslot();
				} else {
					collect(curUser.belongings.backpack);
				}
			}
		}
	};
}
