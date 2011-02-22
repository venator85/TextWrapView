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

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Resizes a bitmap so that its lesser size equals the given size
 * and then center-crops it.
 * 
 * @author Alessio Bianchi (venator85)
 */
public class BitmapCropper {
	public static Bitmap centerCropBitmap(Bitmap bitmap, int newW, int newH) {
		int h_orig = bitmap.getHeight();
		int w_orig = bitmap.getWidth();

		Bitmap ret = bitmap;
		if (h_orig < w_orig) {
			float scaleHeight = ((float) newH) / h_orig;
			float scaleWidth = scaleHeight;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			ret = Bitmap.createBitmap(bitmap, 0, 0, w_orig, h_orig, matrix, true);

			int x = (ret.getWidth() - newW) / 2;
			int y = 0;

			ret = Bitmap.createBitmap(ret, x, y, newW, newH, null, true);
		} else {
			float scaleWidth = ((float) newW) / w_orig;
			float scaleHeight = scaleWidth;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			ret = Bitmap.createBitmap(bitmap, 0, 0, w_orig, h_orig, matrix, true);

			int x = 0;
			int y = (ret.getHeight() - newH) / 2;

			ret = Bitmap.createBitmap(ret, x, y, newW, newH, null, true);
		}
		return ret;
	}
}
