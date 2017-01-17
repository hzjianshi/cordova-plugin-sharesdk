package behring.cordovasharesdk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * This class ShareSDKPlugin a string called from JavaScript.
 */
public class ShareSDKPlugin extends CordovaPlugin {
    /**平台和分享类型的值参考ShareSDK ios源码中的值*/
    /** QQ空间 */
    private final int SSDKPlatformSubTypeQZone = 6;
    /** 拷贝 */
    private final int SSDKPlatformTypeCopy = 21;
    /** 微信好友 */
    private final int SSDKPlatformSubTypeWechatSession = 22;
    /** 微信朋友圈 */
    private final int SSDKPlatformSubTypeWechatTimeline = 23;

    private static final int SHARE_TEXT = 1;
    private static final int SHARE_IMAGE = 2;
    private static final int SHARE_WEBPAGE = 3;

    private CallbackContext callbackContext;
    private PlatformActionListener platformActionStateListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            if(callbackContext!=null)
                callbackContext.success();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            if(callbackContext!=null)
                callbackContext.error(throwable.toString());
        }

        @Override
        public void onCancel(Platform platform, int i) {
            if(callbackContext!=null)
                callbackContext.error(i);
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        ShareSDK.initSDK(cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("share")) {
            int platformType = args.optInt(0);
            int shareType = args.optInt(1);
            JSONObject shareInfo = args.optJSONObject(2);
            this.share(platformType, shareType, shareInfo, callbackContext);
            return true;
        }
        return false;
    }

    private void share(int platformType, int shareType, JSONObject shareInfo, CallbackContext callbackContext) {

        switch (shareType) {
            case SHARE_TEXT:
                if(platformType == SSDKPlatformTypeCopy) {
                    this.copyLink(shareInfo, callbackContext);
                }else {
                    this.shareText(platformType, shareInfo, callbackContext);
                }

                break;
            case SHARE_IMAGE:
                this.shareImages(platformType, shareInfo, callbackContext);
                break;
            case SHARE_WEBPAGE:
                this.shareWebPage(platformType, shareInfo, callbackContext);
                break;
            default:
                break;
        }
    }

    private void copyLink(JSONObject shareInfo, final CallbackContext callbackContext) {
        ClipboardManager myClipboard = (ClipboardManager)cordova.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String text = shareInfo.optString("text");
        ClipData clipData = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(clipData);
        callbackContext.success();
    }

    private void shareText(int platformType, JSONObject shareInfo, final CallbackContext callbackContext) {
        Platform.ShareParams sp = null;
        Platform platform = null;
        switch (platformType) {
            case SSDKPlatformSubTypeWechatSession:
                sp = new Wechat.ShareParams();
                platform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case SSDKPlatformSubTypeWechatTimeline:
                sp = new WechatMoments.ShareParams();
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
            default:
                break;
        }

        sp.setShareType(Platform.SHARE_TEXT);
        sp.setText(shareInfo.optString("text"));
        platform.setPlatformActionListener(platformActionStateListener);
        platform.share(sp);
    }

    private void shareImages(int platformType, JSONObject shareInfo, final CallbackContext callbackContext) {
        Platform.ShareParams sp = null;
        Platform platform = null;
        switch (platformType) {
            case SSDKPlatformSubTypeWechatSession:
                sp = new Wechat.ShareParams();
                platform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case SSDKPlatformSubTypeWechatTimeline:
                sp = new WechatMoments.ShareParams();
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
            default:
                break;
        }

        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setImageUrl((String) shareInfo.optJSONArray("images").opt(0));
        platform.setPlatformActionListener(platformActionStateListener);
        platform.share(sp);
    }

    private void shareWebPage(int platformType, JSONObject shareInfo, final CallbackContext callbackContext) {
        Platform.ShareParams sp = null;
        Platform platform = null;
        switch (platformType) {
            case SSDKPlatformSubTypeWechatSession:
                sp = new Wechat.ShareParams();
                platform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case SSDKPlatformSubTypeWechatTimeline:
                sp = new WechatMoments.ShareParams();
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
            default:
                break;
        }

        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setImageUrl(shareInfo.optString("icon"));
        sp.setTitle(shareInfo.optString("title"));
        sp.setUrl(shareInfo.optString("url"));
        sp.setText(shareInfo.optString("text"));
        platform.setPlatformActionListener(platformActionStateListener);
        platform.share(sp);
    }
}