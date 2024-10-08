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
package com.github.dachhack.sprout.levels.painters;

import com.github.dachhack.sprout.Constants;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.items.Generator;
import com.github.dachhack.sprout.items.Heap.Type;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.keys.IronKey;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Room;
import com.github.dachhack.sprout.levels.Terrain;
import com.watabou.utils.Point;

public class CryptPainter extends Painter {

	public static void paint(Level level, Room room) {

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.EMPTY);

		Point c = room.center();
		int cx = c.x;
		int cy = c.y;

		Room.Door entrance = room.entrance();

		entrance.set(Room.Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));

		if (entrance.x == room.left) {
			set(level, new Point(room.right - 1, room.top + 1), Terrain.STATUE);
			set(level, new Point(room.right - 1, room.bottom - 1),
					Terrain.STATUE);
			cx = room.right - 2;
		} else if (entrance.x == room.right) {
			set(level, new Point(room.left + 1, room.top + 1), Terrain.STATUE);
			set(level, new Point(room.left + 1, room.bottom - 1),
					Terrain.STATUE);
			cx = room.left + 2;
		} else if (entrance.y == room.top) {
			set(level, new Point(room.left + 1, room.bottom - 1),
					Terrain.STATUE);
			set(level, new Point(room.right - 1, room.bottom - 1),
					Terrain.STATUE);
			cy = room.bottom - 2;
		} else if (entrance.y == room.bottom) {
			set(level, new Point(room.left + 1, room.top + 1), Terrain.STATUE);
			set(level, new Point(room.right - 1, room.top + 1), Terrain.STATUE);
			cy = room.top + 2;
		}
		if (Statistics.gravestonesSpawned > Constants.MAX_GRAVES) {
			level.drop(prize(level), cx + cy * Level.getWidth()).type = Type.SKELETON;
		} else {
			level.drop(prize(level), cx + cy * Level.getWidth()).type = Type.TOMB;
			Statistics.gravestonesSpawned++;
		}


	}

	private static Item prize(Level level) {

		Item prize = Generator.random(Generator.Category.ARMOR);

		for (int i = 0; i < 3; i++) {
			Item another = Generator.random(Generator.Category.ARMOR);
			if (another.level > prize.level) {
				prize = another;
			}
		}

		return prize;
	}
}
