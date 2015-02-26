package com.framgia.gallerytraining.controllers;

import java.io.IOException;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.framgia.gallerytraining.models.Picture;
import com.framgia.gallerytraining.utils.ImageResizer;

public class ControllerPicture {

	private Context pContext;

	public ControllerPicture(Context pContext) {
		super();
		this.pContext = pContext;
	}

	//#P7 - fetch picture
	public void fetchPicture() {

	}

	//#P4 - zoom picture
	public void zoomPicture() {

	}

	//#P2 - show detail picture
	public void showDetailPicture() {

	}

	//#P5 - share picture
	public void sharePicture(Picture pic) {
		Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pic.getDataPicture());
        shareIntent.setType("image/*");
        pContext.startActivity(Intent.createChooser(shareIntent, "Share"));
	}

	//#P6 - set background
	public void setBackground(Picture pic, int width, int height) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pic.getDataPicture().toString(), options);

        // Calculate inSampleSize
        options.inSampleSize = ImageResizer.calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(pic.getDataPicture().toString(), options);

        WallpaperManager wm = WallpaperManager.getInstance(pContext);
        try {
            wm.setBitmap(decodedSampleBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
