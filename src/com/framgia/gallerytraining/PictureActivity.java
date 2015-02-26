package com.framgia.gallerytraining;

import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;

import com.framgia.gallerytraining.adapters.DialogMessage;
import com.framgia.gallerytraining.controllers.ControllerAlbum;
import com.framgia.gallerytraining.controllers.ControllerPicture;
import com.framgia.gallerytraining.models.ListPictures;
import com.framgia.gallerytraining.models.Picture;
import com.framgia.gallerytraining.utils.ImageCache;
import com.framgia.gallerytraining.utils.ImageFetcher;
import com.framgia.gallerytraining.utils.Utils;

public class PictureActivity extends ActionBarActivity implements OnClickListener, OnViewTapListener{
	private static final String TAG = "PictureActivity";

	private static final String IMAGE_CACHE_DIR = "pictures";
    public static final String EXTRA_IMAGE = "extra_picture";
    public static final String ALBUM = "album";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;

    private int numTouch = 0;
	private ViewPager mPager;
	private ListPictures listPictures;
	private ControllerAlbum ctrAlbum;
	private ControllerPicture ctrPicture;

	DialogMessage dialogMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);

		ActionBar picBar = getSupportActionBar();
//		picBar.hide();

		final String albumName = getIntent().getStringExtra(ALBUM);
		ctrPicture = new ControllerPicture(getBaseContext());
		ctrAlbum = new ControllerAlbum(getBaseContext());
		listPictures = ctrAlbum.fetchListPictures(albumName);

		// Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        Log.d(TAG, Integer.toString(longest));

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), listPictures.getListPictures().size());
        mPager = (ViewPager) findViewById(R.id.pagerPic);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        mPager.setOffscreenPageLimit(2);

        mPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				numTouch++;
				if (numTouch % 2 == 1) {
					getSupportActionBar().show();
				} else {
					getSupportActionBar().hide();
				}
			}
		});
        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
	}

	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.picture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.sharePicture:
				sharePicture();
				break;
			case R.id.setBackground:
				setBackground();
				break;
			case R.id.detailPicture:
				showDetailPicture();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

	//#P7 - view picture
	public void viewPicture() {

	}

	//#P4 - zoom picture
	public void zoomPicture() {

	}

	//#P2 - show detail picture
	public void showDetailPicture() {
		Picture picCurrent = listPictures.getListPictures().get(mPager.getCurrentItem());

		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(picCurrent.getDataPicture().toString(), options);
        int width = options.outWidth;
        int height = options.outHeight;

		String message = new String("Picture size: " + width + "x" + height + "\n"
				+ "File size:" + Utils.convertByteToHumanReadable(picCurrent.getSizePicture()) + "\n"
				+ "Date:" + Utils.formatTimestamp(picCurrent.getDatePicture()) + "\n"
				+ "Path:" + picCurrent.getDataPicture());

		dialogMessage = new DialogMessage(this, "DETAIL PICTURE", message);
		dialogMessage.show();
	}

	//#P5 - share picture
	public void sharePicture() {
		ctrPicture.sharePicture(listPictures.getListPictures().get(mPager.getCurrentItem()));
	}

	//#P6 - set background
	public void setBackground() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

        dialogMessage = new DialogMessage(this,
        		"SET BACKGROUND",
        		"Are you sure you want to set this picture as background?",
        		new OnClickListener() {

					@Override
					public void onClick(View v) {
						ctrPicture.setBackground(listPictures.getListPictures()
								.get(mPager.getCurrentItem()), width, height);
						dialogMessage.dismiss();
					}
				});
        dialogMessage.show();
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            return PictureFragment.newInstance(listPictures.getListPictures().get(position).getDataPicture());
        }
    }

	/**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

	@Override
	public void onViewTap(View view, float x, float y) {
		numTouch++;
		if (numTouch % 2 == 1) {
			getSupportActionBar().show();
		} else {
			getSupportActionBar().hide();
		}
	}

}
