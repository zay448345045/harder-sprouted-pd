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
import com.github.dachhack.sprout.items.AdamantRing;
import com.github.dachhack.sprout.items.Generator;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ThiefKingSprite;
import com.github.dachhack.sprout.ui.BossHealthBar;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class ThiefKing extends Mob implements Callback {

	
	{
		name = Messages.get(this, "name");
		spriteClass = ThiefKingSprite.class;

		HP = HT = 500;
		defenseSkill = 28;

		EXP = 16;
		maxLvl = 14;
		flying = true;

		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;		
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 70);
	}

	@Override
	public int attackSkill(Char target) {
		return 25;
	}

	@Override
	public int dr() {
		return 14;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
	}

	@Override
	public void die(Object cause) {

		GameScene.bossSlain();
		Dungeon.level.drop(new AdamantRing(), pos).sprite.drop();
		Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), pos).sprite.drop();
		super.die(cause);
		
		Dungeon.banditkingkilled=true;

		yell(Messages.get(this, "die"));
						
	}
	
	
	@Override
	public void call() {
		next();
	}

	@Override
	public void notice() {
		BossHealthBar.assignBoss(this);

		super.notice();
		yell(Messages.get(this, "notice", Dungeon.hero.givenName()));
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(LightningTrap.Electricity.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
