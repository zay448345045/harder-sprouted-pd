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

import java.util.ArrayList;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.DungeonTilemap;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.Strength;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.DeathRay;
import com.github.dachhack.sprout.effects.particles.PurpleParticle;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.items.weapon.enchantments.Fire;
import com.github.dachhack.sprout.items.weapon.melee.MeleeWeapon;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfDisintegration extends Wand {

	{
//		name = "Wand of Disintegration";
		name = Messages.get(this, "name");
		hitChars = false;
	}

	@Override
	public int magicMax(int lvl) {
		return 10 + 7*lvl;
	}

	@Override
	public int magicMin(int lvl) {
		return 1 + (int)(0.5f*lvl);
	}

	@Override
	protected void onZap(int cell) {

		boolean terrainAffected = false;

		int level = level();

		int maxDistance = distance();
		Ballistica.distance = Math.min(Ballistica.distance, maxDistance);

		ArrayList<Char> chars = new ArrayList<Char>();

		for (int i = 1; i < Ballistica.distance; i++) {

			int c = Ballistica.trace[i];

			Char ch;
			if ((ch = Actor.findChar(c)) != null) {
				chars.add(ch);
			}

			int terr = Dungeon.level.map[c];
			if (terr == Terrain.DOOR || terr == Terrain.BARRICADE) {

				Level.set(c, Terrain.EMBERS);
				GameScene.updateMap(c);
				terrainAffected = true;

			} else if (terr == Terrain.HIGH_GRASS) {

				Level.set(c, Terrain.GRASS);
				GameScene.updateMap(c);
				terrainAffected = true;

			}

			CellEmitter.center(c).burst(PurpleParticle.BURST,
					Random.IntRange(1 + level()/8, 2 + level()/4));
		}

		if (terrainAffected) {
			Dungeon.observe();
		}

		int dmg = magicDamageRoll();
		if (Dungeon.hero.buff(Strength.class) != null){ dmg *= (int) 4f; Buff.detach(Dungeon.hero, Strength.class);}
		for (Char ch : chars) {
			ch.damage(dmg, this);
			processSoulMark(ch, chargesPerCast());
			ch.sprite.centerEmitter().burst(PurpleParticle.BURST,
					Random.IntRange(1, 2));
			ch.sprite.flash();
		}
	}

	private int distance() {
		return level() + 4;
	}

	@Override
	protected void fx(int cell, Callback callback) {

		cell = Ballistica.trace[Math.min(Ballistica.distance, distance()) - 1];
		curUser.sprite.parent.add(new DeathRay(curUser.sprite.center(),
				DungeonTilemap.tileCenterToWorld(cell)));
		callback.call();
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		if (Random.Int(10) == 0) {//1/10 times, attack the highest HP enemy on the map!
			Mob highestHP = null;
			int highestHPAmt = -1;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (!mob.ally & mob.HP > highestHPAmt) {
					highestHP = mob;
					highestHPAmt = mob.HP;
				}
			}
			if (highestHP != null) {
				highestHP.damage(wand.magicDamageRoll(), wand);
				attacker.sprite.parent.add(new DeathRay(attacker.sprite.center(),
						DungeonTilemap.tileCenterToWorld(highestHP.pos)));
			}
		}
	}

	@Override
	public String desc() {
//		return "This wand emits a beam of destructive energy, which pierces all creatures in its way. "
//				+ "The more targets it hits, the more damage it inflicts to each of them." +
//				"\n\n" + statsDesc();
		return Messages.get(this, "desc") + statsDesc();
	}
}
