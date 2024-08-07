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
package com.github.dachhack.sprout.plants;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.Fire;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.FireImbue;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroSubClass;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.particles.FlameParticle;
import com.github.dachhack.sprout.items.potions.PotionOfLiquidFlame;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;

public class Firebloom extends Plant {

	private static final String TXT_DESC = "When something touches a Firebloom, it bursts into flames.";

	{
		image = 0;
		plantName = "Firebloom";
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);

		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
			Buff.affect(ch, FireImbue.class).set(15f);
		}

		GameScene.add(Blob.seed(pos, 2, Fire.class));

		if (Dungeon.visible[pos]) {
			CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5);
		}
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = "Firebloom";

			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_FIREBLOOM;

			plantClass = Firebloom.class;
			alchemyClass = PotionOfLiquidFlame.class;
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}
