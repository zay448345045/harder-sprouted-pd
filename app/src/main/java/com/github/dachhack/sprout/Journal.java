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
package com.github.dachhack.sprout;

import com.github.dachhack.sprout.Messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Journal {

	public enum Feature {
		WELL_OF_HEALTH(Messages.get(Journal.class, "well_of_health")), WELL_OF_AWARENESS(Messages.get(Journal.class, "well_of_awareness")), WELL_OF_TRANSMUTATION(
				Messages.get(Journal.class, "well_of_transmutation")), ALCHEMY(Messages.get(Journal.class, "alchemy")), GARDEN(
				Messages.get(Journal.class, "garden")), STATUE(Messages.get(Journal.class, "statue")),

		GHOST(Messages.get(Journal.class, "ghost")), WANDMAKER(Messages.get(Journal.class, "wandmaker")), TROLL(
				Messages.get(Journal.class, "troll")), IMP(Messages.get(Journal.class, "imp"));

		public String desc;

		Feature(String desc) {
			this.desc = desc;
		}
	}

	public static class Record implements Comparable<Record>, Bundlable {

		private static final String FEATURE = "feature";
		private static final String DEPTH = "depth";

		public Feature feature;
		public int depth;

		public Record() {
		}

		public Record(Feature feature, int depth) {
			this.feature = feature;
			this.depth = depth;
		}

		@Override
		public int compareTo(Record another) {
			return another.depth - depth;
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			feature = Feature.valueOf(bundle.getString(FEATURE));
			depth = bundle.getInt(DEPTH);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put(FEATURE, feature.toString());
			bundle.put(DEPTH, depth);
		}
	}

	public static ArrayList<Record> records;

	public static void reset() {
		records = new ArrayList<Journal.Record>();
	}

	private static final String JOURNAL = "journal";

	public static void storeInBundle(Bundle bundle) {
		bundle.put(JOURNAL, records);
	}

	public static void restoreFromBundle(Bundle bundle) {
		records = new ArrayList<Record>();
		for (Bundlable rec : bundle.getCollection(JOURNAL)) {
			records.add((Record) rec);
		}
	}

	public static void add(Feature feature) {
		int size = records.size();
		for (int i = 0; i < size; i++) {
			Record rec = records.get(i);
			if (rec.feature == feature && rec.depth == Dungeon.depth) {
				return;
			}
		}

		records.add(new Record(feature, Dungeon.depth));
	}

	public static void remove(Feature feature) {
		int size = records.size();
		for (int i = 0; i < size; i++) {
			Record rec = records.get(i);
			if (rec.feature == feature && rec.depth == Dungeon.depth) {
				records.remove(i);
				return;
			}
		}
	}
}
