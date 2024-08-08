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

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.github.dachhack.sprout.utils.Utils;

public class WndDewVial extends Window {
	
	//if people don't get it after this, I quit. I just quit.

//	private static final String TXT_MESSAGE = "";
//
//	private static final String TXT_WATER = "Okay! Let's go find 100 dew drops!";
private static final String TXT_MESSAGE = Messages.get(WndDewVial.class, "msg");
	private static final String TXT_WINDOW = Messages.get(WndDewVial.class, "window");
	private static final String TXT_DRINK = Messages.get(WndDewVial.class, "drink");
	private static final String TXT_WATER = Messages.get(WndDewVial.class, "water");
	private static final String TXT_SPLASH = Messages.get(WndDewVial.class, "splash");
	private static final String TXT_BLESS = Messages.get(WndDewVial.class, "bless");
	private static final String TXT_OTHER = Messages.get(WndDewVial.class, "other");


	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final float GAP = 2;

	public WndDewVial(final Item item) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Utils.capitalize(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline message = PixelScene
				.renderMultiline(TXT_MESSAGE, 6);
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add(message);

		RedButton btnBattle = new RedButton(TXT_WATER) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnBattle.setRect(0, message.top() + message.height() + GAP, WIDTH,
				BTN_HEIGHT);
		add(btnBattle);



		resize(WIDTH, (int) btnBattle.bottom());
	}


}
