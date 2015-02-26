package com.framgia.gallerytraining;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.framgia.gallerytraining.adapters.AlbumAdapter;
import com.framgia.gallerytraining.controllers.ControllerGallery;
import com.framgia.gallerytraining.models.ListAlbums;
import com.framgia.gallerytraining.utils.ImageCache;
import com.framgia.gallerytraining.utils.ImageFetcher;
import com.framgia.gallerytraining.utils.Utils;

public class GalleryFragment extends Fragment implements
		AdapterView.OnItemClickListener {
	private static final String TAG = "AlbumGridFragment";
	private static final String ALBUM_CACHE_DIR = "gallery";

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private AlbumAdapter albumAdapter;
	private ImageFetcher mImageFetcher;

	private ListAlbums listAlbums;
	private ControllerGallery ctrGallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

		ctrGallery = new ControllerGallery(getActivity());
		listAlbums = ctrGallery.fetchListAlbums();

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				getActivity(), ALBUM_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

		albumAdapter = new AlbumAdapter(getActivity(), listAlbums, mImageFetcher);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_gallery, container,
				false);
		final GridView mGridView = (GridView) v
				.findViewById(R.id.gridViewGallery);
		mGridView.setAdapter(albumAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					// Before Honeycomb pause image loading on scroll to help
					// with performance
					if (!Utils.hasHoneycomb()) {
						mImageFetcher.setPauseWork(true);
					}
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@TargetApi(VERSION_CODES.JELLY_BEAN)
					@Override
					public void onGlobalLayout() {
						if (albumAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                albumAdapter.setNumColumns(numColumns);
                                albumAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
					}
				});

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		albumAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent albumIntent = new Intent(getActivity(), AlbumActivity.class);
		String bucketName = listAlbums.getListAlbums().get(position)
				.getNameAlbum();
		albumIntent.putExtra("AlbumName", bucketName);
		startActivity(albumIntent);
	}

}
