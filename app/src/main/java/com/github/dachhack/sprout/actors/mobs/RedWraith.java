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
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Amok;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.Charm;
import com.github.dachhack.sprout.actors.buffs.Frost;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Roots;
import com.github.dachhack.sprout.actors.buffs.Sleep;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.actors.buffs.Vertigo;
import com.github.dachhack.sprout.effects.particles.ShadowParticle;
import com.github.dachhack.sprout.items.RedDewdrop;
import com.github.dachhack.sprout.items.rings.RingOfWealth;
import com.github.dachhack.sprout.items.scrolls.ScrollOfPsionicBlast;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.RedWraithSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class RedWraith extends Mob {

	protected static final float SPAWN_DELAY = 2f;

	protected int level;

	{
		name = Messages.get(this, "name");
		spriteClass = RedWraithSprite.class;

		HP = HT = 1;
		EXP = 1+level;

		flying = true;
		
		loot = new RedDewdrop();
		lootChance = 0.5f;
		
	}

	protected static final String LEVEL = "level";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = bundle.getInt(LEVEL);
		adjustStats(level);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 3 + level);
	}

	@Override
	public int attackSkill(Char target) {
		return 10 + level;
	}

	public void adjustStats(int level) {
		this.level = level;
		defenseSkill = attackSkill(null) * 5;
		enemySeen = true;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(4) == 0) {
			Buff.affect(enemy, Vertigo.class, Vertigo.duration(enemy));
			Buff.affect(enemy, Terror.class, Terror.DURATION).object = enemy.id();
		}

		return damage;
	}
	
	@Override
	public void die(Object cause) {
		
		if (!Dungeon.limitedDrops.ringofwealth.dropped() && Random.Float()<0.04f) {
			Dungeon.limitedDrops.ringofwealth.drop();
			Dungeon.level.drop(new RingOfWealth(), pos).sprite.drop();
			explodeDew(pos);				
		} else {
			explodeDew(pos);
		}

		super.die(cause);
	
	}
	
	//public void damage(int dmg, Object src) {
	//	if (enemySeen
	//			&& (src instanceof Wand || src instanceof LightningTrap.Electricity || src instanceof Char)) {
	//		GLog.n("The attack passes through the wraith.");
	//		sprite.showStatus(CharSprite.NEUTRAL, "missed");
	//	} else {
	//		super.damage(dmg, src);
	//	}
	//}

	
	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	public static void spawnAround(int pos) {
		for (int n : Level.NEIGHBOURS4) {
			int cell = pos + n;
			if (Level.passable[cell] && Actor.findChar(cell) == null) {
				spawnAt(cell);
			}
		}
	}

	public static RedWraith spawnAt(int pos) {
		if (Level.passable[pos] && Actor.findChar(pos) == null) {
          
			RedWraith w = new RedWraith();
			w.adjustStats(Dungeon.depth);
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add(w, SPAWN_DELAY);

			w.sprite.alpha(0);
			w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

			w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

			return w;
  			
		} else {
			return null;
		}
	}

	protected static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Death.class);
		IMMUNITIES.add(Terror.class);
		IMMUNITIES.add(Amok.class);
		IMMUNITIES.add(Charm.class);
		IMMUNITIES.add(Sleep.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(ScrollOfPsionicBlast.class);
		IMMUNITIES.add(Vertigo.class);
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(Roots.class);
		IMMUNITIES.add(Frost.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
