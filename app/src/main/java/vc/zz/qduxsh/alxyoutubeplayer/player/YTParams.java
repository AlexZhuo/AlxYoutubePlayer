package vc.zz.qduxsh.alxyoutubeplayer.player;

/**
 * Created by JD on 2016-06-01.
 */
public class YTParams {
    /**
     * 0或1。默认值为0。用于设置初始视频是否在加载播放器时自动播放。
     */
    private int autoplay = 0;
    /**
     * 2（默认值）、1和0。此参数会指明视频控件是否会在视频开始播放之后自动隐藏。默认行为(autohide=2)表示，当播放器控件（播放按钮和音量控件等）处于可见状态时，视频进度条将淡出。
     如果将此参数设为1，则视频进度条和播放器控件将会在视频开始播放几秒钟后退出播放界面。仅在用户将鼠标移动到视频播放器上方或按键盘上的某个键时，进度条和控件才会重新显示。
     如果将此参数设为0，则视频进度条和视频播放器控件在视频播放全程和全屏状态下均会显示。
     */
    private int autohide = 1;
    /**
     * 0或1。默认值为1。此参数将表明初始视频播放结束时，播放器是否应显示相关视频。
     */
    private int rel = 0;
    /**
     * 0或1。此参数的默认值为1。如果您将此参数的值设为0，则在视频开始播放之前，播放器不会显示视频标题和上传者等信息。
     */
    private int showinfo = 1;
    /**
     * 0或1。默认值为0。将此值设为1将会停用Javascript API.
     */
    private int enablejsapi = 0;
    /**
     * 0或1。默认值为0。将此值设为1将会停用播放器键盘控件。键盘控件如下：
     空格键：播放/暂停
     向左箭头：当前视频后退10%
     向右箭头：当前视频前进10%
     向上箭头：调高音量
     向下箭头：降低音量
     */
    private int disablekb = 1;
    /**
     * 0、1或2。默认值为1。此参数会指明视频播放器控件是否会显示。对于加载Flash播放器的iframe嵌入，
     * 此参数还会定义控件何时在播放器中显示，以及播放器加载时间：
     controls=0 - 播放器控件不会在播放器中显示。对于iframe嵌入，Flash播放器会立即加载。
     controls=1 - 播放器控件会在播放器中显示。对于iframe嵌入，控件会立即显示，而且Flash播放器也会立即加载。
     controls=2 - 播放器控件会在播放器中显示。对于iframe嵌入，控件会显示，而且Flash播放器会在用户启动视频播放时加载。
     注意：参数值1和2用于提供一致的用户体验，但是，对于iframe嵌入而言，controls=2提供的性能较之controls=1已得到改进。
     目前，这两个参数值仍会在播放器中产生一些视觉方面的差异（例如，视频标题的字体大小）。
     但是，当两个参数值之间的差异对用户而言变得完全透明时，默认参数值可能会从1更改为2。
     */
    private int controls = 1;
    /**
     * 0或1。默认值为1，该值会使全屏按钮显示。将此参数设为0会阻止全屏按钮显示。
     */
    private int fs = 1;

    private String cc_lang_pref = "en";

    public int getFs() {
        return fs;
    }

    public void setFs(int fs) {
        this.fs = fs;
    }

    public int getAutoplay() {
        return autoplay;
    }

    public void setAutoplay(int autoplay) {
        this.autoplay = autoplay;
    }

    public int getAutohide() {
        return autohide;
    }

    public void setAutohide(int autohide) {
        this.autohide = autohide;
    }

    public int getRel() {
        return rel;
    }

    public void setRel(int rel) {
        this.rel = rel;
    }

    public int getShowinfo() {
        return showinfo;
    }

    public void setShowinfo(int showinfo) {
        this.showinfo = showinfo;
    }

    public int getEnablejsapi() {
        return enablejsapi;
    }

    public void setEnablejsapi(int enablejsapi) {
        this.enablejsapi = enablejsapi;
    }

    public int getDisablekb() {
        return disablekb;
    }

    public void setDisablekb(int disablekb) {
        this.disablekb = disablekb;
    }

    public String getCc_lang_pref() {
        return cc_lang_pref;
    }

    public void setCc_lang_pref(String cc_lang_pref) {
        this.cc_lang_pref = cc_lang_pref;
    }

    public int getControls() {
        return controls;
    }

    public void setControls(int controls) {
        this.controls = controls;
    }
}
