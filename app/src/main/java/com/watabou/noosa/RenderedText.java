
package com.watabou.noosa;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.watabou.gltextures.SmartTexture;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Texture;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class RenderedText extends Image {
	private int maxWidth = Integer.MAX_VALUE;
	protected boolean multiline = false;
	//Moved SHPD
	public void maxWidth(int maxWidth){
		if (this.maxWidth != maxWidth){
			this.maxWidth = maxWidth;
			multiline = true;
			text(text);
		}
	}

	private static Canvas canvas = new Canvas();
	private static Paint painter = new Paint();

	private static Typeface font;

	private static LinkedHashMap<String, CachedText> textCache =
			new LinkedHashMap<String, CachedText>(700, 0.75f, true) {
				private int cachedChars = 0;
				private final int MAX_CACHED = 1000;

				@Override
				public CachedText put(String key, CachedText value) {
					cachedChars += value.length;
					CachedText added = super.put(key, value);
					runGC();
					return added;
				}

				@Override
				public CachedText remove(Object key) {
					CachedText removed = super.remove(key);
					if (removed != null) {
						cachedChars -= removed.length;
						removed.texture.delete();
					}
					return removed;
				}

				@Override
				public void clear() {
					super.clear();
					cachedChars = 0;
				}

				private void runGC() {
					Iterator<Entry<String, CachedText>> it = this.entrySet().iterator();
					while (cachedChars > MAX_CACHED && it.hasNext()) {
						CachedText cached = it.next().getValue();
						if (cached.activeTexts.isEmpty()) it.remove();
					}
				}
			};

	private int size;
	private String text;
	private CachedText cache;

	private boolean needsRender = false;

	public RenderedText() {
		text = null;
	}

	public RenderedText(int size) {
		text = null;
		this.size = size;
	}

	public RenderedText(String text, int size) {
		this.text = text;
		this.size = size;

		needsRender = true;
		measure(this);
	}

	public void text(String text) {
		this.text = text;

		needsRender = true;
		measure(this);
	}

	public String text() {
		return text;
	}

	public void size(int size) {
		this.size = size;
		needsRender = true;
		measure(this);
	}

	public float baseLine() {
		return size * scale.y;
	}

	private static synchronized void measure(RenderedText r) {

		if (r.text == null || r.text.equals("")) {
			r.text = "";
			r.width = r.height = 0;
			r.visible = false;
			return;
		} else {
			r.visible = true;
		}

		painter.setTextSize(r.size);
		painter.setAntiAlias(true);

		if (font != null) {
			painter.setTypeface(font);
		} else {
			painter.setTypeface(Typeface.DEFAULT);
		}

		//paint outer strokes
		painter.setARGB(0xff, 0, 0, 0);
		painter.setStyle(Paint.Style.STROKE);
		painter.setStrokeWidth(r.size / 5f);

		r.width = (painter.measureText(r.text) + (r.size / 5f));
		r.height = (-painter.ascent() + painter.descent() + (r.size / 5f));
	}

	private static synchronized void render(RenderedText r) {
		r.needsRender = false;

		if (r.cache != null)
			r.cache.activeTexts.remove(r);

		String key = "text:" + r.size + " " + r.text;
		if (textCache.containsKey(key)) {
			r.cache = textCache.get(key);
			r.texture = r.cache.texture;
			r.frame(r.cache.rect);
			r.cache.activeTexts.add(r);
		} else {

			measure(r);

			if (r.width == 0 || r.height == 0)
				return;

			//bitmap has to be in a power of 2 for some devices (as we're using openGL methods to render to texture)
			Bitmap bitmap = Bitmap.createBitmap(Integer.highestOneBit((int) r.width) * 2, Integer.highestOneBit((int) r.height) * 2, Bitmap.Config.ARGB_4444);
			bitmap.eraseColor(0x00000000);

			canvas.setBitmap(bitmap);
			canvas.drawText(r.text, (r.size / 10f), r.size, painter);

			//paint inner text
			painter.setARGB(0xff, 0xff, 0xff, 0xff);
			painter.setStyle(Paint.Style.FILL);

			canvas.drawText(r.text, (r.size / 10f), r.size, painter);

			r.texture = new SmartTexture(bitmap, Texture.NEAREST, Texture.CLAMP, true);

			RectF rect = r.texture.uvRect(0, 0, (int) r.width, (int) r.height);
			r.frame(rect);

			r.cache = new CachedText();
			r.cache.rect = rect;
			r.cache.texture = r.texture;
			r.cache.length = r.text.length();
			r.cache.activeTexts = new HashSet<>();
			r.cache.activeTexts.add(r);
			textCache.put("text:" + r.size + " " + r.text, r.cache);
		}
	}

	@Override
	protected void updateMatrix() {
		super.updateMatrix();
		//the y value is set at the top of the character, not at the top of accents.
		Matrix.translate(matrix, 0, -Math.round((baseLine() * 0.15f) / scale.y));
	}

	@Override
	public void draw() {
		if (needsRender)
			render(this);
		if (texture != null)
			super.draw();
	}

	@Override
	public void destroy() {
		if (cache != null)
			cache.activeTexts.remove(this);
		super.destroy();
	}

	public static void clearCache() {
		for (CachedText cached : textCache.values()) {
			cached.texture.delete();
		}
		textCache.clear();
	}

	public static void reloadCache() {
		for (CachedText txt : textCache.values()) {
			txt.texture.reload();
		}
	}

	public static void setFont(String asset) {
		if (asset == null) font = null;
		else font = Typeface.createFromAsset(Game.instance.getAssets(), asset);
		clearCache();
	}

	public static Typeface getFont() {
		return font;
	}

	public void setPos(int i, float v) {
	}

	private static class CachedText {
		public SmartTexture texture;
		public RectF rect;
		public int length;
		public HashSet<RenderedText> activeTexts;
	}
}
