package com.framgia.gallerytraining;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.gallerytraining.utils.ImageFetcher;
import com.framgia.gallerytraining.utils.ImageWorker;
import com.framgia.gallerytraining.utils.Utils;

public class PictureFragment extends Fragment {
	private static final String IMAGE_DATA_EXTRA = "extra_image_data";

	private String pathImage;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    private PhotoViewAttacher mAttacher;

	public static PictureFragment newInstance(String pathPic) {
		PictureFragment frag = new PictureFragment();
		Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, pathPic);
		frag.setArguments(args);
		return frag;
	}

	public PictureFragment() {}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathImage = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_picture, container, false);
		mImageView = (ImageView) rootView.findViewById(R.id.imageViewPic);
		// Display image with full size

		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		mAttacher.setOnViewTapListener(new ViewTapListenter());

		return rootView;
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (PictureActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((PictureActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(pathImage, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
        // Need to call clean-up
        mAttacher.cleanup();
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
        }
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
        }
    }

    private class ViewTapListenter implements OnViewTapListener {

		@Override
		public void onViewTap(View view, float x, float y) {
		}

    }

}
