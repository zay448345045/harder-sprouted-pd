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

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.effects.BadgeBanner;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.watabou.noosa.Image;

public class WndBadge extends Window {

	private static final int WIDTH = 120;
	private static final int MARGIN = 4;

	public WndBadge( Badges.Badge badge ) {

		super();

		Image icon = BadgeBanner.image( badge.image );
		icon.scale.set( 2 );
		add( icon );

		//TODO: this used to be centered, should probably figure that out.
		RenderedTextMultiline info = PixelScene.renderMultiline( badge.description, 8 );
		info.maxWidth(WIDTH - MARGIN * 2);
		PixelScene.align(info);
		add(info);

		float w = Math.max( icon.width(), info.width() ) + MARGIN * 2;

		icon.x = (w - icon.width()) / 2f;
		icon.y = MARGIN;
		PixelScene.align(icon);

		info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
		resize( (int)w, (int)(info.bottom() + MARGIN) );

		BadgeBanner.highlight( icon, badge.image );
	}
}