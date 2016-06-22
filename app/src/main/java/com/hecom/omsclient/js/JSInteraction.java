package com.hecom.omsclient.js;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hecom.omsclient.js.entity.ParamBase;
import com.hecom.omsclient.js.entity.ParamCallFromAppResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * WebView JS - JAVA 交互类
 *
 * @author tianlupan 2015/10/22
 */
public class JSInteraction {
    //被动JS调用监听
    private Map<Integer, JsResolver> mAsyncWorks = new HashMap<Integer, JsResolver>();

    private Map<String, JSResolverFactory> mFactorys = new HashMap<String, JSResolverFactory>();

    private WebView mWebView;
    private final static String TAG = JSInteraction.class.getSimpleName();

    public static final String ERROR_UNKNOWN_TASK = "ERROR_UNKNOWN_TASK";
    public static final String ERROR_CALL_BEFORE_RETURN = "ERROR_CALL_BEFORE_RETURN";
    public static final String ERROR_BAD_ARGUMENT = "ERROR_BAD_ARGUMENT";
    public static final String ERROR_USER_CANCELLED = "ERROR_USER_CANCELLED";
    public static final String ERROR_NETWORK = "ERROR_NETWORK";
    public static final String ERROR_UNKOWN_ERROR = "ERROR_UNKOWN_ERROR";
    public static final String ERROR_SERVER_EXCEPTION = "ERROR_SERVER_EXCEPTION";

    //通用错误:记录不存在
    public static final String ERROR_RECORD_NOT_FOUND = "ERROR_RECORD_NOT_FOUND";

    /**
     * 注意：HTML页面通过window.appClient获取此类的引用,然后调用类似下面的方法:
     * window.appClient.toastMessage("hello");
     * toastMessage这种供JS调用的方法需要用@JavascriptInterface 标注
     */
    private final String CLIENT_NAME_FOR_JS = "appClient";

    private Map<String, JSListener> mListeners = new HashMap<String, JSListener>();

    public static final String JSAPI_ABILITY_VERSION = "0.0.1";

    private Map<String, OnResult> mCallFromAppListeners = new HashMap<>();

    /**
     * 获取范型,参考 {@link TypeToken#TypeToken()}
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        } else {
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return com.google.gson.internal.$Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }
    }

    public static abstract class JsResolver<P> {

        JSInteraction jsInteraction;
        boolean isSync = true;
        final Type type;
        boolean status = true;
        String errorMsg = "";
        JSONObject resultJson = new JSONObject();
        String taskType;
        //当打开新页面时取消通知JS结果
        boolean isCallBackCancelled = false;

        public JsResolver(boolean sync) {
            this.type = getSuperclassTypeParameter(this.getClass());
            this.isSync = sync;
        }

        public boolean getStatus() {
            return status;
        }

        /**
         * 异步Resolver设置错误信息并通知JS
         *
         * @param errorMsg
         */
        public void setError(String errorMsg) {
            this.errorMsg = errorMsg;
            this.status = false;
            if (!isSync) {
                jsInteraction.sendBackResult2Js(this);
            }
        }

        /**
         * 异步Resolver设置结果并通知JS
         *
         * @param result
         */
        public void setResult(JSONObject result) {
            if (isSync) {
                throw new IllegalStateException("同步Resolver需要在onJsCall中返回结果");
            } else {
                resultJson = result;
                status = true;
                jsInteraction.sendBackResult2Js(this);
            }
        }

        public void setResultKeyValue(String key, Object value) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(jsonObject);
        }

        final void onCall(String jsonArgs) {
            try {
                P args = new Gson().fromJson(jsonArgs, type);
                if (args instanceof ParamBase) {
                    if (!((ParamBase) args).isValid()) {
                        setError(ERROR_BAD_ARGUMENT);
                        return;
                    }
                }
                resultJson = onJsCall(args);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("", "Json解析失败,jsonArgs=" + jsonArgs + ",exception=" + ex.getMessage());
                if (!isSync) {
                    setError(ERROR_BAD_ARGUMENT);
                } else {
                    resultJson = null;
                    errorMsg = ERROR_BAD_ARGUMENT;
                    status = false;
                }
            }

        }

        /**
         * 需要在这个调用中设置是否成功,如果失败调用setError
         *
         * @param args
         * @return
         */
        protected abstract JSONObject onJsCall(P args);


    }

    public static abstract class JSListener<P> {

        public boolean isRegister = false;
        JSInteraction jsInteraction;
        final Type type;

        public JSListener() {
            this.type = getSuperclassTypeParameter(this.getClass());
        }

        final void onCall(String jsonArgs) {
            P args = new Gson().fromJson(jsonArgs, type);
            onJsCall(args);
        }

        protected abstract void onJsCall(P args);

        public void onClick() {
            onClick(-1);
        }

        public void onClick(int buttonIndex) {
            if (isRegister) {
                jsInteraction.onClick(this, buttonIndex);
            }
        }

        //设置JS端是否有效，如果失效，不调用JS
        public void setRegister(boolean value) {
            this.isRegister = value;
        }

        public boolean isRegister() {
            return isRegister;
        }

    }


    public JSInteraction() {
    }

    public void setWebView(WebView webView) {
        this.mWebView = webView;
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, CLIENT_NAME_FOR_JS);
    }

    /**
     * WebPage前页面打开时把没有返回的回调取消
     * 必须在主线程中调用
     */
    public void onNewPage() {

        for (Map.Entry<Integer, JsResolver> entry : mAsyncWorks.entrySet()) {
            entry.getValue().isCallBackCancelled = true;
        }

        for (Map.Entry<String, JSListener> entry : mListeners.entrySet()) {
            entry.getValue().setRegister(false);
        }

    }

    public WebView getWebView() {
        return mWebView;
    }

    @JavascriptInterface
    public void onSetListenerFromJS(final String type, final String args) {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onSetListenerFromJS(type, args);
                }
            });
            return;
        }

        JSListener listener = mListeners.get(type);
        if (listener != null) {
            listener.onCall(args);
        } else {
            Log.e(TAG, "未发现监听器, type=" + type);
        }

    }


    /**
     * APP可以向JS主动发起的请求类列数组
     */
//    private static final List<String> CALL_FROM_APP_TAKS=Arrays.asList(JSTaskTypes.GET_USER_INPUT);

    /**
     * 处理JS端发来的请求，不要手动调用此方法
     *
     * @param taskType
     * @param args
     */
    @JavascriptInterface
    public void onCallFromJS(final int taskId, final String taskType, final String args) {

        //把回调转到主线程
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onCallFromJS(taskId, taskType, args);
                }
            });
            return;
        }

        Log.d(TAG, "callFromJS, , taskId=" + taskId + ",   taskType=" + taskType + ", args=" + args + " ,thread=" + Thread.currentThread());

//        if(!TextUtils.isEmpty(taskType) && CALL_FROM_APP_TAKS.contains(taskType)){
//            onCallFromApp(taskId,taskType,args);
//            return;
//        }

        if (!TextUtils.isEmpty(taskType) && mFactorys.containsKey(taskType)) {

            //先检查有没有没完成且相同的taskType,如果有，此次请求失败
            for (Map.Entry<Integer, JsResolver> entry : mAsyncWorks.entrySet()) {
                if (entry.getValue().taskType.equals(taskType)) {
                    sendBackResult2Js(taskId, false, ERROR_CALL_BEFORE_RETURN, null);
                    return;
                }
            }

            JSResolverFactory factory = mFactorys.get(taskType);
            JsResolver resolver = factory.create(taskId);
            if (resolver.isSync) {
                resolver.onCall(args);
                if (resolver.getStatus()) {
                    sendBackResult2Js(taskId, true, null, resolver.resultJson);
                } else {
                    sendBackResult2Js(taskId, false, resolver.errorMsg, null);
                }
            } else {
                resolver.taskType = taskType;
                resolver.jsInteraction = this;
                mAsyncWorks.put(taskId, resolver);
                resolver.onCall(args);
            }
        } else {
            sendBackResult2Js(taskId, false, ERROR_UNKNOWN_TASK, null);
        }

    }


    private String wrapJsonValue(String json) {
        String combo = "'";
        boolean addCombo = true;
        if (json == null) json = "";

        json = json.replace("\"", "'");

        if (!TextUtils.isEmpty(json) && json.trim().startsWith("{")) {
            addCombo = false;
        }

        return (addCombo ? combo + json + combo : json);


    }

    /**
     * 发送请求结果给JS,
     *
     * @param result
     */
    void sendBackResult2Js(int taskId, boolean success, String errorMsg, JSONObject result) {
        JSONObject json = new JSONObject();
        try {
            json.put("taskId", taskId);
            json.put("status", success ? 1 : 0);
            if (success) {
                if (result == null) result = new JSONObject();
                json.put("status", 1);
                json.put("result", result);
            } else {
                json.put("status", 0);
                json.put("errorMsg", errorMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        runJsFunction("onAppClientResult", json.toString());
    }

    public void sendBackResult2Js(JsResolver resolver) {
        //找到对应的id
        boolean found = false;
        Integer tId = -1;
        for (Integer taskId : mAsyncWorks.keySet()) {
            if (mAsyncWorks.get(taskId) == resolver) {
                tId = taskId;
                found = true;
                break;
            }
        }

        if (found) {
            if (!resolver.isCallBackCancelled) {
                sendBackResult2Js(tId, resolver.getStatus(), resolver.errorMsg, resolver.resultJson);
            }
            mAsyncWorks.remove(tId);
            Log.d(TAG, "异步任务成功回调");
        } else {
            Log.w(TAG, "异步任务回调JS通知结果失败");
        }

    }

    /**
     * webView JS调用
     *
     * @param js 完整的JS脚本；如:  var age=30;  showReport("laotian",age); 参考 {@link #runJsFunction(String, String)}
     *           为了确保有续调用，设置为了private
     */
    private void runJs(String js) {
        final String call = "javascript:" + js;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(call);
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        //确保在主线程调用
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }

    /***
     * 调用JS端函数
     *
     * @param functionName
     * @param args
     */
    private void runJsFunction(String functionName, String args) {
        String arg = "";
        if (!TextUtils.isEmpty(args)) {
            arg += "'" + args + "'";
        }
        String js = functionName + "(" + arg + ")";
        Log.d(TAG, "runJsFunction js=" + js);
        runJs(js);
    }


    public void addJsResolver(String taskType, JSResolverFactory factory) {
        if (!TextUtils.isEmpty(taskType)) {
            mFactorys.put(taskType, factory);
        }
    }

    public void setListener(String taskType, JSListener listener) {
        if (listener == null) {
            if (mListeners.containsKey(taskType)) {
                mListeners.get(taskType).jsInteraction = null;
            }
            mListeners.remove(taskType);
        } else {
            listener.jsInteraction = this;
            mListeners.put(taskType, listener);
        }
    }

    public void onClick(JSListener listener, int buttonIndex) {
        String type = null;
        for (Map.Entry<String, JSListener> entry : mListeners.entrySet()) {
            if (entry.getValue() == listener) {
                type = entry.getKey();
                break;
            }
        }

        Log.e(TAG, "onClick listener=" + type);

        JSONObject jsonObject = new JSONObject();
        JSONObject clickArg = new JSONObject();
        try {
            jsonObject.put("type", type);
            if (buttonIndex >= 0) {
                clickArg.put("buttonIndex", buttonIndex);
            }
            jsonObject.put("clickArg", clickArg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        runJsFunction("onJSListener", jsonObject.toString());
    }

    public void callOnJSListener(String type, Boolean cangoback) {
        JSONObject params = new JSONObject();
        try {
            params.put("type", type);
            if (cangoback != null) {
                params.put("cangoback", cangoback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cangoback == null) {
            runJsFunction("onJSListener", params.toString());
        }
    }

    public void onNewLinkClose() {
        runJs("onJSPageResume()");
    }

    public interface OnResult {
        void onResult(JsonElement json);

        void onError(String errorMsg);
    }

    /**
     * 向Webview主动发起请求并获取处理结果
     *
     * @param taskType
     * @param args
     * @param onResult
     */
    public void callFromApp(String taskType, JSONObject args, OnResult onResult) {
        JSONObject jsonObject = new JSONObject();
        if (args == null) {
            args = new JSONObject();
        }
        try {
            jsonObject.put("taskId", 1);
            jsonObject.put("taskType", taskType);
            jsonObject.put("args", args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCallFromAppListeners.put(taskType, onResult);
        runJsFunction("onCallFromApp", jsonObject.toString());
    }

    /**
     * APP主动调用JS,JS通过此方法返回结果
     *
     * @param taskId
     * @param taskType
     * @param args
     */
    private void onCallFromApp(final int taskId, final String taskType, final String args) {
        Log.e(TAG, "onCallFromApp ,taskType=" + taskType + ",args=" + args);
        ParamCallFromAppResult result = new Gson().fromJson(args, ParamCallFromAppResult.class);
        OnResult onResult = mCallFromAppListeners.get(taskType);
        if (onResult != null && result.isValid()) {
            if (result.isSuccess()) {
                onResult.onResult(result.result);
            } else {
                onResult.onError(result.errorMsg);
            }
            mCallFromAppListeners.remove(taskType);
        }
    }
}
