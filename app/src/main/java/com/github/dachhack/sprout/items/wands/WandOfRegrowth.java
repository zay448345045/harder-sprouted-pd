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
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.Regrowth;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.MagicMissile;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfRegrowth extends Wand {

	{
//		name = "Wand of Regrowth";
		name = Messages.get(this, "name");
	}

	@Override
	protected void onZap(int cell) {

		for (int i = 1; i < Ballistica.distance - 1; i++) {
			int p = Ballistica.trace[i];
			int c = Dungeon.level.map[p];
			if (c == Terrain.EMPTY || c == Terrain.EMBERS
					|| c == Terrain.EMPTY_DECO) {

				Level.set(p, Terrain.GRASS);

			}
		}
		Char ch = Actor.findChar(cell);
		if (ch != null) {
			processSoulMark(ch, chargesPerCast());
		}
		int c = Dungeon.level.map[cell];
		if (c == Terrain.EMPTY || c == Terrain.EMBERS
				|| c == Terrain.EMPTY_DECO || c == Terrain.GRASS
				|| c == Terrain.HIGH_GRASS) {

			GameScene.add(Blob.seed(cell, (level() + 2) * 20, Regrowth.class));

		} else {

//			GLog.i("nothing happened");
			GLog.i(Messages.get(WandOfPoison.class, "nothing"));

		}
	}

	@Override
	protected void fx(int cell, Callback callback) {
		MagicMissile
				.foliage(curUser.sprite.parent, curUser.pos, cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		int maxValue = (int) (damage * 0.2f);
		int effValue = Math.min(Random.IntRange(0, maxValue), attacker.HT
				- attacker.HP);

		if (effValue > 0) {

			attacker.HP += effValue;
			attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f,
					1);
			attacker.sprite.showStatus(CharSprite.POSITIVE,
					Integer.toString(effValue));
		}
	}

	@Override
//	public String desc() {
//		return "\"When life ceases new life always begins to grow... The eternal cycle always remains!\"";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}
}
