package com.framgia.gallerytraining.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.gallerytraining.R;
import com.framgia.gallerytraining.models.Album;

public class AlbumAdapter extends CommonAdapter<Album> {

	private Context mContext;

	public AlbumAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		TextView txAlbumName = null;
		TextView txAlbumCount = null;

		if (converView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			converView = vi.inflate(R.layout.list_item_album, null);
			txAlbumName = (TextView) converView.findViewById(R.id.list_gallery_albumname);
			txAlbumCount = (TextView) converView.findViewById(R.id.list_gallery_albumcount);
		}

		final Album album = getItem(position);
		txAlbumName.setText(album.getNameAlbum());
		txAlbumCount.setText(String.valueOf(album.getTotalPicture()));

		return converView;
	}

}
