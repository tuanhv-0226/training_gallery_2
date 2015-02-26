package com.framgia.gallerytraining;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.framgia.gallerytraining.adapters.PictureAdapter;
import com.framgia.gallerytraining.controllers.ControllerAlbum;
import com.framgia.gallerytraining.models.ListPictures;
import com.framgia.gallerytraining.models.Picture;
import com.framgia.gallerytraining.utils.ImageCache;
import com.framgia.gallerytraining.utils.ImageFetcher;
import com.framgia.gallerytraining.utils.Utils;

public class AlbumFragment extends Fragment implements AdapterView.OnItemClickListener {

	private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private PictureAdapter mAdapter;
    private ImageFetcher mImageFetcher;

    private String albumName;
    private ControllerAlbum ctrAlbum;
	private ListPictures listPictures;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public AlbumFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ctrAlbum = new ControllerAlbum(getActivity());
		albumName = getActivity().getIntent().getStringExtra("AlbumName");
		listPictures = ctrAlbum.fetchListPictures(albumName);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

        mAdapter = new PictureAdapter(getActivity(), listPictures, mImageFetcher);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_album, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridViewAlbum);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
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

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
					@TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
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
        mAdapter.notifyDataSetChanged();
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

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), PictureActivity.class);
        i.putExtra(PictureActivity.EXTRA_IMAGE, (int) id);
        i.putExtra(PictureActivity.ALBUM, albumName);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.album, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.clear_cache:
//                mImageFetcher.clearCache();
//                Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
//                        Toast.LENGTH_SHORT).show();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

  //#P1 - fetch list pictures of album
  	public void fetchListPictures(String bucketName) {
  		listPictures = ctrAlbum.fetchListPictures(bucketName);
//  		showListPictures(listPictures);
  	}

  	public void showListPictures(ListPictures listPic) {
//  		if (picAdapter == null) {
//  			picAdapter = new ListPictureAdapter(getActivity());
//  		} else {
//  			picAdapter.notifyDataSetChanged();
//  		}
//  		picAdapter.setData(listPic.getListPictures());
//  		abGridView.setAdapter(picAdapter);
  	}

  	//#P8
  	public void sortedAlbum(ListPictures listPic, String orderBy) {
  		ctrAlbum.sortedAlbum(listPic, orderBy);
  		showSortedAlbum(listPic);
  	}

  	public void showSortedAlbum(ListPictures listPic) {

  	}

  	//#A2 - detail album
  	public void showDetailAlbum() {

  	}

  	//#P2
  	public void retrieveDetailPic(Picture pic) {
  		ctrAlbum.retrieveDetailPic(pic);
  		showDetailPic(pic);
  	}

  	public void showDetailPic(Picture pic) {

  	}

  	//#P5
  	public void shareListPicture(ListPictures listPic) {
  		ctrAlbum.shareListPicture(listPic);
  	}

  	//#P6
  	public void setBackground(Picture pic) {
  		ctrAlbum.setBackground(pic);
  	}

}
