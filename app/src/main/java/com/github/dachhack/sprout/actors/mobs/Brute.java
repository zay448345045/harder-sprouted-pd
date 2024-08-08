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
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.food.Meat;
import com.github.dachhack.sprout.sprites.BruteSprite;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Brute extends Mob {

	private static final String TXT_ENRAGED = "%s becomes enraged!";

	{
		name = Messages.get(this, "name");
		spriteClass = BruteSprite.class;

		HP = HT = 100;

		EXP = 8;
		maxLvl = 15;

		loot = Gold.class;
		lootChance = 0.5f;

		lootOther = new Meat();
		lootChanceOther = 0.5f; // by default, see die()
	}

	private boolean enraged = false;

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		enraged = HP < HT / 4;
	}

	@Override
	public int damageRoll() {
		return enraged ? Random.NormalIntRange(60, 90) : Random.NormalIntRange(20, 45);
	}

	@Override
	public int attackSkill(Char target) {
		return defaultAccuracy(target)*2;
	}

	@Override
	public int dr() {
		return 10+ adjustForDepth(0);
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if (isAlive() && !enraged && HP < HT / 4) {
			enraged = true;
			spend(TICK);
			if (Dungeon.visible[pos]) {
				GLog.w(Messages.get(Brute.class, "enraged"), name);
				sprite.showStatus(CharSprite.NEGATIVE, "enraged");
			}
		}
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Terror.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
