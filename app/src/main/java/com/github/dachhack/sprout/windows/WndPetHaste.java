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
package com.github.dachhack.sprout.windows;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.mobs.pets.PET;
import com.github.dachhack.sprout.items.rings.RingOfHaste;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;

public class WndPetHaste extends Window {
	
	//if people don't get it after this, I quit. I just quit.

//	private static final String TXT_MESSAGE = "You can apply a portion of your haste buff to your pet. " +
//			                                  "This will limit your haste level to 10 but your pet will be able to keep up with you while you move. "
//			                                  +"You can unhaste your pet by removing your ring of haste. ";
//
//	private static final String TXT_YES = "Haste my Pet";
//	private static final String TXT_NO = "No Thanks";

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final float GAP = 2;

	public WndPetHaste(final PET pet, final RingOfHaste ring) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(pet.sprite());
		titlebar.label(Utils.capitalize(pet.name));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline message = PixelScene
				.renderMultiline(Messages.get(WndPetHaste.class, "msg"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add(message);

		RedButton btnBattle = new RedButton(Messages.get(WndPetHaste.class, "yes")) {
			@Override
			protected void onClick() {
				Dungeon.petHasteLevel=ring.level;
				//GLog.p("Your "+pet.name+" is moving faster!");
				GLog.p(Messages.get(WndPetHaste.class, "faster", pet.name));
				hide();
			}
		};
		btnBattle.setRect(0, message.top() + message.height() + GAP, WIDTH,
				BTN_HEIGHT);
		add(btnBattle);

		RedButton btnNonBattle = new RedButton(Messages.get(WndPetHaste.class, "no")+" "+ring.level+" "+pet.speed()) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		
		btnNonBattle.setRect(0, btnBattle.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnNonBattle);
		
		
		resize(WIDTH, (int) btnNonBattle.bottom());
	}

	
	
	
}
