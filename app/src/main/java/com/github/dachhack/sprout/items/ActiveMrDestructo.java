/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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

import java.util.ArrayList;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.MrDestructo;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class ActiveMrDestructo extends Item {

	{
//		name = "mr destructo";

		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ACTIVEMRD;
		defaultAction = AC_ACTIVATETHROW;
		stackable = true;
	}

	
	private static boolean activate = false;

//	private static final String AC_ACTIVATETHROW = "Activate & Throw";
	private static final String AC_ACTIVATETHROW = Messages.get(Dewdrop.class, "value");

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ACTIVATETHROW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_ACTIVATETHROW)) {
			activate = true;
			action = AC_THROW;
		} else
			activate = false;

		super.execute(hero, action);
	}

	@Override
	protected void onThrow(int cell) {
		
		if (Actor.findChar(cell) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : Level.NEIGHBOURS8)
				if (Level.passable[cell + i])
					candidates.add(cell + i);
			int newCell = candidates.isEmpty() ? cell : Random
					.element(candidates);
			
			   if (!Level.pit[newCell] && activate) {
				   MrDestructo.spawnAt(newCell);
			   } else {
			   Dungeon.level.drop(this, newCell).sprite.drop(cell);
			   }
			   
		} else if (!Level.pit[cell] && activate) {
				MrDestructo.spawnAt(cell);
		} else {
			
			super.onThrow(cell);
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
	public String info() {
		return Messages.get(this, "desc");
	}

	
		
	
}
