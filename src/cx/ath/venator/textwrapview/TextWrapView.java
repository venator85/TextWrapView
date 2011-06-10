/***
	Copyright (c) 2008-2009 Alessio Bianchi (venator85)
	Portions (c) 2009 Google, Inc.
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package cx.ath.venator.textwrapview;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Provides a simple TextView-like control which allows us to set a max number
 * of lines to wrap an input string, then ellipsizes the last line if there's
 * not enough room to handle the entire input string.
 * 
 * The basis for this widget is taken from the android custom widget doc:
 *    http://developer.android.com/guide/samples/ApiDemos/src/com/example/android/apis/view/LabelView.html
 * 
 * @author Alessio Bianchi (venator85)
 */
public class TextWrapView extends View {
	private TextPaint textPaint;
	private String text;
	private int maxLines;
	private TextBreaker textBreaker;

	public TextWrapView(Context context) {
		super(context);
		init();
	}

	private void init() {
		maxLines = -1;

		textBreaker = new TextBreaker();

		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(14);
		textPaint.setColor(0xff000000);
		textPaint.setTextAlign(Align.LEFT);
	}
	
	private void clearCache() {
		setDrawingCacheEnabled(false);
	}
	
	public TextWrapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextWrapView);
		String s;

		s = a.getString(R.styleable.TextWrapView_text);
		if (s != null)
			setText(s);

		final float scale = getContext().getResources().getDisplayMetrics().density;
		setTextSize(a.getDimensionPixelSize(R.styleable.TextWrapView_textSize, (int) (14 * scale)));

		setTextColor(a.getInt(R.styleable.TextWrapView_textColor, 0xff000000));

		setMaxLines(a.getInt(R.styleable.TextWrapView_maxLines, 2));
		
		int typeface = a.getInt(R.styleable.TextWrapView_typeface, -1);
		if (typeface == 0)
			textPaint.setTypeface(Typeface.DEFAULT);
		else if (typeface == 1)
			textPaint.setTypeface(Typeface.SANS_SERIF);
		else if (typeface == 2)
			textPaint.setTypeface(Typeface.SERIF);
		else if (typeface == 3)
			textPaint.setTypeface(Typeface.MONOSPACE);
		
		a.recycle();
	}

	public void setText(String text) {
		this.text = text;
		clearCache();
		requestLayout();
		invalidate();
	}

	public void setTextSize(int size) {
		textPaint.setTextSize(size);
		clearCache();
		requestLayout();
		invalidate();
	}

	public void setTextColor(int color) {
		textPaint.setColor(color);
		clearCache();
		invalidate();
	}

	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
		clearCache();
		requestLayout();
		invalidate();
	}
	
	public void setTypeface(Typeface typeface) {
		textPaint.setTypeface(typeface);
		clearCache();
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be.
			result = specSize;
			// Format the text using this exact width, and the current mode.
			breakWidth(specSize);
		} else {
			if (specMode == MeasureSpec.AT_MOST) {
				// Use the AT_MOST size - if we had very short text, we may need
				// even less than the AT_MOST value, so return the minimum.
				result = breakWidth(specSize);
				result = Math.min(result, specSize);
			} else {
				// We're not given any width - so in this case we assume we have
				// an unlimited width?
				breakWidth(specSize);
			}
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be, so nothing to do.
			result = specSize;
		} else {
			// The lines should already be broken up. Calculate our max desired height for our current mode.
			int numLines = textBreaker.getLines().size();
			float lineHeight = -textPaint.ascent() + textPaint.descent();
			result = (int) (numLines * lineHeight + getPaddingTop() + getPaddingBottom());

			// Respect AT_MOST value if that was what is called for by measureSpec.
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	private int breakWidth(int availableWidth) {
		int maxW = availableWidth - getPaddingLeft() - getPaddingRight();
		textBreaker.setMaxWidthLines(maxW, maxLines);
		return (int) Math.ceil(textBreaker.breakText(text, textPaint)) + getPaddingLeft() + getPaddingRight();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		List<String> lines = textBreaker.getLines();

		float x = getPaddingLeft();
		float y = getPaddingTop() - textPaint.ascent();
		
		float lineHeight = -textPaint.ascent() + textPaint.descent();
		
		for (int i = 0; i < lines.size(); i++) {
			// Draw the current line
			canvas.drawText(lines.get(i), x, y, textPaint);
			y += lineHeight;
			if (y > canvas.getHeight()) {
				break;
			}
		}
		
		//enable drawing cache, onDraw() won't be called again until invalidate() is invoked
		setDrawingCacheEnabled(true);
	}
}