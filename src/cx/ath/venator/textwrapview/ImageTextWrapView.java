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

import cx.ath.venator.textwrapview.R;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Provides an ImageView + TextView-like control which allows us to show an image,
 * a title at its right and a text flowing below it. The image size can be expressed
 * in term of title+text lines or as an exact dimension. The bitmap is center-cropped
 * by the BitmapCropper class. The text length is expressed in term of lines and it
 * is ellipsized.
 * 
 * The basis for this widget is taken from the android custom widget doc:
 *    http://developer.android.com/guide/samples/ApiDemos/src/com/example/android/apis/view/LabelView.html
 * 
 * @author Alessio Bianchi (venator85)
 */
public class ImageTextWrapView extends View {
	private int titleTextPadding = 5;
	private int imagePadding = 5;
	
	private TextPaint titlePaint;
	private TextPaint textPaint;
	
	private String title;
	private TextBreaker titleBreaker;

	private String text;
	private int textMaxLines = -1;
	private TextBreaker textBreaker;
	
	private Bitmap bitmap, croppedBitmap;
	private int imgWidthInPixel = -1;
	private int imgHeightInPixel = -1;
	private int imgSizeInLines = -1;
	private int tabbedTextLines = 0;
	private int tabbedTitleLines;
	
	private Object tag;
	private int imgSizeMode;
	
	public ImageTextWrapView(Context context) {
		super(context);
		init();
	}

	private void init() {
		titleBreaker = new TextBreaker();
		textBreaker = new TextBreaker();

		titlePaint = new TextPaint();
		titlePaint.setAntiAlias(true);
		titlePaint.setTextSize(14);
		titlePaint.setColor(0xff0000ff);
		titlePaint.setTextAlign(Align.LEFT);
//		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(14);
		textPaint.setColor(0xff000000);
		textPaint.setTextAlign(Align.LEFT);
	}
	
	public ImageTextWrapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		int defaultFontSize = (int) (14 * scale);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextWrapView);
		
		String s;
		s = a.getString(R.styleable.ImageTextWrapView_title);
		if (s != null) setTitle(s);
		setTitleSize(a.getDimensionPixelSize(R.styleable.ImageTextWrapView_titleSize, defaultFontSize));
		setTitleColor(a.getInt(R.styleable.ImageTextWrapView_titleColor, 0xff000000));
		setTitleStyle(a.getInt(R.styleable.ImageTextWrapView_titleStyle, 0));
		
		s = a.getString(R.styleable.ImageTextWrapView_text);
		if (s != null) setText(s);
		setTextSize(a.getDimensionPixelSize(R.styleable.ImageTextWrapView_textSize, defaultFontSize));
		setTextColor(a.getInt(R.styleable.ImageTextWrapView_textColor, 0xff000000));
		setTextStyle(a.getInt(R.styleable.ImageTextWrapView_textStyle, 0));
		setTextMaxLines(a.getInt(R.styleable.ImageTextWrapView_textMaxLines, 2));
		
		titleTextPadding = a.getInt(R.styleable.ImageTextWrapView_titleTextPadding, 5);
		imagePadding = a.getInt(R.styleable.ImageTextWrapView_imagePadding, 5);
		
		int v;
		imgSizeMode = a.getInt(R.styleable.ImageTextWrapView_imgSizeMode, 0);
		if (imgSizeMode == 0) {
			v = a.getInt(R.styleable.ImageTextWrapView_imgSizeInLines, -1);
			if (v != -1) setImgSizeInLines(v);
		} else if (imgSizeMode == 1) {
			v = a.getDimensionPixelSize(R.styleable.ImageTextWrapView_imgWidth, -1);
			if (v != -1) setImgWidth(v);
			v = a.getDimensionPixelSize(R.styleable.ImageTextWrapView_imgHeight, -1);
			if (v != -1) setImgHeight(v);
		}
		
		v = a.getResourceId(R.styleable.ImageTextWrapView_img, -1);
		if (v != -1) setBitmap(v);

		a.recycle();
	}
	
	public int getImgHeight() {
		return imgHeightInPixel;
	}
	
	public int getImgWidth() {
		return imgWidthInPixel;
	}
	
	public void setImgHeight(int imgHeight) {
		this.imgHeightInPixel = imgHeight;
		this.imgSizeInLines = -1;
		requestLayout();
		invalidate();
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidthInPixel = imgWidth;
		this.imgSizeInLines = -1;
		requestLayout();
		invalidate();
	}
	
	public void setImgSizeInLines(int lines) {
		this.imgSizeInLines = lines;
		this.imgHeightInPixel = -1;
		this.imgWidthInPixel = -1;
		requestLayout();
		invalidate();
	}
	
	public void setText(String text) {
		this.text = text;
		requestLayout();
		invalidate();
	}

	public void setTextSize(int size) {
		textPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	public void setTextColor(int color) {
		textPaint.setColor(color);
		invalidate();
	}

	public void setTextMaxLines(int maxLines) {
		textMaxLines = maxLines;
		requestLayout();
		invalidate();
	}
	
	public void setTitle(String title) {
		this.title = title;
		requestLayout();
		invalidate();
	}

	public void setTitleSize(int size) {
		titlePaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	public void setTitleColor(int color) {
		titlePaint.setColor(color);
		invalidate();
	}
	
	public void setTitleStyle(int style) {
		if (style == 0) // normal
			titlePaint.setTypeface(Typeface.DEFAULT);
		else if (style == 1) // bold
			titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		else if (style == 2) // italic
			// BUG: doesn't work
			titlePaint.setTypeface(Typeface.create(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.ITALIC));
		else if (style == 3) // bold+italic
			// BUG: doesn't work
			titlePaint.setTypeface(Typeface.create(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.BOLD_ITALIC));
		requestLayout();
		invalidate();
	}
	
	public void setTextStyle(int style) {
		if (style == 0) // normal
			textPaint.setTypeface(Typeface.DEFAULT);
		else if (style == 1) // bold
			textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		else if (style == 2) // italic
			// BUG: doesn't work
			textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
		else if (style == 3) // bold+italic
			// BUG: doesn't work
			textPaint.setTypeface(Typeface.create(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.BOLD_ITALIC));
		requestLayout();
		invalidate();
	}
	
	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public void setBitmap(int imgId) {
		this.bitmap = BitmapFactory.decodeResource(getResources(), imgId);
		requestLayout();
		invalidate();
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = measureWidth(widthMeasureSpec);
		int h = measureHeight(heightMeasureSpec);
		setMeasuredDimension(w, h);
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
			int numTitleLines = titleBreaker.getLines().size();
			float titleLineHeight = -textPaint.ascent() + titlePaint.descent();
			int titleBlockHeight = (int) (numTitleLines * titleLineHeight);

			int numTextLines = textBreaker.getLines().size();
			float textLineHeight = -titlePaint.ascent() + textPaint.descent();
			int textBlockHeight = (int) (numTextLines * textLineHeight);
			
			result = getPaddingTop() + titleBlockHeight + titleTextPadding + textBlockHeight + getPaddingBottom();
			
			if (imgSizeMode == 0) { // calculate height and width from title/text
				imgHeightInPixel = 0;
				if (numTitleLines < imgSizeInLines) {
					// title lines are not enough, also add title-text padding and remaining lines from text
					imgHeightInPixel = (int) (numTitleLines * titleLineHeight + titleTextPadding);
					imgHeightInPixel += (imgSizeInLines - numTitleLines) * textLineHeight;
				} else {
					// title lines are enough
					imgHeightInPixel = (int) (imgSizeInLines * titleLineHeight);
				}
				imgHeightInPixel -= imagePadding;
				imgWidthInPixel = imgHeightInPixel;
			}
			// Image alone may be higher than title+text combined, => calculate its height
			result = Math.max(result, getPaddingTop() + imgHeightInPixel + getPaddingBottom());

			// Respect AT_MOST value if that was what is called for by measureSpec.
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	private int breakWidth(int availableWidth) {
		float titleLineHeight = -titlePaint.ascent() + titlePaint.descent();
		float textLineHeight = -textPaint.ascent() + textPaint.descent();
		
		if (imgSizeMode == 0 && (imgHeightInPixel == -1 || imgWidthInPixel == -1)) {
			// We don't know the exact size of the image yet, so approximate it
			imgHeightInPixel = (int) (titleLineHeight * imgSizeInLines);
			imgWidthInPixel = imgHeightInPixel;
		}
		
		int fullWidth = availableWidth - getPaddingLeft() - getPaddingRight();
		int tabbedWidth = (int) (fullWidth - (imgWidthInPixel + imagePadding));

		int imageHeightInTitleLines = (int) Math.ceil(imgHeightInPixel / titleLineHeight);
		
		int titleLines = 100;
		int[] titleMaxWidths = new int[titleLines];
		tabbedTitleLines = Math.min(titleLines, imageHeightInTitleLines);
		Arrays.fill(titleMaxWidths, 0, tabbedTitleLines, tabbedWidth);
		Arrays.fill(titleMaxWidths, tabbedTitleLines, titleLines, fullWidth);
		titleBreaker.setMaxWidths(titleMaxWidths);
		titleBreaker.breakText(title, titlePaint);
		
		int textLinesToTab = 0;
		float realTitleLinesHeight = titleBreaker.getLines().size() * titleLineHeight ;
		if ((realTitleLinesHeight + titleTextPadding) < imgHeightInPixel) {
			float d = imgHeightInPixel - (realTitleLinesHeight + titleTextPadding);
			textLinesToTab = (int) Math.ceil(d / textLineHeight);
		}
		
		int[] textMaxWidths = new int[textMaxLines];
		tabbedTextLines = Math.min(textLinesToTab, textMaxLines);
		Arrays.fill(textMaxWidths, 0, tabbedTextLines, tabbedWidth);
		Arrays.fill(textMaxWidths, tabbedTextLines, textMaxLines, fullWidth);
		textBreaker.setMaxWidths(textMaxWidths);
		textBreaker.breakText(text, textPaint);
		
		return availableWidth;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		List<String> titleLines = titleBreaker.getLines();
		List<String> textLines = textBreaker.getLines();

		Rect dst = new Rect(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + imgWidthInPixel, getPaddingTop() + imgHeightInPixel); 
		croppedBitmap = BitmapCropper.centerCropBitmap(bitmap, imgWidthInPixel, imgHeightInPixel);
		canvas.drawBitmap(croppedBitmap, null, dst, null);
		
		float x, y;
		float textLineHeight = -textPaint.ascent() + textPaint.descent();
		float titleLineHeight = -titlePaint.ascent() + titlePaint.descent();
		
		// Draw title
		y = getPaddingTop() + (-titlePaint.ascent());
		for (int i = 0; i < titleLines.size(); i++) {
			x = getPaddingLeft();
			if (i < tabbedTitleLines)
				x += imgWidthInPixel + imagePadding;
			
			String s = titleLines.get(i);
			canvas.drawText(s, x, y, titlePaint);
			y += titleLineHeight;
			if (y > canvas.getHeight()) {
				break;
			}
		}
		
		// Draw text
		y += titleTextPadding;
		for (int i = 0; i < textLines.size(); i++) {
			x = getPaddingLeft();
			if (i < tabbedTextLines)
				x += imgWidthInPixel + imagePadding;
			
			String s = textLines.get(i);
			canvas.drawText(s, x, y, textPaint);
			y += textLineHeight;
			if (y > canvas.getHeight()) {
				break;
			}
		}
	}
	
	
}
