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
package com.github.dachhack.sprout.levels;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.DungeonTilemap;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.actors.mobs.npcs.Blacksmith;
import com.github.dachhack.sprout.actors.mobs.npcs.Tinkerer2;
import com.github.dachhack.sprout.items.Bomb;
import com.github.dachhack.sprout.items.Mushroom;
import com.github.dachhack.sprout.levels.Room.Type;
import com.github.dachhack.sprout.levels.painters.Painter;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Scene;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class MineLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

		viewDistance = 6;
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_SEAL;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}

	@Override
	protected boolean[] water() {
		return Patch.generate(feeling == Feeling.WATER ? 0.60f : 0.45f, 6);
	}

	@Override
	protected boolean[] grass() {
		return Patch.generate(feeling == Feeling.GRASS ? 0.55f : 0.35f, 3);
	}

	@Override
	protected void setPar(){
		Dungeon.pars[Dungeon.depth] = 400+(Dungeon.depth*50)+(secretDoors*20);
	}

	@Override
	protected void createItems() {
		
		
		if (Dungeon.hero.heroClass==HeroClass.ROGUE && Random.Int(3) == 0){addItemToSpawn(new Bomb());}
		super.createItems();
	}
	
	
	
	
	@Override
	protected void decorate() {

		for (Room room : rooms) {
			if (room.type != Room.Type.STANDARD) {
				continue;
			}

			if (room.width() <= 3 || room.height() <= 3) {
				continue;
			}

			int s = room.square();

			if (Random.Int(s) > 8) {
				int corner = (room.left + 1) + (room.top + 1) * getWidth();
				if (map[corner - 1] == Terrain.WALL
						&& map[corner - getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.right - 1) + (room.top + 1) * getWidth();
				if (map[corner + 1] == Terrain.WALL
						&& map[corner - getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.left + 1) + (room.bottom - 1) * getWidth();
				if (map[corner - 1] == Terrain.WALL
						&& map[corner + getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.right - 1) + (room.bottom - 1) * getWidth();
				if (map[corner + 1] == Terrain.WALL
						&& map[corner + getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			for (Room n : room.connected.keySet()) {
				if ((n.type == Room.Type.STANDARD || n.type == Room.Type.TUNNEL)
						&& Random.Int(3) == 0) {
					Painter.set(this, room.connected.get(n), Terrain.EMPTY_DECO);
				}
			}
		}

		for (int i = getWidth() + 1; i < getLength() - getWidth(); i++) {
			if (map[i] == Terrain.EMPTY) {
				int n = 0;
				if (map[i + 1] == Terrain.WALL) {
					n++;
				}
				if (map[i - 1] == Terrain.WALL) {
					n++;
				}
				if (map[i + getWidth()] == Terrain.WALL) {
					n++;
				}
				if (map[i - getWidth()] == Terrain.WALL) {
					n++;
				}
				if (Random.Int(6) <= n) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}

		for (int i = 0; i < getLength(); i++) {
			if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}

				
		setPar();		

		if (Dungeon.bossLevel(Dungeon.depth + 1)) {
			return;
		}

		for (Room r : rooms) {
			if (r.type == Type.STANDARD) {
				for (Room n : r.neigbours) {
					if (n.type == Type.STANDARD && !r.connected.containsKey(n)) {
						Rect w = r.intersect(n);
						if (w.left == w.right && w.bottom - w.top >= 5) {

							w.top += 2;
							w.bottom -= 1;

							w.right++;

							Painter.fill(this, w.left, w.top, 1, w.height(),
									Terrain.BARRICADE);

						} else if (w.top == w.bottom && w.right - w.left >= 5) {

							w.left += 2;
							w.right -= 1;

							w.bottom++;

							Painter.fill(this, w.left, w.top, w.width(), 1,
									Terrain.BARRICADE);
						}
					}
				}
			}
		}
		
		
	}

	@Override
	public String tileName(int tile) {
		switch (tile) {
		case Terrain.GRASS:
			return "Fluorescent moss";
		case Terrain.HIGH_GRASS:
			return "Fluorescent mushrooms";
		case Terrain.WATER:
			return "Freezing cold water.";
		default:
			return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.ENTRANCE:
			return "The ladder leads up to the upper depth.";
		case Terrain.EXIT:
			return "The ladder leads down to the lower depth.";
		case Terrain.HIGH_GRASS:
			return "Huge mushrooms block the view.";
		case Terrain.WALL_DECO:
			return "A vein of some ore is visible on the wall. Gold?";
		case Terrain.BOOKSHELF:
			return "Who would need a bookshelf in a cave?";
		default:
			return super.tileDesc(tile);
		}
	}

	@Override
	public void addVisuals(Scene scene) {
		super.addVisuals(scene);
		addVisuals(this, scene);
	}

	public static void addVisuals(Level level, Scene scene) {
		for (int i = 0; i < getLength(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				scene.add(new Vein(i));
			}
		}
	}

	private static class Vein extends Group {

		private int pos;

		private float delay;

		public Vein(int pos) {
			super();

			this.pos = pos;

			delay = Random.Float(2);
		}

		@Override
		public void update() {

			if (visible = Dungeon.visible[pos]) {

				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					delay = Random.Float();

					PointF p = DungeonTilemap.tileToWorld(pos);
					((Sparkle) recycle(Sparkle.class)).reset(
							p.x + Random.Float(DungeonTilemap.SIZE), p.y
									+ Random.Float(DungeonTilemap.SIZE));
				}
			}
		}
	}

	public static final class Sparkle extends PixelParticle {

		public void reset(float x, float y) {
			revive();

			this.x = x;
			this.y = y;

			left = lifespan = 0.5f;
		}

		@Override
		public void update() {
			super.update();

			float p = left / lifespan;
			size((am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2);
		}
	}
}