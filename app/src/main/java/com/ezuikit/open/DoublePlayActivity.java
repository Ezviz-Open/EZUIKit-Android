package com.ezuikit.open;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.videogo.util.LogUtil;
import java.util.Calendar;

/**
 * 预览界面
 */
public class DoublePlayActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "PlayActivity";
    public static final String APPKEY = "AppKey";
    public static final String AccessToekn = "AccessToekn";
    public static final String PLAY_URL1 = "play_url1";
    public static final String PLAY_URL2 = "play_url2";
    public static final String Global_AreanDomain = "global_arean_domain";
    public static final String TYPE = "type";

    private EZUIPlayer mEZUIPlayer1;

    private EZUIPlayer mEZUIPlayer2;

    private Button mBtnSwitchPlay;

    private MyOrientationDetector mOrientationDetector;
    /**
     * onresume时是否恢复播放
     */
    private boolean isResumePlay = false;


    /**
     *  开发者申请的Appkey
     */
    private String appkey;
    /**
     *  授权accesstoken
     */
    private String accesstoken;
    /**
     *  播放url：ezopen协议
     */
    private String playUrl1;
    /**
     *  播放url：ezopen协议
     */
    private String playUrl2;
    /**
     * 海外版本areaDomin
     */
    private String mGlobalAreaDomain;



    /**
     * 开启预览播放
     * @param context
     * @param appkey       开发者申请的appkey
     * @param accesstoken   开发者登录授权的accesstoken
     * @param url1           预览url1
     * @param url2           预览url2
     */
    public static void startPlayActivity(Context context,String appkey,String accesstoken,String url1,String url2){
        Intent intent = new Intent(context, DoublePlayActivity.class);
        intent.putExtra(APPKEY,appkey);
        intent.putExtra(AccessToekn,accesstoken);
        intent.putExtra(PLAY_URL1,url1);
        intent.putExtra(PLAY_URL2,url2);
        context.startActivity(intent);
    }

    /**
     * 开启预览播放
     * @param context
     * @param appkey       开发者申请的appkey
     * @param accesstoken   开发者登录授权的accesstoken
     * @param url1           预览url1
     * @param url2           预览url2
     */
    public static void startPlayActivity(Context context,String appkey,String accesstoken,String url1,String url2,String global_AreanDomain){
        Intent intent = new Intent(context, DoublePlayActivity.class);
        intent.putExtra(APPKEY,appkey);
        intent.putExtra(AccessToekn,accesstoken);
        intent.putExtra(PLAY_URL1,url1);
        intent.putExtra(PLAY_URL2,url2);
        intent.putExtra(Global_AreanDomain,global_AreanDomain);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG,"onCreate");
        setContentView(R.layout.activity_double_play);
        mOrientationDetector = new DoublePlayActivity.MyOrientationDetector(this);
        Intent intent = getIntent();
        appkey = intent.getStringExtra(APPKEY);
        accesstoken = intent.getStringExtra(AccessToekn);
        playUrl1 = intent.getStringExtra(PLAY_URL1);
        playUrl2 = intent.getStringExtra(PLAY_URL2);
        mGlobalAreaDomain = intent.getStringExtra(Global_AreanDomain);
        if (TextUtils.isEmpty(appkey)
                || TextUtils.isEmpty(accesstoken)
                || TextUtils.isEmpty(playUrl1) || TextUtils.isEmpty(playUrl2)){
            Toast.makeText(this,"appkey,accesstoken or playUrl is null",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mBtnSwitchPlay = (Button) findViewById(R.id.btn_switch);
        //获取EZUIPlayer实例
        mEZUIPlayer1 = (EZUIPlayer) findViewById(R.id.player_ui);

        //设置加载需要显示的view
        mEZUIPlayer1.setLoadingView(initProgressBar());
        mEZUIPlayer1.setRatio(3*1.0f/4);

        mEZUIPlayer2 = (EZUIPlayer) findViewById(R.id.player_ui2);
        mEZUIPlayer2.setLoadingView(initProgressBar());
        mEZUIPlayer2.setRatio(3*1.0f/4);


        //DisplayMetrics dm = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(dm);
        //boolean isWideScrren = mOrientationDetector.isWideScrren();
        ////竖屏
        //if (!isWideScrren) {
        //    //竖屏调整播放区域大小，宽全屏，高根据视频分辨率自适应
        //    mEZUIPlayer1.setSurfaceSize(dm.widthPixels, 0);
        //    mEZUIPlayer2.setSurfaceSize(dm.widthPixels, 0);
        //
        //}


        mBtnSwitchPlay.setOnClickListener(this);
        preparePlay();
    }

    /**
     * 创建加载view
     * @return
     */
    private ProgressBar initProgressBar() {
        ProgressBar mProgressBar = new ProgressBar(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
        mProgressBar.setLayoutParams(lp);
        return mProgressBar;
    }

    /**
     * 准备播放资源参数
     */
    private void preparePlay(){
        //设置debug模式，输出log信息
        EZUIKit.setDebug(true);
        if (TextUtils.isEmpty(mGlobalAreaDomain)) {
            //appkey初始化
            EZUIKit.initWithAppKey(this.getApplication(), appkey);

        }else{
            //appkey初始化 海外版本
            EZUIKit.initWithAppKeyGlobal(this.getApplication(), appkey,mGlobalAreaDomain);
        }
        //设置授权accesstoken
        EZUIKit.setAccessToken(accesstoken);
        //设置播放资源参数
        mEZUIPlayer1.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {
                Log.d(TAG,"onPlaySuccess");
            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {
                Log.d(TAG,"onPlayFail");
                // TODO: 2017/2/21 播放失败处理
                if (ezuiError.getErrorString().equals(EZUIError.UE_ERROR_INNER_VERIFYCODE_ERROR)){

                }else if(ezuiError.getErrorString().equalsIgnoreCase(EZUIError.UE_ERROR_NOT_FOUND_RECORD_FILES)){
                    // TODO: 2017/5/12
                    //未发现录像文件
                    Toast.makeText(DoublePlayActivity.this,getString(R.string.string_not_found_recordfile),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onVideoSizeChange(int width, int height) {
                // TODO: 2017/2/16 播放视频分辨率回调
                Log.d(TAG,"onVideoSizeChange  width = "+width+"   height = "+height);

            }
            @Override
            public void onPrepared() {
                Log.d(TAG,"onPrepared");
                //播放
                mEZUIPlayer1.startPlay();
            }
            @Override
            public void onPlayTime(Calendar calendar) {
                //Log.d(TAG,"onPlayTime");
                if (calendar != null) {
                    // TODO: 2017/2/16 当前播放时间
                    //Log.d(TAG,"onPlayTime calendar = "+calendar.getTime().toString());
                }
            }
            @Override
            public void onPlayFinish() {

            }
        });
        mEZUIPlayer1.setUrl(playUrl1);
        mEZUIPlayer2.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }
            @Override
            public void onPlayFail(EZUIError ezuiError) {

            }
            @Override
            public void onVideoSizeChange(int width, int height) {
                Log.d(TAG,"onVideoSizeChange  width = "+width+"   height = "+height);

            }
            @Override
            public void onPrepared() {
                mEZUIPlayer2.startPlay();
            }
            @Override
            public void onPlayTime(Calendar calendar) {

            }
            @Override
            public void onPlayFinish() {

            }
        });
        mEZUIPlayer2.setUrl(playUrl2);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");

        //释放资源
        mEZUIPlayer1.stopPlay();
        mEZUIPlayer1.releasePlayer();

        mEZUIPlayer2.stopPlay();
        mEZUIPlayer2.releasePlayer();
    }


    int position = 1;
    private EZUIPlayer getEZplayer(int position){
        int i = position%2;
        if (i == 0){
            LogUtil.d(TAG,"mEZUIPlayer2");
           return mEZUIPlayer2;
        }else{
            LogUtil.d(TAG,"mEZUIPlayer1");
           return mEZUIPlayer1;
        }
    }
    @Override
    public void onClick(View view) {
        if (view == mBtnSwitchPlay){
            getEZplayer(position++).setVisibility(View.INVISIBLE);
            getEZplayer(position).setVisibility(View.VISIBLE);
        }
    }

    public class MyOrientationDetector extends OrientationEventListener {

        private WindowManager mWindowManager;
        private int mLastOrientation = 0;

        public MyOrientationDetector(Context context) {
            super(context);
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        public boolean isWideScrren() {
            Display display = mWindowManager.getDefaultDisplay();
            Point pt = new Point();
            display.getSize(pt);
            return pt.x > pt.y;
        }
        @Override
        public void onOrientationChanged(int orientation) {
            int value = getCurentOrientationEx(orientation);
            if (value != mLastOrientation) {
                mLastOrientation = value;
                int current = getRequestedOrientation();
                if (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    || current == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }

        private int getCurentOrientationEx(int orientation) {
            int value = 0;
            if (orientation >= 315 || orientation < 45) {
                // 0度
                value = 0;
                return value;
            }
            if (orientation >= 45 && orientation < 135) {
                // 90度
                value = 90;
                return value;
            }
            if (orientation >= 135 && orientation < 225) {
                // 180度
                value = 180;
                return value;
            }
            if (orientation >= 225 && orientation < 315) {
                // 270度
                value = 270;
                return value;
            }
            return value;
        }
    }
}