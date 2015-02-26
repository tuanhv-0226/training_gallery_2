package com.framgia.gallerytraining.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.framgia.gallerytraining.models.ListPictures;
import com.framgia.gallerytraining.utils.ImageFetcher;

public class PictureAdapter extends BaseAdapter {

	private final Context mContext;
    private int mItemHeight = 0;
    private int mNumColumns = 0;
    private GridView.LayoutParams mImageViewLayoutParams;

    private ImageFetcher mImageFetcher;
    private ListPictures mListPictures;

    public PictureAdapter(Context context, ListPictures lsPictures, ImageFetcher imgFetcher) {
        super();
        mContext = context;
        mListPictures = lsPictures;
        mImageFetcher = imgFetcher;
        mImageViewLayoutParams = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

	@Override
	public int getCount() {
		// If columns have yet to be determined, return no items
        if (getNumColumns() == 0) {
            return 0;
        }

        // Size + number of columns for top empty row
        return mListPictures.getListPictures().size() + mNumColumns;
	}

	@Override
    public Object getItem(int position) {
        return position < mNumColumns ?
                null : mListPictures.getListPictures().get(position - mNumColumns);
    }

    @Override
    public long getItemId(int position) {
        return position < mNumColumns ? 0 : position - mNumColumns;
    }

    @Override
    public int getViewTypeCount() {
        // Two types of views, the normal ImageView and the top row of empty views
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mNumColumns) ? 1 : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        //BEGIN_INCLUDE(load_gridview_item)
        // First check if this is the top row
        if (position < mNumColumns) {
            if (convertView == null) {
                convertView = new View(mContext);
            }
            return convertView;
        }

        // Now handle the main ImageView thumbnails
        ImageView imageView;
        if (convertView == null) { // if it's not recycled, instantiate and initialize
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(mImageViewLayoutParams);
        } else { // Otherwise re-use the converted view
            imageView = (ImageView) convertView;
        }

        // Check the height matches our calculated column width
        if (imageView.getLayoutParams().height != mItemHeight) {
            imageView.setLayoutParams(mImageViewLayoutParams);
        }

        // Finally load the image asynchronously into the ImageView, this also takes care of
        // setting a placeholder image while the background thread runs
        mImageFetcher.loadImage(mListPictures.getListPictures().get(position - mNumColumns).getDataPicture(), imageView);
        return imageView;
        //END_INCLUDE(load_gridview_item)
    }

    /**
     * Sets the item height. Useful for when we know the column width so the height can be set
     * to match.
     *
     * @param height
     */
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
