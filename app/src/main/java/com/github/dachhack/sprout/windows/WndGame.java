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
import com.github.dachhack.sprout.ShatteredPixelDungeon;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.scenes.InterlevelScene;
import com.github.dachhack.sprout.scenes.RankingsScene;
import com.github.dachhack.sprout.scenes.TitleScene;
import com.github.dachhack.sprout.ui.GameLog;
import com.github.dachhack.sprout.ui.Icons;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.Window;
import com.watabou.noosa.Game;

import java.io.IOException;

public class WndGame extends Window {

//	private static final String TXT_SETTINGS = "Settings";
//	private static final String TXT_CHALLEGES = "Challenges";
//	private static final String TXT_RANKINGS = "Rankings";
//	private static final String TXT_START = "Start New Game";
//	private static final String TXT_MENU = "Main Menu";
//	private static final String TXT_EXIT = "Exit Game";
//	private static final String TXT_RETURN = "Return to Game";
private static final String TXT_SETTINGS = Messages.get(WndGame.class, "settings");
	private static final String TXT_CHALLEGES = Messages.get(WndGame.class, "challenges");
	private static final String TXT_RANKINGS = Messages.get(WndGame.class, "rankings");
	private static final String TXT_START = Messages.get(WndGame.class, "start");
	private static final String TXT_MENU = Messages.get(WndGame.class, "menu");
	private static final String TXT_EXIT = Messages.get(WndGame.class, "exit");
	private static final String TXT_RETURN = Messages.get(WndGame.class, "return");

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP = 2;

	private int pos;

	public WndGame() {

		super();

		addButton(new RedButton(Messages.get(WndGame.class, "settings")) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndSettings());
			}
		});

		// Challenges window
		if (Dungeon.challenges > 0) {
			addButton(new RedButton(Messages.get(WndGame.class, "challenges")) {
				@Override
				protected void onClick() {
					hide();
					GameScene
							.show(new WndChallenges(Dungeon.challenges, false));
				}
			});
		}

		// Restart
		if (!Dungeon.hero.isAlive()) {

			RedButton btnStart;
			addButton(btnStart = new RedButton(Messages.get(WndGame.class, "start")) {
				@Override
				protected void onClick() {
					Dungeon.hero = null;
					ShatteredPixelDungeon.challenges(Dungeon.challenges);
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					InterlevelScene.noStory = true;
					Game.switchScene(InterlevelScene.class);
				}
			});
			btnStart.icon(Icons.get(Dungeon.hero.heroClass));

			addButton(new RedButton(Messages.get(WndGame.class, "rankings")) {
				@Override
				protected void onClick() {
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene(RankingsScene.class);
				}
			});
		}



		addButtons(
		// Main menu
				new RedButton(Messages.get(WndGame.class, "menu")) {
					@Override
					protected void onClick() {
						GameLog.wipe();
						try {
							Dungeon.saveAll();
						} catch (IOException e) {
							// Do nothing
						}
						Game.switchScene(TitleScene.class);
					}
				},
				// Quit
				new RedButton(Messages.get(WndGame.class, "exit")) {
					@Override
					protected void onClick() {
						Game.instance.finish();
					}
				});

		// Cancel
		addButton(new RedButton(Messages.get(WndGame.class, "return")) {
			@Override
			protected void onClick() {
				hide();
			}
		});

		resize(WIDTH, pos);
	}

	private void addButton(RedButton btn) {
		add(btn);
		btn.setRect(0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT);
		pos += BTN_HEIGHT;
	}

	private void addButtons(RedButton btn1, RedButton btn2) {
		add(btn1);
		btn1.setRect(0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT);
		add(btn2);
		btn2.setRect(btn1.right() + GAP, btn1.top(),
				WIDTH - btn1.right() - GAP, BTN_HEIGHT);
		pos += BTN_HEIGHT;
	}
}
