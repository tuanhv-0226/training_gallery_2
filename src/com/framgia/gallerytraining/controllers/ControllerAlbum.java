package com.framgia.gallerytraining.controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.framgia.gallerytraining.models.ListPictures;
import com.framgia.gallerytraining.models.Picture;

public class ControllerAlbum {

	private Context abContext;

	public ControllerAlbum(Context abContext) {
		super();
		this.abContext = abContext;
	}

	//#P1 - fetch list pictures of album
	public ListPictures fetchListPictures(String bucketName) {
		ListPictures listPictures = new ListPictures();

		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String searchParams = null;
			String bucket = bucketName;
			searchParams = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = \"" + bucket + "\"";

			Uri[] images = { MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI };
			for (Uri uri : images) {
				Cursor picCursor = abContext.getContentResolver().query(uri, null,
											searchParams, null, orderBy + " DESC");
				Log.v("ListingImages", " query images count = " + picCursor.getCount());
				if (picCursor != null) {
					int bucketIdColumn = picCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
					int bucketColumn = picCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
					int dateColumn = picCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
					int dataColumn = picCursor.getColumnIndex(MediaStore.Images.Media.DATA);
					int sizeColumn = picCursor.getColumnIndex(MediaStore.Images.Media.SIZE);

					while (picCursor.moveToNext()) {
						Picture pic = new Picture();
						pic.setIdPicture(picCursor.getInt(bucketIdColumn));
						pic.setNamePicture(picCursor.getString(bucketColumn));
						pic.setDatePicture(picCursor.getString(dateColumn));
						pic.setDataPicture(picCursor.getString(dataColumn));
						pic.setSizePicture(picCursor.getInt(sizeColumn));
						listPictures.addPicture(pic);
						Log.v("ListingImages", " bucket=" + pic.getNamePicture()
								+ " bucket_id=" + pic.getIdPicture()
								+ " date_taken=" + pic.getDatePicture()
								+ " size=" + pic.getSizePicture()
								+ " data=" + pic.getDataPicture());
					}
					picCursor.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listPictures;
	}

	//#P8
	public void sortedAlbum(ListPictures listPic, String orderBy) {
		listPic.sortedListPictures(orderBy);
	}

	//#P2
	public void retrieveDetailPic(Picture pic) {

	}

	//#P5
	public void shareListPicture(ListPictures listPic) {

	}

	//#P6
	public void setBackground(Picture pic) {

	}

}
