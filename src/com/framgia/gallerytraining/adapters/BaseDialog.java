package com.framgia.gallerytraining.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;

public class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.WHITE));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Rect displayRectangle = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(
				displayRectangle);
		int mWith = (int) (displayRectangle.width() * 0.9);
		getWindow().setLayout(mWith, LayoutParams.WRAP_CONTENT);
	}

}
