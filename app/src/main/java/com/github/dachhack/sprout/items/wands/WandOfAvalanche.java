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
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.Cripple;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Strength;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.MagicMissile;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.utils.BArray;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfAvalanche extends Wand {

	{
//		name = "Wand of Avalanche";
		name = Messages.get(this, "name");
	}

	@Override
	public int magicMax(int lvl) {
		return 15 + 10*lvl;
	}

	@Override
	public int magicMin(int lvl) {
		return 6 + 3*lvl;
	}

	@Override
	protected void onZap(int cell) {

		Sample.INSTANCE.play(Assets.SND_ROCKS);

		int level = level();

		Ballistica.distance = Math.min(Ballistica.distance, 8 + level);

		int size = 2;
		PathFinder.buildDistanceMap(cell, BArray.not(Level.solid, null), size);

		for (int i = 0; i < Level.getLength(); i++) {

			int d = PathFinder.distance[i];

			if (d < Integer.MAX_VALUE) {

				Char ch = Actor.findChar(i);
				if (ch != null) {

					ch.sprite.flash();
					
					 int damage = magicDamageRoll();
			         if (Dungeon.hero.buff(Strength.class) != null){ damage *= (int) 4f; Buff.detach(Dungeon.hero, Strength.class);}
			         if (ch == Dungeon.hero) {
			         	damage = damage/3;
					 }
			         damage -= Random.NormalIntRange(ch.dr()/2,ch.dr());
					 ch.damage(damage, this);

					 processSoulMark(ch, chargesPerCast());
	

					if (ch.isAlive() && Random.Int(2 + d) < 2 && !(ch instanceof Hero)) {//Doesn't self-paralyze
						Buff.prolong(ch, Paralysis.class, Random.IntRange(5, 10));
					}
				}

				CellEmitter.get(i).start(Speck.factory(Speck.ROCK), 0.07f,
						3 + (size - d));
				Camera.main.shake(2 + level()/5, 0.07f * 10);
			}
		}

		if (!curUser.isAlive()) {
			Dungeon.fail(Utils.format(ResultDescriptions.ITEM, name));
//			GLog.n("You killed yourself with your own Wand of Avalanche...");
			GLog.n(Messages.get(this, "kill"));
		}
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		if (Random.Int(8) == 0) {
			Buff.affect(defender, Paralysis.class, 2 + wand.level() / 10);
			Buff.affect(defender, Cripple.class, Cripple.DURATION);
			defender.damage(defender.HT/2,new Death());
			CellEmitter.get(defender.pos).start(Speck.factory(Speck.ROCK), 0.07f,
					3 + (wand.level()/5));
			Camera.main.shake(2 + wand.level()/5, 0.07f * 10);
		}
	}

	@Override
	protected void fx(int cell, Callback callback) {
		MagicMissile.earth(curUser.sprite.parent, curUser.pos, cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
//	public String desc() {
//		return "When a discharge of this wand hits a wall (or any other solid obstacle) it causes "
//				+ "an avalanche of stones, damaging and stunning all creatures in the affected area." +
//				"It will deal reduced damage at distance, but may harm the player when close up." +
//				"\n\n" + statsDesc();
//	}
	public String desc() {
		return Messages.get(this, "desc", 2, 7 + level() / 3);
	}
}
