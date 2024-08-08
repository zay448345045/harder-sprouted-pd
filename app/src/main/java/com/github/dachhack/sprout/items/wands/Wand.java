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
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Invisibility;
import com.github.dachhack.sprout.actors.buffs.SoulMark;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.actors.hero.HeroSubClass;
import com.github.dachhack.sprout.effects.MagicMissile;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.ItemStatusHandler;
import com.github.dachhack.sprout.items.KindOfWeapon;
import com.github.dachhack.sprout.items.bags.Bag;
import com.github.dachhack.sprout.items.rings.RingOfMagic.Magic;
import com.github.dachhack.sprout.items.scrolls.ScrollOfRecharging;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.CellSelector;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.ui.QuickSlotButton;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public abstract class Wand extends KindOfWeapon {

	private static final int USAGES_TO_KNOW = 20;

//	public static final String AC_ZAP = "ZAP";
//
//	private static final String TXT_WOOD = "This thin %s wand is warm to the touch. Who knows what it will do when used?";
//	private static final String TXT_DAMAGE = "When this wand is used as a melee weapon, it will deal  %1$d-%2$d damage.";
//	private static final String TXT_WEAPON = "You can use this wand as a melee weapon.";
//
//	private static final String TXT_FIZZLES = "your wand fizzles; it must be out of charges for now";
//	private static final String TXT_SELF_TARGET = "You can't target yourself";
//
//	private static final String TXT_IDENTIFY = "You are now familiar enough with your %s.";
//
//	private static final String TXT_REINFORCED = "\n\nIt is reinforced. ";
public static final String AC_ZAP = Messages.get(Wand.class, "ac_zap");

	private static final String TXT_WOOD = Messages.get(Wand.class, "wood");
	private static final String TXT_DAMAGE = Messages.get(Wand.class, "damage");
	private static final String TXT_WEAPON = Messages.get(Wand.class, "weapon");

	private static final String TXT_FIZZLES = Messages.get(Wand.class, "fizzles");
	private static final String TXT_SELF_TARGET = Messages.get(Wand.class, "self_target");

	private static final String TXT_IDENTIFY = Messages.get(Wand.class, "identify");

	private static final String TXT_REINFORCED = Messages.get(Wand.class, "re");

	private static final float TIME_TO_ZAP = 1f;

	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;

	protected Charger charger;

	private boolean curChargeKnown = false;

	private int usagesToKnow = USAGES_TO_KNOW;

	protected boolean hitChars = true;

	private static final Class<?>[] wands = { WandOfTeleportation.class,
			WandOfSlowness.class, WandOfFirebolt.class, WandOfPoison.class,
			WandOfRegrowth.class, WandOfBlink.class, WandOfLightning.class,
			WandOfAmok.class, WandOfTelekinesis.class, WandOfFlock.class,
			WandOfDisintegration.class, WandOfAvalanche.class };
	private static final String[] woods = { "holly", "yew", "ebony", "cherry",
			"teak", "rowan", "willow", "mahogany", "bamboo", "purpleheart",
			"oak", "birch" };
	private static final Integer[] images = { ItemSpriteSheet.WAND_HOLLY,
			ItemSpriteSheet.WAND_YEW, ItemSpriteSheet.WAND_EBONY,
			ItemSpriteSheet.WAND_CHERRY, ItemSpriteSheet.WAND_TEAK,
			ItemSpriteSheet.WAND_ROWAN, ItemSpriteSheet.WAND_WILLOW,
			ItemSpriteSheet.WAND_MAHOGANY, ItemSpriteSheet.WAND_BAMBOO,
			ItemSpriteSheet.WAND_PURPLEHEART, ItemSpriteSheet.WAND_OAK,
			ItemSpriteSheet.WAND_BIRCH };

	private static ItemStatusHandler<Wand> handler;

	private String wood;

	{
		defaultAction = AC_ZAP;
	}

	@SuppressWarnings("unchecked")
	public static void initWoods() {
		handler = new ItemStatusHandler<Wand>((Class<? extends Wand>[]) wands,
				woods, images);
	}

	public static void save(Bundle bundle) {
		handler.save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		handler = new ItemStatusHandler<Wand>((Class<? extends Wand>[]) wands,
				woods, images, bundle);
	}

	public Wand() {
		super();

		calculateDamage();

		try {
			syncVisuals();
		} catch (Exception e) {
			// Wand of Magic Missile
		}
	}

	@Override
	public void syncVisuals() {
		image = handler.image(this);
		wood = handler.label(this);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (curCharges > 0 || !curChargeKnown) {
			actions.add(AC_ZAP);
		}
		if (hero.heroClass != HeroClass.MAGE) {
			actions.remove(AC_EQUIP);
			actions.remove(AC_UNEQUIP);
		}
		return actions;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		onDetach();
		return super.doUnequip(hero, collect, single);
	}

	@Override
	public void activate(Hero hero) {
		charge(hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_ZAP)) {

			curUser = hero;
			curItem = this;
			GameScene.selectCell(zapper);

		} else {

			super.execute(hero, action);

		}
	}

	protected abstract void onZap(int cell);

	@Override
	public boolean collect(Bag container) {
		if (super.collect(container)) {
			if (container.owner != null) {
				charge(container.owner);
			}
			return true;
		} else {
			return false;
		}
	};

	public void charge(Char owner) {
		if (charger == null)
			(charger = new Charger()).attachTo(owner);
	}

	@Override
	public void onDetach() {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}

	public int level() {
		
		int magicLevel = 0;
		if (charger != null) {
			Magic magic = charger.target.buff(Magic.class);
			if  (magic != null ){
			    magicLevel = magic.level;
			}
			return magic == null ? level : Math.max(level + magicLevel, 0);
		} else {
			return level;
		}
	}

	protected boolean isKnown() {
		return handler.isKnown(this);
	}

	protected void processSoulMark(Char target, int chargesUsed){
		if (target != Dungeon.hero &&
				Dungeon.hero.subClass == HeroSubClass.WARLOCK &&
				Random.Float() > (Math.pow(0.99f, (level()*chargesUsed)+1) - 0.09f)){//5% chance at +0, up to 48.4% chance at +100, 72.4% chance at +200, 86.8% chance at +300
			SoulMark.prolong(target, SoulMark.class, SoulMark.DURATION + level()/20f);
			String marked = "Soul Marked";
			target.sprite.showStatus(CharSprite.NEGATIVE, marked);
		}
	}

	public void setKnown() {
		if (!isKnown()) {
			handler.know(this);
		}

		Badges.validateAllWandsIdentified();
	}

	@Override
	public Item identify() {

		setKnown();
		curChargeKnown = true;
		super.identify();

		updateQuickslot();

		return this;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(super.toString());

		String status = status();
		if (status != null) {
			sb.append(" (" + status + ")");
		}

		return sb.toString();
	}

	@Override
	public String name() {
		return isKnown() ? name : wood + " wand";
	}

	@Override
	public String info() {
		StringBuilder info = new StringBuilder(isKnown() ? desc()
				: String.format(TXT_WOOD, wood));
		if (Dungeon.hero.heroClass == HeroClass.MAGE) {
			info.append("\n\n");
			if (levelKnown) {
				info.append(String.format(TXT_DAMAGE, MIN, MAX));
			} else {
				info.append(String.format(TXT_WEAPON));
			}
			
		}
		if(reinforced){
			info.append(String.format(TXT_REINFORCED));
		}
		return info.toString();
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown() && curChargeKnown;
	}

	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}

	@Override
	public Item upgrade() {

		super.upgrade();

		updateLevel();
		curCharges = Math.min(curCharges + 1, maxCharges);
		updateQuickslot();

		return this;
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		super.proc(attacker, defender, damage);
		if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE) {
			onHit(this, (Hero) attacker, defender, damage);
		}
	}

	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		if (wand.curCharges < wand.maxCharges && damage > 0) {

			wand.curCharges++;
			if (Dungeon.quickslot.contains(wand)) {
				QuickSlotButton.refresh();
			}

			ScrollOfRecharging.charge(attacker);
		}
	}

	public int magicMax(int lvl) {
		return 0;
	}
	public int magicMin(int lvl) {
		return 0;
	}

	public int magicMax() {
		return magicMax(level());
	}
	public int magicMin() {
		return magicMin(level());
	}

	@Override
	public int damageRoll(Hero owner) {
		float multiplier = 1f + curCharges/(float)maxCharges;//Up to double damage at full charges
		int damage = super.damageRoll(owner);
		damage = (int) (damage*multiplier);
		return damage;
	}

	public int magicDamageRoll() {
		return magicDamageRoll(level);
	}

	public int magicDamageRoll(int lvl) {
		return Random.NormalIntRange(magicMin(lvl),magicMax(lvl));
	}

	public String statsDesc() {
		if (!levelKnown) {
			return Messages.get(this,"this_1") + magicMin(0) + "-" + magicMax(0) + Messages.get(this,"dam");
		} else {
			return Messages.get(this,"this_2") + magicMin() + "-" + magicMax() + Messages.get(this,"dam");
		}
	}

	@Override
	public Item degrade() {
		super.degrade();

		updateLevel();
		updateQuickslot();

		return this;
	}

	public void updateLevel() {
		maxCharges = Math.min(initialCharges() + level, 15);
		curCharges = Math.min(curCharges, maxCharges);

		calculateDamage();
	}

	protected int initialCharges() {
		return 2;
	}

	protected int chargesPerCast() {
		return 1;
	}

	private void calculateDamage() {
		int tier = 2;
		MIN = tier + level;
		MAX = (tier * tier - tier + 10) + level*tier;
	}

	protected void fx(int cell, Callback callback) {
		MagicMissile.blueLight(curUser.sprite.parent, curUser.pos, cell,
				callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	protected void wandUsed() {
		curCharges -= chargesPerCast();
		if (!isIdentified() && --usagesToKnow <= 0) {
			identify();
			GLog.w(TXT_IDENTIFY, name());
		} else {
			updateQuickslot();
		}

		curUser.spendAndNext(TIME_TO_ZAP);
	}
	
	protected void wandEmpty() {
		curCharges=0;
		updateQuickslot();
	}

	@Override
	public Item random() {
		if (Random.Float() < 0.5f) {
			upgrade();
			if (Random.Float() < 0.15f) {
				upgrade();
			}
		}

		return this;
	}

	public static boolean allKnown() {
		return handler.known().size() == wands.length;
	}

	@Override
	public int price() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level > 0) {
				price *= (level + 1);
			} else if (level < 0) {
				price /= (1 - level);
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

		
	private static final String UNFAMILIRIARITY = "unfamiliarity";
	private static final String MAX_CHARGES = "maxCharges";
	private static final String CUR_CHARGES = "curCharges";
	private static final String CUR_CHARGE_KNOWN = "curChargeKnown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNFAMILIRIARITY, usagesToKnow);
		bundle.put(MAX_CHARGES, maxCharges);
		bundle.put(CUR_CHARGES, curCharges);
		bundle.put(CUR_CHARGE_KNOWN, curChargeKnown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if ((usagesToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		maxCharges = bundle.getInt(MAX_CHARGES);
		curCharges = bundle.getInt(CUR_CHARGES);
		curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN);
	}

	protected static CellSelector.Listener zapper = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer target) {

			if (target != null) {
				
				final Wand curWand = (Wand) Item.curItem;

				curWand.setKnown();

				final int cell = Ballistica.cast(curUser.pos, target, true,	curWand.hitChars);
				
				if (target == curUser.pos || cell == curUser.pos) {
					GLog.i(TXT_SELF_TARGET);
					return;
				}
				
				curUser.sprite.zap(cell);

				QuickSlotButton.target(Actor.findChar(cell));

				if (curWand.curCharges > 0) {

					curUser.busy();

					curWand.fx(cell, new Callback() {
						@Override
						public void call() {
							curWand.onZap(cell);
							curWand.wandUsed();
						}
					});

					Invisibility.dispel();

				} else {

					curUser.spendAndNext(TIME_TO_ZAP);
					GLog.w(TXT_FIZZLES);
					curWand.levelKnown = true;

					curWand.updateQuickslot();
				}

			}
		}

		@Override
		public String prompt() {
//			return "Choose direction to zap";
			return Messages.get(Wand.class, "prompt");
		}
	};

	protected class Charger extends Buff {

		private static final float TIME_TO_CHARGE = 40f;

		@Override
		public boolean attachTo(Char target) {
			super.attachTo(target);
			delay();

			return true;
		}

		@Override
		public boolean act() {

			if (curCharges < maxCharges) {
				curCharges++;
				updateQuickslot();
			}

			delay();

			return true;
		}

		protected void delay() {
			float time2charge = ((Hero) target).heroClass == HeroClass.MAGE ? TIME_TO_CHARGE
					/ (float) Math.sqrt(1 + level)
					: TIME_TO_CHARGE;
			spend(time2charge);
		}
	}
}
