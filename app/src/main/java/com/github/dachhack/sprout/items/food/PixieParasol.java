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
package com.github.dachhack.sprout.items.food;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.buffs.BerryRegeneration;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Drowsy;
import com.github.dachhack.sprout.actors.buffs.Hunger;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Vertigo;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Random;

public class PixieParasol extends Food {

	{
//		name = "pixie parasol mushroom";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.MUSHROOM_PIXIEPARASOL;
		energy = (Hunger.STARVING - Hunger.HUNGRY)/10;
//		message = "Munch munch";
		message = Messages.get(BlueMilk.class, "eat");
		hornValue = 1;
		bones = false;
	}

//	private static final String TXT_PREVENTING = "Something tells you that wouldn't be a good idea here! ";
//	private static final String TXT_EFFECT = "You are floating in your dream! ";
private static final String TXT_PREVENTING = Messages.get(BlueMilk.class, "prevent");
	private static final String TXT_EFFECT = Messages.get(PixieParasol.class, "effect");

	@Override
	public void execute(Hero hero, String action) {
		
		if (action.equals(AC_EAT)) {
			
			if (Dungeon.bossLevel()){
				GLog.w(TXT_PREVENTING);
				return;
			}

		}
		
	   if (action.equals(AC_EAT)) {
		   
		   
		   GLog.w(TXT_EFFECT);
			
		   switch (Random.Int(10)) {
			case 1:
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					Buff.affect(mob, Drowsy.class);
					Buff.prolong(mob, Paralysis.class, Random.IntRange(10, 16));
					mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE),	0.3f, 5);
				}
				Buff.affect(hero, BerryRegeneration.class).level(hero.HT);
				break;
			case 0: case 2: case 3: case 4: case 5: 
			case 6: case 7: case 8: case 9: case 10:
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					Buff.affect(mob, Drowsy.class);
					Buff.prolong(mob, Paralysis.class, Random.IntRange(10, 16));
					mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE),	0.3f, 5);
				}
				Buff.affect(hero, Vertigo.class, 5f);
				Buff.affect(hero, BerryRegeneration.class).level(hero.HT);
				break;
			}
		}
	   
	   super.execute(hero, action);
	}	
	
//	@Override
//	public String info() {
//		return "These mushrooms seem to spin in front of your eyes. "
//				+"You hear fleeting laughter. ";
//	}
@Override
public String info() {
	return Messages.get(this, "desc");
}

	@Override
	public int price() {
		return 20 * quantity;
	}
	
	public PixieParasol() {
		this(1);
	}

	public PixieParasol(int value) {
		this.quantity = value;
	}
}
