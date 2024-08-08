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

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.sprites.FossilSkeletonSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class FossilSkeleton extends Skeleton {

	{
		name = Messages.get(this, "name");
		spriteClass = FossilSkeletonSprite.class;

		HP = HT = 40;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 40);
	}

	@Override
	protected float attackDelay() {
		return 1.5f;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int) (super.defenseSkill(enemy)*0.5f);
	}

	@Override
	public int dr() {
		return 10;
	}

	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Death.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
