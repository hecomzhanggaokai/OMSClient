package com.hecom.omsclient.js;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.js.entity.BDPointInfo;
import com.hecom.omsclient.js.entity.ChosenEntity;
import com.hecom.omsclient.js.entity.ParamActionSheet;
import com.hecom.omsclient.js.entity.ParamAlert;
import com.hecom.omsclient.js.entity.ParamConfirm;
import com.hecom.omsclient.js.entity.ParamPreloader;
import com.hecom.omsclient.js.entity.ParamText;
import com.hecom.omsclient.js.entity.ParamTimeFormat;
import com.hecom.omsclient.server.BDLocationHandler;
import com.hecom.omsclient.server.BaseHandler;
import com.hecom.omsclient.widget.AlertDialogWidget;
import com.hecom.omsclient.widget.DialogContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author tianlupan 2015/11/5
 */
public class BackgroundRequests /*implements OnRequestSavedListener*/ {

    private Activity mContext;
    private JSInteraction jsInteraction;

    public BackgroundRequests(Activity context) {
        this.mContext = context;
//        recordHandler = OperatorRecordHandler.getInstance(context);
    }

    private static final String TAG = BackgroundRequests.class.getSimpleName();
    private Dialog progressDialog;

//    private OperatorRecordHandler recordHandler = null;

    public void bind(JSInteraction interaction) {
        this.jsInteraction = interaction;

        // TOAST
        jsInteraction.addJsResolver(JSTaskTypes.TOAST, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<ParamText>(true) {
                    @Override
                    protected JSONObject onJsCall(ParamText args) {
                        Toast.makeText(mContext, args.getText(),
                                Toast.LENGTH_LONG).show();
                        return null;
                    }
                };
            }
        });

//        // JSAPI_ABILITY
//        jsInteraction.addJsResolver(JSTaskTypes.JSAPI_ABILITY,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        return new JSInteraction.JsResolver<Void>(true) {
//                            @Override
//                            protected JSONObject onJsCall(Void args) {
//                                JSONObject object = new JSONObject();
//                                try {
//                                    object.put("ability",
//                                            JSInteraction.JSAPI_ABILITY_VERSION);
//                                    object.put("serverUrl", Config.getHost());
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                return object;
//                            }
//                        };
//                    }
//                });

        // 震动
//        jsInteraction.addJsResolver(JSTaskTypes.VIBARATE,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        return new JSInteraction.JsResolver<ParamVibarate>(true) {
//                            @Override
//                            protected JSONObject onJsCall(ParamVibarate args) {
//                                int milliseconds = 500;
//                                if (args != null && args.getDuration() > 0) {
//                                    milliseconds = args.getDuration();
//                                }
//                                Vibrator vibrator = (Vibrator) mContext
//                                        .getSystemService(Context.VIBRATOR_SERVICE);
//                                vibrator.vibrate(milliseconds);
//                                return null;
//                            }
//                        };
//                    }
//                });

        // ALERT
        jsInteraction.addJsResolver(JSTaskTypes.ALERT, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<ParamAlert>(false) {
                    @Override
                    protected JSONObject onJsCall(ParamAlert args) {

                        if (args != null && !TextUtils.isEmpty(args.getMessage()) && !TextUtils.isEmpty(args.getButtonName())) {
                            DialogContent.getInstances().setCancelable(false);
                            AlertDialogWidget
                                    .getInstance(mContext)
                                    .createAlertDialog(
                                            args.getTitle(),
                                            args.getMessage(),
                                            args.getButtonName(),
                                            new AlertDialogWidget.PopupDialogClick() {
                                                @Override
                                                public void onDialogBottonButtonClick() {
                                                    setResult(null);
                                                    DialogContent.getInstances().setCancelable(true);
                                                }
                                            });
                        } else {
                            setError(JSInteraction.ERROR_BAD_ARGUMENT);
                        }

                        return null;
                    }
                };
            }
        });

        // CONFIRM
        jsInteraction.addJsResolver(JSTaskTypes.CONFIRM,
                new JSResolverFactory() {
                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamConfirm>(false) {

                            private void sendToJs(int buttonIndex) {
                                setResultKeyValue("buttonIndex", buttonIndex);
                                DialogContent.getInstances()
                                        .setCancelable(true);
                            }

                            @Override
                            protected JSONObject onJsCall(ParamConfirm args) {

                                if (args.getButtonLabels().length != 2) {
                                    setError(JSInteraction.ERROR_BAD_ARGUMENT);
                                    return null;
                                }

                                DialogContent.getInstances().setCancelable(
                                        false);
                                AlertDialogWidget
                                        .getInstance(mContext)
                                        .createAlertDialog(
                                                args.getTitle(),
                                                args.getMessage(),
                                                args.getButtonLabels()[1],
                                                new AlertDialogWidget.PopupDialogClick() {
                                                    @Override
                                                    public void onDialogBottonButtonClick() {
                                                        sendToJs(1);
                                                    }
                                                },
                                                args.getButtonLabels()[0],
                                                new AlertDialogWidget.PopupDialogClick() {
                                                    @Override
                                                    public void onDialogBottonButtonClick() {
                                                        sendToJs(0);
                                                    }
                                                });

                                return null;
                            }
                        };
                    }
                });

        // PROMPT
        jsInteraction.addJsResolver(JSTaskTypes.PROMPT,
                new JSResolverFactory() {
                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamConfirm>(false) {

                            private void sendToJs(int buttonIndex, String input) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("buttonIndex", buttonIndex);
                                    jsonObject.put("value", input);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                setResult(jsonObject);
                            }


                            @Override
                            protected JSONObject onJsCall(ParamConfirm args) {

                                final EditText editText = new EditText(mContext);
                                editText.setSingleLine(true);
                                // 不允许输入符号，防止出错
                                final List<Character> forbiddenChars = new ArrayList<Character>();
                                forbiddenChars.add('\'');
                                forbiddenChars.add('"');
                                editText.setFilters(new InputFilter[]{new InputFilter() {
                                    public CharSequence filter(
                                            CharSequence source, int start,
                                            int end, Spanned dest, int dstart,
                                            int dend) {
                                        for (int i = start; i < end; i++) {
                                            if (forbiddenChars.contains(source
                                                    .charAt(i))) {
                                                return "";
                                            }
                                        }
                                        return null;
                                    }
                                }});

                                new AlertDialog.Builder(mContext)
                                        .setTitle(args.getTitle())
                                        .setCancelable(false)
                                        .setMessage(args.getMessage())
                                        .setIcon(
                                                android.R.drawable.ic_dialog_info)
                                        .setView(editText)
                                        .setPositiveButton(
                                                args.getButtonLabels()[1],
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        dialog.dismiss();
                                                        sendToJs(1, editText
                                                                .getText()
                                                                .toString());
                                                    }
                                                })
                                        .setNegativeButton(
                                                args.getButtonLabels()[0],
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int i) {
                                                        dialog.dismiss();
                                                        sendToJs(0, editText
                                                                .getText()
                                                                .toString());
                                                    }
                                                }).show();

                                return null;
                            }
                        };
                    }
                });

//        // ACTION_SHEET
        jsInteraction.addJsResolver(JSTaskTypes.CHOSEN,
                new JSResolverFactory() {
                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ChosenEntity>(
                                false) {

                            void selectIndex(ChosenEntity.Item item) {
                                if (item == null) {
                                    setError(JSInteraction.ERROR_USER_CANCELLED);
                                } else {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("key", item.getKey());
                                        jsonObject.put("value", item.getValue());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    setResult(jsonObject);
                                }
                            }

                            @Override
                            protected JSONObject onJsCall(final ChosenEntity args) {

                                new AlertDialog.Builder(mContext)
                                        .setCancelable(true)
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                selectIndex(null);
                                            }
                                        })
                                        .setSingleChoiceItems(
                                                args.getKeys(),
                                                -1,
                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        dialog.dismiss();
                                                        selectIndex(args.getSource()[which]);
                                                    }
                                                }).show();

                                return null;
                            }
                        };
                    }
                });
//
//        //MODEL
//        jsInteraction.addJsResolver(JSTaskTypes.MODEL_DIALOG, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamModelDialog>(false) {
//
//                    private AlertDialog dialog;
//
//                    @Override
//                    protected JSONObject onJsCall(ParamModelDialog args) {
//
//                        LayoutInflater mInflater = (LayoutInflater) mContext
//                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                        View view = mInflater.inflate(R.layout.plugin_model, null);
//                        TextView textViewTitle = (TextView) view.findViewById(R.id.modelTitle);
//                        textViewTitle.setText(args.getTitle());
//                        TextView textViewContent = (TextView) view.findViewById(R.id.modelContent);
//                        textViewContent.setText(args.getContent());
//                        ImageView imageView = (ImageView) view.findViewById(R.id.modelImage);
//                        SOSApplication.getGlobalImageLoader().displayImage(args.getImage(), imageView,
//                                ImageOptionsFactory.getCircleBitmapOption(Tools.dip2px(mContext, 4), R.drawable.default_image));
//
//                        if (args.getButtonLabels().length > 0) {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_DARK)
//                                    .setCancelable(false)
//                                    .setView(view)
//                                    .setNegativeButton(args.getButtonLabels()[0], new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialog.dismiss();
//                                            setResultKeyValue("buttonIndex", 0);
//                                        }
//                                    });
//
//                            if (args.getButtonLabels().length > 1) {
//                                builder.setPositiveButton(args.getButtonLabels()[1], new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialog.dismiss();
//                                        setResultKeyValue("buttonIndex", 1);
//                                    }
//                                });
//                            }
//                            dialog = builder.create();
//                            dialog.show();
//                        }
//
//                        return null;
//                    }
//                };
//            }
//        });

        // SHOW_PRELOAD
        jsInteraction.addJsResolver(JSTaskTypes.SHOW_PRELOADER,
                new JSResolverFactory() {
                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamPreloader>(
                                true) {
                            @Override
                            protected JSONObject onJsCall(ParamPreloader args) {
                                showLoading(true, args.getText());
                                return null;
                            }
                        };
                    }
                });

        // HIDE_PRELOAD
        jsInteraction.addJsResolver(JSTaskTypes.HIDE_PRELOADER,
                new JSResolverFactory() {
                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<Void>(true) {
                            @Override
                            protected JSONObject onJsCall(Void args) {
                                showLoading(false, "");
                                return null;
                            }
                        };
                    }
                });

        // DATE_PICKER
        jsInteraction.addJsResolver(JSTaskTypes.DATE_PICKER,
                new JSResolverFactory() {

                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamTimeFormat>(
                                false) {

                            private DatePickerDialog dialog;
                            private SimpleDateFormat dateFormat;

                            @Override
                            protected JSONObject onJsCall(
                                    final ParamTimeFormat timeFormat) {

                                dateFormat = new SimpleDateFormat(timeFormat
                                        .getFormat());
                                Date date = new Date();
                                if (!TextUtils.isEmpty(timeFormat.getValue())) {
                                    try {
                                        if (!TextUtils.isEmpty(timeFormat.getValue())) {
                                            date = dateFormat.parse(timeFormat
                                                    .getValue());
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        setError(JSInteraction.ERROR_BAD_ARGUMENT);
                                        return null;
                                    }
                                }

                                final Calendar calendar = Calendar
                                        .getInstance();
                                calendar.setTime(date);

                                dialog = new DatePickerDialog(
                                        mContext,
                                        DatePickerDialog.THEME_HOLO_LIGHT,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(
                                                    DatePicker view, int year,
                                                    int monthOfYear,
                                                    int dayOfMonth) {
                                                calendar.set(year, monthOfYear,
                                                        dayOfMonth);
                                                String t = dateFormat
                                                        .format(calendar
                                                                .getTime());
                                                setResultKeyValue("value", t);
                                                dialog.dismiss();
                                            }
                                        }, calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH), calendar
                                        .get(Calendar.DAY_OF_MONTH));

                                dialog.setCancelable(false);

                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        setError(JSInteraction.ERROR_USER_CANCELLED);
                                    }
                                });

                                dialog.show();

                                return null;
                            }
                        };
                    }
                });

        // TIME_PICKER
        jsInteraction.addJsResolver(JSTaskTypes.TIME_PICKER,
                new JSResolverFactory() {

                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamTimeFormat>(
                                false) {

                            private TimePickerDialog dialog;
                            private SimpleDateFormat dateFormat;

                            @Override
                            protected JSONObject onJsCall(
                                    final ParamTimeFormat timeFormat) {
                                dateFormat = new SimpleDateFormat(timeFormat
                                        .getFormat());
                                final int hourOfDay, minute;
                                Date date = new Date();
                                if (!TextUtils.isEmpty(timeFormat.getValue())) {
                                    try {
                                        if (!TextUtils.isEmpty(timeFormat
                                                .getValue())) {
                                            date = dateFormat.parse(timeFormat
                                                    .getValue());
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        setError(JSInteraction.ERROR_BAD_ARGUMENT);
                                        return null;
                                    }
                                }

                                final Calendar calendar = Calendar
                                        .getInstance(TimeZone.getDefault());
                                calendar.setTime(date);

                                dialog = new TimePickerDialog(
                                        mContext,
                                        DatePickerDialog.THEME_HOLO_LIGHT,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker timePicker,
                                                    int hour, int min) {
                                                calendar.set(
                                                        Calendar.HOUR_OF_DAY,
                                                        hour);
                                                calendar.set(Calendar.MINUTE,
                                                        min);
                                                String t = dateFormat
                                                        .format(calendar
                                                                .getTime());
                                                setResultKeyValue("value", t);
                                                dialog.dismiss();
                                            }
                                        }, calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE), true);

                                dialog.setCancelable(false);

                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        setError(JSInteraction.ERROR_USER_CANCELLED);
                                    }
                                });


                                dialog.show();

                                return null;
                            }
                        };
                    }
                });

        // DATE_TIME_PICKER
        jsInteraction.addJsResolver(JSTaskTypes.DATE_TIME_PICKER,
                new JSResolverFactory() {

                    @Override
                    public JSInteraction.JsResolver create(int taskId) {
                        return new JSInteraction.JsResolver<ParamTimeFormat>(
                                false) {

                            private AlertDialog dialog;
                            private SimpleDateFormat dateFormat;

                            @Override
                            protected JSONObject onJsCall(
                                    final ParamTimeFormat timeFormat) {
                                dateFormat = new SimpleDateFormat(timeFormat
                                        .getFormat());

                                Date date = new Date();
                                if (!TextUtils.isEmpty(timeFormat.getValue())) {
                                    try {
                                        date = dateFormat.parse(timeFormat
                                                .getValue());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        setError(JSInteraction.ERROR_BAD_ARGUMENT);
                                        return null;
                                    }
                                }

                                final Calendar calendar = Calendar
                                        .getInstance();
                                calendar.setTime(date);

                                LinearLayout dateTimeLayout = (LinearLayout) mContext
                                        .getLayoutInflater().inflate(
                                                R.layout.common_datetime, null);
                                final DatePicker datePicker = (DatePicker) dateTimeLayout
                                        .findViewById(R.id.datepicker);
                                final TimePicker timePicker = (TimePicker) dateTimeLayout
                                        .findViewById(R.id.timepicker);

                                datePicker
                                        .setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                                timePicker
                                        .setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

                                datePicker.init(calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        null);
                                timePicker.setCurrentHour(calendar
                                        .get(Calendar.HOUR_OF_DAY));
                                timePicker.setCurrentMinute(calendar
                                        .get(Calendar.MINUTE));
                                timePicker.setIs24HourView(true);

                                dialog = new AlertDialog.Builder(mContext)
                                        .setTitle("请选择")
                                        .setView(dateTimeLayout)
                                        .setPositiveButton(
                                                "确定",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int whichButton) {
                                                        calendar.set(
                                                                datePicker
                                                                        .getYear(),
                                                                datePicker
                                                                        .getMonth(),
                                                                datePicker
                                                                        .getDayOfMonth(),
                                                                timePicker
                                                                        .getCurrentHour(),
                                                                timePicker
                                                                        .getCurrentMinute());
                                                        String t = dateFormat
                                                                .format(calendar
                                                                        .getTime());
                                                        setResultKeyValue(
                                                                "value", t);
                                                        dialog.dismiss();
                                                    }
                                                }).setCancelable(true).show();
                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        setError(JSInteraction.ERROR_USER_CANCELLED);
                                    }
                                });
                                return null;
                            }
                        };
                    }
                });

        // ONLINE_TEXT
//        jsInteraction.addJsResolver(JSTaskTypes.ONLINE_TEXT,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        return new JSInteraction.JsResolver<ParamOnlineText>(
//                                false) {
//                            @Override
//                            protected JSONObject onJsCall(ParamOnlineText args) {
//                                if (args.getUrl() == null
//                                        || args.getUrl().isEmpty()) {
//                                    setError("请求地址不能为空");
//                                    return null;
//                                }
//                                if (args.getJsonObj() == null) {
//                                    setError("请求数据不能为空");
//                                    return null;
//                                }
//                                final String functionType = args
//                                        .getFunctionType();
//                                // 构造请求参数
//                                RequestParams params = new RequestParams();
//                                try {
//                                    JSONObject json = new JSONObject(args
//                                            .getJsonObj());
//                                    Iterator keys = json.keys();
//                                    while (keys.hasNext()) {
//                                        String key = (String) keys.next();
//                                        String value = json.getString(key);
//                                        params.put(key, value);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                String requestData = RequestInfo
//                                        .requestParamsToJson(params);
//                                AsyncHttpClient httpClient = SOSApplication.getGlobalHttpClient();
//                                httpClient.post(mContext, args.getUrl(),
//                                        params,
//                                        new OnlineHttpResponseHandler(this,
//                                                functionType, requestData));
//                                showLoading(true, "正在上传");
//                                return null;
//                            }
//                        };
//                    }
//                });

        // OFFLINE_TEXT
//        jsInteraction.addJsResolver(JSTaskTypes.OFFLINE_TEXT,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        return new JSInteraction.JsResolver<ParamOnlineText>(
//                                false) {
//                            @Override
//                            protected JSONObject onJsCall(ParamOnlineText args) {
//                                if (args.getUrl() == null
//                                        || args.getUrl().isEmpty()) {
//                                    setError("请求地址不能为空");
//                                    return null;
//                                }
//                                if (args.getJsonObj() == null) {
//                                    setError("请求数据不能为空");
//                                    return null;
//                                }
//                                final String functionType = args
//                                        .getFunctionType();
//                                // 构造请求参数
//                                RequestParams requestParams = new RequestParams();
//                                try {
//                                    JSONObject json = new JSONObject(args
//                                            .getJsonObj());
//                                    Iterator keys = json.keys();
//                                    while (keys.hasNext()) {
//                                        String key = (String) keys.next();
//                                        String value = json.getString(key);
//                                        requestParams.put(key, value);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                UploadUtils uploadUtils = new UploadUtils(
//                                        mContext);
//                                uploadUtils
//                                        .setOnSavedListener(BackgroundRequests.this);
//                                uploadUtils.syncUpload(Config.getUploadUrl(),
//                                        functionType, requestParams);
//                                // 设置返回值
//                                JSONObject result = new JSONObject();
//                                try {
//                                    result.put("result", "操作成功");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                setResult(result);
//                                return null;
//                            }
//                        };
//                    }
//                });
//        //
//        // OFFLINE_IMAGE
//        jsInteraction.addJsResolver(JSTaskTypes.OFFLINE_IMAGE,
//                new JSResolverFactory() {
//                    @Override
//                    public JSInteraction.JsResolver create(int taskId) {
//                        return new JSInteraction.JsResolver<ParamOfflineImage>(
//                                true) {
//                            @Override
//                            protected JSONObject onJsCall(ParamOfflineImage args) {
//                                if (args.getImagePath() == null
//                                        || args.getImagePath().isEmpty()) {
//                                    setError("照片数据为空");
//                                } else {
//                                    PhotoImageHandler photoImageHandler = new PhotoImageHandler(
//                                            mContext);
//                                    long id = photoImageHandler.insertImgList(
//                                            args.getCustomerCode(),
//                                            args.getImagePath());
//                                    photoImageHandler.uploadImage(id);
//                                    // 设置返回值
//                                    JSONObject result = new JSONObject();
//                                    try {
//                                        result.put("result", "操作成功");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    setResult(result);
//                                }
//                                return null;
//                            }
//                        };
//                    }
//                });
//
//        //LOCAL DATA
//        jsInteraction.addJsResolver(JSTaskTypes.LOCAL_DATA, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamLocalData>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamLocalData args) {
//                        String method = args.getMethod();
//                        Map<String, String> arguments = args.getArguments();
//                        JSONArray array = PluginLocalDataUtils.getResult(method, arguments);
//                        JSONObject json = new JSONObject();
//                        try {
//                            json.put("data", array);
//                        } catch (JSONException e) {
//                            HLog.e(TAG, e.toString());
//                        }
//                        return json;
//                    }
//                };
//            }
//        });
//
//        //SHARE
//        jsInteraction.addJsResolver(JSTaskTypes.SHARE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamShare>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamShare args) {
//                        Tools.showShare(mContext, args.getTitle(), args.getUrl(), args.getContent(), args.getUrl(), args.getImage(), null, null, args.getUrl());
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //打开本地页面
//        jsInteraction.addJsResolver(JSTaskTypes.OPEN, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamOpenActivity>(false) {
//                    @Override
//                    protected JSONObject onJsCall(ParamOpenActivity args) {
//                        String name = args.getName();
//                        boolean validType = false;
//                        if (!TextUtils.isEmpty(name)) {
//                            if (name.equalsIgnoreCase("profile:points")) {
//                                validType = true;
//                                mContext.startActivity(new Intent(mContext, TodayTaskRewardActivity.class));
//                            } else if (name.equalsIgnoreCase("profile:invite")) {
//                                validType = true;
//                                mContext.startActivity(new Intent(mContext, InviteFriend.class));
//                            } else if (name.equalsIgnoreCase("im:chat")) {
//                                validType = true;
//                                String deviceId = args.getParams().get("deviceId");
//                                String nickname = args.getParams().get("nickname");
//                                PluginFunUtil.initiateChat(mContext, deviceId, nickname);
//                            } else if (name.equalsIgnoreCase("customer:detail")) {
//                                validType = true;
//                                String customerCode = args.getParams().get("customerCode");
//                                PluginFunUtil.openCustomerDetail(mContext, customerCode);
//                            } else if (name.equalsIgnoreCase("im:personDetail")) {
//                                validType = true;
//                                String deviceId = args.getParams().get("deviceId");
//                                String empCode = args.getParams().get("empCode");
//                                if (!TextUtils.isEmpty(deviceId)) {
//                                    PluginFunUtil.openUserDetail(mContext, deviceId);
//                                } else if (!TextUtils.isEmpty(empCode)) {
//                                    PluginFunUtil.openUserDetailByEmpCode(mContext, empCode);
//                                }
//                            } else if (name.equalsIgnoreCase("employee:add")) {
//                                Intent intent = new Intent(mContext, InviteColleagueActivity.class);
//                                String deptCode = args.getParams().get("deptCode");
//                                if (!TextUtils.isEmpty(deptCode)) {
//                                    intent.putExtra("deptCode", deptCode);
//                                }
//                                mContext.startActivity(intent);
//                            } else if (name.equalsIgnoreCase("mustreceived:send")) {
//                                String empCode = args.getParams().get("empCode");
//                                ArrayList<String> codeList = new ArrayList<String>();
//                                if (!TextUtils.isEmpty(empCode)) {
//                                    String codeArray[] = empCode.split(",");
//                                    for (String code : codeArray) {
//                                        if (!TextUtils.isEmpty(code)) {
//                                            codeList.add(code);
//                                        }
//                                    }
//                                }
//                                DuangSendActivity.startSelf(mContext, codeList);
//                            }
//                        }
//
//                        if (validType) {
//                            setResult(null);
//                        } else {
//                            setError(JSInteraction.ERROR_BAD_ARGUMENT);
//                        }
//                        return null;
//                    }
//                };
//            }
//        });
//
//        jsInteraction.addJsResolver(JSTaskTypes.ACCOUNT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<Void>(true) {
//                    @Override
//                    protected JSONObject onJsCall(Void args) {
//                        Context context = SOSApplication.getAppContext();
//                        String deviceCode = DeviceInfo.getDeviceId(mContext);
//
//                        JSONObject jsonObject = new JSONObject();
//                        UserInfo userInfo = UserInfo.getUserInfo();
//                        if (userInfo == null) {
//                            return jsonObject;
//                        }
//
//                        String deviceId = userInfo.getAccount();
//                        String entCode = userInfo.getEntCode();
//
//
//                        boolean isAdmin = Config.isManagerOrOwner();
//
//                        /**
//                         *
//
//                         loginId:'',//
//                         deviceId: '13011111111',//  ID
//                         version:'',//
//                         isSysAdmin: '0',//-1         ,0       ,1
//                         entCode: 'v40new',//
//                         uid:'',
//                         sessionId:'',
//                         orgCode:'',//
//                         orgName:'',//
//                         deviceCode: 'XXXXXFFFFFFFXXXXXXVVVV',//
//                         experienceAccount: '',//TODO:
//
//                         */
//
//
//                        try {
//                            jsonObject.put("entCode", entCode);
//                            jsonObject.put("deviceId", deviceId);
//                            jsonObject.put("deviceCode", deviceCode);
//                            jsonObject.put("isAdmin", (isAdmin ? "1" : "0"));
//                            jsonObject.put("version", CommonParams.getVersionName(context));
//
//                            jsonObject.put("orgCode", userInfo.getOrgCode());
//                            jsonObject.put("orgName", userInfo.getOrgName());
//                            jsonObject.put("isSysAdmin", userInfo.getIsSysAdmin());
//
//                            if (Config.isLoggedIn()) {
//                                jsonObject.put("loginId", userInfo.getAccount());
//                                jsonObject.put("sessionId", userInfo.getSessionId());
//                                jsonObject.put("uid", userInfo.getUid());
//                                jsonObject.put("empCode", userInfo.getEmpCode());
//                            }
//
//                            if (Config.isDemo()) {
//                                String experienceAccount;
//                                if (Config.isOfficalToDemo()) {
//                                    experienceAccount = PrefUtils.getOfficialAccount();
//                                } else {
//                                    experienceAccount = PrefUtils.getSavedDemoPhonenum();
//                                }
//                                jsonObject.put("experienceAccount", experienceAccount);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        return jsonObject;
//                    }
//                };
//            }
//        });
//
//        //用户行为日志
//        jsInteraction.addJsResolver(JSTaskTypes.USER_TRACK, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamUserTrack>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamUserTrack args) {
//                        boolean success = UserTrack.onJsUserTrack(args.getMethod(), args.getArguments());
//                        if (!success) {
//                            setError(JSInteraction.ERROR_BAD_ARGUMENT);
//                        }
//                        return null;
//                    }
//                };
//            }
//        });

        //静默获取地理位置
        jsInteraction.addJsResolver(JSTaskTypes.SILENT_LOCATION, new JSResolverFactory() {
            @Override
            public JSInteraction.JsResolver create(int taskId) {
                return new JSInteraction.JsResolver<Void>(false) {
                    @Override
                    protected JSONObject onJsCall(Void args) {

                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                BDLocationHandler locationHandler = new BDLocationHandler(OMSClientApplication.getInstance());
                                locationHandler.setmHandlerListener(new BaseHandler.IHandlerListener() {
                                    @Override
                                    public <T> void onHandlerListener(T t) {
                                        Message msg = (Message) t;
                                        switch (msg.what) {
                                            case BDLocationHandler.LOCATION_SUCCESS:
                                                BDPointInfo bdPointInfo = (BDPointInfo) msg.obj;
                                                if (bdPointInfo != null) {
                                                    String poi = new Gson().toJson(bdPointInfo, new TypeToken<BDPointInfo>() {
                                                    }.getType());
                                                    try {
                                                        setResult(new JSONObject(poi));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case BDLocationHandler.LOCATION_FAILD:
                                                setError(JSInteraction.ERROR_UNKOWN_ERROR);
                                                break;
                                        }
                                    }
                                });
                                locationHandler.startLocation();
                                Looper.loop();
                            }
                        }.start();

                        return null;
                    }
                };
            }
        });

        //获取指定模板数据
//        jsInteraction.addJsResolver(JSTaskTypes.GET_TEMPLATE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamTemplateType>(false) {
//                    @Override
//                    protected JSONObject onJsCall(ParamTemplateType args) {
//                        Template template = TemplateManager.getInstance().getTemplate(args.getTemplateId());
//                        if (template != null) {
//                            try {
//                                setResult(new JSONObject(new Gson().toJson(EntityConvert.dbToGson(template, TemplateGson.class))));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            setError(JSInteraction.ERROR_RECORD_NOT_FOUND);
//                        }
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //通知本地数据改变,刷新数据库
//        jsInteraction.addJsResolver(JSTaskTypes.NOTIFY_CHANGE, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<BatchResult>(true) {
//                    @Override
//                    protected JSONObject onJsCall(BatchResult args) {
//                        TemplateManager.getInstance().onNotifyChange(args);
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //保存草稿或离线上传
//        jsInteraction.addJsResolver(JSTaskTypes.SAVE_DETAIL, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamSaveDetail>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamSaveDetail args) {
//                        TemplateManager.getInstance().handleSaveDetail(args);
//                        return null;
//                    }
//                };
//            }
//        });
//
//
//        //获取用户填写的模板详情
//        jsInteraction.addJsResolver(JSTaskTypes.GET_DETAIL, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamGetDetail>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamGetDetail args) {
//                        TemplateRecord detailItem = TemplateManager.getInstance().getDetail(args);
//                        if (detailItem == null) {
//                            setError(JSInteraction.ERROR_RECORD_NOT_FOUND);
//                        } else {
//                            try {
//                                return new JSONObject(new Gson().toJson(detailItem));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                setError(JSInteraction.ERROR_UNKOWN_ERROR);
//                            }
//                        }
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //删除用户草稿
//        jsInteraction.addJsResolver(JSTaskTypes.DEL_DRAFT, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamDelDraft>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamDelDraft args) {
//                        TemplateManager.getInstance().deleteDraft(args.templateType, args.draftId);
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //日志获取下一条,上一条
//        jsInteraction.addJsResolver(JSTaskTypes.GET_NEXT_DETAIL, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<ParamNextDetail>(true) {
//                    @Override
//                    protected JSONObject onJsCall(ParamNextDetail args) {
//                        TemplateDetail templateDetail = DailyManager.getNextOrPre(args);
//                        if (templateDetail != null) {
//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                jsonObject.put("detailId", templateDetail.getDetailId());
//                                jsonObject.put("templateId", templateDetail.getTemplateId());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            return jsonObject;
//                        }
//                        setError(JSInteraction.ERROR_RECORD_NOT_FOUND);
//                        return null;
//                    }
//                };
//            }
//        });
//
//        //获取指定模板数据
//        jsInteraction.addJsResolver(JSTaskTypes.SHARE_CARD, new JSResolverFactory() {
//            @Override
//            public JSInteraction.JsResolver create(int taskId) {
//                return new JSInteraction.JsResolver<JsonElement>(true) {
//                    @Override
//                    protected JSONObject onJsCall(JsonElement args) {
//                        String card = new Gson().toJson(args.getAsJsonObject().get("data"));
////                        Toast.makeText(mContext,"分享卡片功能待添加,json:"+card,Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(mContext, ShareActivity.class);
//                        intent.putExtra(ShareActivity.START_MODE, ShareActivity.START_MODE_CARD);
//                        intent.putExtra(ShareActivity.IMCARD, card);
//                        mContext.startActivity(intent);
//                        return null;
//                    }
//                };
//            }
//        });

    }

    /**
     * 在线上传文本回调
     *
     * @author HEcom
     */
//    class OnlineHttpResponseHandler extends HecomHttpResponseHandler {
//
//        private JSInteraction.JsResolver jsResolver;
//
//        private String functionType;
//
//        private String requestData;
//
//        public OnlineHttpResponseHandler(JSInteraction.JsResolver resolver,
//                                         String fType, String rData) {
//            jsResolver = resolver;
//            functionType = fType;
//            requestData = rData;
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers,
//                              String responseString, Throwable throwable) {
//            jsResolver.setError(responseString);
//        }
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers,
//                              String responseString) {
//            try {
//                jsResolver.setResult(new JSONObject(responseString));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            saveOperatorRecord(-1, OperatorRecord.SUCCESS, functionType,
//                    requestData);
//            showLoading(false, null);
//        }
//
//    }

//    @Override
//    public void onRequestSaved(int id, String function, String requestData) {
//        saveOperatorRecord(id, OperatorRecord.OFFLINE, function, requestData);
//    }

//    /**
//     * 提交操作记录到本地工作记录表中
//     *
//     * @param recordsId
//     * @param functionType
//     * @param requestData
//     */
//    private void saveOperatorRecord(long recordsId, String status,
//                                    String functionType, String requestData) {
//        String content = ModulsContentManager.getModulsName(mContext,
//                functionType);
//        OperatorRecord vo = new OperatorRecord(String.valueOf(recordsId), null, content, requestData,
//                new Date().getTime(), status);
//        recordHandler.insertOperRecords(vo);
//    }
    public void showLoading(boolean visible, String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (visible) {
            progressDialog = AlertDialogWidget.getInstance(mContext)
                    .createProgressDialog("", text);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

}
