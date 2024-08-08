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
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Poison;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.AdamantRing;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.scrolls.ScrollOfPsionicBlast;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.TenguSprite;
import com.github.dachhack.sprout.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class TenguDen extends Mob {

	private static final int JUMP_DELAY = 5;
	protected static final float SPAWN_DELAY = 2f;
	
	{
		name = Messages.get(this, "name");
		spriteClass = TenguSprite.class;
		baseSpeed = 2f;

		HP = HT = 400;
		EXP = 20;
		defenseSkill = 30;
	}

	private int timeToJump = JUMP_DELAY;
	
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 25);
	}

	@Override
	public int attackSkill(Char target) {
		return 28;
	}

	@Override
	public int dr() {
		return 20;
	}

	
	
	
	@Override
	public void die(Object cause) {

		
		    GameScene.bossSlain();
	        Dungeon.tengudenkilled=true;
			yell(Messages.get(this, "die"));
	   	
	    	Dungeon.level.drop(new AdamantRing(), pos).sprite.drop();
			Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), pos).sprite.drop();
	    	
	    	super.die(cause); 	
					
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
		if (timeToJump <= 0 ) {
			jump();
			return true;
		} else {
			return super.doAttack(enemy);
		}
	}

	private void jump() {
		timeToJump = JUMP_DELAY;

		int newPos;
		do {
			newPos = Random.Int(Level.getLength());
		} while (!Level.passable[newPos]
				|| Level.adjacent(newPos, Dungeon.hero.pos)
				|| Actor.findChar(newPos) != null);

		sprite.move(pos, newPos);
		move(newPos);

		if (Dungeon.visible[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.SND_PUFF);
		}

		spend(1 / speed());
		
		if (Dungeon.level.mobs.size()<7){
		 Assassin.spawnAt(pos);
		}
		
		
	}

	public static TenguDen spawnAt(int pos) {
		if (Level.passable[pos] && Actor.findChar(pos) == null) {
          
			TenguDen w = new TenguDen();
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add(w, SPAWN_DELAY);

			//w.sprite.alpha(0);
			//w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

			return w;
  			
		} else {
			return null;
		}
	}
	
	@Override
	public void notice() {
		BossHealthBar.assignBoss(this);

		super.notice();
		yell(Messages.get(this, "notice"));
	}

	@Override
	public String description() {
		return Messages.get(Tengu.class, "desc");
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
