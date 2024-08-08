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
package com.github.dachhack.sprout.actors.hero;

import com.github.dachhack.sprout.Messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroSubClass {

	NONE(null),
	GLADIATOR("gladiator"),
	BERSERKER("berserker"),

	WARLOCK("warlock"),
	BATTLEMAGE("battlemage"),

	ASSASSIN("assassin"),
	FREERUNNER("freerunner"),

	SNIPER("sniper"),
	WARDEN("warden");

//	GLADIATOR(
//			"gladiator",
//			"A successful attack with a melee weapon allows the _Gladiator_ to start a combo, "
//					+ "in which every next successful hit inflicts more damage."),
//	BERSERKER(
//			"berserker",
//			"When severely wounded, the _Berserker_ enters a state of wild fury "
//					+ "significantly increasing his damage output."),
//
//	WARLOCK("warlock",
//			"When attacking with wands, the _Warlock_ may mark their soul. "
//					+ "When this enemy takes physical damage, it will replenish the Warlock's health and hunger."),
//	BATTLEMAGE(
//			"battlemage",
//			"When fighting with a wand in his hands, the _Battlemage_ inflicts additional effects depending "
//					+ "on the wand's type. Every successful hit restores 1 charge to this wand."),
//
//	ASSASSIN(
//			"assassin",
//			"When performing a surprise attack, the _Assassin_ inflicts additional damage to his target."),
//	FREERUNNER(
//			"freerunner",
//			"The _Freerunner_ moves faster when he unencumbered and not starving, "
//					+ "if he is invisible, this speed boost is much stronger."),
//
//	SNIPER("sniper",
//			"_Snipers_ are able to detect weak points in an enemy's armor, "
//					+ "effectively ignoring it when using a missile weapon."),
//	WARDEN(
//			"warden",
//			"Having a strong connection with forces of nature gives _Wardens_ more powerful effects when stepping on plants. "
//					+ "Also trampling a high grass grants them a temporary armor buff.");

	private String title;

	public String title() {
		return Messages.get(this, title);
	}

	public String desc() {
		return Messages.get(this, title + "_desc");
	}

	HeroSubClass(String title) {
		this.title = title;
	}

	private static final String SUBCLASS = "subClass";

	public void storeInBundle(Bundle bundle) {
		bundle.put(SUBCLASS, toString());
	}

	public static HeroSubClass restoreInBundle(Bundle bundle) {
		String value = bundle.getString(SUBCLASS);
		return valueOf(value);
	}

}
