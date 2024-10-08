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

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.Water;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Haste;
import com.github.dachhack.sprout.actors.buffs.Invisibility;
import com.github.dachhack.sprout.actors.buffs.Levitation;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.particles.ShadowParticle;
import com.github.dachhack.sprout.items.bags.Bag;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.github.dachhack.sprout.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class DewVial extends Item {

	private static final int MAX_VOLUME = 100;
	private static final int EXT_VOLUME = 300;
	private static final int BLESS_VOLUME = 10;
	
	
	private static final int MAX_VOLUME(){
		return Dungeon.wings ? EXT_VOLUME : MAX_VOLUME;
	}

	private static final String AC_SIP = "SIP";
	private static final String AC_DRINK = "DRINK";
	private static final String AC_WATER = "WATER";
	private static final String AC_SPLASH = "SPLASH";
	private static final String AC_BLESS = "BLESS";


	private static final float TIME_TO_DRINK = 1f;
	private static final float TIME_TO_WATER = 3f;
	protected static final float TIME_TO_BLESS = 1f;

	private static final String TXT_VALUE = "%+dHP";
	private static final String TXT_STATUS = "%d/%d";

	//private static final String TXT_AUTO_DRINK = "The dew vial was emptied to heal your wounds.";
	private static final String TXT_COLLECTED = "You collected a dewdrop into your dew vial.";
	private static final String TXT_WATERED = "The dungeon regrows around you.";
	private static final String TXT_REFRESHED = "You splash the dew on your face.";
	private static final String TXT_BLESSED = "You feel the dew blessing items in your backpack.";
	private static final String TXT_PROCCED = "Your pack glows with a cleansing light, and a malevolent energy disperses.";
	private static final String TXT_NOT_PROCCED = "Your pack glows with a cleansing light, but nothing was uncursed.";
	private static final String TXT_FULL = "Your dew vial is full!";
	private static final String TXT_EMPTY = "Your dew vial is empty!";
	private static final String TXT_LOOKS_BETTER = "your %s certainly looks better now";
	private static final String TXT_SELECT = "Select an item to upgrade";

	{
		name = "dew vial";
		image = ItemSpriteSheet.VIAL;

		defaultAction = AC_BLESS;

		unique = true;
	}

	private int volume = 0;
	
	public int checkVol () {
		return volume;
	}

	public void setVol (int vol) {
		volume=vol;		
	}

	public static int upgradeLimit() {
		int levelLimit = 2+Math.round(Statistics.deepestFloor/2f);
		if (Dungeon.hero.heroClass == HeroClass.MAGE){levelLimit++;}
		levelLimit = Math.min(15,levelLimit);
		return levelLimit;
	}
	
	private static final String VOLUME = "volume";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VOLUME, volume);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		volume = bundle.getInt(VOLUME);
	}
	
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (volume > 99) {
			actions.add(AC_BLESS);
		}
		
		 if (volume > 29) {
			actions.add(AC_SPLASH);
		}
		 if (volume > 2) {
			actions.add(AC_DRINK);

		} 
		if (volume > 0) {
			actions.add(AC_SIP);
			
		}
		if (volume > 2 & Dungeon.dewWater) {
			actions.add(AC_WATER);
		}
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		
		if (action.equals(AC_SIP)) {

			if (volume > 0) {

				int value = 1 + (Dungeon.depth - 1) / 5;
				if (hero.heroClass == HeroClass.HUNTRESS) {
					value++;
				}
				if (volume < 3){
				value *= volume;
				} else {
				value *= 3;	
				}
				int effect = Math.min(hero.HT - hero.HP, value);
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect);
				}

				if (volume < 3) {

					volume = 0;

				} else {

					volume = volume - 3;
				}

				hero.spend(TIME_TO_DRINK);
				hero.busy();

				Sample.INSTANCE.play(Assets.SND_DRINK);
				hero.sprite.operate(hero.pos);

				updateQuickslot();

			} else {
				GLog.w(TXT_EMPTY);
			}

		
		
		}else if (action.equals(AC_DRINK)) {

			if (volume > 0) {

				float missingHealthPercent = 1f - (hero.HP / (float)hero.HT);

				//trimming off 0.01 drops helps with floating point errors
				int dropsNeeded = (int)Math.ceil((missingHealthPercent / 0.05f) - 0.01f);
				dropsNeeded = (int) GameMath.gate(1, dropsNeeded, volume);

				//20 drops for a full heal normally
				int heal = Math.round( hero.HT * 0.05f * dropsNeeded );

				int effect = Math.min( hero.HT - hero.HP, heal );
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 + dropsNeeded/5 );
					hero.sprite.showStatus( CharSprite.POSITIVE, "+" + effect + "HP" );
				}
				volume -= dropsNeeded;

				hero.spend( TIME_TO_DRINK );
				hero.busy();

				Sample.INSTANCE.play( Assets.SND_DRINK );
				hero.sprite.operate( hero.pos );

				updateQuickslot();



			} else {
				GLog.w(TXT_EMPTY);
			}

		} else if (action.equals(AC_WATER)) {
			
			Statistics.waters++;			
			int positive = 0;
			int negative = 0;

			int distance = 1 + positive + negative;

			if (distance <= 0) {
				level /= 2 - distance;
				distance = 1;
			}

			int cx = hero.pos % Level.getWidth();
			int cy = hero.pos / Level.getWidth();
			int ax = cx - distance;
			if (ax < 0) {
				ax = 0;
			}
			int bx = cx + distance;
			if (bx >= Level.getWidth()) {
				bx = Level.getWidth() - 1;
			}
			int ay = cy - distance;
			if (ay < 0) {
				ay = 0;
			}
			int by = cy + distance;
			if (by >= Level.HEIGHT) {
				by = Level.HEIGHT - 1;
			}

			
			for (int y = ay; y <= by; y++) {
				for (int x = ax, p = ax + y * Level.getWidth(); x <= bx; x++, p++) {

					if (Dungeon.visible[p]) {
						int c = Dungeon.level.map[p];
						
						if (c == Terrain.GRASS) {
							GameScene.add(Blob.seed(p, (2) * 20, Water.class));
						}
					}
				}
			}
			volume = volume - 2;
			GLog.i(TXT_WATERED);
			hero.sprite.operate(hero.pos);
			hero.busy();
			hero.spend(TIME_TO_WATER);
		
		} else if (action.equals(AC_SPLASH)) {	
			Buff.affect(hero, Haste.class, Haste.DURATION);
			Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
			if(Dungeon.wings && Dungeon.depth<51){
				Buff.affect(hero, Levitation.class, Levitation.DURATION);
			    GLog.i("You float into the air!");
			}
			GLog.i(TXT_REFRESHED);
			GLog.i("You see your hands turn invisible!");
			GLog.i("You are moving much faster!");
			volume = volume - 10;
			
		} else if (action.equals(AC_BLESS) && !Dungeon.dewDraw && volume >= 100) {

			boolean procced = uncurse(hero, hero.belongings.backpack.items.toArray(new Item[0]));
			procced = uncurse(hero, hero.belongings.weapon,
					hero.belongings.armor, hero.belongings.misc1,
					hero.belongings.misc2)
					|| procced;
			
			if (procced) {
				GLog.p(TXT_PROCCED);
			} else {
				GLog.i(TXT_NOT_PROCCED);
			}
													
			volume -= 65;
			
		} else if (action.equals(AC_BLESS) && Dungeon.dewDraw && volume >= 100) {

			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEDEW,	TXT_SELECT);
			
		} else {

			super.execute(hero, action);

		}
	}

	public static boolean uncurse(Hero hero, Item... items) {


		int levelLimit = DewVial.upgradeLimit();
        
        float lvlchance = 0.50f;
        if (hero.heroClass == HeroClass.MAGE){lvlchance = 0.55f;}
        
        boolean procced = false;
		boolean proccedUp = false;
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (item != null && item.cursed) {
				item.uncurse();
				if(item.level<0){item.upgrade(-item.level);} //upgrade to even
				if (!item.cursed) {procced = true;}
			}
			
			if (item != null && Random.Float()<lvlchance && item.isUpgradable() && item.level < levelLimit){
			    item.upgrade();
			    proccedUp = true;
			    hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
			    GLog.p(TXT_LOOKS_BETTER, item.name());
			    Badges.validateItemLevelAquired(item);
			}
			
			if (item instanceof Bag) {
				for (Item bagItem: ((Bag)item).items){
                   if (bagItem != null && bagItem.cursed) {
                	   bagItem.uncurse();
                	   if(bagItem.level<0){bagItem.upgrade(-bagItem.level);}
                	   if (!bagItem.cursed) {procced = true;}
                   }
                   
                   if (bagItem != null && Random.Float()<lvlchance && bagItem.isUpgradable() && bagItem.level < levelLimit){
                	   bagItem.upgrade();
					    proccedUp = true;
					    hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
					    GLog.p(TXT_LOOKS_BETTER, bagItem.name());
					    Badges.validateItemLevelAquired(bagItem);
					}
				}   
			}			
		}
		
		if (proccedUp){GLog.i(TXT_BLESSED);}

		if (procced) {
			hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
		}
					
		return procced;
	}
			
  private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				upgrade(item);
				volume = volume - 90;
			}
		}
	};
	
	private void upgrade(Item item) {

		GLog.w(TXT_LOOKS_BETTER, item.name());

		item.upgrade();
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		Badges.validateItemLevelAquired(item);
		
		curUser.busy();
		
	}

	
	public void empty() {
		volume = volume - 10;
		updateQuickslot();
	}
	
	public void sip() {
		volume = volume - 1;
		updateQuickslot();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public boolean isFullBless() {
		return volume >= BLESS_VOLUME;
	}

	@Override
	public boolean collect() {
		Dungeon.dewVial = this;
		return super.collect();
	}

	public boolean isFull() {
		return volume >= MAX_VOLUME();
	}

	public void collectDew(Dewdrop dew, int amountToFill) {

		GLog.i(TXT_COLLECTED);
		volume += dew.quantity*amountToFill;
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}

		updateQuickslot();
	}
	
	
	public DewVial fill() {
		volume = MAX_VOLUME();
		GLog.p(TXT_FULL);
		updateQuickslot();
		return this;
	}

	// removed as people need a bigger distinction to realize the dew vial
	// doesn't revive.
	/*
	 * private static final Glowing WHITE = new Glowing( 0xFFFFCC );
	 * 
	 * @Override public Glowing glowing() { return isFull() ? WHITE : null; }
	 */

	@Override
	public String status() {
		return Utils.format(TXT_STATUS, volume, MAX_VOLUME());
	}

	@Override
	public String info() {
		return "You can store excess dew in this tiny vessel for drinking it later. "
				+ "The more full the vial is, the more each dew drop will heal you. "
				+ "Ten drops would probably be sufficient to fully heal your wounds. "
				+ "\n\nVials like this one used to be imbued with revival magic, "
				+ "but that power has faded. There still seems to be some residual power "
				+ "left, perhaps collected drops can bless another revival item.";
	}

	@Override
	public String toString() {
		return super.toString() + " (" + status() + ")";
	}
}
