package com.framgia.gallerytraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.gallerytraining.R;
import com.framgia.gallerytraining.models.ListAlbums;
import com.framgia.gallerytraining.utils.ImageFetcher;

public class AlbumAdapter extends BaseAdapter {

	private final Context mContext;
	private ListAlbums mListAlbums;
	private ImageFetcher mImageFetcher;

	private int mItemHeight = 0;
    private int mNumColumns = 0;
    private GridView.LayoutParams mImageViewLayoutParams;

	public AlbumAdapter(Context context, ListAlbums lsAlbum, ImageFetcher imgFetcher) {
		super();
		mContext = context;
		mListAlbums = lsAlbum;
		mImageFetcher = imgFetcher;
		mImageViewLayoutParams = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	public int getCount() {
		return mListAlbums.getListAlbums().size();
	}

	@Override
	public Object getItem(int position) {
		return mListAlbums.getListAlbums()
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		// BEGIN_INCLUDE(load_gridview_item)
		// First check if this is the top row
		ViewHolder holder;

		// Now handle the main ImageView thumbnails
		if (convertView == null) { // if it's not recycled, instantiate and
									// initialize
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_item_album, container, false);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setLayoutParams(mImageViewLayoutParams);

		holder.thumbAlbum = (ImageView) convertView
				.findViewById(R.id.grid_thumb_album);
		holder.thumbAlbum.setScaleType(ImageView.ScaleType.CENTER_CROP);
		holder.nameAlbum = (TextView) convertView
				.findViewById(R.id.txt_album_name);
		holder.numAlbum = (TextView) convertView
				.findViewById(R.id.txt_album_count);

		// Check the height matches our calculated column width
		if (convertView.getLayoutParams().height != mItemHeight) {
			convertView.setLayoutParams(mImageViewLayoutParams);
        }

		// Finally load the image asynchronously into the ImageView, this
		// also takes care of
		// setting a placeholder image while the background thread runs
		mImageFetcher.loadImage(
				mListAlbums.getListAlbums().get(position)
						.getDataAlbum(), holder.thumbAlbum);
		holder.nameAlbum.setText(mListAlbums.getListAlbums()
				.get(position).getNameAlbum());
		holder.numAlbum.setText(Integer.toString(mListAlbums.getListAlbums()
				.get(position).getTotalPicture()));

		return convertView;
		// END_INCLUDE(load_gridview_item)
	}

	private class ViewHolder {
		ImageView thumbAlbum;
		TextView nameAlbum, numAlbum;
	}

	public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams =
                new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
        mImageFetcher.setImageSize(height);
        notifyDataSetChanged();
    }

	public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }
}
