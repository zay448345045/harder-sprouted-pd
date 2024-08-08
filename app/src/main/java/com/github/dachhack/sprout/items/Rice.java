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
package com.github.dachhack.sprout.items;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.weapon.missiles.RiceBall;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Rice extends Item {

	public static final String AC_COOK = "Make Dumplings";
	public static final String AC_COOKBOMB = "Make Dumpling Bomb";

	public static final float TIME_TO_COOK = 1;
	public static final float TIME_TO_COOK_BOMB = 4;

	{
		name = "grain of magic rice";
		image = ItemSpriteSheet.SEED_RICE;

		stackable = false;

		defaultAction = AC_COOK;
	}

	private int bombcost=5;
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COOK);
		actions.add(AC_COOKBOMB);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action.equals(AC_COOK)) {

			hero.spend(TIME_TO_COOK);
			hero.busy();

			hero.sprite.operate(hero.pos);
			
			RiceBall riceball = new RiceBall();
			if (riceball.doPickUp(Dungeon.hero)) {
				GLog.i(Messages.get(Hero.class,"have"), riceball.name());
				Statistics.ballsCooked++;
			} else {
				Dungeon.level.drop(riceball, Dungeon.hero.pos).sprite.drop();
				Statistics.ballsCooked++;
			}
			
				
			if (Statistics.ballsCooked>200){
				detach(Dungeon.hero.belongings.backpack);
				GLog.n("Your magic rice crumbles to dust!");
			} else if (Statistics.ballsCooked>175){
				GLog.n("Your magic rice is cracking...");
			} else if (Statistics.ballsCooked>150){
				GLog.n("Your magic rice begins to look dull...");
			}
				
		} else if (action.equals(AC_COOKBOMB)) {

			hero.spend(TIME_TO_COOK_BOMB);
			hero.busy();

			hero.sprite.operate(hero.pos);
			
			DumplingBomb bomb = new DumplingBomb();
			if (bomb.doPickUp(Dungeon.hero)) {
				GLog.i(Messages.get(Hero.class,"have"), bomb.name());
				Statistics.ballsCooked+=bombcost;
			} else {
				Dungeon.level.drop(bomb, Dungeon.hero.pos).sprite.drop();
				Statistics.ballsCooked+=bombcost;
			}
			
				
			if (Statistics.ballsCooked>200){
				detach(Dungeon.hero.belongings.backpack);
				GLog.n("Your magic rice crumbles to dust!");
			} else if (Statistics.ballsCooked>175){
				GLog.n("Your magic rice is cracking...");
			} else if (Statistics.ballsCooked>150){
				GLog.n("Your magic rice begins to look dull...");
			}
					

		} else {
			super.execute(hero, action);

		}
	}
	
	@Override
	public boolean doPickUp(Hero hero) {
		if (super.doPickUp(hero)) {

			if (Dungeon.level != null && Dungeon.depth==32) {
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					mob.beckon(Dungeon.hero.pos);
				}

				GLog.w("The denizens awake and they are hungry! Here come the Oni!");
				CellEmitter.center(Dungeon.hero.pos).start(
						Speck.factory(Speck.SCREAM), 0.3f, 3);
				Sample.INSTANCE.play(Assets.SND_CHALLENGE);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int price() {
		return 10 * quantity;
	}

	@Override
	public String info() {
		return "This magic grain of rice can create many satisfying dumplings.";
	}
}
