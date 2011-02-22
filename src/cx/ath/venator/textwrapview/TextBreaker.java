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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.text.TextPaint;

/**
 * Breaks an input string in multiple chunks given their size in pixels.
 * 
 * @author Alessio Bianchi (venator85)
 * @author Text breaking algorithm by Andrea Gambitta
 */
public class TextBreaker {
	private List<String> mLines;
	private int[] maxWidths;
	private int maxWidth, maxLines;

	public int[] getMaxWidths() {
		return maxWidths;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	
	public int getMaxLines() {
		return maxLines;
	}

	public void setMaxWidths(int[] maxWidths) {
		this.maxWidths = maxWidths;
		this.maxLines = maxWidths.length;
		this.maxWidth = -1;
	}

	public void setMaxWidthLines(int maxWidth, int maxLines) {
		this.maxWidth = maxWidth;
		this.maxLines = maxLines;
		this.maxWidths = null;
	}
	
	private int getLineWidth(int i) {
		if (maxWidths != null)
			return maxWidths[i];
		else
			return maxWidth;
	}

	public void breakText(String input, TextPaint tp) {
		mLines = new ArrayList<String>();
		if (input == null) return;
		StringTokenizer st = new StringTokenizer(input, "\n");
		while (st.hasMoreTokens()) {
			int moreLines = maxLines - mLines.size();
			if (moreLines == 0)
				break;
			String line = st.nextToken();
			mLines.addAll(breakLine(line, tp));
		}
	}

	private List<String> breakLine(String input, TextPaint tp) {
		List<String> lines = new ArrayList<String>();
		if (maxLines == 0)
			return lines;
		boolean lastLines = (maxLines == 1);
		String line = "", DOTS = "...";

		for (String word : input.split(" ")) {
			// word fits line, append it
			if (tp.measureText(line + " " + word + (lastLines ? DOTS : "")) <= getLineWidth(lines.size()))
				line += (!line.equals("") ? " " : "") + word;

			// word doesn't fit and it's the last line
			else if (lastLines) {
				line += DOTS;
				break;
			}

			// word doesn't fit, use it in the next line
			else if (tp.measureText(word) <= getLineWidth(lines.size())) {
				lines.add(line);
				lastLines = (lines.size() == maxLines - 1);
				line = word;
			}

			// word doesn't fit but is too large for entire next line, hard word-wrap it
			else {
				int j = word.length();
				while (tp.measureText(line + " " + word.substring(0, j)) > getLineWidth(lines.size()))
					j--;

				line += (!line.equals("") ? " " : "") + word.substring(0, j);
				lines.add(line);
				lastLines = (lines.size() == maxLines - 1);

				String resto = word.substring(j);

				while (tp.measureText(resto + (lastLines ? DOTS : "")) > getLineWidth(lines.size())) {
					j = resto.length();
					while (tp.measureText(resto.substring(0, j) + (lastLines ? DOTS : "")) > getLineWidth(lines.size()))
						j--;

					if (lastLines) {
						resto = resto.substring(0, j);
						break;
					} else {
						lines.add(resto.substring(0, j));
						lastLines = (lines.size() == maxLines - 1);
						resto = resto.substring(j);
					}
				}
				line = resto;
			}
		}

		if (line.length() != 0)
			lines.add(line);
		return lines;
	}

	public List<String> getLines() {
		return mLines;
	}
}