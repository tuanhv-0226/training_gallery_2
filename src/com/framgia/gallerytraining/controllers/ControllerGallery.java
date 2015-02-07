package com.framgia.gallerytraining.controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import com.framgia.gallerytraining.models.Album;
import com.framgia.gallerytraining.models.ListAlbums;

public class ControllerGallery {

	private Context mContext;

	public ControllerGallery(Context mContext) {
		super();
		this.mContext = mContext;
	}

	//#A1 - fetch list album
	public ListAlbums fetchListAlbums() {
		//retrieve list albums from device, both internal and external
		ListAlbums listAlbums = new ListAlbums();

		//which image properties are queried
		String[] PROJECTION_BUCKET = { ImageColumns.BUCKET_ID,
				ImageColumns.BUCKET_DISPLAY_NAME,
				ImageColumns.DATE_TAKEN,
				ImageColumns.SIZE,
				ImageColumns.DATA };

		//group by bucket_id
		String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
		String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

		Uri[] images = { MediaStore.Images.Media.INTERNAL_CONTENT_URI,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI };
		for (Uri uri : images) {
			Cursor cur = mContext.getContentResolver().query(uri, PROJECTION_BUCKET,
					BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
			Log.v("ListingAlbums", " query albums count = " + cur.getCount());

			if (cur != null) {
				int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
				int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
				int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
				int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
				int sizeColumn = cur.getColumnIndex(MediaStore.Images.Media.SIZE);

				while (cur.moveToNext()) {
					Album album = new Album();
					album.setIdAlbum(cur.getInt(bucketIdColumn));
					album.setNameAlbum(cur.getString(bucketColumn));
					album.setDateAlbum(cur.getString(dateColumn));
					album.setDataAlbum(cur.getString(dataColumn));
					album.setSizeAlbum(cur.getInt(sizeColumn));
					album.setTotalPicture(countPicturesByAlbum(uri, cur.getInt(bucketIdColumn)));
					listAlbums.addAlbum(album);
					Log.v("ListingAlbums", " bucket=" + album.getNameAlbum()
							+ " bucket_id=" + album.getIdAlbum()
							+ " date_taken=" + album.getDateAlbum()
							+ " size=" + album.getSizeAlbum()
							+ " data=" + album.getDataAlbum()
							+ " Pictures count = " + album.getTotalPicture());
				}
				cur.close();
			}
		}

		return listAlbums;
	}

	private int countPicturesByAlbum(Uri uri, int bucketIdColumn) {
		int count = 0;
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String searchParams = null;
			int bucketId = bucketIdColumn;
			searchParams = MediaStore.Images.Media.BUCKET_ID + " = " + bucketId;
			Cursor mPhotoCursor = mContext.getContentResolver().query(uri, null,
								searchParams, null, orderBy + " DESC");
			if (mPhotoCursor.getCount() > 0) {
				count = mPhotoCursor.getCount();
			}
			mPhotoCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

	//#A2 - retrieve detail album
	public void retrieveDetailAlbum(Album album) {
	}

	public void arriveAlbum(Album album) {
		//arrive Album
	}
}
