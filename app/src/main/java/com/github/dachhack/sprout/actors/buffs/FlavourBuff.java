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
package com.github.dachhack.sprout.actors.buffs;

import java.text.DecimalFormat;

//buff whose only logic is to wait and detach after a time.
public class FlavourBuff extends Buff {

	@Override
	public boolean act() {
		detach();
		return true;
	}

	public int dispTurnsInt() {
		//add one turn as buffs act last, we want them to end at 1 visually, even if they end at 0 internally.
		float visualTurnsLeft = cooldown() + 1f;
		return (int) visualTurnsLeft;
	}

	//flavour buffs can all just rely on cooldown()
	public String dispTurns() {
		//add one turn as buffs act last, we want them to end at 1 visually, even if they end at 0 internally.
		float visualTurnsLeft = cooldown() + 1f;
		return visualTurnsLeft == 1 ? "1" : new DecimalFormat("#.##").format(visualTurnsLeft);
	}
}
