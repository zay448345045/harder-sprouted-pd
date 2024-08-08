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
package com.github.dachhack.sprout.items.wands;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.npcs.NPC;
import com.github.dachhack.sprout.effects.MagicMissile;
import com.github.dachhack.sprout.items.scrolls.ScrollOfTeleportation;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class WandOfTeleportation extends Wand {

	{
//		name = "Wand of Teleportation";
		name = Messages.get(this, "name");
	}


	public static void teleportChar(Char ch) {
		int count = 10;
		int pos;
		do {
			pos = Dungeon.level.randomRespawnCell();
			if (count-- <= 0) {
				break;
			}
		} while (pos == -1);

		if (pos == -1) {

			GLog.w(ScrollOfTeleportation.TXT_NO_TELEPORT);

		} else {

			ch.pos = pos;
			ch.sprite.place(ch.pos);
			ch.sprite.visible = Dungeon.visible[pos];
//			GLog.i(curUser.name + " teleported " + ch.name
//					+ " to somewhere");
			GLog.i(Messages.get(WandOfTeleportation.class, "tele", curUser.name, ch.name));

		}
	}


	@Override
	protected void onZap(int cell) {

		Char ch = Actor.findChar(cell);

		if (ch == curUser) {

			setKnown();
			ScrollOfTeleportation.teleportHero(curUser);

		} else if (ch != null && !(ch instanceof NPC)) {

			teleportChar(ch);
			processSoulMark(ch, chargesPerCast());

		} else {

			GLog.i("nothing happened");

		}
	}

	@Override
	protected void fx(int cell, Callback callback) {
		MagicMissile.coldLight(curUser.sprite.parent, curUser.pos, cell,
				callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		teleportChar(defender);
	}

	@Override
	public String desc() {
		return "A blast from this wand will teleport a creature against "
				+ "its will to a random place on the current level.";
	}
}
