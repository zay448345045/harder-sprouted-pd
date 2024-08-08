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
package com.github.dachhack.sprout.actors.mobs;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Charm;
import com.github.dachhack.sprout.actors.buffs.Light;
import com.github.dachhack.sprout.actors.buffs.Sleep;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.food.Meat;
import com.github.dachhack.sprout.items.scrolls.ScrollOfLullaby;
import com.github.dachhack.sprout.items.wands.WandOfBlink;
import com.github.dachhack.sprout.items.weapon.enchantments.Leech;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.mechanics.Ballistica;
import com.github.dachhack.sprout.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Succubus extends Mob {

	private static final int BLINK_DELAY = 5;

	private int delay = 0;

	{
		name = Messages.get(this, "name");
		spriteClass = SuccubusSprite.class;

		HP = HT = 300;
		viewDistance = Light.DISTANCE;

		EXP = 12;
		maxLvl = 25;

		loot = new ScrollOfLullaby();
		lootChance = 0.05f;
		
		lootOther = new Meat();
		lootChanceOther = 0.1f; // by default, see die()
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(30, 90);
	}

	@Override
	public int attackProc(Char enemy, int damage) {

		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Charm.class, Charm.durationFactor(enemy)
					* Random.IntRange(3, 7)).object = id();
			enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART),
					0.2f, 5);
			Sample.INSTANCE.play(Assets.SND_CHARMS);
		}

		return damage;
	}

	@Override
	protected boolean getCloser(int target) {
		if (Level.fieldOfView[target] && Level.distance(pos, target) > 2
				&& delay <= 0) {

			blink(target);
			spend(-1 / speed());
			return true;

		} else {

			delay--;
			return super.getCloser(target);

		}
	}

	private void blink(int target) {

		int cell = Ballistica.cast(pos, target, true, true);
		
		if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}
		
       if (!Level.pit[cell]){
		WandOfBlink.appear(this, cell);
       }

		delay = BLINK_DELAY;
	}

	@Override
	public int attackSkill(Char target) {
		return 40+ adjustForDepth(1);
	}

	@Override
	public int dr() {
		return 10+ adjustForDepth(1);
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Leech.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Sleep.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
