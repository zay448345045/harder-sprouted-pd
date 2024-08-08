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
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.particles.ElmoParticle;
import com.github.dachhack.sprout.effects.particles.SparkParticle;
import com.github.dachhack.sprout.items.OtilukesJournal;
import com.github.dachhack.sprout.items.artifacts.CapeOfThorns;
import com.github.dachhack.sprout.items.journalpages.Sokoban3;
import com.github.dachhack.sprout.items.keys.SkeletonKey;
import com.github.dachhack.sprout.items.scrolls.ScrollOfPsionicBlast;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.DM300Sprite;
import com.github.dachhack.sprout.ui.BossHealthBar;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class DM300 extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP = 2f;

	private static final String TXT_LIGHTNING_KILLED = "%s's lightning bolt killed you...";

	{
		name = Messages.get(this, "name");
		spriteClass = DM300Sprite.class;

		HP = HT = 1000;
		EXP = 30;
		defenseSkill = 40;

		loot = new CapeOfThorns().identify();
		lootChance = 1f;
	}

	private int bossAlive = 0;

	@Override
	public int damageRoll() {
		
		return Random.NormalIntRange(18, 80);
	}

	@Override
	public int attackSkill(Char target) {
		return 28;
	}

	@Override
	public int dr() {
		return 30;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (dmg >= 50){
			dmg = 49 + (int)(Math.sqrt((dmg - 50)) - 1);
		}
		super.damage(dmg, src);
	}

	@Override
	public boolean act() {
        
		GameScene.add(Blob.seed(pos, 30, ToxicGas.class));

		return super.act();
	}
	
	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
	}

	
	@Override
	protected boolean doAttack(Char enemy) {

		if (Level.distance(pos, enemy.pos) <= 1) {

			return super.doAttack(enemy);

		} else {

			boolean visible = Level.fieldOfView[pos]
					|| Level.fieldOfView[enemy.pos];
			if (visible) {
				((DM300Sprite) sprite).zap(enemy.pos);
			}

			spend(TIME_TO_ZAP);

			if (hit(this, enemy, true)) {
				int dmg = Random.Int(5, 22);
				if (Level.water[enemy.pos] && !enemy.flying) {
					dmg *= 1.5f;
				}
				enemy.damage(dmg, LightningTrap.LIGHTNING);

				enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				enemy.sprite.flash();

				if (enemy == Dungeon.hero) {

					Camera.main.shake(2, 0.3f);

					if (!enemy.isAlive()) {
						Dungeon.fail(Utils.format(ResultDescriptions.MOB,
								Utils.indefinite(name)));
						GLog.n(Messages.get(DM300.class, "kill"), name);
					}
				}
			} else {
				enemy.sprite
						.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			}

			return !visible;
		}
	}

	
	@Override
	public void move(int step) {
		super.move(step);

		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && HP < HT) {

			HP += Random.Int(1, HT - HP);
			sprite.emitter().burst(ElmoParticle.FACTORY, 5);

			if (Dungeon.visible[step] && Dungeon.hero.isAlive()) {
				GLog.n(Messages.get(DM300.class, "heal"));
			}
		}

		int[] cells = { step - 1, step + 1, step - Level.getWidth(),
				step + Level.getWidth(), step - 1 - Level.getWidth(),
				step - 1 + Level.getWidth(), step + 1 - Level.getWidth(),
				step + 1 + Level.getWidth() };
		int cell = cells[Random.Int(cells.length)];

		if (Dungeon.visible[cell]) {
			CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Camera.main.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.SND_ROCKS);

			if (Level.water[cell]) {
				GameScene.ripple(cell);
			} else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
				Level.set(cell, Terrain.EMPTY_DECO);
				GameScene.updateMap(cell);
			}
		}

		Char ch = Actor.findChar(cell);
		if (ch != null && ch != this) {
			Buff.prolong(ch, Paralysis.class, 2);
		}
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

           for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			
			  if (mob instanceof Tower){
				   bossAlive++;
				   }
			
			}
			
			 if(bossAlive==0){
				 
					GameScene.bossSlain();
					Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
					Badges.validateBossSlain();
			 }
			 
			 if (!Dungeon.limitedDrops.journal.dropped()){ 
				  Dungeon.level.drop(new OtilukesJournal(), pos).sprite.drop();
				  Dungeon.limitedDrops.journal.drop();
				}
			 
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
				
				
				Dungeon.level.drop(new Sokoban3(), pos).sprite.drop();


		yell(Messages.get(DM300.class, "die"));
	}

	@Override
	public void notice() {
		BossHealthBar.assignBoss(this);

		super.notice();
		yell(Messages.get(DM300.class, "notice"));
	}
	
	@Override
	public void call() {
		next();
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(Terror.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	
}
