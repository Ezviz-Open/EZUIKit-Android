package com.ezuikit.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ezuikit.open.scan.main.CaptureActivity;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.videogo.openapi.EZOpenSDK;
import org.json.JSONArray;
import org.json.JSONException;

import static com.ezuikit.open.DoublePlayActivity.TYPE;
import static com.ezuikit.open.PlayActivity.APPKEY;
import static com.ezuikit.open.PlayActivity.AccessToekn;
import static com.ezuikit.open.PlayActivity.Global_AreanDomain;
import static com.ezuikit.open.PlayActivity.PLAY_URL;

public class MainActivity extends Activity implements View.OnClickListener {

    /**
     * 二维码扫描按钮
     */
    private Button mButtonCode;
    /**
     * 预览播放按钮
     */
    private Button mButtonPlay;

    private CheckBox mCheckBoxBack;

    /**
     * 清除播放缓存参数按钮
     */
    private Button mButtonClear;

    /**
     * 开发者申请的Appkey
     */
    private String mAppKey;

    /**
     * 授权accesstoken
     */
    private String mAccessToken;

    /**
     * 播放url：ezopen协议
     */
    private String mUrl;

    /**
     * 海外版本areaDomin
     */
    private String mGlobalAreaDomain;

    private EditText mAppkeyEditText;

    private EditText mAccessTokenEditText;

    private EditText mUrlEditText;

    private EditText mGlobalAreanDoaminEditText;

    private TextView mTextViewVersion;

    private boolean isGlobal = false;

    private String mType = "";//UFO CATCHER为是双流切换播放,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGlobal = getIntent().getBooleanExtra(PlayActivity.Global_AreanDomain,false);
        if (isGlobal){
            findViewById(R.id.layout_global).setVisibility(View.VISIBLE);
        }
        mButtonCode = (Button) findViewById(R.id.btn_code);
        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mCheckBoxBack = (CheckBox) findViewById(R.id.checkbox_playback);
        mButtonClear = (Button) findViewById(R.id.btn_clear_cache);
        mAppkeyEditText = (EditText) findViewById(R.id.edit_appkey);
        mAccessTokenEditText = (EditText) findViewById(R.id.edit_accesstoken);
        mUrlEditText = (EditText) findViewById(R.id.edit_url);
        mTextViewVersion = (TextView) findViewById(R.id.text_version);
        mGlobalAreanDoaminEditText = (EditText) findViewById(R.id.edit_areadomain);
        mButtonCode.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mTextViewVersion.setText(EZUIKit.EZUIKit_Version+" (SDK "+ EZOpenSDK.getVersion()+")");
        if (isGlobal){
            mCheckBoxBack.setVisibility(View.GONE);
        }
        getDefaultParams();
    }

    int position = 1;
    @Override
    public void onClick(View view) {
        if (view == mButtonCode) {
            //跳转到二维码扫描页面扫描二维码获取预览所需参数appkey、accesstoken、url
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,200);
        }else if(view  == mButtonClear){
            //弹出清除数据确认框
            showClearDialog();
        } else if(view == mButtonPlay){
            mAppKey = mAppkeyEditText.getText().toString().trim();
            mAccessToken = mAccessTokenEditText.getText().toString().trim();
            mUrl = mUrlEditText.getText().toString().trim();
            mGlobalAreaDomain = mGlobalAreanDoaminEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mAppKey)){
                Toast.makeText(this,"appkey can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mAccessToken)){
                Toast.makeText(this,"accesstoken can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mUrl)){
                Toast.makeText(this,"url can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            if (isGlobal){
                if (TextUtils.isEmpty(mGlobalAreaDomain)){
                    Toast.makeText(this,"AreaDomain can not be null",Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //mAppKey = "f7562853bab8442fb57a0ba9463edacf";
            //mAccessToken = "at.19z7anlx6koz4fx76w3b4730c08eurib-2a2fvbyaes-0wb2s9a-incldujjv";
            //mUrl = "ezopen://LTVYKC@open.ys7.com/166791384/1.hd.live?mute=true";


            //mAccessToken = "at.1eusxoyx8bdhetp36621safi08616oob-5byd0nmlj4-04zf5h1-te7jx24yv";
            //mAppKey = "7c1cee6826a642a6ae810aad0b32cb32";
            //if (position++%2!=0){
            //    mUrl = "ezopen://QVRUFO@open.ys7.com/541123823.hd.live";
            //}else {
            //    mUrl = "ezopen://HTYPVB@global.open.ezviz.com/602147544.hd.live";
            //}

            //mAppKey = "00d317f1511e41f985f2c34b9c057e00";
            //mAccessToken = "at.3no5jqee27v21pav75rftx1u2cayfv8h-1ossxe2pze-1fuehbc-ntinhlrol";
            ////mAccessToken = "at.20s1gcqa4ac1621wcivw93l647p8y6e7-1hpl3li5yj-15hq165-vrtpdtilp";
            //mUrl = "ezopen://HLIPKT@open.ys7.com/203541402.hd.live";

            //mAccessToken = "at.4khtpc5f39jaddzg9oq5udr4043sy7io-3k5drnvnjk-1nhzl7y-dw40fjtwv";
            //mAppKey = "76d8a02ae81a4260a02e470ebb48077d";
            //mUrl = "ezopen://open.ys7.com/187504088/1.rec?begin=20180409000000&end=20180409152000";
            //EZOpenSDK.showSDKLog(true);
            //    EZOpenSDK.initLib(this.getApplication(),mAppKey);
            //    EZOpenSDK.getInstance().startConfigWifi(this,"","ezviz_test_liny","test12345",
            //        EZConstants.EZWiFiConfigMode.EZWiFiConfigSmart| EZConstants.EZWiFiConfigMode.EZWiFiConfigWave,
            //        new EZOpenSDKListener.EZStartConfigWifiCallback() {
            //            @Override
            //            public void onStartConfigWifiCallback(String deviceSerial,EZConstants.EZWifiConfigStatus status) {
            //                LogUtil.d("onStartConfigWifiCallback","deviceSerial  = "+deviceSerial + "    status  = "+status.name() );
            //            }
            //        });
            //
            //}else {
            //    EZOpenSDK.getInstance().stopConfigWiFi();
            //}
            //mAppKey = "e04f5aa1bad14f559686172609b0ee3a";
            //mAccessToken = "at.5gt90imy3d01cqrvbln40jyi2rxg8lpp-7p100o5dp0-0va39pl-6xeuz3nny";
            //mUrl = "ezopen://open.ys7.com/185558461/1.cloud.rec?begin=20180507084123&end=20180507084229";
            saveDefaultParams();
            String url = mUrl;
            String[] urls = mUrl.split(",");
            if (urls != null && urls.length == 2){
                //直播预览
                if (isGlobal){
                    //启动普通回放页面
                    DoublePlayActivity.startPlayActivity(this, mAppKey, mAccessToken, urls[0],urls[1],mGlobalAreaDomain);
                    return;
                }
                //启动普通回放页面
                DoublePlayActivity.startPlayActivity(this, mAppKey, mAccessToken, urls[0],urls[1]);
                return;
            }

            EZUIPlayer.EZUIKitPlayMode mode = null;
            mode = EZUIPlayer.getUrlPlayType(mUrl);
            if (mode == EZUIPlayer.EZUIKitPlayMode.EZUIKIT_PLAYMODE_LIVE){
                //直播预览
                if (isGlobal){
                    //启动播放页面
                    PlayActivity.startPlayActivityGlobal(this, mAppKey, mAccessToken, mUrl,mGlobalAreaDomain);
                    //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                    return;
                }
                //启动播放页面
                PlayActivity.startPlayActivity(this, mAppKey, mAccessToken, mUrl);
            }else if(mode == EZUIPlayer.EZUIKitPlayMode.EZUIKIT_PLAYMODE_REC){
                //回放
                if (mCheckBoxBack.isChecked()){
                    if (isGlobal){
                        //启动普通回放页面
                        PlayBackActivity.startPlayActivityGlobal(this, mAppKey, mAccessToken, mUrl,mGlobalAreaDomain);
                        //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                        return;
                    }
                    //启动回放带时间轴页面
                    PlayBackActivity.startPlayBackActivity(this, mAppKey, mAccessToken, mUrl);
                }else{
                    if (isGlobal){
                        //启动普通回放页面
                        PlayActivity.startPlayActivityGlobal(this, mAppKey, mAccessToken, mUrl,mGlobalAreaDomain);
                        //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                        return;
                    }
                    //启动普通回放页面
                    PlayActivity.startPlayActivity(this, mAppKey, mAccessToken, mUrl);
                }
            }else{
               Toast.makeText(this,"播放模式未知，默认进入直播预览模式",Toast.LENGTH_LONG).show();
                //直播预览
                if (isGlobal){
                    //启动播放页面
                    PlayActivity.startPlayActivityGlobal(this, mAppKey, mAccessToken, mUrl,mGlobalAreaDomain);
                    //应用内只能初始化一次，当首次选择了国内或者海外版本，并点击进入预览回放，此时不能再进行国内海外切换
                    return;
                }
                //启动播放页面
                PlayActivity.startPlayActivity(this, mAppKey, mAccessToken, mUrl);
            }
        }
    }

    /**
     * 清除缓存弹框
     */
    private void showClearDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setMessage(R.string.string_btn_clear_cache_sure);
        exitDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearDefaultParams();
            }
        });
        exitDialog.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }

    //二维码扫描返回值获取
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 200){
                String appkey = data.getStringExtra(APPKEY);
                String accesstoken = data.getStringExtra(AccessToekn);
                String playUrl = data.getStringExtra(PLAY_URL);
                String areadoamin = data.getStringExtra(Global_AreanDomain);
                String type = data.getStringExtra(DoublePlayActivity.TYPE);
                if (!TextUtils.isEmpty(appkey)){
                    mAppKey = appkey;
                    mAppkeyEditText.setText(appkey);
                }
                if (!TextUtils.isEmpty(accesstoken)){
                    mAccessToken = accesstoken;
                    mAccessTokenEditText.setText(accesstoken);
                }
                if (!TextUtils.isEmpty(playUrl)){
                    try {
                        JSONArray jsonArray = new JSONArray(playUrl);
                        StringBuffer displayUrl = new StringBuffer();
                        for (int i =0;i<jsonArray.length();i++){
                            if (i != 0){
                                displayUrl.append(",");
                            }
                            displayUrl.append(jsonArray.getString(i));
                        }
                        mUrl = displayUrl.toString();
                        mUrlEditText.setText(mUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mUrl = playUrl;
                        mUrlEditText.setText(mUrl);
                    }
                }
                if (!TextUtils.isEmpty(areadoamin)){
                    mGlobalAreaDomain = areadoamin;
                    mGlobalAreanDoaminEditText.setText(mGlobalAreaDomain);
                }else{
                    mGlobalAreaDomain = "";
                    mGlobalAreanDoaminEditText.setText("");
                }
                mType = type;
                saveDefaultParams();
            }
        }
    }

    /**
     * 获取缓存播放参数
     */
    private void getDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        mAppKey = sharedPreferences.getString(PlayActivity.APPKEY,"");
        mAccessToken = sharedPreferences.getString(PlayActivity.AccessToekn,"");
        mUrl = sharedPreferences.getString(PlayActivity.PLAY_URL,"");
        mGlobalAreaDomain = sharedPreferences.getString(PlayActivity.Global_AreanDomain,"");
        mType = sharedPreferences.getString(TYPE,"");
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
        mGlobalAreanDoaminEditText.setText(mGlobalAreaDomain);
    }

    /**
     * 缓存播放参数
     */
    private void saveDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PlayActivity.APPKEY,mAppKey);
        editor.putString(PlayActivity.AccessToekn,mAccessToken);
        editor.putString(PlayActivity.PLAY_URL,mUrl);
        editor.putString(DoublePlayActivity.TYPE,mType);
        if (!isGlobal){
            editor.putString(PlayActivity.Global_AreanDomain,"");
        }else{
            editor.putString(PlayActivity.Global_AreanDomain,mGlobalAreaDomain);
        }
        editor.commit();
    }

    /**
     * 清除播放参数缓存
     */
    private void clearDefaultParams(){
        mAppKey = "";
        mAccessToken = "";
        mUrl = "";
        mGlobalAreaDomain = "";
        mType = "";
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PlayActivity.APPKEY,mAppKey);
        editor.putString(PlayActivity.AccessToekn,mAccessToken);
        editor.putString(PlayActivity.PLAY_URL,mUrl);
        editor.putString(PlayActivity.Global_AreanDomain,mGlobalAreaDomain);
        editor.putString(TYPE,mType);
        editor.commit();
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
        mGlobalAreanDoaminEditText.setText(mGlobalAreaDomain);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 如果需要切换国内和海外平台需要杀掉应用重启
         *
         */
        System.exit(0);
    }
}
