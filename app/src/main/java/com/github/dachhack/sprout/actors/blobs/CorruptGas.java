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
package com.github.dachhack.sprout.actors.blobs;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Bleeding;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Cripple;
import com.github.dachhack.sprout.actors.buffs.Vertigo;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.BlobEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Random;

public class CorruptGas extends Blob implements Hero.Doom {

	@Override
	protected void evolve() {
		super.evolve();

		int levelDamage = (int) (5 + Dungeon.depth * 1.5);
		int bleedDamage = 5 + Dungeon.depth;

		Char ch;
		for (int i = 0; i < LENGTH; i++) {
			if (cur[i] > 0 && (ch = Actor.findChar(i)) != null) {
				
			    if (!ch.immunities().contains(this.getClass())) {
					if (!ch.immunities().contains(ConfusionGas.class)) {
						Buff.prolong(ch, Vertigo.class, 2);
					}
					Buff.affect(ch, Bleeding.class).set(bleedDamage);
					Buff.prolong(ch, Cripple.class, Cripple.DURATION);

					int damage = (ch.HT + levelDamage) / 40;
					if (Random.Int(40) < (ch.HT + levelDamage) % 40) {//So damage can be increased very occasionally by 1 if a certain condition is met? Dachhack what where you doing!?
						damage++;
					}

					ch.damage(damage, this);
				}
			}
		}
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);

		emitter.pour(Speck.factory(Speck.CORRUPT), 0.6f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void onDeath() {
		Dungeon.fail(ResultDescriptions.GAS);
		Badges.validateDeathFromGas();
		GLog.n(Messages.get(this, "die"));
	}
}



