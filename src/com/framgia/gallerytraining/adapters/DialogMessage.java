package com.framgia.gallerytraining.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.framgia.gallerytraining.R;

public class DialogMessage extends BaseDialog {

	public DialogMessage(Context context, String title, String message) {
		super(context);
		setContentView(R.layout.layout_dialog_detail);
		TextView tvTitle, tvMessage, tvBtnClose;
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvMessage = (TextView) findViewById(R.id.tvDetailMessage);
		tvBtnClose = (TextView) findViewById(R.id.tvButtonClose);
		tvBtnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage.this.dismiss();
			}
		});

		tvTitle.setText(title);
		tvMessage.setText(message);
	}

	public DialogMessage(Context context, String title, String message,
			View.OnClickListener callBackClickSubmit) {
		super(context);
		setContentView(R.layout.layout_dialog_set);
		TextView tvTitle, tvMessage, tvBtnClose, tvBtnSubmit;
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvMessage = (TextView) findViewById(R.id.tvDetailMessage);
		tvBtnClose = (TextView) findViewById(R.id.tvButtonClose);
		tvBtnSubmit = (TextView) findViewById(R.id.tvButtonSubmit);

		tvBtnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage.this.dismiss();
			}
		});
		tvBtnSubmit.setOnClickListener(callBackClickSubmit);
		tvTitle.setText(title);
		tvMessage.setText(message);
	}

}
