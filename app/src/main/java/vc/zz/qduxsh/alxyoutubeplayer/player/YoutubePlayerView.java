package vc.zz.qduxsh.alxyoutubeplayer.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import vc.zz.qduxsh.alxyoutubeplayer.R;

public class YoutubePlayerView extends WebView {

    private static final String TAG = "Alex";

    private QualsonBridge bridge = new QualsonBridge();

    private YTParams params = new YTParams();

    private YouTubeListener youTubeListener;
    private String backgroundColor = "#000000";
    private STATE mPlayState = STATE.UNSTARTED;

    public YoutubePlayerView(Context context) {
        super(context);
        setWebViewClient(new MyWebViewClient((Activity) context));
    }

    public YoutubePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewClient(new MyWebViewClient((Activity) context));
    }

    private static final String VIDEO_ID_START_EMBED = "embed/";
    private static final String VIDEO_ID_START_NORMAL = "?v=";
    private static final String VIDEO_ID_START_SHORT = "youtu.be/";
    //在youtobe的连接中截取视频的ID
    public static String parseIDfromVideoUrl(String videoUrl){
        if(TextUtils.isEmpty(videoUrl)) {
            Log.i("Alex", "videoUrl is null");
            return "";
        }
        int startIndex = videoUrl.indexOf(VIDEO_ID_START_NORMAL);
        int prefixLength = VIDEO_ID_START_NORMAL.length();
        if(startIndex <= 0){
            startIndex = videoUrl.indexOf(VIDEO_ID_START_EMBED);
            prefixLength = VIDEO_ID_START_EMBED.length();
        }
        if(startIndex <= 0){
            startIndex = videoUrl.indexOf(VIDEO_ID_START_SHORT);
            prefixLength = VIDEO_ID_START_SHORT.length();
        }
        Log.i("Alex","startIndex=="+startIndex);
        if(startIndex != -1){
            startIndex = startIndex + prefixLength;
            int endIndex = 0;//有些url后面会带参数，不能把参数当id
            if(prefixLength == VIDEO_ID_START_NORMAL.length()){//如果当前是普通类型的url
                endIndex = videoUrl.indexOf("&");
            }else {
                endIndex = videoUrl.indexOf("?");
            }
            if(endIndex == -1)endIndex = videoUrl.length();
            Log.i("Alex","startIndex::"+startIndex+"   end=="+endIndex);
            if(startIndex < endIndex)return videoUrl.substring(startIndex,endIndex);

        }else {
            Log.i("Alex","不能解析视频的ID");
        }
        return "";
    }

    @SuppressLint("JavascriptInterface")
    public void initialize(String videoId, YouTubeListener youTubeListener, WebChromeClient webChromeClient) {
        WebSettings set = this.getSettings();
        set.setJavaScriptEnabled(true);
        set.setUseWideViewPort(true);
        set.setLoadWithOverviewMode(true);
        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setPluginState(WebSettings.PluginState.ON);
        set.setPluginState(WebSettings.PluginState.ON_DEMAND);
        set.setAllowContentAccess(true);
        set.setAllowFileAccess(true);

        if (webChromeClient != null) {
            this.setWebChromeClient(webChromeClient);
        }

        this.mPlayState = STATE.UNSTARTED;
        this.youTubeListener = youTubeListener;
        this.setLayerType(View.LAYER_TYPE_NONE, null);
        this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        this.addJavascriptInterface(bridge, "QualsonInterface");
        this.loadDataWithBaseURL("https://www.youtube.com", getVideoHTML(videoId), "text/html", "utf-8", null);
        this.setLongClickable(true);
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
    }

    public void initialize(String videoId, YTParams params, YouTubeListener youTubeListener, WebChromeClient webChromeClient) {
        if (params != null) {
            this.params = params;
        }
        initialize(videoId, youTubeListener, webChromeClient);
    }

    public void setWhiteBackgroundColor() {
        backgroundColor = "#ffffff";
    }

    public void setAutoPlayerHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.getLayoutParams().height = (int) (displayMetrics.widthPixels * 0.5625);
    }

    /**
     * APP TO WEB
     */
    public void seekToMillis(double mil) {
        Log.d(TAG, "seekToMillis : ");
        this.loadUrl("javascript:onSeekTo(" + mil + ")");
    }

    public void pause() {
        Log.d(TAG, "pause");
        this.loadUrl("javascript:onVideoPause()");
    }

    public void stop(){
        Log.d(TAG,"stop");
        this.loadUrl("javascript:onVideoStop()");
    }

    public STATE getPlayerState(){
        Log.d(TAG,"getPlayerState");
        return mPlayState;
    }

    public void play() {
        Log.d(TAG, "play");
        this.loadUrl("javascript:onVideoPlay()");
    }

    public void onLoadVideo(String videoId, float mil) {
        Log.d(TAG, "onLoadVideo : " + videoId + ", " + mil);
        this.loadUrl("javascript:loadVideo('" + videoId + "', " + mil + ")");
    }

    public void onCueVideo(String videoId) {
        Log.d(TAG, "onCueVideo : " + videoId);
        this.loadUrl("javascript:cueVideo('" + videoId + "')");
    }

    private void notifyStateChange(STATE state){
        if(youTubeListener!=null){
            youTubeListener.onStateChange(state);
        }
        this.mPlayState = state;
    }

    /**
     * WEB TO APP
     */
    private class QualsonBridge {

        @JavascriptInterface
        public void onReady(String arg) {
            Log.d(TAG, "onReady(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.onReady();
            }
        }

        @JavascriptInterface
        public void onStateChange(String arg) {
            Log.d(TAG, "onStateChange(" + arg + ")");
            if ("UNSTARTED".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.UNSTARTED);
            } else if ("ENDED".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.ENDED);
            } else if ("PLAYING".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.PLAYING);
            } else if ("PAUSED".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.PAUSED);
            } else if ("BUFFERING".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.BUFFERING);
            } else if ("CUED".equalsIgnoreCase(arg)) {
                notifyStateChange(STATE.CUED);
            }
        }

        @JavascriptInterface
        public void onPlaybackQualityChange(String arg) {
            Log.d(TAG, "onPlaybackQualityChange(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.onPlaybackQualityChange(arg);
            }
        }

        @JavascriptInterface
        public void onPlaybackRateChange(String arg) {
            Log.d(TAG, "onPlaybackRateChange(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.onPlaybackRateChange(arg);
            }
        }

        @JavascriptInterface
        public void onError(String arg) {
            Log.e(TAG, "onError(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.onError(arg);
            }
        }

        @JavascriptInterface
        public void onApiChange(String arg) {
            Log.d(TAG, "onApiChange(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.onApiChange(arg);
            }
        }

        @JavascriptInterface
        public void currentSeconds(String seconds) {
            if (youTubeListener != null) {
                youTubeListener.onCurrentSecond(Double.parseDouble(seconds));
            }
        }

        @JavascriptInterface
        public void duration(String seconds) {
            if (youTubeListener != null) {
                youTubeListener.onDuration(Double.parseDouble(seconds));
            }
        }

        @JavascriptInterface
        public void logs(String arg) {
            Log.d(TAG, "logs(" + arg + ")");
            if (youTubeListener != null) {
                youTubeListener.logs(arg);
            }
        }
    }


    /**
     * NonLeakingWebView
     */
    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) {
            // ignored
        }
    }

    public void onDestroy() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        youTubeListener = null;
        this.clearCache(true);
        this.clearHistory();
        try {
            if (sConfigCallback != null)
                sConfigCallback.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        protected WeakReference<Activity> activityRef;

        public MyWebViewClient(Activity activity) {
            this.activityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                final Activity activity = activityRef.get();
                if (activity != null)
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (RuntimeException ignored) {
                // ignore any url parsing exceptions
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished()");
        }
    }

    public interface YouTubeListener {
        void onReady();

        void onStateChange(STATE state);

        void onPlaybackQualityChange(String arg);

        void onPlaybackRateChange(String arg);

        void onError(String arg);

        void onApiChange(String arg);

        void onCurrentSecond(double second);

        void onDuration(double duration);

        void logs(String log);
    }

    public enum STATE {
        UNSTARTED,
        ENDED,
        PLAYING,
        PAUSED,
        BUFFERING,
        CUED,
        NONE
    }

    private String getVideoHTML(String videoId) {
        try {
            InputStream in = getResources().openRawResource(R.raw.players);
            if (in != null) {
                InputStreamReader stream = new InputStreamReader(in, "utf-8");
                BufferedReader buffer = new BufferedReader(stream);
                String read;
                StringBuilder sb = new StringBuilder("");

                while ((read = buffer.readLine()) != null) {
                    sb.append(read + "\n");
                }

                in.close();

                String html = sb.toString().replace("[VIDEO_ID]", videoId).replace("[BG_COLOR]", backgroundColor);
                html = html.replace("[AUTO_PLAY]", String.valueOf(params.getAutoplay())).replace("[AUTO_HIDE]", String.valueOf(params.getAutohide())).replace("[REL]", String.valueOf(params.getRel())).replace("[SHOW_INFO]", String.valueOf(params.getShowinfo())).replace("[ENABLE_JS_API]", String.valueOf(params.getEnablejsapi())).replace("[DISABLE_KB]", String.valueOf(params.getDisablekb())).replace("[CC_LANG_PREF]", String.valueOf(params.getCc_lang_pref())).replace("[CONTROLS]", String.valueOf(params.getControls())).replace("[FS]", String.valueOf(params.getFs()));
                return html;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
