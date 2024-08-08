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
package com.github.dachhack.sprout.items.misc;

import java.util.ArrayList;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.ItemStatusHandler;
import com.github.dachhack.sprout.items.KindofMisc;
import com.github.dachhack.sprout.items.rings.Ring.RingBuff;
import com.github.dachhack.sprout.items.rings.RingOfAccuracy.Accuracy;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Spectacles extends MiscEquippable {

	{
//		name = "spectacles";

		name = Messages.get(this, "name");		image = ItemSpriteSheet.OTILUKES_SPECS;

		unique = true;
	}
	
	@Override
	protected MiscBuff buff() {
		return new MagicSight();
	}

	public class MagicSight extends MiscBuff {
	}
	
    @Override
//	public String cursedDesc(){
//		return "your " + this  + " are cursed";
//	}
//
	public String cursedDesc() {
		return Messages.get(this, "cursed", this);
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
//	public String desc() {
//		return "I wonder what these do??? ";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}


}
