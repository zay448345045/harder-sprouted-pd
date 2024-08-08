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
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Paralysis;
import com.github.dachhack.sprout.actors.buffs.Roots;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.FlyingProtector;
import com.github.dachhack.sprout.actors.mobs.npcs.NPC;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.MagicMissile;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.SheepSprite;
import com.github.dachhack.sprout.utils.BArray;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfFlock extends Wand {

	{
//		name = "Wand of Flock";
		name = Messages.get(this, "name");
	}

	@Override
	protected void onZap(int cell) {

		int level = level();

		int n = level + 2;

		Char ch = Actor.findChar(cell);

		if (ch != null) {
			processSoulMark(ch, chargesPerCast());
			if( Ballistica.distance > 2) {

				cell = Ballistica.trace[Ballistica.distance - 2];
			}
		}


		boolean[] passable = BArray.or(Level.passable, Level.avoid, null);
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char) actor).pos] = false;
			}
		}

		PathFinder.buildDistanceMap(cell, passable, n);
		int dist = 0;

		if (Actor.findChar(cell) != null) {
			PathFinder.distance[cell] = Integer.MAX_VALUE;
			dist = 1;
		}

		float lifespan = level + 3;

		sheepLabel: for (int i = 0; i < n; i++) {
			do {
				for (int j = 0; j < Level.getLength(); j++) {
					if (PathFinder.distance[j] == dist) {

						Sheep sheep = new Sheep();
						sheep.lifespan = lifespan;
						sheep.pos = j;
						GameScene.add(sheep);
						Dungeon.level.mobPress(sheep);

						CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4);

						PathFinder.distance[j] = Integer.MAX_VALUE;

						continue sheepLabel;
					}
				}
				dist++;
			} while (dist < n);
		}
		
		if (Dungeon.depth>50 && Dungeon.depth<55){
			int spawnCell = Dungeon.level.randomRespawnCellMob();
			if (spawnCell>0){
			   FlyingProtector.spawnAt(spawnCell);
//			   GLog.w("How dare you violate the magic of this place! ");
//			   GLog.w("A Protector has spawned to defend the level!");
				GLog.w(Messages.get(this, "s1"));
				GLog.w(Messages.get(this, "s2"));
			}
		}
	}

	@Override
	protected void fx(int cell, Callback callback) {
		MagicMissile.wool(curUser.sprite.parent, curUser.pos, cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
//	public String desc() {
//		return "A flick of this wand summons a flock of magic sheep, creating temporary impenetrable obstacle.";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void onHit(Wand wand, Hero attacker, Char defender, int damage) {
		super.onHit(wand, attacker, defender, damage);
		Buff.affect(defender, Roots.class, Paralysis.duration(defender));
	}

	public static class Sheep extends NPC {

//		private static final String[] QUOTES = { "Baa!", "Baa?", "Baa.",
//				"Baa..." };
private static final String[] QUOTES = {Messages.get(WandOfFlock.class, "1"), Messages.get(WandOfFlock.class, "2"), Messages.get(WandOfFlock.class, "3"),
		Messages.get(WandOfFlock.class, "4")};

		{
//			name = "sheep";
			name = Messages.get(WandOfFlock.class, "sname");
			spriteClass = SheepSprite.class;
		}

		public float lifespan;

		private boolean initialized = false;

		@Override
		protected boolean act() {
			if (initialized) {
				HP = 0;

				destroy();
				sprite.die();

			} else {
				initialized = true;
				spend(lifespan + Random.Float(2));
			}
			return true;
		}

		@Override
		public void damage(int dmg, Object src) {
		}



		@Override
//		public String description() {
//			return "This is a magic sheep. What's so magical about it? You can't kill it. "
//					+ "It will stand there until it magcially fades away, all the while chewing cud with a blank stare.";
//		}
		public String description() {
			return Messages.get(WandOfFlock.class, "sdesc");
		}

		@Override
		public void interact() {
			yell(Random.element(QUOTES));
		}
	}
}
