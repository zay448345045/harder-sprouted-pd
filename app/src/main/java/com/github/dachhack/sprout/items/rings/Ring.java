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
package com.github.dachhack.sprout.items.rings;

import java.util.ArrayList;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.ItemStatusHandler;
import com.github.dachhack.sprout.items.KindofMisc;
import com.github.dachhack.sprout.items.artifacts.Artifact;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Ring extends KindofMisc {

	private static final int TICKS_TO_KNOW = 200;

	private static final float TIME_TO_EQUIP = 1f;

//	private static final String TXT_IDENTIFY = "you are now familiar enough with your %s to identify it. It is %s.";

	private static final String TXT_IDENTIFY = Messages.get(Ring.class, "identify");

	protected Buff buff;

	private static final Class<?>[] rings = { RingOfAccuracy.class,
			RingOfEvasion.class, RingOfElements.class, RingOfForce.class,
			RingOfFuror.class, RingOfHaste.class, RingOfMagic.class,
			RingOfMight.class, RingOfSharpshooting.class, RingOfTenacity.class,
			RingOfWealth.class, };
//	private static final String[] gems = { "diamond", "opal", "garnet", "ruby",
//			"amethyst", "topaz", "onyx", "tourmaline", "emerald", "sapphire",
//			"quartz", "agate" };
private static final String[] gems = {
		Messages.get(Ring.class, "diamond"),
		Messages.get(Ring.class, "opal"),
		Messages.get(Ring.class, "garnet"),
		Messages.get(Ring.class, "ruby"),
		Messages.get(Ring.class, "amethyst"),
		Messages.get(Ring.class, "topaz"),
		Messages.get(Ring.class, "onyx"),
		Messages.get(Ring.class, "tourmaline"),
		Messages.get(Ring.class, "emerald"),
		Messages.get(Ring.class, "sapphire"),
		Messages.get(Ring.class, "quartz"),
		Messages.get(Ring.class, "agate")};
	private static final Integer[] images = { ItemSpriteSheet.RING_DIAMOND,
			ItemSpriteSheet.RING_OPAL, ItemSpriteSheet.RING_GARNET,
			ItemSpriteSheet.RING_RUBY, ItemSpriteSheet.RING_AMETHYST,
			ItemSpriteSheet.RING_TOPAZ, ItemSpriteSheet.RING_ONYX,
			ItemSpriteSheet.RING_TOURMALINE, ItemSpriteSheet.RING_EMERALD,
			ItemSpriteSheet.RING_SAPPHIRE, ItemSpriteSheet.RING_QUARTZ,
			ItemSpriteSheet.RING_AGATE };

	private static ItemStatusHandler<Ring> handler;

	private String gem;

	private int ticksToKnow = TICKS_TO_KNOW;

	@SuppressWarnings("unchecked")
	public static void initGems() {
		handler = new ItemStatusHandler<Ring>((Class<? extends Ring>[]) rings,
				gems, images);
	}

	public static void save(Bundle bundle) {
		handler.save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		handler = new ItemStatusHandler<Ring>((Class<? extends Ring>[]) rings,
				gems, images, bundle);
	}

	public Ring() {
		super();
		syncVisuals();
	}

	@Override
	public void syncVisuals() {
		image = handler.image(this);
		gem = handler.label(this);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(isEquipped(hero) ? AC_UNEQUIP : AC_EQUIP);
		return actions;
	}

	//	need fix
//	@Override
//	public boolean doEquip(Hero hero) {
//
//		if (hero.belongings.misc1 != null && hero.belongings.misc2 != null) {
//
//			GLog.w("you can only wear 2 misc items at a time");
//			return false;
//
//		} else {
//
//			if (hero.belongings.misc1 == null) {
//				hero.belongings.misc1 = this;
//			} else {
//				hero.belongings.misc2 = this;
//			}
//
//			detach(hero.belongings.backpack);
//
//			activate(hero);
//
//			cursedKnown = true;
//			if (cursed) {
//				equipCursed(hero);
//				GLog.n("your " + this
//						+ " tightens around your finger painfully");
//			}
//
//			hero.spendAndNext(TIME_TO_EQUIP);
//			return true;
//
//		}
//
//	}

	@Override
	public void activate(Char ch) {
		buff = buff();
		buff.attachTo(ch);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)) {

			if (hero.belongings.misc1 == this) {
				hero.belongings.misc1 = null;
			} else {
				hero.belongings.misc2 = null;
			}

			hero.remove(buff);
			buff = null;

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean isEquipped(Hero hero) {
		return hero.belongings.misc1 == this || hero.belongings.misc2 == this;
	}

	@Override
	public Item upgrade() {

		super.upgrade();

		if (buff != null) {

			Char owner = buff.target;
			buff.detach();
			if ((buff = buff()) != null) {
				buff.attachTo(owner);
			}
		}

		return this;
	}

	public boolean isKnown() {
		return handler.isKnown(this);
	}

	protected void setKnown() {
		if (!isKnown()) {
			handler.know(this);
		}

		Badges.validateAllRingsIdentified();
	}

	@Override
	public String name() {
		return isKnown() ? name : gem + " ring";
	}

	@Override
//	public String desc() {
//		return "This metal band is adorned with a large "
//				+ gem
//				+ " gem "
//				+ "that glitters in the darkness. Who knows what effect it has when worn?";
//	}
	public String desc() {
		return Messages.get(this, "unknown_desc", gem);
	}

	@Override
	public String info() {
		if (isEquipped(Dungeon.hero)) {

//			return desc()
//					+ "\n\n"
//					+ "The "
//					+ name()
//					+ " is on your finger"
//					+ (cursed ? ", and because it is cursed, you are powerless to remove it. "
//							: ".")
//					+ (reinforced ? "\n\nIt is reinforced."
//							: "")
//							;
			return desc()
					+ "\n\n" + Messages.get(this, "on_finger", name())
					+ (cursed ? Messages.get(this, "cursed_worn") : "")
					+ (reinforced ? Messages.get(this, "reinforced") : "");

		} else if (cursed && cursedKnown) {

//			return desc()
//					+ "\n\nYou can feel a malevolent magic lurking within the "
//					+ name() + ".";
			return desc()
					+ "\n\n" + Messages.get(this, "curse_known", name());


		} else {

//			return desc() + (reinforced ? "\n\nIt is reinforced." : "");
			return desc() + (reinforced ? Messages.get(this, "reinforced") : "");

		}
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}

	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}

	@Override
	public Item random() {
		if (Random.Float() < 0.3f) {
			level = -Random.Int(1, 3);
			cursed = true;
		} else
			level = Random.Int(1, 2);
		return this;
	}

	public static boolean allKnown() {
		return handler.known().size() == rings.length - 2;
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

	protected RingBuff buff() {
		return null;
	}

	private static final String UNFAMILIRIARITY = "unfamiliarity";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNFAMILIRIARITY, ticksToKnow);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if ((ticksToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
			ticksToKnow = TICKS_TO_KNOW;
		}
	}

	public class RingBuff extends Buff {

//		private static final String TXT_KNOWN = "This is a %s";
private final String TXT_KNOWN = Messages.get(Ring.class, "known");


		public int level;

		public RingBuff() {
			level = Ring.this.level;
		}

		@Override
		public boolean attachTo(Char target) {

			if (target instanceof Hero
					&& ((Hero) target).heroClass == HeroClass.ROGUE
					&& !isKnown()) {
				setKnown();
				GLog.i(TXT_KNOWN, name());
				Badges.validateItemLevelAquired(Ring.this);
			}

			return super.attachTo(target);
		}

		@Override
		public boolean act() {

			if (!isIdentified() && --ticksToKnow <= 0) {
				String gemName = name();
				identify();
				GLog.w(TXT_IDENTIFY, gemName, Ring.this.toString());
				Badges.validateItemLevelAquired(Ring.this);
			}

			spend(TICK);

			return true;
		}
	}
}
