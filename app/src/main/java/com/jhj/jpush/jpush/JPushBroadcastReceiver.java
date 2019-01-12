package com.jhj.jpush.jpush;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.jhj.jpush.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送
 * <p>
 * Created by jhj on 19-1-12.
 */
public class JPushBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushBroadcastReceiver";

    private NotificationManager nm;
    private PushEnum pushEnum;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush 用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");
            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            openNotification(context, bundle);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String EXTRA_EXTRA = bundle.getString(JPushInterface.EXTRA_EXTRA);
        PushData pushData = new Gson().fromJson(EXTRA_EXTRA, PushData.class);
        PushEnum pushEnum = null;
        String type = "-1";
        try {
            pushEnum = PushEnum.valueOf(pushData.type);
            if (pushData.data != null) {
                pushData.data = pushData.data.replace("&quot;", "\"");
            }
            JSONObject jsonObj = new JSONObject(pushData.data);
            Iterator it = jsonObj.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if (key.equals("type")) {
                    type = jsonObj.getString(key);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pushEnum != null) {
            //通常用于设置小红点
            //EventBus.getDefault().post(new RefreshNumberEvent(String.valueOf(pushEnum), type));
        }
    }

    /**
     * 打开Activity
     *
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle) {
        if (bundle == null) {
            return;
        }

        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        PushData pushData = new Gson().fromJson(extras, PushData.class);

        try {
            //根据传递的 type 得到对应的 Activity
            pushEnum = PushEnum.valueOf(pushData.type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pushEnum != null) {
            Intent mIntent = new Intent(context, pushEnum.cls);
            //todo 根据传过来的json数据设置Intent参数
            try {
                if (pushData.data != null) {
                    pushData.data = pushData.data.replace("&quot;", "\"");
                }
                // 启动Activity所需要的Bean类
                mIntent.putExtra("data", "" + pushData.data);

                // 好像没什么用，根据实际需求而定
                JSONObject jsonObj = new JSONObject(pushData.data);
                Iterator it = jsonObj.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    mIntent.putExtra(key, jsonObj.getString(key));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:")
                            .append(key)
                            .append(", value:")
                            .append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:")
                            .append(key)
                            .append(", value:")
                            .append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                        Log.i(TAG, "This message has no Extra data");
                        continue;
                    }

                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next().toString();
                            sb.append("\nkey:")
                                    .append(key)
                                    .append(", value: [")
                                    .append(myKey)
                                    .append(" - ")
                                    .append(json.optString(myKey))
                                    .append("]");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Get message extra JSON error!");
                    }

                    break;
                default:
                    sb.append("\nkey:")
                            .append(key)
                            .append(", value:")
                            .append(bundle.getString(key));
                    break;
            }
        }
        return sb.toString();
    }

    private enum PushEnum {
        notice(MainActivity.class);

        Class<? extends Activity> cls;

        PushEnum(Class<? extends Activity> cls) {
            this.cls = cls;
        }

    }

    private class PushData {
        public String type;
        public String data;
    }

}
