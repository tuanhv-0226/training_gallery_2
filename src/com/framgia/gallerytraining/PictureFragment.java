package com.framgia.gallerytraining;

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
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }

}
