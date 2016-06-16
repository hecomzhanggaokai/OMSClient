package com.hecom.omsclient.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hecom.omsclient.ImageOptionsFactory;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zhubo on 16/5/28.
 */
public class PhotoViewerDetailFragment extends Fragment {

    private final static String TAG = "PhotoViewerDetailFragment";
    private final static String IMG_URL = "url";
    private final static String ONECLICK_CAN_DISMISS = "one_click_can_dismiss";

    private String mImgUrl;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private PhotoViewAttacher mAttacher;
    private Bitmap mBitmap;

    // 单击是否消失
    private boolean mOnClickCanDismiss;

    public static PhotoViewerDetailFragment newInstance(String imageUrl, boolean oneClickCanDismiss) {
        PhotoViewerDetailFragment fragment = new PhotoViewerDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString(IMG_URL, imageUrl);
        arguments.putBoolean(ONECLICK_CAN_DISMISS, oneClickCanDismiss);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImgUrl = getArguments() != null ? getArguments().getString(IMG_URL) : "";
        mOnClickCanDismiss = getArguments() != null ? getArguments().getBoolean(
                ONECLICK_CAN_DISMISS) : false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);
        if (mOnClickCanDismiss) {
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    getActivity().finish();
                }
            });
        }
        mProgressBar = (ProgressBar) view.findViewById(R.id.loading);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OMSClientApplication
                .getInstance()
                .getImageLoader()
                .displayImage(mImgUrl, mImageView, ImageOptionsFactory.getPicBitmapOption(0),
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                String cachePath = OMSClientApplication.getInstance().getImageLoader()
                                        .getDiskCache().get(mImgUrl).getPath();
//                                com.hecom.logs.Log.v(TAG, "cachePath = " + cachePath);
                                mBitmap = getBitmapByFile(new File(cachePath));
                                if (mBitmap != null) {
                                    mImageView.setImageBitmap(mBitmap);
                                } else {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                String message;
                                switch (failReason.getType()) {
                                    case IO_ERROR:
                                        message = "无效图片地址";

                                        break;
                                    case DECODING_ERROR:
                                        message = "图片无法显示";
                                        break;
                                    case NETWORK_DENIED:
                                        message = "网络有问题，无法下载";
                                        break;
                                    case OUT_OF_MEMORY:
                                        message = "图片太大无法显示";
                                        break;
                                    case UNKNOWN:
                                        message = "未知的错误";
                                        break;
                                    default:
                                        message = "未知的错误";
                                        break;
                                }
                                if (getActivity() == null) {
                                    return;
                                }
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                mBitmap = BitmapFactory.decodeResource(
                                        getActivity().getResources(), R.drawable.defaultimg);
                                showBitmap();
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                                          Bitmap loadedImage) {
                                mProgressBar.setVisibility(View.GONE);
                                mBitmap = loadedImage;
                                showBitmap();
                                mAttacher.update();
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
    }

    /**
     * 获取bitmap
     */
    public static Bitmap getBitmapByFile(File file) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        if (bitmap == null) {
        }
        return bitmap;
    }

    private void showBitmap() {
        if (mBitmap == null) {
            Toast.makeText(getActivity(), "图片出现问题～", Toast.LENGTH_SHORT).show();
        } else {
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
