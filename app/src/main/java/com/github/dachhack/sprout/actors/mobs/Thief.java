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
package com.github.dachhack.sprout.actors.mobs;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.items.Generator;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.Honeypot;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.artifacts.MasterThievesArmband;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.ThiefSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {

	protected static final String TXT_STOLE = "%s stole %s from you!";
	protected static final String TXT_CARRIES = "\n\n%s is carrying a _%s_. Stolen obviously.";
	protected static final String TXT_RATCHECK1 = "Spork is avail";
	protected static final String TXT_RATCHECK2 = "Spork is not avail";

	public Item item;

	{
		name = Messages.get(this, "name");
		spriteClass = ThiefSprite.class;

		HP = HT = 20+(adjustForDepth(0)*Random.NormalIntRange(3, 5));

		EXP = 5;
		
		loot = new MasterThievesArmband().identify();
		lootChance = 0.01f;
		
		lootOther = Generator.Category.BERRY;
		lootChanceOther = 0.1f; // by default, see die()

		FLEEING = new Fleeing();
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ITEM, item);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		item = (Item) bundle.get(ITEM);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 7+ adjustForDepth(0));
	}

	@Override
	protected float attackDelay() {
		return 0.5f;
	}

	@Override
	public void die(Object cause) {
		
		super.die(cause);

		if (item != null) {
			Dungeon.level.drop(item, pos).sprite.drop();
		}
	}

	@Override
	protected Item createLoot() {
		if (!Dungeon.limitedDrops.armband.dropped()) {
			Dungeon.limitedDrops.armband.drop();
			return super.createLoot();
		} else
			return new Gold(Random.NormalIntRange(100, 250));
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int) (super.defenseSkill(enemy)*1.5f);
	}

	@Override
	public int dr() {
		return 3;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (item == null && enemy instanceof Hero && steal((Hero) enemy)) {
			state = FLEEING;
		}

		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) {
			Dungeon.level.drop(new Gold(), pos).sprite.drop();
		}

		return damage;
	}

	protected boolean steal(Hero hero) {

		Item item = hero.belongings.randomUnequipped();
		if (item != null) {

			item.updateQuickslot();
			
			GLog.w(Messages.get(Thief.class, "stole"), this.name, item.name());
					
			if (item instanceof Honeypot) {
				this.item = ((Honeypot) item).shatter(this, this.pos);
				item.detach(hero.belongings.backpack);
			} else {
				this.item = item;
				if (item instanceof Honeypot.ShatteredPot)
					((Honeypot.ShatteredPot) item).setHolder(this);
				item.detachAll(hero.belongings.backpack);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		String desc = Messages.get(this, "desc");

		if (item != null) {
			desc += String.format(Messages.get(Thief.class, "carries"), Utils.capitalize(this.name),
					item.name());
		}

		return desc;
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}
}
