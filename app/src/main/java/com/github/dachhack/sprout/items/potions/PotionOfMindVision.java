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
package com.github.dachhack.sprout.items.potions;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Invisibility;
import com.github.dachhack.sprout.actors.buffs.MindVision;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.SpellSprite;
import com.github.dachhack.sprout.items.misc.Spectacles.MagicSight;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class PotionOfMindVision extends Potion {

	{
//		name = "Potion of Mind Vision";
		name = Messages.get(this, "name");
	}

//	private static final String TXT_PREVENTING = "Something scrambles the scrying magic! ";
private static final String TXT_PREVENTING = Messages.get(PotionOfMindVision.class, "prevent");
	
	@Override
	public void apply(Hero hero) {
		setKnown();
		
		if (Dungeon.level.locked && Dungeon.depth>50 && Dungeon.hero.buff(MagicSight.class) == null){
			GLog.w(TXT_PREVENTING);	
			return;
		}
		
		Buff.affect(hero, MindVision.class, Dungeon.hero.buff(MagicSight.class) != null ? MindVision.DURATION*4 : MindVision.DURATION);
		Dungeon.observe();

//		if (Dungeon.level.mobs.size() > 0) {
//			GLog.i("You can somehow feel the presence of other creatures' minds!");
//		} else {
//			GLog.i("You can somehow tell that you are alone on this level at the moment.");
//		}
		if (Dungeon.level.mobs.size() > 0) {
			GLog.i(Messages.get(this, "see_mobs"));
		} else {
			GLog.i(Messages.get(this, "see_none"));
		}
	}

//	@Override
//	public String desc() {
//		return "After drinking this, your mind will become attuned to the psychic signature "
//				+ "of distant creatures, enabling you to sense biological presences through walls. "
//				+ "Also this potion will permit you to see through nearby walls and doors.";
//	}
@Override
public String desc() {
	return Messages.get(this, "desc");
}

	@Override
	public int price() {
		return isKnown() ? 35 * quantity : super.price();
	}
}
