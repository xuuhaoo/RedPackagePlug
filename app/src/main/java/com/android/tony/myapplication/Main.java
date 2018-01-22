package com.android.tony.myapplication;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.android.tony.myapplication.util.XmlToJson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_LONG;
import static com.android.tony.myapplication.DDVersionParam.DD_PACKAGE_NAME;
import static com.android.tony.myapplication.WeChatVersionParam.WECHAT_PACKAGE_NAME;
import static com.android.tony.myapplication.WeChatVersionParam.getNetworkByModelMethod;
import static com.android.tony.myapplication.WeChatVersionParam.getTransferRequest;
import static com.android.tony.myapplication.WeChatVersionParam.luckyMoneyReceiveUI;
import static com.android.tony.myapplication.WeChatVersionParam.networkRequest;
import static com.android.tony.myapplication.WeChatVersionParam.receiveLuckyMoneyRequest;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findConstructorBestMatch;
import static de.robv.android.xposed.XposedHelpers.findConstructorExactIfExists;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.newInstance;


public class Main implements IXposedHookLoadPackage {


    private static Object requestCaller;

    private static String wechatVersion = "";
    private static String dingdingVersion = "";
    private static List<LuckyMoneyMessage> luckyMoneyMessages = new ArrayList<>();


    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        log("handleLoadPackage:" + lpparam.packageName);
        new HideModule().hide(lpparam);
        hookWechat(lpparam);
        hookDingDing(lpparam);

    }

    private void hookDingDing(final LoadPackageParam lpparam) throws PackageManager.NameNotFoundException {
        if (lpparam.packageName.equals(DD_PACKAGE_NAME)) {
            if (isEmpty(dingdingVersion)) {
                Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
                String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                log("##########Found alidingding version:" + versionName);
                dingdingVersion = versionName;
                DDVersionParam.init(versionName);
            }

            findAndHookMethod("com.alibaba.sqlcrypto.sqlite.SQLiteDatabase", lpparam.classLoader, "insertWithOnConflict", String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];

                    if (TextUtils.isEmpty(tableName) || !tableName.equals("tblastmsg")) {
                        return;
                    }
                    log("##########DingdingSQLiteDatabase tableName:" + tableName);
                    if (contentValues == null) {
                        return;
                    }
                    Integer contentType = contentValues.getAsInteger("contentType");
                    //anonymousClass87 is itemClick回调 f8038L 点击监听器
                    log("##########DingdingSQLiteDatabase contentType:" + contentType);
                    log("##########DingdingSQLiteDatabase:" + contentValues.toString());

                    if (901 == contentType || 902 == contentType || 905 == contentType || 906 == contentType) {
                        getDDRedPackage(contentValues, lpparam);
                    }
                }
            });
        }
    }

    private void getDDRedPackage(ContentValues contentValues, LoadPackageParam lpparam) throws IllegalAccessException, InvocationTargetException, InstantiationException, JSONException {
        log("##########getDDRedPackage begin");
        //RedPacketsRpc.b(Long sender, String clusterId, bmk<RedPacketsClusterPickingStatus> listener)
        Object redPacketsRpc = callStaticMethod(findClass(DDVersionParam.RedPacketsRpc, lpparam.classLoader), "a");
        Object redPacketsRpc$9 = findConstructorBestMatch(findClass(DDVersionParam.RedPacketsRpc + "$9", lpparam.classLoader), redPacketsRpc.getClass(), findClass(DDVersionParam.ApiEventListener, lpparam.classLoader)).newInstance(redPacketsRpc, null);

        Object redEnvelopPickIService = callStaticMethod(findClass(DDVersionParam.ServiceFactory, lpparam.classLoader), "a", findClass("com.alibaba.android.dingtalk.redpackets.idl.service.RedEnvelopPickIService", lpparam.classLoader));
        String clusterIdJsonStr = contentValues.getAsString("extension");
        if (TextUtils.isEmpty(clusterIdJsonStr)) {
            return;
        }
        JSONObject clusterIdJson = new JSONObject(clusterIdJsonStr);
        String clusterId = clusterIdJson.optString("clusterId");
        Long sender = contentValues.getAsLong("senderId");
        callMethod(redEnvelopPickIService, "pickRedEnvelopCluster", sender, clusterId, redPacketsRpc$9);
        log("##########getDDRedPackage end");
    }

    private void hookWechat(final LoadPackageParam lpparam) throws PackageManager.NameNotFoundException {
        if (lpparam.packageName.equals(WECHAT_PACKAGE_NAME)) {
            if (isEmpty(wechatVersion)) {
                Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
                String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                log("##########Found wechat version:" + versionName);
                wechatVersion = versionName;
                WeChatVersionParam.init(versionName);
            }
            findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];
                    log("##########SQLiteDatabase tableName:" + tableName);

                    if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {
                        return;
                    }
                    Integer type = contentValues.getAsInteger("type");
                    log("##########SQLiteDatabase type:" + type);

                    if (null == type) {
                        return;
                    }
                    if (type == 436207665 || type == 469762097) {
                        handleLuckyMoney(contentValues, lpparam);
                    } else if (type == 419430449) {
                        handleTransfer(contentValues, lpparam);
                    }
                }
            });


            findAndHookMethod(receiveLuckyMoneyRequest, lpparam.classLoader, "a", int.class, String.class, JSONObject.class, new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws JSONException {
                            if (!WeChatVersionParam.hasTimingIdentifier) {
                                return;
                            }

                            if (luckyMoneyMessages.size() <= 0) {
                                return;
                            }

                            String timingIdentifier = ((JSONObject) (param.args[2])).getString("timingIdentifier");
                            if (isEmpty(timingIdentifier)) {
                                return;
                            }
                            LuckyMoneyMessage luckyMoneyMessage = luckyMoneyMessages.get(0);
                            Object luckyMoneyRequest = newInstance(findClass(WeChatVersionParam.luckyMoneyRequest, lpparam.classLoader),
                                    luckyMoneyMessage.getMsgType(), luckyMoneyMessage.getChannelId(), luckyMoneyMessage.getSendId(), luckyMoneyMessage.getNativeUrlString(), "", "", luckyMoneyMessage.getTalker(), "v1.0", timingIdentifier);
                            callMethod(requestCaller, "a", luckyMoneyRequest, getDelayTime());
                            luckyMoneyMessages.remove(0);
                        }
                    }
            );

            findAndHookMethod(luckyMoneyReceiveUI, lpparam.classLoader, WeChatVersionParam.receiveUIFunctionName, int.class, int.class, String.class, WeChatVersionParam.receiveUIParamName, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesUtils.quickOpen()) {
                        Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                        if (button.isShown() && button.isClickable()) {
                            button.performClick();
                        }
                    }
                }
            });

            findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesUtils.showWechatId()) {
                        Activity activity = (Activity) param.thisObject;
                        ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        String wechatId = activity.getIntent().getStringExtra("Contact_User");
                        cmb.setText(wechatId);
                        Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                    }
                }
            });

            findAndHookMethod("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesUtils.showWechatId()) {
                        Activity activity = (Activity) param.thisObject;
                        String wechatId = activity.getIntent().getStringExtra("RoomInfo_Id");
                        ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(wechatId);
                        Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void handleLuckyMoney(ContentValues contentValues, LoadPackageParam lpparam) throws XmlPullParserException, IOException, JSONException {
        int status = contentValues.getAsInteger("status");
        log("##########handleLuckyMoney status:" + status);

        if (status == 4) {
            return;
        }

        String talker = contentValues.getAsString("talker");
        log("##########handleLuckyMoney status:" + talker);

        String blackList = PreferencesUtils.blackList();
        if (!isEmpty(blackList)) {
            for (String wechatId : blackList.split(",")) {
                if (talker.equals(wechatId.trim())) {
                    return;
                }
            }
        }

        int isSend = contentValues.getAsInteger("isSend");
        if (PreferencesUtils.notSelf() && isSend != 0) {
            return;
        }


        if (PreferencesUtils.notWhisper() && !isGroupTalk(talker)) {
            return;
        }

        if (!isGroupTalk(talker) && isSend != 0) {
            return;
        }

        String content = contentValues.getAsString("content");
        if (!content.startsWith("<msg")) {
            content = content.substring(content.indexOf("<msg"));
        }

        JSONObject wcpayinfo = new XmlToJson.Builder(content).build()
                .getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");
        String senderTitle = wcpayinfo.getString("sendertitle");
        String notContainsWords = PreferencesUtils.notContains();
        if (!isEmpty(notContainsWords)) {
            for (String word : notContainsWords.split(",")) {
                if (senderTitle.contains(word)) {
                    return;
                }
            }
        }

        String nativeUrlString = wcpayinfo.getString("nativeurl");
        Uri nativeUrl = Uri.parse(nativeUrlString);
        int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
        int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
        String sendId = nativeUrl.getQueryParameter("sendid");
        requestCaller = callStaticMethod(findClass(networkRequest, lpparam.classLoader), getNetworkByModelMethod);

        if (WeChatVersionParam.hasTimingIdentifier) {
            callMethod(requestCaller, "a", newInstance(findClass(receiveLuckyMoneyRequest, lpparam.classLoader), channelId, sendId, nativeUrlString, 0, "v1.0"), 0);
            luckyMoneyMessages.add(new LuckyMoneyMessage(msgType, channelId, sendId, nativeUrlString, talker));
            return;
        }
        Object luckyMoneyRequest = newInstance(findClass(WeChatVersionParam.luckyMoneyRequest, lpparam.classLoader),
                msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0");

        callMethod(requestCaller, "a", luckyMoneyRequest, getDelayTime());
    }

    private void handleTransfer(ContentValues contentValues, LoadPackageParam lpparam) throws IOException, XmlPullParserException, PackageManager.NameNotFoundException, InterruptedException, JSONException {
        if (!PreferencesUtils.receiveTransfer()) {
            return;
        }
        JSONObject wcpayinfo = new XmlToJson.Builder(contentValues.getAsString("content")).build()
                .getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

        int paysubtype = wcpayinfo.getInt("paysubtype");
        if (paysubtype != 1) {
            return;
        }

        String transactionId = wcpayinfo.getString("transcationid");
        String transferId = wcpayinfo.getString("transferid");
        int invalidtime = wcpayinfo.getInt("invalidtime");

        if (null == requestCaller) {
            requestCaller = callStaticMethod(findClass(networkRequest, lpparam.classLoader), getNetworkByModelMethod);
        }

        String talker = contentValues.getAsString("talker");
        callMethod(requestCaller, "a", newInstance(findClass(getTransferRequest, lpparam.classLoader), transactionId, transferId, 0, "confirm", talker, invalidtime), 0);
    }


    private int getDelayTime() {
        int delayTime = 0;
        if (PreferencesUtils.delay()) {
            delayTime = getRandom(PreferencesUtils.delayMin(), PreferencesUtils.delayMax());
        }
        return delayTime;
    }

    private boolean isGroupTalk(String talker) {
        return talker.endsWith("@chatroom");
    }


    private int getRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }


}
