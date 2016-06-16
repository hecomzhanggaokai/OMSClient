/**
 *
 */
package com.hecom.omsclient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenming
 */
public class ImageOptionsFactory {

//    /**
//     * 得到圆角图片的optiont
//     */
//    public static DisplayImageOptions getRoundedBitmapOption() {
//        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.login_head)
//                .showImageForEmptyUri(R.drawable.login_head).showImageOnFail(R.drawable.login_head)
//                .cacheInMemory(true).cacheOnDisc(true)
//                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
//                .displayer(new RoundedBitmapDisplayer(10)).build();
//    }
//
//    /**
//     * 得到圆图片的option
//     */
//    public static DisplayImageOptions getCircleBitmapOption(int radius) {
//        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.login_head)
//                .showImageForEmptyUri(R.drawable.login_head).showImageOnFail(R.drawable.login_head)
//                .cacheInMemory(true).cacheOnDisc(true)
//                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
//                .displayer(new RoundedBitmapDisplayer(radius)).build();
//    }

    /**
     * @Deprecate 得到圆图片的option
     */
    public static DisplayImageOptions getCircleBitmapOption(int radius, int uri) {
        return new DisplayImageOptions.Builder().showImageOnLoading(uri).showImageForEmptyUri(uri)
                .showImageOnFail(uri).cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(radius)).build();
    }

    /**
     * 类库自带 的圆形滤镜
     *
     * @param defaultIcon
     * @return
     * @url 图片网址
     */
    public static DisplayImageOptions getCircleBitmapOption2(int defaultIcon) {
        return new DisplayImageOptions.Builder().showImageOnLoading(defaultIcon).showImageForEmptyUri(defaultIcon)
                .showImageOnFail(defaultIcon).cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new CircleBitmapDisplayer()).build();
    }

    /**
     * 得到圆图片的option
     */
    public static DisplayImageOptions getCircleBitmapOptionNoRadius(int uri) {
        return new DisplayImageOptions.Builder().showImageOnLoading(uri).showImageForEmptyUri(uri)
                .showImageOnFail(uri).cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .build();
    }

    /**
     * 得到查看图片的option
     */
    public static DisplayImageOptions getPicBitmapOption() {
        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.defaultimg)
                .showImageForEmptyUri(R.drawable.defaultimg).showImageOnFail(R.drawable.defaultimg)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();
    }

    /**
     * 得到查看图片的option
     */
    public static DisplayImageOptions getPicBitmapOption(int radius) {
        if (radius == 0) {
            return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.defaultimg)
                    .showImageForEmptyUri(R.drawable.defaultimg)
                    .showImageOnFail(R.drawable.defaultimg).cacheInMemory(true).cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.NONE)
                    .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                    .build();
        }
        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.defaultimg)
                .showImageForEmptyUri(R.drawable.defaultimg).showImageOnFail(R.drawable.defaultimg)
                .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(radius)).build();
    }

    /**
     * 得到查看图片的option
     */
    public static DisplayImageOptions getPicBitmapOption(int radius, int defaultimg) {
        if (radius == 0) {
            return new DisplayImageOptions.Builder().showImageOnLoading(defaultimg)
                    .showImageForEmptyUri(defaultimg)
                    .showImageOnFail(defaultimg).cacheInMemory(true).cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.NONE)
                    .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                    .build();
        }
        return new DisplayImageOptions.Builder().showImageOnLoading(defaultimg)
                .showImageForEmptyUri(defaultimg).showImageOnFail(defaultimg)
                .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(radius)).build();
    }

    /**
     * 得到工作圈图片的option
     *
     * @return
     */
    public static DisplayImageOptions getWorkingCircleBitmapOption() {
        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.defaultimg)
                .showImageForEmptyUri(R.drawable.defaultimg).showImageOnFail(R.drawable.defaultimg)
                .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).considerExifParams(true)
                .build();
    }

//    /**
//     * 得到聊天界面的头像的option
//     */
//    public static DisplayImageOptions getMessageHeadOption() {
//        return getMessageHeadOptionByIdx(1);
//    }
//
//    public static List<DisplayImageOptions> getMessageHeadOptions(int count) {
//        int realCount = count;
//        if (count > 10) {
//            realCount = 10;
//        }
//        if (count < 1) {
//            realCount = 1;
//        }
//        List<DisplayImageOptions> options = new ArrayList<DisplayImageOptions>(realCount);
//        for (int i = 1; i <= realCount; i++) {
//            options.add(getMessageHeadOptionByIdx(i));
//        }
//        return options;
//    }

//    public static DisplayImageOptions getMessageHeadOptionByIdx(int index) {
//        int rcId;
//        switch (index) {
//            case 1:
//                rcId = R.drawable.chat_head1;
//                break;
//            case 2:
//                rcId = R.drawable.chat_head2;
//                break;
//            case 3:
//                rcId = R.drawable.chat_head3;
//                break;
//            case 4:
//                rcId = R.drawable.chat_head4;
//                break;
//            case 5:
//                rcId = R.drawable.chat_head5;
//                break;
//            case 6:
//                rcId = R.drawable.chat_head6;
//                break;
//            case 7:
//                rcId = R.drawable.chat_head7;
//                break;
//            case 8:
//                rcId = R.drawable.chat_head8;
//                break;
//            case 9:
//                rcId = R.drawable.chat_head9;
//                break;
//            case 10:
//                rcId = R.drawable.contact_head_group;
//                break;
//            default:
//                rcId = R.drawable.chat_head5;
//                break;
//        }
//        return new DisplayImageOptions.Builder().showImageOnLoading(rcId)
//                .showImageForEmptyUri(rcId).showImageOnFail(rcId).cacheInMemory(true)
//                .cacheOnDisc(true).bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
//                .considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
//    }

    /**
     * 得到splash图片的option
     *
     * @return
     */
//    public static DisplayImageOptions getSplashBitmapOption() {
//        return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_splash)
//                .showImageForEmptyUri(R.drawable.bg_splash).showImageOnFail(R.drawable.bg_splash)
//                .build();
//    }

}
