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
import com.github.dachhack.sprout.Challenges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Amok;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Dewcharge;
import com.github.dachhack.sprout.actors.buffs.Hunger;
import com.github.dachhack.sprout.actors.buffs.Sleep;
import com.github.dachhack.sprout.actors.buffs.SoulMark;
import com.github.dachhack.sprout.actors.buffs.Terror;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroSubClass;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.Surprise;
import com.github.dachhack.sprout.effects.Wound;
import com.github.dachhack.sprout.items.Generator;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.RedDewdrop;
import com.github.dachhack.sprout.items.VioletDewdrop;
import com.github.dachhack.sprout.items.YellowDewdrop;
import com.github.dachhack.sprout.items.artifacts.TimekeepersHourglass;
import com.github.dachhack.sprout.items.rings.RingOfAccuracy;
import com.github.dachhack.sprout.items.rings.RingOfWealth;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Level.Feeling;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public abstract class Mob extends Char {

	private static final String TXT_DIED = "You hear something died in the distance";

	protected static final String TXT_NOTICE1 = "?!";
	protected static final String TXT_RAGE = Messages.get(Mob.class, "rage");
	protected static final String TXT_EXP = Messages.get(Mob.class, "exp");

	public AiState SLEEPING = new Sleeping();
	public AiState HUNTING = new Hunting();
	public AiState WANDERING = new Wandering();
	public AiState FLEEING = new Fleeing();
	public AiState PASSIVE = new Passive();
	public AiState state = SLEEPING;

	public boolean seeThroughInvisibility = false;

	public boolean scalesWithHeroLevel = false;

	public Class<? extends CharSprite> spriteClass;

	protected int target = -1;

	protected int defenseSkill = 0;

	protected int EXP = 1;
	protected int maxLvl = 30;
	protected int dewLvl = 1;

	protected Char enemy;
	protected boolean enemySeen;
	protected boolean alerted = false;

	protected static final float TIME_TO_WAKE_UP = 1f;

	public boolean hostile = true;
	public boolean ally = false;
	public boolean originalgen = false;

	private static final String STATE = "state";
	private static final String SEEN = "seen";
	private static final String TARGET = "target";
	private static final String ORIGINAL = "originalgen";
	
	public int getExp(){
		return EXP;
	}

	public boolean isPassive(){
		return state==PASSIVE;
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {

		super.storeInBundle(bundle);

		if (state == SLEEPING) {
			bundle.put(STATE, Sleeping.TAG);
		} else if (state == WANDERING) {
			bundle.put(STATE, Wandering.TAG);
		} else if (state == HUNTING) {
			bundle.put(STATE, Hunting.TAG);
		} else if (state == FLEEING) {
			bundle.put(STATE, Fleeing.TAG);
		} else if (state == PASSIVE) {
			bundle.put(STATE, Passive.TAG);
		}
		bundle.put(SEEN, enemySeen);
		bundle.put(TARGET, target);
		bundle.put(ORIGINAL, originalgen);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		String state = bundle.getString(STATE);
		if (state.equals(Sleeping.TAG)) {
			this.state = SLEEPING;
		} else if (state.equals(Wandering.TAG)) {
			this.state = WANDERING;
		} else if (state.equals(Hunting.TAG)) {
			this.state = HUNTING;
		} else if (state.equals(Fleeing.TAG)) {
			this.state = FLEEING;
		} else if (state.equals(Passive.TAG)) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean(SEEN);

		target = bundle.getInt(TARGET);
		
		originalgen = bundle.getBoolean(ORIGINAL);
	}

	public CharSprite sprite() {
		CharSprite sprite = null;
		try {
			sprite = spriteClass.newInstance();
		} catch (Exception e) {
		}
		return sprite;
	}

	@Override
	public int attackSkill(Char target) {
		return defaultAccuracy(target);
	}

	@Override
	protected boolean act() {

		super.act();

		boolean justAlerted = alerted;
		alerted = false;

		sprite.hideAlert();

		if (paralysed) {
			enemySeen = false;
			spend(TICK);
			return true;
		}

		enemy = chooseEnemy();
		
		/*
		boolean invisibleCheck=false;

		if (enemy.invisible > 0 && this.resistances().contains(Invisibility.class)){
			invisibleCheck=false;			
		} else if (enemy.invisible > 0) {
			invisibleCheck=true;
		}
		*/
		boolean enemyInFOV = enemy != null && enemy.isAlive()
				&& Level.fieldOfView[enemy.pos] && (enemy.invisible<=0 | seeThroughInvisibility) ;

		return state.act(enemyInFOV, justAlerted);
	}

	protected Char chooseEnemy() {

		Terror terror = buff(Terror.class);
		if (terror != null) {
			Char source = (Char) Actor.findById(terror.object);
			if (source != null) {
				return source;
			}
		}

		// resets target if: the target is dead, the target has been lost
		// (wandering)
		// or if the mob is amoked and targeting the hero (will try to target
		// something else)
		if (enemy != null && !enemy.isAlive() || state == WANDERING
				|| (buff(Amok.class) != null && enemy == Dungeon.hero))
			enemy = null;

		// if there is no current target, find a new one.
		if (enemy == null) {

			HashSet<Char> enemies = new HashSet<Char>();

			// if the mob is amoked...
			if (buff(Amok.class) != null) {

				// try to find an enemy mob to attack first.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos]
							&& mob.hostile)
						enemies.add(mob);
				if (enemies.size() > 0)
					return Random.element(enemies);

				// try to find ally mobs to attack second.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos] && mob.ally)
						enemies.add(mob);
				if (enemies.size() > 0)
					return Random.element(enemies);

				// if there is nothing, go for the hero.
				return Dungeon.hero;

				// if the mob is not amoked...
			} else {

				// try to find ally mobs to attack.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos] && mob.ally)
						enemies.add(mob);

				// and add the hero to the list of targets.
				enemies.add(Dungeon.hero);

				// target one at random.
				return Random.element(enemies);

			}

		} else
			return enemy;
	}

	protected boolean moveSprite(int from, int to) {

		if (sprite.isVisible()
				&& (Dungeon.visible[from] || Dungeon.visible[to])) {
			sprite.move(from, to);
			return true;
		} else {
			sprite.place(to);
			return true;
		}
	}

	@Override
	public void add(Buff buff) {
		super.add(buff);
		if (buff instanceof Amok) {
			if (sprite != null) {
				sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
			}
			state = HUNTING;
		} else if (buff instanceof Terror) {
			state = FLEEING;
		} else if (buff instanceof Sleep) {
			state = SLEEPING;
			this.sprite().showSleep();
			postpone(Sleep.SWS);
		}
	}

	@Override
	public void remove(Buff buff) {
		super.remove(buff);
		if (buff instanceof Terror) {
			sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
			state = HUNTING;
		}
	}

	protected boolean canAttack(Char enemy) {
		return Level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
	}

	protected boolean getCloser(int target) {

		if (rooted) {
			return false;
		}

		int step = Dungeon.findPath(this, pos, target, Level.passable,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	protected boolean getFurther(int target) {
		int step = Dungeon.flee(this, pos, target, Level.passable,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null)
			sprite.add(CharSprite.State.PARALYSED);
	}

	@Override
	public void move(int step) {
		super.move(step);

		if (!flying) {
			Dungeon.level.mobPress(this);
		}
	}

	protected float attackDelay() {
		return 1f;
	}

	protected boolean doAttack(Char enemy) {

		boolean visible = Dungeon.visible[pos];

		if (visible) {
			sprite.attack(enemy.pos);
		} else {
			attack(enemy);
		}

		spend(attackDelay());

		return !visible;
	}

	@Override
	public void onAttackComplete() {
		attack(enemy);
		super.onAttackComplete();
	}

	@Override
	public int defenseSkill(Char enemy) {
		if (enemySeen && !paralysed) {
			int defenseSkill = this.defaultEvasion(enemy);
			int penalty = 0;
			for (Buff buff : enemy.buffs(RingOfAccuracy.Accuracy.class)) {
				penalty += ((RingOfAccuracy.Accuracy) buff).level;
			}
			if (penalty != 0 && enemy == Dungeon.hero)
				defenseSkill *= Math.pow(0.75, penalty);
			return defenseSkill;
		} else {
			return 0;
		}
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (!(enemySeen && enemy == Dungeon.hero)) {
			if (enemy instanceof Hero && ((Hero) enemy).subClass == HeroSubClass.ASSASSIN) {
				damage *= 1.5f;
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}
		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP);
			Buff.affect(Dungeon.hero, Hunger.class).satisfy(restoration);
			Dungeon.hero.HP = (int)Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP+(restoration*0.15f)));
			Dungeon.hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
		}
		return damage;
	}

	public void aggro(Char ch) {
		enemy = ch;
	}
	
	public int adjustForDepth(int type){
		
		int adjustment;
				
			switch (type){
			  case 0:
				  adjustment = Dungeon.depth;
				  break;
			  case 1:
				  adjustment = (int) Dungeon.depth/2;
				  break;
			  case 2:
				  adjustment = (int) Dungeon.depth/4;
				  break;
			  case 3:
				  adjustment = (int) Dungeon.depth*2;
				  break;
		      default:
		    	  adjustment = 1;
		    	  break;
		    }
		
		return adjustment;
	}

	public int defaultAccuracy(Char enemy) {
		int scaleFactor;
		if (enemy instanceof Hero & scalesWithHeroLevel) {
			scaleFactor = ((Hero)enemy).lvl;
		} else {
			scaleFactor = (Dungeon.depth-1)*2;
		}
		return 10 + scaleFactor;
	}

	public int defaultEvasion(Char enemy) {
		int scaleFactor;
		if (enemy instanceof Hero & scalesWithHeroLevel) {
			scaleFactor = ((Hero)enemy).lvl;
		} else {
			scaleFactor = (Dungeon.depth-1)*2;
		}
		return 4 + scaleFactor;
	}

	@Override
	public void damage(int dmg, Object src) {

		Terror.recover(this);

		if (state == SLEEPING) {
			state = WANDERING;
		}
		alerted = true;

		super.damage(dmg, src);
	}

	@Override
	public void destroy() {

		super.destroy();

		Dungeon.level.mobs.remove(this);

		if (Dungeon.hero.isAlive()) {

			if (hostile) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;

				if (Dungeon.level.feeling == Feeling.DARK) {
					Statistics.nightHunt++;
				} else {
					Statistics.nightHunt = 0;
				}
				Badges.validateNightHunter();
			}

			//if (Dungeon.hero.lvl <= maxLvl && EXP > 0) {
				if (EXP > 0) {
				Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, TXT_EXP, EXP);
				Dungeon.hero.earnExp(EXP);
			}
		}
	}
	
	public boolean checkOriginalGenMobs (){
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob.originalgen){return true;}
		 }	
		return false;
	}

	@Override
	public void die(Object cause) {

		super.die(cause);
		
		int generation=0;
		
		if(this instanceof Swarm){
			Swarm swarm = (Swarm) this;
			generation=swarm.generation;
		}
				
		if (Dungeon.hero.buff(Dewcharge.class) != null && generation==0) {
			explodeDewHigh(pos);
		}
		
		if (!Dungeon.level.cleared && originalgen && !checkOriginalGenMobs() && Dungeon.dewDraw && Dungeon.depth>2 && Dungeon.depth<25 && !Dungeon.bossLevel(Dungeon.depth)){
			Dungeon.level.cleared=true;
			GameScene.levelCleared();		
			if(Dungeon.depth>0){Statistics.prevfloormoves=Math.max(Dungeon.pars[Dungeon.depth]-Dungeon.level.currentmoves,0);
			   if (Statistics.prevfloormoves>1){
			     GLog.h("Level cleared in %s moves under goal.", Statistics.prevfloormoves);
			   } else if (Statistics.prevfloormoves==1){
			     GLog.h("Level cleared in 1 move under goal."); 
			   } else if (Statistics.prevfloormoves==0){
				 GLog.h("Level cleared over goal moves.");
			   }
			} 
		}

		float lootChance = this.lootChance;
		float lootChanceOther = this.lootChanceOther;
		int bonus = 0;
		for (Buff buff : Dungeon.hero.buffs(RingOfWealth.Wealth.class)) {
			bonus += ((RingOfWealth.Wealth) buff).level;
		}

		lootChance *= Math.pow(1.1, bonus);
		lootChanceOther *= Math.pow(1.1, bonus);

		if (Random.Float() < lootChance && Dungeon.hero.lvl <= maxLvl + 800) {
			Item loot = createLoot();
			if (loot != null)
				Dungeon.level.drop(loot, pos).sprite.drop();

		} else if (Random.Float() < lootChanceOther
				&& Dungeon.hero.lvl <= maxLvl + 800) {
			Item lootOther = createLootOther();
			if (lootOther != null)
				Dungeon.level.drop(lootOther, pos).sprite.drop();
		} else if (Random.Float() < lootChanceThird
				&& Dungeon.hero.lvl <= maxLvl + 800) {
			Item lootThird = createLootThird();
			if (lootThird != null)
				Dungeon.level.drop(lootThird, pos).sprite.drop();
		}

		if (Dungeon.hero.isAlive() && !Dungeon.visible[pos]) {
			GLog.i(Messages.get(Mob.class, "died"));
		}
	}

	protected Object loot = null;
	protected Object lootOther = null;
	protected Object lootThird = null;
	protected float lootChance = 0;
	protected float lootChanceOther = 0;
	protected float lootChanceThird = 0;

	@SuppressWarnings("unchecked")
	protected Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.random((Generator.Category) loot);

		} else if (loot instanceof Class<?>) {

			item = Generator.random((Class<? extends Item>) loot);

		} else {

			item = (Item) loot;

		}
		return item;
	}

	@SuppressWarnings("unchecked")
	protected Item createLootOther() {
		Item item;
		if (lootOther instanceof Generator.Category) {

			item = Generator.random((Generator.Category) lootOther);

		} else if (lootOther instanceof Class<?>) {

			item = Generator.random((Class<? extends Item>) lootOther);

		} else {

			item = (Item) lootOther;

		}
		return item;
	}
	
	@SuppressWarnings("unchecked")
	protected Item createLootThird() {
		Item item;
		if (lootThird instanceof Generator.Category) {

			item = Generator.random((Generator.Category) lootThird);

		} else if (lootThird instanceof Class<?>) {

			item = Generator.random((Class<? extends Item>) lootThird);

		} else {

			item = (Item) lootThird;

		}
		return item;
	}

	public void explodeDew(int cell) {
		
		if (Dungeon.dewDraw){
		  Sample.INSTANCE.play(Assets.SND_BLAST, 2);

		  for (int n : Level.NEIGHBOURS9) {
			 int c = cell + n;
			 if (c >= 0 && c < Level.getLength() && Level.passable[c]) {
						
				if (Random.Int(10)==1){Dungeon.level.drop(new RedDewdrop(), c).sprite.drop();}
				else if (Random.Int(3)==1){Dungeon.level.drop(new YellowDewdrop(), c).sprite.drop();}
			}
		  }	
		}
	}
	
public void explodeDewHigh(int cell) {
		
		if (Dungeon.dewDraw){
		  Sample.INSTANCE.play(Assets.SND_BLAST, 2);

		  for (int n : Level.NEIGHBOURS9) {
			 int c = cell + n;
			 if (c >= 0 && c < Level.getLength() && Level.passable[c]) {
						
				if (Random.Int(8)==1){Dungeon.level.drop(new VioletDewdrop(), c).sprite.drop();}
				else if (Random.Int(2)==1){Dungeon.level.drop(new RedDewdrop(), c).sprite.drop();}
			}
		  }	
		}
	}
	
	public boolean reset() {
		return false;
	}

	public void beckon(int cell) {

		notice();

		if (state != HUNTING) {
			state = WANDERING;
		}
		target = cell;
	}

	public String description() {
		return "Real description is coming soon!";
	}

	public void notice() {
		sprite.showAlert();
	}

	public void yell(String str) {
		GLog.n("%s: \"%s\" ", name, str);
	}

	// returns true when a mob sees the hero, and is currently targeting them.
	public boolean focusingHero() {
		return enemySeen && (target == Dungeon.hero.pos);
	}

	public interface AiState {
		public boolean act(boolean enemyInFOV, boolean justAlerted);

		public String status();
	}

	private class Sleeping implements AiState {

		public static final String TAG = "SLEEPING";

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV
					&& Random.Int(distance(enemy) + enemy.stealth()
							+ (enemy.flying ? 2 : 0)) == 0) {

				enemySeen = true;

				notice();
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
						if (mob != Mob.this) {
							mob.beckon(target);
						}
					}
				}

				spend(TIME_TO_WAKE_UP);

			} else {

				enemySeen = false;

				spend(TICK);

			}
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name);
		}
	}

	private class Wandering implements AiState {

		public static final String TAG = "WANDERING";

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV
					&& (justAlerted || Random.Int(distance(enemy) / 2
							+ enemy.stealth()) == 0)) {

				enemySeen = true;

				notice();
				state = HUNTING;
				target = enemy.pos;

			} else {

				enemySeen = false;

				int oldPos = pos;
				if (target != -1 && getCloser(target)) {
					spend(1 / speed());
					return moveSprite(oldPos, pos);
				} else {
					target = Dungeon.level.randomDestination();
					spend(TICK);
				}

			}
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name);
		}
	}

	private class Hunting implements AiState {

		public static final String TAG = "HUNTING";

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && canAttack(enemy)) {

				return doAttack(enemy);

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				}

				int oldPos = pos;
				if (target != -1 && getCloser(target)) {

					spend(1 / speed());
					return moveSprite(oldPos, pos);

				} else {

					spend(TICK);
					state = WANDERING;
					target = Dungeon.level.randomDestination();
					return true;
				}
			}
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name);
		}
	}

	protected class Fleeing implements AiState {

		public static final String TAG = "FLEEING";

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;
			if (enemyInFOV) {
				target = enemy.pos;
			}

			int oldPos = pos;
			if (target != -1 && getFurther(target)) {

				spend(1 / speed());
				return moveSprite(oldPos, pos);

			} else {

				spend(TICK);
				nowhereToRun();

				return true;
			}
		}

		protected void nowhereToRun() {
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name);
		}
	}

	private class Passive implements AiState {

		public static final String TAG = "PASSIVE";

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = false;
			spend(TICK);
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name);
		}
	}
	
	
}
