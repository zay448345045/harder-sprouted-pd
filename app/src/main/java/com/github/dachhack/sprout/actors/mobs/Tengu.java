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

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Badges.Badge;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Poison;
import com.github.dachhack.sprout.actors.buffs.Slow;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.particles.FlameParticle;
import com.github.dachhack.sprout.items.Egg;
import com.github.dachhack.sprout.items.OtilukesJournal;
import com.github.dachhack.sprout.items.TomeOfMastery;
import com.github.dachhack.sprout.items.journalpages.Sokoban2;
import com.github.dachhack.sprout.items.keys.SkeletonKey;
import com.github.dachhack.sprout.items.scrolls.ScrollOfMagicMapping;
import com.github.dachhack.sprout.items.scrolls.ScrollOfPsionicBlast;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.TenguSprite;
import com.github.dachhack.sprout.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Tengu extends Mob {

	private static final int JUMP_DELAY = 5;

	{
		name = Messages.get(this, "name");
		spriteClass = TenguSprite.class;
		baseSpeed = 2f;

		HP = HT = 300;
		EXP = 20;
		defenseSkill = 30;
	}

	private int timeToJump = JUMP_DELAY;
	
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 18);
	}

	@Override
	public int attackSkill(Char target) {
		return 20;
	}

	@Override
	public int dr() {
		return 10;
	}

	@Override
	public void die(Object cause) {

		//note
		Badges.Badge badgeToCheck = null;
		switch (Dungeon.hero.heroClass) {
		case WARRIOR:
			badgeToCheck = Badge.MASTERY_WARRIOR;
			break;
		case MAGE:
			badgeToCheck = Badge.MASTERY_MAGE;
			break;
		case ROGUE:
			badgeToCheck = Badge.MASTERY_ROGUE;
			break;
		case HUNTRESS:
			badgeToCheck = Badge.MASTERY_HUNTRESS;
			break;
		}
		if (!Badges.isUnlocked(badgeToCheck)) {
			Dungeon.level.drop(new TomeOfMastery(), pos).sprite.drop();
		} else {
			Dungeon.level.drop(new Egg(), pos).sprite.drop();
		}
		
		if (!Dungeon.limitedDrops.journal.dropped()){ 
		  Dungeon.level.drop(new OtilukesJournal(), pos).sprite.drop();
		  Dungeon.limitedDrops.journal.drop();
		}
		
		Dungeon.level.drop(new Sokoban2(), pos).sprite.drop();
       
		
		GameScene.bossSlain();
		Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
			
		super.die(cause);

		Badges.validateBossSlain();
		Dungeon.tengukilled=true;

//		yell("Time to flee");
		TenguEscape.spawnAt(pos);
					
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if(Random.Int(5)==0){
			Buff.affect(enemy, Burning.class).reignite(enemy);
			enemy.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}
		if(Random.Int(10)==0){
			Buff.affect(enemy, Slow.class, Slow.duration(enemy) / 2);
		}

		if(Random.Int(20)==0){
			Buff.prolong(enemy, Paralysis.class, 2f);
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	protected boolean getCloser(int target) {
		if (Level.fieldOfView[target]) {
			jump();
			return true;
		} else {
			return super.getCloser(target);
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && Level.adjacent(pos, enemy.pos)) {
			jump();
			return true;
		} else {
			return super.doAttack(enemy);
		}
	}

	private void jump() {
		timeToJump = JUMP_DELAY;

		for (int i = 0; i < 4; i++) {
			int trapPos;
			do {
				trapPos = Random.Int(Level.getLength());
			} while (!Level.fieldOfView[trapPos] || !Level.passable[trapPos]);

			if (Dungeon.level.map[trapPos] == Terrain.INACTIVE_TRAP) {
				Level.set(trapPos, Terrain.POISON_TRAP);
				GameScene.updateMap(trapPos);
				ScrollOfMagicMapping.discover(trapPos);
			}
		}

		int newPos;
		do {
			newPos = Random.Int(Level.getLength());
		} while (!Level.fieldOfView[newPos] || !Level.passable[newPos]
				|| Level.adjacent(newPos, enemy.pos)
				|| Actor.findChar(newPos) != null);

		sprite.move(pos, newPos);
		move(newPos);

		if (Dungeon.visible[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.SND_PUFF);
		}

		spend(1 / speed());
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
		RESISTANCES.add(ToxicGas.class);
		RESISTANCES.add(Poison.class);
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
