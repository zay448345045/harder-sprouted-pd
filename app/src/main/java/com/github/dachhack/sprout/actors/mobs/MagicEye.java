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
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Light;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.particles.PurpleParticle;
import com.github.dachhack.sprout.items.potions.PotionOfMending;
import com.github.dachhack.sprout.items.wands.Wand;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.items.weapon.enchantments.Leech;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.MagicEyeSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Random;

import java.util.HashSet;

public class MagicEye extends Mob {

	private static final String TXT_DEATHGAZE_KILLED = "%s's deathgaze killed you...";
	protected static final float SPAWN_DELAY = 2f;

	public static final int RANGE = 4;

	{
		name = Messages.get(this, "name");
		spriteClass = MagicEyeSprite.class;

		HP = HT = 1500;
		viewDistance = Light.DISTANCE;

		EXP = 16;

		flying = true;

		loot = new PotionOfMending();
		lootChance = 0.05f;

		scalesWithHeroLevel = true;
	}

	@Override
	public int dr() {
		return 120;
	}

	private int hitCell;

	@Override
	protected boolean canAttack(Char enemy) {

		hitCell = Ballistica.cast(pos, enemy.pos, true, false);

		for (int i = 1; i < Ballistica.distance; i++) {
			if (Ballistica.trace[i] == enemy.pos & Level.distance(enemy.pos, pos) < RANGE) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected float attackDelay() {
		return 2f;
	}

	@Override
	protected boolean doAttack(Char enemy) {

		spend(attackDelay());

		boolean rayVisible = false;

		for (int i = 0; i < Ballistica.distance; i++) {
			if (Dungeon.visible[Ballistica.trace[i]]) {
				rayVisible = true;
			}
		}

		if (rayVisible) {
			sprite.attack(hitCell);
			return false;
		} else {
			attack(enemy);
			return true;
		}
	}

	@Override
	public boolean attack(Char enemy) {

		for (int i = 1; i < Ballistica.distance; i++) {

			int pos = Ballistica.trace[i];

			Char ch = Actor.findChar(pos);
			if (ch == null) {
				continue;
			}

			if (hit(this, ch, true)) {
				ch.damage(Random.NormalIntRange(50, 80), this);

				if (Dungeon.visible[pos]) {
					ch.sprite.flash();
					CellEmitter.center(pos).burst(PurpleParticle.BURST,
							Random.IntRange(1, 2));
				}

				if (!ch.isAlive() && ch == Dungeon.hero) {
					Dungeon.fail(Utils.format(ResultDescriptions.MOB,
							Utils.indefinite(name)));
					GLog.n(Messages.get(MagicEye.class, "kill"), name);
				}
			} else {
				ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
			}
		}

		return true;
	}

	public static void spawnAroundChance(int pos) {
		for (int n : Level.NEIGHBOURS4) {
			int cell = pos + n;
			if (Level.passable[cell] && Actor.findChar(cell) == null && Random.Float()<0.50f) {
				spawnAt(cell);
				return;
			}
		}
	}

	public static MagicEye spawnAt(int pos) {
		if (Level.passable[pos] && Actor.findChar(pos) == null) {
          
			MagicEye e = new MagicEye();
			e.pos = pos;
			e.state = e.HUNTING;
			GameScene.add(e, SPAWN_DELAY);

			e.sprite.alpha(0);
			e.sprite.parent.add(new AlphaTweener(e.sprite, 1, 0.5f));

		return e;
  			
		} else {
			return null;
		}
	}


	@Override
	public void damage(int dmg, Object src) {
		if (src instanceof Wand) {
			dmg = Random.NormalIntRange(1,dmg/2);
		}
		super.damage(dmg, src);
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Death.class);
		RESISTANCES.add(Leech.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
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
