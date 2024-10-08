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

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.Frost;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Roots;
import com.github.dachhack.sprout.items.CavesKey;
import com.github.dachhack.sprout.items.food.Meat;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.sprites.PiranhaSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Piranha extends Mob {

	{
		name = Messages.get(this, "name");
		spriteClass = PiranhaSprite.class;

		baseSpeed = 2f;

		EXP = 0;
		
	}

	public Piranha() {
		super();

		HP = HT = 10 + Dungeon.depth * 5;
		defenseSkill = 10 + Dungeon.depth * 2;
	}

	@Override
	protected boolean act() {
		if (!Level.water[pos]) {
			die(null);
			return true;
		} else {
			// this causes pirahna to move away when a door is closed on them.
			Dungeon.level.updateFieldOfView(this);
			enemy = chooseEnemy();
			if (state == this.HUNTING
					&& !(enemy.isAlive() && Level.fieldOfView[enemy.pos] && enemy.invisible <= 0)) {
				state = this.WANDERING;
				int oldPos = pos;
				int i = 0;
				do {
					i++;
					target = Dungeon.level.randomDestination();
					if (i == 100)
						return true;
				} while (!getCloser(target));
				moveSprite(oldPos, pos);
				return true;
			}

			return super.act();
		}
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return super.defenseSkill(enemy)*2;
	}

	@Override
	public int attackSkill(Char target) {
		return super.attackSkill(target)*2;
	}

	@Override
	public int dr() {
		return Dungeon.depth;
	}

	@Override
	public void die(Object cause) {
		Dungeon.level.drop(new Meat(), pos).sprite.drop();
		
		if (!Dungeon.limitedDrops.caveskey.dropped() && Statistics.deepestFloor > 10) {
			Dungeon.limitedDrops.caveskey.drop();
			Dungeon.level.drop(new CavesKey(), pos).sprite.drop();
			explodeDew(pos);				
		}
		
		super.die(cause);

		Statistics.piranhasKilled++;
		Badges.validatePiranhasKilled();
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {

		if (rooted) {
			return false;
		}

		int step = Dungeon.findPath(this, pos, target, Level.water,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean getFurther(int target) {
		int step = Dungeon.flee(this, pos, target, Level.water,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(Roots.class);
		IMMUNITIES.add(Frost.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
