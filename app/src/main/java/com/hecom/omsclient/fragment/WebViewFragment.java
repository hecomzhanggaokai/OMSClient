package com.hecom.omsclient.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.hecom.omsclient.BuildConfig;
import com.hecom.omsclient.R;
import com.hecom.omsclient.activity.PhotoViewerActivity;
import com.hecom.omsclient.activity.WebViewDemoActivity;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.camera.CameraActivity;
import com.hecom.omsclient.js.BackgroundRequests;
import com.hecom.omsclient.js.JSInteraction;
import com.hecom.omsclient.js.JSResolverFactory;
import com.hecom.omsclient.js.JSTaskTypes;
import com.hecom.omsclient.js.entity.ParamCreateChat;
import com.hecom.omsclient.js.entity.ParamOpenLink;
import com.hecom.omsclient.js.entity.ParamPreviewImage;
import com.hecom.omsclient.js.entity.ParamSetRight;
import com.hecom.omsclient.js.entity.ParamSetTitle;
import com.hecom.omsclient.js.entity.ParamText;
import com.hecom.omsclient.js.entity.ParamTextLeft;
import com.hecom.omsclient.js.entity.ParamUpload;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.Tools;
import com.hecom.omsclient.utils.tar.TarCache;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * 支持JSAPI的webview Fragment
 * Created by tianlupan on 16/4/26.
 */
public class WebViewFragment extends Fragment implements View.OnClickListener {

    public static final String INTENT_MODE = "mode";
    public static final String INTENT_QUICK_MODE = "quickMode";
    public static final String INTENT_MODE_MESSAGE_DETAIL = "messageDetail";
    public static final String INTENT_MODE_URL = "url";
    public static final String INTENT_MODE_TRANSPARENT = "transparent";
    public static final String INTENT_MODE_PLUGIN = "plugin";
    public static final String INTENT_MODE_PLUGIN_ID = "pluginId";
    public static final String INTENT_MODE_MARKET = "market";
    public static final String INTENT_MODE_MARKET_TYPE = "marketType";
    public static final String MODE_FULL_SCREEN = "fullScreen";
    public static final String IS_FROM_WEBVIEW = "is_from_webview";
    public static final String FROM_WEBVIEW_ISMULTIPLE = "from_webview_is_multiple";
    private static final String DEFAULT_URL = "file:///android_asset/market/plugin.html";
    //private static final String DEFAULT_URL = "file:///android_asset/demo.html";
    private final static int ACTIVITY_REQUEST_LOCATION = 0x1;
    private final static int ACTIVITY_REQUEST_TAKE_PHOTO = 0x2;
    private final static int ACTIVITY_REQUEST_SELECT_IMAGE = 0x3;
    private final static int ACTIVITY_REQUEST_NEW_LINK = 0x4;
    private final static int ACTIVITY_REQUEST_CREATE_CHAT = 0x5;

    private final static int ACTIVITY_REQUEST_SELECT_TEMPLATE = 0x6;
    private final static int ACTIVITY_REQUEST_ENTERPRISE_CHOOSE = 0x7;

    private final static int ACTIVITY_REQUEST_CHOOSE_PRODUCT = 0x8;

    private final static int ACTIVITY_REQUEST_COMMON_RECENT = 0x9;

    private final static int ACTIVITY_REQUEST_SELECT_FILE = 0x10;

    private final static String TAG = WebViewFragment.class.getSimpleName();

    //计算总共开了多少个页面,当关掉所有页面时，检查是否替换插件更新
    private static int mInstanceCount = 0;

    private JSInteraction.JsResolver uploadImageResolver, durationPicker;

    private JSInteraction jsInteraction;

    private BackgroundRequests backgroundRequests;

    private RelativeLayout top_bar;
    private TextView tv_back;
    private TextView tv_close;
    private TextView tv_title;
    private ImageView icon_right1, icon_right2;
    private TextView tv_right1, tv_right2, right_text;
    private ProgressBar pb_loading;
    private WebView webview;

    private String mUrl = DEFAULT_URL;
//    private boolean fullScreenMode = true;
    /**
     * 是否透明，以便显示后面的水印背景
     */
    private boolean mTransparent = false;

    private String user_track_page_id = "cj";

    private enum UPLOAD_IMAGE_TYPE {
        TAKE_PHOTO, SELECT_IMAGE, USER_SELECT;
    }

    private UPLOAD_IMAGE_TYPE upload_image_type;

    //选择本地图片后直接上传为true,selectLocalFile为false
    private boolean selectImageAndUploadDirectly = true;

//    //有效的插件域，如果URL以这个开头
//    private static final String AUTHORIZED_PLUGIN_HOST = "hecom.cn";

//
//    private String[] value_date_selector = {"0天", "1天", "2天", "3天", "4天", "5天", "6天", "7天", "8天",
//            "9天", "10天", "11天", "12天", "13天", "14天", "15天", "16天", "17天", "18天", "19天", "20天",
//            "21天", "22天", "23天", "24天", "25天", "26天", "27天", "28天", "29天", "30天", "31天"};
//    private String[] mSelectOfHour = {"0小时", "1小时", "2小时", "3小时", "4小时", "5小时", "6小时", "7小时",
//            "8小时", "9小时", "10小时", "11小时", "12小时", "13小时", "14小时", "15小时", "16小时", "17小时", "18小时",
//            "19小时", "20小时", "21小时", "22小时", "23小时"};


//    public JSInteraction getJsInteraction(){
//        return jsInteraction;
//    }


    private Handler uiHandler = new Handler();

    /**
     * 改变webView大小
     *
     * @param newHeight
     */
    public void changeWebViewHeight(int newHeight) {
        Log.d(TAG, "changeWebViewHeight =" + newHeight + ", scale=" + webview.getScale());
//        if (!fullScreenMode) {
//            int height = (int) (newHeight * webview.getScale());
//            webview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//        }
    }

//    /**
//     * 获取客户模块填写的内容
//     *
//     * @param onResult
//     */
//    public void getUserInput(JSInteraction.OnResult onResult) {
//        jsInteraction.callFromApp(JSTaskTypes.GET_USER_INPUT, null, onResult);
//    }

    private void hideTopRightButtons() {
        icon_right1.setVisibility(View.GONE);
        icon_right2.setVisibility(View.GONE);
        tv_right1.setVisibility(View.GONE);
        tv_right2.setVisibility(View.GONE);
    }

    private JSInteraction.JSListener setRightListener = new JSInteraction.JSListener<ParamText>() {

        @Override
        protected void onJsCall(ParamText args) {

            String text = args.getText();
            if (TextUtils.isEmpty(text)) {
                right_text.setText("");
                setRegister(false);
            } else {
                right_text.setText(text);
                setRegister(true);
            }


//            String[] values = args.getValue();
//            String type = args.getType();
//            hideTopRightButtons();
//            setRegister(values.length > 0);
//
//            Log.e("here", "1");
//
//            if (type.equalsIgnoreCase(ParamSetRight.TYPE_ICON)) {
//                //TODO 设置左侧图标
//                if (values.length > 0) {
//                    icon_right1.setVisibility(View.VISIBLE);
//                }
//                //TODO 设置右侧图标
//                if (values.length > 1) {
//                    icon_right2.setVisibility(View.VISIBLE);
//                }
//            } else if (type.equalsIgnoreCase(ParamSetRight.TYPE_TEXT)) {
//                if (values.length > 0) {
//                    tv_right1.setVisibility(View.VISIBLE);
//                    tv_right1.setText(values[0]);
//                }
//                if (values.length > 1) {
//                    tv_right2.setVisibility(View.VISIBLE);
//                    tv_right2.setText(values[1]);
//                }
//            }
        }
    };


    private JSInteraction.JSListener setLeftListener = new JSInteraction.JSListener<ParamTextLeft>() {
        @Override
        protected void onJsCall(ParamTextLeft args) {
            String text = args.getText();
            if (TextUtils.isEmpty(text)) {
                tv_back.setText("返回");
                setRegister(false);
            } else {
                tv_back.setText(text);
                setRegister(true);
            }

            if (args.isVisible()) {
                tv_back.setVisibility(View.VISIBLE);
            } else {
                tv_back.setVisibility(View.GONE);
            }
        }

    };

    private class SelectLocalFileResolver extends JSInteraction.JsResolver<ParamUpload> {
        public ParamUpload args;

        public SelectLocalFileResolver(boolean sync) {
            super(sync);
        }

        @Override
        protected JSONObject onJsCall(ParamUpload args) {

            this.args = args;
            selectImageAndUploadDirectly = false;
            upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
            new AlertDialog.Builder(getActivity())
                    .setTitle("选择图片来源")
                    .setCancelable(false)
                    .setSingleChoiceItems(new String[]{"拍照上传", "选择图片上传"}, -1,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (which == 0) {
                                        selectPicFromCamera();
                                    } else {
                                        selectPicFromLocal();
                                    }
                                }
                            }
                    )
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectLocalFileResolver.this.setError(jsInteraction.ERROR_USER_CANCELLED);
                        }
                    })
                    .show();
            return null;
        }
    }


    SelectLocalFileResolver selectLocalFileResolver = new SelectLocalFileResolver(false);
//        public ParamUpload args;
//
//
//        @Override
//        protected JSONObject onJsCall(ParamUpload args) {
//            this.args = args;
//            selectImageAndUploadDirectly = false;
//            upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
//            new AlertDialog.Builder(getActivity())
//                    .setTitle("选择图片来源")
//                    .setCancelable(false)
//                    .setSingleChoiceItems(new String[]{"拍照上传", "选择图片上传"}, -1,
//                            new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (which == 0) {
//                                        selectPicFromCamera();
//                                    } else {
//                                        selectPicFromLocal();
//                                    }
//                                }
//                            }
//                    )
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            selectLocalFileResolver
//                                    .setError(jsInteraction.ERROR_USER_CANCELLED);
//                        }
//                    })
//                    .show();
//            return null;
//        }
//    };

    private void selectPicFromCamera() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivityForResult(intent, ACTIVITY_REQUEST_TAKE_PHOTO);
    }

//    private JSInteraction.JsResolver locationResolver = new JSInteraction.JsResolver<Void>(false) {
//        @Override
//        protected JSONObject onJsCall(Void args) {
//            Intent locationIntent = new Intent(getActivity(),
//                    InitiativeLocationActivity.class);
//            locationIntent.putExtra(LocationHandler.INTENT_KEY_TITLENAME,
//                    getResources().getString(R.string.im_dialog_location));
//            startActivityForResult(locationIntent, ACTIVITY_REQUEST_LOCATION);
//            return null;
//        }
//    };

//    private JSInteraction.JsResolver createChatResolver = new JSInteraction.JsResolver<ParamCreateChat>(false) {
//
//        @Override
//        protected JSONObject onJsCall(ParamCreateChat args) {
//            Intent intent = new Intent(getActivity(), CreateChatActivity.class);
//            intent.putExtra("multiple", args.isMultiple());
//            intent.putExtra("title", args.getTitle());
//            intent.putExtra("jsapi", true);
//            startActivityForResult(intent, ACTIVITY_REQUEST_CREATE_CHAT);
//            return null;
//        }
//    };

//    private JSInteraction.JsResolver selectTemplateResolver = new JSInteraction.JsResolver<ParamTemplateType>(false) {
//        @Override
//        protected JSONObject onJsCall(ParamTemplateType args) {
//            Intent intent = new Intent(getActivity(), SelectTemplateActivity.class);
//            intent.putExtra(SelectTemplateActivity.INTENT_TEMPLATE_TYPE, args.getTemplateType());
//            startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_TEMPLATE);
//            return null;
//        }
//    };

//    private JSInteraction.JsResolver selectLocalFileResolver = new JSInteraction.JsResolver<ParamSelectLocalFile>(false) {
//        @Override
//        protected JSONObject onJsCall(ParamSelectLocalFile args) {
//            selectImageAndUploadDirectly = false;
//            upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
//            new AlertDialog.Builder(getActivity())
//                    .setTitle("选择图片来源")
//                    .setCancelable(false)
//                    .setSingleChoiceItems(new String[]{"拍照上传", "选择图片上传"}, -1,
//                            new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (which == 0) {
//                                        selectPicFromCamera();
//                                    } else {
//                                        selectPicFromLocal();
//                                    }
//                                }
//                            }
//                    )
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            selectLocalFileResolver
//                                    .setError(jsInteraction.ERROR_USER_CANCELLED);
//                        }
//                    })
//                    .show();
//            return null;
//        }
//    };

    //选择组织架构
//    private JSInteraction.JsResolver chooseEnterpriseResolver = new JSInteraction.JsResolver<ParamChooseEnterprise>(false) {
//        @Override
//        protected JSONObject onJsCall(ParamChooseEnterprise args) {
//            Intent intent = new Intent();
//            if (ParamChooseEnterprise.TYPE_ALL.equals(args.type) || ParamChooseEnterprise.TYPE_DEPT.equals(args.type)) {
//                intent.setClass(getActivity(), args.multiple ? PluginOrgnazationMultiSiftActivity.class : PluginOrgnazationSingleSiftActivity.class);
//                intent.putExtra(TreeSiftParams.PARAM_TITLE, args.title);
//                intent.putExtra(OrgSiftParams.PARAM_CODES, args.empCodes);
//                intent.putExtra(OrgSiftParams.PARAM_PARENTCODE, args.parentCode);
//                intent.putExtra(OrgSiftParams.PARAM_TYPE, args.type);
//                intent.putExtra(OrgSiftParams.PARAM_HAS_SELF, args.hasSelf);
//                startActivityForResult(intent, ACTIVITY_REQUEST_ENTERPRISE_CHOOSE);
//            } else if (ParamChooseEnterprise.TYPE_EMP.equals(args.type)) {
//                intent.setClass(getActivity(), PluginOrgnazationIndexSiftActivity.class);
//                intent.putExtra(TreeSiftParams.PARAM_TITLE, args.title);
//                intent.putExtra(OrgSiftParams.PARAM_CODES, args.empCodes);
//                intent.putExtra(OrgSiftParams.PARAM_PARENTCODE, args.parentCode);
//                intent.putExtra(OrgSiftParams.PARAM_TYPE, args.type);
//                intent.putExtra(OrgSiftParams.PARAM_TYPE_MODE, args.mode);
//                intent.putExtra(OrgSiftParams.PARAM_HAS_SELF, args.hasSelf);
//                startActivityForResult(intent, ACTIVITY_REQUEST_ENTERPRISE_CHOOSE);
//            } else if (ParamChooseEnterprise.TYPE_APPROVE.equals(args.type) || ParamChooseEnterprise.TYPE_EXEC.equals(args.type)) {
//                intent.setClass(getActivity(), CommonRecentEmpActivity.class);
//                String[] codes = args.empCodes.split(",");
//                ArrayList<String> codeList = new ArrayList<>();
//                for (String code : codes) {
//                    if (TextUtils.isEmpty(code)) {
//                        continue;
//                    }
//                    codeList.add(code);
//                }
//                if ("1".equals(args.hasSelf)) {
//                    intent.putExtra("hasMe", false);
//                } else {
//                    intent.putExtra("hasMe", true);
//                }
//                if (ParamChooseEnterprise.TYPE_APPROVE.equals(args.type)) {
//                    intent.putExtra("title", "选择审批人");
//                }
//                if (ParamChooseEnterprise.TYPE_EXEC.equals(args.type)) {
//                    intent.putExtra("title", "选择接收人");
//                }
//                intent.putExtra("type", args.type);
//                intent.putExtra("codes", codeList);
//                intent.putExtra("isMultiple", args.multiple);
//                startActivityForResult(intent, ACTIVITY_REQUEST_COMMON_RECENT);
//            }
//
//
//            return null;
//        }
//    };

    //选择产品
//    private JSInteraction.JsResolver chooseProductResolver = new JSInteraction.JsResolver<ParamChooseProduct>(false) {
//        @Override
//        protected JSONObject onJsCall(ParamChooseProduct args) {
//            Intent intent = new Intent();
//            if ("0".equals(args.multiple)) {
//                intent.setClass(getActivity(), ProductSelectActivity.class);
//                startActivityForResult(intent, ACTIVITY_REQUEST_CHOOSE_PRODUCT);
//            } else {
//                // TODO: 16/5/11 和俊杰讨论，暂时不支持多选
//                chooseProductResolver.setError(JSInteraction.ERROR_BAD_ARGUMENT);
//            }
//            return null;
//        }
//    };
    //图片预览

    private JSInteraction.JsResolver previewImageResolver = new JSInteraction.JsResolver<ParamPreviewImage>(true) {
        @Override
        protected JSONObject onJsCall(ParamPreviewImage args) {
//            Intent locationIntent = new Intent(getActivity(),
//                    InitiativeLocationActivity.class);
//            locationIntent.putExtra(LocationHandler.INTENT_KEY_TITLENAME,
//                    getResources().getString(R.string.im_dialog_location));
//            startActivityForResult(locationIntent, ACTIVITY_REQUEST_LOCATION);

            StringBuilder sb = new StringBuilder();


            for (int i = 0; i < args.getUrls().length; i++) {
                String url = args.getUrls()[i];
                if (i != args.getUrls().length - 1) {
                    sb.append(getLocalPath(url) + ",");
                } else {
                    sb.append(getLocalPath(url));
                }
            }

            Intent intent = new Intent();
            intent.putExtra(PhotoViewerActivity.URLS, sb.toString());
            intent.putExtra(PhotoViewerActivity.SELECT_URL, args.current);
            intent.setClass(getActivity(), PhotoViewerActivity.class);
            startActivity(intent);
            return null;
        }
    };


    private String getLocalPath(String remoteUrl) {

        String app_preview_url = "http://app/imagePreview?filePath=";
        if (remoteUrl.startsWith(app_preview_url) && remoteUrl.length() > app_preview_url.length()) {
            String picTmpDir = Tools.getPicSaveDir("");
            String imageFileName = remoteUrl.substring(app_preview_url.length());
            try {
                imageFileName = URLDecoder.decode(imageFileName, "UTF-8");
                File file = new File(picTmpDir, imageFileName);
                if (file.exists()) {
                    return "file://" + file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return remoteUrl;
    }


    private void bindTaskResolvers() {
        jsInteraction = new JSInteraction();
        jsInteraction.setWebView(webview);

        webview.setWebViewClient(new WebViewClient() {

            @SuppressLint("NewApi")
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = TarCache.getCache(url);
                // Log.d(TAG,"shouldInterceptRequest,url="+url+", intercept="+(response!=null));
                if (response != null) {
                    return response;
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final String tel_protocal = "tel:";
                //拔打电话
                if (!TextUtils.isEmpty(url) && url.startsWith(tel_protocal) && url.length() > tel_protocal.length()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                jsInteraction.onNewPage();
                return false;
            }

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {
                //参考：http://blog.csdn.net/feifei454498130/article/details/23627557
                Log.d(TAG, "webview onReceivedError,errorCode=" + errorCode + ",failingURL="
                        + failingUrl);


                //断网时使用自定义页面内容
                if (errorCode < 0 && failingUrl != null && failingUrl.startsWith("http")) {

                    uiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String html = getError404();
                            final String PAGE = "{PAGE}";
                            if (html.contains(PAGE)) {
                                html = html.replace(PAGE, failingUrl);
                            }
                            view.loadUrl("javascript:document.body.innerHTML='" + html + "'");
                        }
                    }, 50);


                }
//                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        backgroundRequests = new BackgroundRequests(getActivity());
        backgroundRequests.bind(jsInteraction);

//        jsInteraction.addJsResolver(JSTaskTypes.GET_LOCATION, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                //不能同时响应两个地图定位请求
//                return locationResolver;
//            }
//        });

        //OPEN_LINK
//        jsInteraction.addJsResolver(JSTaskTypes.OPEN_LINK, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamOpenLink>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamOpenLink args) {
//                        Intent intent = new Intent(getActivity(), PluginActivity.class);
//                        intent.putExtra("mode", "url");
//                        intent.putExtra("url", args.getUrl());
//                        startActivityForResult(intent, ACTIVITY_REQUEST_NEW_LINK);
//                        return null;
//                    }
//                };
//            }
//        });

        //拍照上传
//        jsInteraction.addJsResolver(JSTaskTypes.UPLOAD_IMAGE_TAKE_PHOTO, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                uploadImageResolver = new JSInteraction.JsResolver<Void>(false) {
//                    @Override
//                    protected JSONObject onJsCall(Void args) {
//                        selectImageAndUploadDirectly = true;
//                        upload_image_type = UPLOAD_IMAGE_TYPE.TAKE_PHOTO;
//                        selectPicFromCamera();
//                        return null;
//                    }
//                };
//                return uploadImageResolver;
//            }
//        });

        //从图库选择上传
//        jsInteraction.addJsResolver(JSTaskTypes.UPLOAD_IMAGE_SELECT_FROM_GALLERY,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        uploadImageResolver = new JSInteraction.JsResolver<Void>(false) {
//                            @Override
//                            protected JSONObject onJsCall(Void args) {
//                                Log.e(TAG, "上传选 图片");
//                                selectImageAndUploadDirectly = true;
//                                upload_image_type = UPLOAD_IMAGE_TYPE.SELECT_IMAGE;
//                                selectPicFromLocal();
//                                return null;
//                            }
//                        };
//                        return uploadImageResolver;
//                    }
//                });

        //图片上传 - 用户选择
//        jsInteraction.addJsResolver(JSTaskTypes.UPLOAD_IMAGE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                uploadImageResolver = new JSInteraction.JsResolver<Void>(false) {
//                    @Override
//                    protected JSONObject onJsCall(Void args) {
//                        selectImageAndUploadDirectly = true;
//                        upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
//                        selectImageSource();
//                        return null;
//                    }
//                };
//                return uploadImageResolver;
//            }
//        });

//        jsInteraction.addJsResolver(JSTaskTypes.SELECT_LOCAL_FILE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                uploadImageResolver = new JSInteraction.JsResolver<ParamSelectLocalFile>(false) {
//                    @Override
//                    protected JSONObject onJsCall(ParamSelectLocalFile args) {
//                        selectImageAndUploadDirectly = false;
//                        upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
//                        if (args.isSourceFromGallery() && args.isSourceFromCamera()) {
//                            selectImageSource();
//                        } else if (args.isSourceFromCamera()) {
//                            selectPicFromCamera();
//                        }
//                        return null;
//                    }
//                };
//                return uploadImageResolver;
//            }
//        });

        //CREATE_CHAT
//        jsInteraction.addJsResolver(JSTaskTypes.CREATE_CHAT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return createChatResolver;
//            }
//        });

        //选择模板
//        jsInteraction.addJsResolver(JSTaskTypes.SELECT_TEMPLATE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return selectTemplateResolver;
//            }
//        });

        //顶部左右按钮事件监听
        jsInteraction.setListener(JSTaskTypes.LISTENER_TOP_RIGHT, setRightListener);
        jsInteraction.setListener(JSTaskTypes.LISTENER_TOP_LEFT, setLeftListener);
        jsInteraction.addJsResolver(JSTaskTypes.TOP_TITLE, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<ParamSetTitle>(true) {
                    @Override
                    protected JSONObject onJsCall(ParamSetTitle args) {
                        tv_title.setText(args.getTitle());
                        return null;
                    }
                };
            }
        });

        //关闭页面
        jsInteraction.addJsResolver(JSTaskTypes.CLOSE_WINDOW, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<Void>(true) {
                    @Override
                    protected JSONObject onJsCall(Void args) {
                        finish();
                        return null;
                    }
                };
            }
        });


        //图片上传 - 用户选择
        jsInteraction.addJsResolver(JSTaskTypes.UPLOADIMAGE, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
//                uploadImageResolver = new JSInteraction.JsResolver<Void>(false) {
//                    @Override
//                    protected JSONObject onJsCall(Void args) {
//                        selectImageAndUploadDirectly = true;
//                        upload_image_type = UPLOAD_IMAGE_TYPE.USER_SELECT;
//                        selectImageSource();
//                        return null;
//                    }
//                };
                return selectLocalFileResolver;
            }
        });


        //顶部显示隐藏
//        jsInteraction.addJsResolver(JSTaskTypes.SET_TITLE_VISIBLE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamTitleVisible>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamTitleVisible args) {
//                        top_bar.setVisibility((fullScreenMode && args.isVisible()) ? View.VISIBLE : View.GONE);
//                        return null;
//                    }
//                };
//            }
//        });

        //公司组织架构筛选
//        jsInteraction.addJsResolver(JSTaskTypes.ENTERPRISE_CHOOSE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return chooseEnterpriseResolver;
//            }
//        });

//        jsInteraction.addJsResolver(JSTaskTypes.ADJUST_WEBVIEW_HEIGHT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Map<String, String>>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Map<String, String> args) {
//                        String height = args.get("height");
//                        int h = -1;
//                        try {
//                            h = Integer.valueOf(height);
//                        } catch (NumberFormatException exception) {
//                        }
//
//                        if (h < 0) {
//                            setError(JSInteraction.ERROR_BAD_ARGUMENT);
//                        } else {
//                            changeWebViewHeight(h);
//                        }
//                        return null;
//                    }
//                };
//            }
//        });

        //选择产品
//        jsInteraction.addJsResolver(JSTaskTypes.CHOOSE_PRODUCT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return chooseProductResolver;
//            }
//        });

        //解散企业

//        jsInteraction.addJsResolver(JSTaskTypes.ENTERPRISE_DISMISS, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Map<String, String>>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Map<String, String> args) {
//                        Log.d(TAG, "解散企业");
//                        return null;
//                    }
//                };
//            }
//        });

        //解散企业

//        jsInteraction.addJsResolver(JSTaskTypes.ENTERPRISE_DISMISS, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Map<String, String>>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Map<String, String> args) {
//                        Log.d(TAG, "解散企业");
//                        return null;
//                    }
//                };
//            }
//        });

        //保存模板审批／接收人

//        jsInteraction.addJsResolver(JSTaskTypes.TEMPLATE_SAVECONTACT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Map<String, String>>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Map<String, String> args) {
//                        Log.d(TAG, "保存模板审批／接收人");
//
//                        String templateId = args.get("templateId");
//                        String data = args.get("data");
//                        if (!TextUtils.isEmpty(templateId)) {
//                            PrefUtils.getAccountSharedPreferences().edit().putString(templateId, data).commit();
//                            return null;
//                        }
//
//                        //处理templateId为空的时候,用templateType作为key
//                        String templateType = args.get("templateType");
//
//                        if (!TextUtils.isEmpty(templateType)) {
//                            PrefUtils.getAccountSharedPreferences().edit().putString(templateType, data).commit();
//                            return null;
//                        }
//
////                        TemplateContact templateContact = new TemplateContact();
////                        templateContact.setTemplateId(args.get("templateId"));
////                        templateContact.setCreateon(System.currentTimeMillis());
////                        templateContact.setData(args.get("data"));
////                        templateContact.setTemplateType(args.get("templateType"));
////                        MainDBManager.getDaoSession().getTemplateContactDao().insertOrReplace(templateContact);
//                        return null;
//                    }
//                };
//            }
//        });

        //获取模板审批／接收人

//        jsInteraction.addJsResolver(JSTaskTypes.TEMPLATE_GETCONTACT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Map<String, String>>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Map<String, String> args) {
//                        Log.d(TAG, "获取模板审批／接收人");
//
//                        String templateId = args.get("templateId");
//                        if (!TextUtils.isEmpty(templateId)) {
//                            String data = PrefUtils.getAccountSharedPreferences().getString(templateId, "");
//                            if (!TextUtils.isEmpty(data)) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject();
//                                    jsonObject.put("data", data);
//                                    return jsonObject;
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        String templateType = args.get("templateType");
//
//                        if (!TextUtils.isEmpty(templateType)) {
//                            String data = PrefUtils.getAccountSharedPreferences().getString(templateType, "");
//                            if (!TextUtils.isEmpty(data)) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject();
//                                    jsonObject.put("data", data);
//                                    return jsonObject;
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
////                        TemplateContact templateContact = MainDBManager.getDaoSession().getTemplateContactDao().load(args.get("templateId"));
////                        if (templateContact == null) {
////                            return null;
////                        }
////                        try {
////                            JSONObject jsonObject = new JSONObject();
////                            jsonObject.put("data", templateContact.getData());
////                            return jsonObject;
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//                        return null;
//                    }
//                };
//            }
//        });

        //图片浏览

        jsInteraction.addJsResolver(JSTaskTypes.PREVIEWIMAGE, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return previewImageResolver;
            }
        });

        //时长选择
//        jsInteraction.addJsResolver(JSTaskTypes.DURATIONPICKER, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                durationPicker = new JSInteraction.JsResolver<Void>(false) {
//                    @Override
//                    protected JSONObject onJsCall(Void args) {
//                        View editbox = getActivity().getLayoutInflater().inflate(R.layout.apply_leave_period, null);
//                        final NumberPicker numberPicker = (NumberPicker) editbox
//                                .findViewById(R.id.apply_value_day);
//                        final NumberPicker hourPicker = (NumberPicker) editbox
//                                .findViewById(R.id.apply_value_hour);
//                        numberPicker.setDisplayedValues(value_date_selector);
//                        numberPicker.setMinValue(0);
//                        numberPicker.setMaxValue(value_date_selector.length - 1);
//                        numberPicker.setValue(0);
//                        numberPicker.invalidate();
//                        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//
//                        hourPicker.setDisplayedValues(mSelectOfHour);
//                        hourPicker.setMinValue(0);
//                        hourPicker.setMaxValue(mSelectOfHour.length - 1);
//                        hourPicker.setValue(0);
//                        hourPicker.invalidate();
//                        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//
//                        String title = "选择时长";
////                        if (mSendType.equals(TYPE_OUT)) {
////                            title = getResources().getString(R.string.change_out_day_num);
////                        } else if (mSendType.equals(TYPE_LEAVE)) {
////                            title = getResources().getString(R.string.change_leave_day_num);
////                        } else if (mSendType.equals(TYPE_BUSINESS)) {
////                            title = getResources().getString(R.string.change_business_day_num);
////                        }
//                        AlertDialog dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
//                                .setCancelable(true)
//                                .setTitle(title).setView(editbox)
//                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        numberPicker.getValue();
//                                        hourPicker.getValue();
//                                        String result = getUseTime(value_date_selector[numberPicker
//                                                .getValue()]
//                                                + " "
//                                                + mSelectOfHour[hourPicker.getValue()]);
//
//                                        JSONObject jsonObject = new JSONObject();
//                                        try {
//                                            jsonObject.put("data", result);
//                                            durationPicker.setResult(jsonObject);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }).create();
//
//
//                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                setError(JSInteraction.ERROR_USER_CANCELLED);
//                            }
//                        });
//                        dialog.show();
//
//                        return null;
//                    }
//                };
//                return durationPicker;
//            }
//
//            /**
//             * 对天数进行处理.
//             */
//            private String getUseTime(String useTime) {
//                if (!TextUtils.isEmpty(useTime)) {
//                    if ("0天 0小时".equals(useTime)) {
//                        useTime = "";
//                    } else if (useTime.startsWith("0天")) {
//                        useTime = useTime.substring(2, useTime.length());
//                    } else if (useTime.endsWith("0小时")) {
//                        useTime = useTime.substring(0, useTime.length() - 3);
//                    }
//                }
//                return useTime;
//            }
//
//        });
        //OPEN_LINK
        jsInteraction.addJsResolver(JSTaskTypes.OPEN_LINK, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<ParamOpenLink>(true) {
                    @Override
                    protected JSONObject onJsCall(ParamOpenLink args) {
                        Intent intent = new Intent(getActivity(), WebViewDemoActivity.class);
                        intent.putExtra("isFromOpenLink", true);
                        intent.putExtra("url", args.getUrl());
                        startActivityForResult(intent, ACTIVITY_REQUEST_NEW_LINK);
                        return null;
                    }
                };
            }
        });
    }

//    private void selectImageSource() {
//        new AlertDialog.Builder(getActivity())
//                .setTitle("选择图片来源")
//                .setCancelable(false)
//                .setSingleChoiceItems(new String[]{"拍照上传", "选择图片上传"}, -1,
//                        new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                if (which == 0) {
//                                    selectPicFromCamera();
//                                } else {
//                                    selectPicFromLocal();
//                                }
//                            }
//                        }
//                )
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        uploadImageResolver
//                                .setError(jsInteraction.ERROR_USER_CANCELLED);
//                    }
//                })
//                .show();
//    }

    private String error404 = null;

    private String getError404() {

        if (error404 != null) {
            return error404;
        }

        String file = "market/disconnect.html";
        try {
            InputStream inputStream = getActivity().getApplicationContext().getAssets().open(file);
            byte[] content = new byte[inputStream.available()];
            int count;
            int offset = 0;
            while ((count = inputStream.read(content, offset, content.length - offset)) != -1) {
                offset += count;
                if (offset >= content.length)
                    break;
            }
            error404 = new String(content, "UTF-8");
            error404 = error404.replace("'", "\"");
        } catch (IOException e) {
            e.printStackTrace();
            error404 = "网络暂不可用";
        }

        return error404;
    }


    //@Override
    public void initView() {
        //View rootView=getView();
        top_bar = (RelativeLayout) rootView.findViewById(R.id.top_bar);

//        //全屏才显示导航条,内嵌不显示
//        top_bar.setVisibility(fullScreenMode ? View.VISIBLE : View.GONE);

        tv_back = (TextView) rootView.findViewById(R.id.tv_back);
        tv_close = (TextView) rootView.findViewById(R.id.tv_close);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_right1 = (TextView) rootView.findViewById(R.id.tv_right1);
        tv_right2 = (TextView) rootView.findViewById(R.id.tv_right2);
        right_text = (TextView) rootView.findViewById(R.id.right_text);
        icon_right1 = (ImageView) rootView.findViewById(R.id.icon_right1);
        icon_right2 = (ImageView) rootView.findViewById(R.id.icon_right2);
        webview = (WebView) rootView.findViewById(R.id.webview);
        pb_loading = (ProgressBar) rootView.findViewById(R.id.pb_loading);
        pb_loading.setMax(100);

        tv_back.setOnClickListener(this);
        tv_close.setOnClickListener(this);
        tv_right1.setOnClickListener(this);
        tv_right2.setOnClickListener(this);
        icon_right1.setOnClickListener(this);
        icon_right2.setOnClickListener(this);
        right_text.setOnClickListener(this);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pb_loading.setVisibility(View.VISIBLE);
                pb_loading.setProgress(progress);
                if (pb_loading.getProgress() == pb_loading.getMax()) {
                    pb_loading.setVisibility(View.GONE);
                }
            }
        });

        if (mTransparent) {
            webview.setBackgroundColor(0);
        }

        //启用WebView localStorage
        webview.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        webview.getSettings().setDatabaseEnabled(true);
        String databasePath = OMSClientApplication.getInstance().getDir("database", Context.MODE_PRIVATE).getPath();
        webview.getSettings().setDatabasePath(databasePath);

        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webview.getSettings().setAllowFileAccess(true);
        webview.setVerticalScrollBarEnabled(false);
        //禁止弹出是否记住密码 参考http://stackoverflow.com/questions/11531778/how-to-disable-the-save-password-dialog-on-an-android-webview
        if (Build.VERSION.SDK_INT <= 18) {
            webview.getSettings().setSavePassword(false);
        } else {
        // do nothing. because as google mentioned in the documentation -
        // "Saving passwords in WebView will not be supported in future versions"
        }

//        webview.getSettings().setCacheMode(WebSettings.);
//        webview.getSettings().setAppCacheEnabled(true);

//        if (PathUtils.getFileDirs() != null) {
//            String appCachePath = PathUtils.getFileDirs().getAbsolutePath();
//            webview.getSettings().setAppCachePath(appCachePath);
//            webview.getSettings().setDatabasePath(appCachePath);
//        }


        /////
//        webview.getSettings().setUseWideViewPort(true);
//        webview.getSettings().setLoadWithOverviewMode(true);

//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
//			webview.getSettings().setLoadsImagesAutomatically(true);
//		} else {
//			webview.getSettings().setLoadsImagesAutomatically(false);
//		}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

    }


    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_product_market_layout, null);
        return rootView;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstanceCount++;
        if (getArguments() != null) {
            mUrl = getArguments().getString(INTENT_MODE_URL, DEFAULT_URL);
//            fullScreenMode = getArguments().getBoolean(MODE_FULL_SCREEN);
            mTransparent = getArguments().getBoolean(INTENT_MODE_TRANSPARENT, false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        bindTaskResolvers();
        webview.loadUrl(mUrl);
    }

//    public void loadUrl(String url) {
//        mUrl = url;
//        Log.d(TAG, "load url=" + url);
//        PluginManager.getInstance().preloadCache4URL(url);
//        webview.loadUrl(url);
//    }

//    private Map<String, String> getWebViewHeaders(String url) {
//
//        Map<String, String> headers = new HashMap<String, String>();
//
//        //检查是否有效url
//        boolean validURL = false;
//        if (!TextUtils.isEmpty(url) && url.toLowerCase().startsWith("http://") || url.toLowerCase()
//                .startsWith("https://")) {
//            validURL = true;
//            Uri uir = Uri.parse(url.toLowerCase());
//            String requestHost = uir.getHost();
//            //一级域名以 {@link AUTHORIZED_PLUGIN_HOST} 结束
//            if (!TextUtils.isEmpty(requestHost) && (
//                    requestHost.equalsIgnoreCase(AUTHORIZED_PLUGIN_HOST) || requestHost
//                            .toLowerCase().endsWith(AUTHORIZED_PLUGIN_HOST))) {
//                validURL = true;
//            }
//        }
//
//        if (!validURL)
//            return headers;
//
//        String uid = UserInfo.getUserInfo().getUid();
//        String entCode = UserInfo.getUserInfo().getEntCode();
//        String deviceCode = DeviceInfo.getDeviceId(SOSApplication.getAppContext());
//        boolean isAdmin = Config.isManagerOrOwner();
//
//        headers.put("entCode", entCode);
//        headers.put("deviceId", uid);
//        headers.put("deviceCode", deviceCode);
//        headers.put("isAdmin", (isAdmin ? "1" : "0"));
//        return headers;
//    }

//    private void openPlugin(Plugin plugin, boolean quickMode) {
//        String url = plugin.getHomepageURL();
//        int pluginId = plugin.getId();
//        user_track_page_id = "cj_" + pluginId;
//        if (!plugin.getHasSeen()) {
//            PluginManager.getInstance().setPluginSeen(pluginId);
//        }
//
//        PluginManager.getInstance().clearUnreadMessage(pluginId);
//
//        if (plugin.getType() == Plugin.PLUGIN_TYPE_CURSOTMER_JOB_DATA_COLLECT
//                || plugin.getType() == Plugin.PLUGIN_TYPE_CUSTOMER_VISIT) {
//            if (!TextUtils.isEmpty(url)) {
//                if (url.contains("?")) {
//                    url += "&" + PluginCustomerInfo.getParam();
//                } else {
//                    url += "?" + PluginCustomerInfo.getParam();
//                }
//                if (quickMode) {
//                    url += "&directReturn=1";
//                }
//            }
//        }
//
//        webview.loadUrl(url);
//    }

//    private void openMarket(int type) {
//        String url = Config.getPluginMarket(type);
//        user_track_page_id = "cjsc_" + type;
//        webview.loadUrl(url, getWebViewHeaders(url));
//    }

//    private void selectPicFromCamera() {
//        Intent intent = new Intent(getActivity(), CameraActivity.class);
//        startActivityForResult(intent, ACTIVITY_REQUEST_TAKE_PHOTO);
//    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_IMAGE);
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_FILE);
    }

    /**
     * 根据图库图片uri发送图片
     */
    private String getLocalFile(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);

        String filePath = null;

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            filePath = selectedImage.getPath();
        }

        if (filePath != null && !filePath.equals("null") && new File(filePath).exists()) {
            return filePath;
        } else {
            Toast toast = Toast.makeText(getActivity(), st8, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return null;
        }

    }

    private void upLoadFile(final String localFilePath, final boolean needDelete) {
        if (selectLocalFileResolver.args == null) {
            selectLocalFileResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
            return;
        }


        RequestParams params = new RequestParams();
        try {
            params.put(selectLocalFileResolver.args.getName(), new File(localFilePath));

            for (Map.Entry<String, String> key : selectLocalFileResolver.args.getParams().entrySet()) {
                params.add(key.getKey(), key.getValue());
            }
            OMSClientApplication.getHttpClient().post(selectLocalFileResolver.args.getPosturl(), params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    backgroundRequests.showLoading(true, "正在上传");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    JSONObject result = null;
                    try {
                        result = new JSONObject(new String(responseBody));
                        selectLocalFileResolver.setResult(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        selectLocalFileResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
                    }
                    backgroundRequests.showLoading(false, "");
                    if (needDelete) {
                        File file = new File(localFilePath);
                        file.delete();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    selectLocalFileResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
                    backgroundRequests.showLoading(false, "");
                    if (needDelete) {
                        File file = new File(localFilePath);
                        file.delete();
                    }
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            selectLocalFileResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
        }

//        OMSClientApplication.getHttpClient().post()


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //注意：拍照页面弹出时，低内存手机可能会回收此页，在此方法并调用时重新oncreate(), onActiivtyResult(),
        //所以需要对jsResolver进行判空操作
        if (requestCode == ACTIVITY_REQUEST_NEW_LINK) {
//            jsInteraction.onNewLinkClose();
        } else if (requestCode == ACTIVITY_REQUEST_SELECT_IMAGE) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                String localFile = getLocalFile(uri);
                if (localFile == null) {
                    selectLocalFileResolver.setError(jsInteraction.ERROR_UNKOWN_ERROR);
                } else {
                    upLoadFile(localFile, false);
//                    onFileSelect(localFile, false);
                }
            } else {
                selectLocalFileResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
            }
        } else if (requestCode == ACTIVITY_REQUEST_TAKE_PHOTO) {

            // 设置文件保存路径
            //上传，返回结果，删除临时文件
            if (data != null) {
                Bundle bundle = data.getExtras();
                String path = bundle.getString("imgfilepath");
                upLoadFile(path, true);
            } else {
                selectLocalFileResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
            }
        }
//        else if (requestCode == ACTIVITY_REQUEST_TAKE_PHOTO && uploadImageResolver != null) {
//
//            // 设置文件保存路径
//            //上传，返回结果，删除临时文件
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                String path = bundle.getString("imgfilepath");
//                onFileSelect(path);
//            } else {
//                uploadImageResolver.setError(JSInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_SELECT_IMAGE && uploadImageResolver != null) {
//            if (data != null && data.getData() != null) {
//                Uri uri = data.getData();
//                String localFile = getLocalFile(uri);
//                if (localFile == null) {
//                    uploadImageResolver.setError(jsInteraction.ERROR_UNKOWN_ERROR);
//                } else {
//                    onFileSelect(localFile);
//                }
//            } else {
//                uploadImageResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_LOCATION) {
//            if (resultCode == FragmentActivity.RESULT_OK && data != null) {
//                PointInfo pointInfo = data.getParcelableExtra(LocationHandler.INTENT_KEY_POINTINFO);
//                String poi = new Gson().toJson(pointInfo, new TypeToken<PointInfo>() {
//                }.getType());
//                try {
//                    locationResolver.setResult(new JSONObject(poi));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                locationResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_CREATE_CHAT) {
//
//            if (resultCode == FragmentActivity.RESULT_OK && data != null && data.hasExtra("result")) {
//                String jsonResult = data.getStringExtra("result");
//                if (!TextUtils.isEmpty(jsonResult)) {
//                    try {
//                        createChatResolver.setResult(new JSONObject(jsonResult));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                createChatResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_SELECT_TEMPLATE) {
//            if (resultCode == FragmentActivity.RESULT_OK && data != null) {
//                String templateType = data.getStringExtra("templateType");
//                String templateId = data.getStringExtra("templateId");
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("templateId", templateId);
//                    jsonObject.put("templateType", templateType);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                selectTemplateResolver.setResult(jsonObject);
//                //Toast.makeText(this,"选中的templateId:"+templateId+", type:"+templateType,Toast.LENGTH_LONG).show();
//            } else {
//                selectTemplateResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_ENTERPRISE_CHOOSE) {
//            if (resultCode == FragmentActivity.RESULT_OK && data != null) {
//                ArrayList<PluginOrgSelectResult> templateType = (ArrayList<PluginOrgSelectResult>) data.getSerializableExtra(OrgSiftParams.RESULT);
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    JSONArray jsonArray = new JSONArray(new Gson().toJson(templateType));
//                    jsonObject.put("data", jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                chooseEnterpriseResolver.setResult(jsonObject);
//                //Toast.makeText(this,"选中的templateId:"+templateId+", type:"+templateType,Toast.LENGTH_LONG).show();
//            } else {
//                chooseEnterpriseResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_COMMON_RECENT) {
//            if (resultCode == FragmentActivity.RESULT_OK && data != null) {
//                ArrayList<MenuItem> menuItemArrayList = (ArrayList<MenuItem>) data.getSerializableExtra(OrgSiftParams.RESULT);
//                ArrayList<PluginOrgSelectResult> list = new ArrayList<PluginOrgSelectResult>();
//
//                for (MenuItem item : menuItemArrayList) {
//                    PluginOrgSelectResult result = new PluginOrgSelectResult();
//                    result.setCode(item.getCode());
//                    result.setName(item.getName());
//                    result.setIsEmployee(item.isHasChild() ? "0" : "1");
//                    list.add(result);
//                }
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    JSONArray jsonArray = new JSONArray(new Gson().toJson(list));
//                    jsonObject.put("data", jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                chooseEnterpriseResolver.setResult(jsonObject);
//                //Toast.makeText(this,"选中的templateId:"+templateId+", type:"+templateType,Toast.LENGTH_LONG).show();
//            } else {
//                chooseEnterpriseResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        } else if (requestCode == ACTIVITY_REQUEST_CHOOSE_PRODUCT) {
//            if (resultCode == ProductSelectActivity.INTENT_RESULT_CODE && data != null) {
//                String params = data.getStringExtra("params");
//                JSONObject result = new JSONObject();
//                try {
//                    JSONObject jsonObject = new JSONObject(params);
//                    JSONArray jsonArray = new JSONArray();
//                    jsonArray.put(jsonObject);
//                    result.put("data", jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                chooseProductResolver.setResult(result);
//            } else {
//                chooseProductResolver.setError(jsInteraction.ERROR_USER_CANCELLED);
//            }
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 根据图库图片uri发送图片
     */
//    private String getLocalFile(Uri selectedImage) {
//        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
//        String st8 = getResources().getString(R.string.cant_find_pictures);
//
//        String filePath = null;
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex("_data");
//            filePath = cursor.getString(columnIndex);
//            cursor.close();
//        } else {
//            filePath = selectedImage.getPath();
//        }
//
//        if (filePath != null && !filePath.equals("null") && new File(filePath).exists()) {
//            return filePath;
//        } else {
//            Toast toast = Toast.makeText(getActivity(), st8, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//            return null;
//        }
//
//    }

    //拍照 或从gallery中选择图片后
//    private void onFileSelect(String path) {
//        String savePath = Tools
//                .getPicSaveFilePath(Config.getUniqueUserId(), "");
//        Log.d(TAG, "savepath=" + savePath);
//        if (new File(savePath).getParentFile().exists()) {
//            new File(savePath).getParentFile().mkdirs();
//        }
//        FileUtil.copyFile(path, savePath);
//        Tools.commpressImage(savePath);
//        if (selectImageAndUploadDirectly) {
//            uploadImage(savePath);
//        } else {
//            String fileName = new File(savePath).getName();
//            //selectLocalFile
//            JSONObject result = new JSONObject();
//            try {
//                result.put("filePath", fileName);
//                result.put("fileName", fileName);
//                result.put("fileType", "image");
//                result.put("remoteUrl", Tools.getRemoteUrl(fileName));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            uploadImageResolver.setResult(result);
//        }
//    }

//    private void uploadImage(String path) {
//        if (new File(path).exists()) {
//            backgroundRequests.showLoading(true, "正在上传图片，请稍候...");
//            PhotoUploadUtil
//                    .uploadPic(path, new RemoteHandler<String>() {
//
//                        @Override
//                        protected void onSuccess(RemoteResult<String> entity, String rawJsonResponse) {
//                            if (entity.isSuccess()) {
//                                backgroundRequests.showLoading(false, "");
//                                boolean validResult = false;
//                                String data = entity.getData();
//                                if (!TextUtils.isEmpty(data)) {
//                                    String[] split = data.split(";");
//                                    if (split.length > 0) {
//                                        String relativePath = split[0];
//                                        if (relativePath.length() > 0 && relativePath.startsWith("/")) {
//                                            relativePath = relativePath.substring(0);
//                                        }
//                                        String url = Config.getHost() + relativePath;
//                                        JSONObject result = new JSONObject();
//                                        try {
//                                            result.put("url", url);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        uploadImageResolver.setResult(result);
//                                        validResult = true;
//                                    }
//                                }
//
//                                if (!validResult) {
//                                    uploadImageResolver.setError(jsInteraction.ERROR_SERVER_EXCEPTION);
//                                }
//
//                            } else {
//                                uploadImageResolver.setError(jsInteraction.ERROR_UNKOWN_ERROR);
//                            }
//                        }
//
//                        @Override
//                        protected void onFailure(int statusCode, boolean notConnected, String rawJsonResponse) {
//                            Log.w(TAG, "onFailed to upload Image,statusCode=" + statusCode
//                                    + ", responseString=" + rawJsonResponse);
//                            backgroundRequests.showLoading(false, "");
//                            uploadImageResolver.setError(jsInteraction.ERROR_NETWORK);
//                        }
//                    });
//        } else {
//            uploadImageResolver.setError(jsInteraction.ERROR_UNKOWN_ERROR);
//        }
//    }
    private void finish() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);

        WebViewDemoActivity activity = (WebViewDemoActivity) getActivity();

        if (getActivity() != null) {
            if (activity.isFromOpenLink) {
                getActivity().finish();
            } else {
                //返回桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }


        }
    }

    private void onBack() {

        jsInteraction.callOnJSListener("backbutton", webview.canGoBack());
//        if (setLeftListener != null && setLeftListener.isRegister()) {
//            setLeftListener.onClick();
//        } else if (webview != null) {
//            if (webview.canGoBack()) {
//                webview.goBack();
//            } else {
//                finish();
//            }
//        } else {
//            finish();
//        }
    }

    //    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            onBack();
        }
        return true;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        //如果市场都关闭了，清理缓存
//        if (--mInstanceCount <= 0) {
//            PluginManager.getInstance().clearCache();
//            mInstanceCount = 0;
//        }
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                Log.e(TAG, "后退键按了...");
//                UserTrack.click("fh");
//                setLeftListener.onClick();
                if (setLeftListener != null && setLeftListener.isRegister()) {
                    setLeftListener.onClick();
                } else {
                    onBack();
                }
                break;
            case R.id.tv_right1:
            case R.id.icon_right1:
            case R.id.right_text:
//                UserTrack.click("qd");
                setRightListener.onClick(0);
                break;
            case R.id.tv_right2:
            case R.id.icon_right2:
//                UserTrack.click("qd2");
                setRightListener.onClick(1);
                break;
            case R.id.tv_close:
//                UserTrack.click("gb");
                finish();
                break;
        }
    }

    //第一次的时候不凋resunme
    private boolean isFirstOpen = true;

    @Override
    public void onResume() {
        super.onResume();
        if (jsInteraction != null && !isFirstOpen) {
            jsInteraction.callOnJSListener("resume", null);
        }
    }

    @Override
    public void onPause() {
        isFirstOpen = false;
        super.onPause();
        if (jsInteraction != null) {
            jsInteraction.callOnJSListener("pause", null);
        }
    }
}
