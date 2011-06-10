package cx.ath.venator.textwrapview.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cx.ath.venator.textwrapview.ImageTextWrapView;
import cx.ath.venator.textwrapview.R;
import cx.ath.venator.textwrapview.TextWrapView;

public class DemoActivity extends Activity {
	private TextWrapView textwrapview1, textwrapview2;
	private ImageTextWrapView imagetextwrapview1, imagetextwrapview2;
	private TextView textview;

	private boolean state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imagetextwrapview1 = (ImageTextWrapView) findViewById(R.id.imagetextwrapview1);
		imagetextwrapview2 = (ImageTextWrapView) findViewById(R.id.imagetextwrapview2);

		textwrapview1 = (TextWrapView) findViewById(R.id.textwrapview1);
		textwrapview2 = (TextWrapView) findViewById(R.id.textwrapview2);

		textview = (TextView) findViewById(R.id.textview);
		textview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggle();
			}
		});
	}

	public void toggle() {
		int strId = state ? R.string.text4 : R.string.text5;
		int titleId = state ? R.string.title3 : R.string.title4;
		String str = getResources().getString(strId);
		String title = getResources().getString(titleId);
		imagetextwrapview1.setText(str);
		imagetextwrapview1.setTitle(title);
		imagetextwrapview2.setText(str);
		imagetextwrapview2.setTitle(title);
		textwrapview1.setText(str);
		textwrapview2.setTypeface(state ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
		state = !state;
	}
}