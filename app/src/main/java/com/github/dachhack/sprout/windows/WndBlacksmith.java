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

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Chrome;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.npcs.Blacksmith;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.ui.ItemSlot;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class WndBlacksmith extends Window {

	private static final int BTN_SIZE = 36;
	private static final float GAP = 2;
	private static final float BTN_GAP = 10;
	private static final int WIDTH = 116;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnReforge;

	private static final String TXT_PROMPT =
//			"Ok, a deal is a deal, dat's what I can do for you: I can reforge "
			Messages.get(WndBlacksmith.class, "prompt");
//			+ "any 2 items and turn them into one of a better quality. "
//			+ "The first item will get some or all of the upgrades from the second. "
//			+ "The second item will be destroyed. "
//			+ "I'm more successful when you bring me lots of dark gold. ";
	private static final String TXT_SELECT1 =  Messages.get(WndBlacksmith.class, "select1");
	private static final String TXT_SELECT2 = Messages.get(WndBlacksmith.class, "select2");
	private static final String TXT_REFORGE = (Messages.get(WndBlacksmith.class, "reforge"));

	public WndBlacksmith(Blacksmith troll, Hero hero) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(troll.sprite());
		titlebar.label(Utils.capitalize(troll.name));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline message = PixelScene.renderMultiline(Messages.get(WndBlacksmith.class, "prompt"), 6);
		message.maxWidth = WIDTH;
		message.setPos(0, titlebar.bottom() + GAP);
		add(message);

		btnItem1 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem1;
				GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLESIMPLE,
						TXT_SELECT1);
			}
		};
		btnItem1.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE);
		add(btnItem1);

		btnItem2 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem2;
				GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLESIMPLE,
						TXT_SELECT2);
			}
		};
		btnItem2.setRect(btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE,
				BTN_SIZE);
		add(btnItem2);

		btnReforge = new RedButton(TXT_REFORGE) {
			@Override
			protected void onClick() {
				Blacksmith.upgrade(btnItem1.item, btnItem2.item);
				hide();
			}
		};
		btnReforge.enable(false);
		btnReforge.setRect(0, btnItem1.bottom() + BTN_GAP, WIDTH, 20);
		add(btnReforge);

		resize(WIDTH, (int) btnReforge.bottom());
	}

	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				btnPressed.item(item);

				if (btnItem1.item != null && btnItem2.item != null) {
					String result = Blacksmith.verify(btnItem1.item,
							btnItem2.item);
					if (result != null) {
						GameScene.show(new WndMessage(result));
						btnReforge.enable(false);
					} else {
						btnReforge.enable(true);
					}
				}
			}
		}
	};

	public static class ItemButton extends Component {

		protected NinePatch bg;
		protected ItemSlot slot;

		public Item item = null;

		@Override
		protected void createChildren() {
			super.createChildren();

			bg = Chrome.get(Chrome.Type.BUTTON);
			add(bg);

			slot = new ItemSlot() {
				@Override
				protected void onTouchDown() {
					bg.brightness(1.2f);
					Sample.INSTANCE.play(Assets.SND_CLICK);
				};

				@Override
				protected void onTouchUp() {
					bg.resetColor();
				}

				@Override
				protected void onClick() {
					ItemButton.this.onClick();
				}
			};
			add(slot);
		}

		protected void onClick() {
		};

		@Override
		protected void layout() {
			super.layout();

			bg.x = x;
			bg.y = y;
			bg.size(width, height);

			slot.setRect(x + 2, y + 2, width - 4, height - 4);
		};

		public void item(Item item) {
			slot.item(this.item = item);
		}
	}
}
