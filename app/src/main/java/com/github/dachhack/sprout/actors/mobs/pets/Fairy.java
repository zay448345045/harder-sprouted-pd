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
package com.github.dachhack.sprout.actors.mobs.pets;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.MagicalSleep;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.particles.SparkParticle;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.FairySprite;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Fairy extends PET implements Callback{
	
	{
		name = Messages.get(this, "name");
		spriteClass = FairySprite.class;       
		flying=true;
		state = HUNTING;
		level = 1;
		type = 10;
		cooldown=1000;
	}
	private static final float TIME_TO_ZAP = 2f;
	private static final String TXT_LIGHTNING_KILLED = "%s's lightning bolt killed you...";

	@Override
	protected float attackDelay() {
		return 0.5f;
	}
	

	@Override
	public int dr(){
		return level*2;
	}
	
	//Frames 0,2 are idle, 0,1,2 are moving, 0,3,4,1 are attack and 5,6,7 are for death 

				
	protected int regen = 1;	
	protected float regenChance = 0.2f;	
		

	@Override
	public void adjustStats(int level) {
		this.level = level;
		HT = (level * 8);
		defenseSkill = 5+(level*level);
	}
	

	@Override
	public int attackSkill(Char target) {
		return defenseSkill;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(defenseSkill/2, defenseSkill);
	}

	@Override
	protected boolean act() {
		
		if (cooldown>0){
			cooldown=Math.max(cooldown-(level*level),0);
			if (cooldown==0) {GLog.p(Messages.get(Fairy.class, "ready"));}
		}
		
		if (cooldown==0 && Level.adjacent(pos, Dungeon.hero.pos) && Random.Int(2)==0){
			
			int bless = Random.Int(level*level);
			
			Dungeon.hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f,	1);
			Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE,Integer.toString(bless));
			
			Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP+bless);
		}
		
		if (Random.Float()<regenChance && HP<HT){HP+=regen;}

		return super.act();
	}
	
	
	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
		
	}


	
	@Override
	protected boolean doAttack(Char enemy) {

		if (Level.adjacent(pos, enemy.pos)) {

			return super.doAttack(enemy);

		} else {

			boolean visible = Level.fieldOfView[pos]
					|| Level.fieldOfView[enemy.pos];
			if (visible) {
				((FairySprite) sprite).zap(enemy.pos);
			}

			spend(TIME_TO_ZAP);
			cooldown=1000;
			yell(Messages.get(Fairy.class, "atk"));
			if (hit(this, enemy, true)) {
				int dmg = damageRoll()*2;
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
						GLog.n(Messages.get(Fairy.class, "kill"), name);
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
	public void call() {
		next();
	}
		
	

	@Override
	public void interact() {

		if (this.buff(MagicalSleep.class) != null) {
			Buff.detach(this, MagicalSleep.class);
		}
		
		if (state == SLEEPING) {
			state = HUNTING;
		}
		if (buff(Paralysis.class) != null) {
			Buff.detach(this, Paralysis.class);
			GLog.i(Messages.get(bee.class, "shake"), name);
		}
		
		int curPos = pos;

		moveSprite(pos, Dungeon.hero.pos);
		move(Dungeon.hero.pos);

		Dungeon.hero.sprite.move(Dungeon.hero.pos, curPos);
		Dungeon.hero.move(curPos);

		Dungeon.hero.spend(1 / Dungeon.hero.speed());
		Dungeon.hero.busy();
	}


	@Override
	public String description() {
		return Messages.get(bee.class, "desc");
	}


}