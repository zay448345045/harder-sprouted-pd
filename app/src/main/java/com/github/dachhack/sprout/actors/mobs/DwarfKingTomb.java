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
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.items.ArmorKit;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.RedDewdrop;
import com.github.dachhack.sprout.items.keys.SkeletonKey;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.DwarfKingTombSprite;
import com.github.dachhack.sprout.ui.BossHealthBar;
import com.watabou.utils.Random;

public class DwarfKingTomb extends Mob  {

	{
		name = Messages.get(this, "name");
		spriteClass = DwarfKingTombSprite.class;

		HP = HT = 800;
		defenseSkill = 5;

		EXP = 10;
		
		hostile = false;
		state = PASSIVE;
		
		loot = new RedDewdrop();
		lootChance = 0.05f;
		
	}
	
	@Override
	public void beckon(int cell) {
		// Do nothing
	}
	
	@Override
	public void add(Buff buff) {
	}
	
	
	@Override
	public int damageRoll() {
		return 0;
	}
	
	@Override
	public int attackSkill(Char target) {
		return 0;
	}

	@Override
	public int dr() {
		return 18;
		
	}
	
	
	public boolean checkKing(){
		
		int kingAlive=0;
		if(Dungeon.level.mobs!=null){
       for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof King){
				kingAlive++;
			   }
			}
		}
       if (kingAlive>0){
		return true;
       } else {
      return false;
       }
	}

	@Override
	public void damage(int dmg, Object src) {
		if (checkKing()) {
			yell(Messages.get(this, "im"));
		} else {
			BossHealthBar.assignBoss(this);
			super.damage(dmg, src);
		}
	}


	@Override
	public String description() {
		return Messages.get(this, "desc");
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void die(Object cause) {

		for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
			if (mob instanceof DwarfLich || mob instanceof King || mob instanceof King.Undead || mob instanceof Wraith) {
				mob.die(cause);
			}
		}
		
		GameScene.bossSlain();
		Dungeon.level.drop(new ArmorKit(), pos).sprite.drop();
		Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
		Dungeon.level.drop(new Gold(Random.Int(4900, 10000)), pos).sprite.drop();

		super.die(cause);

		Badges.validateBossSlain();
	
	}

		
}
