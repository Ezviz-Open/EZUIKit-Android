package com.ezuikit.open.scan.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.ezuikit.open.DoublePlayActivity;
import com.ezuikit.open.MainActivity;
import com.ezuikit.open.PlayActivity;
import com.ezuikit.open.R;
import com.ezuikit.open.scan.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.videogo.util.LocalValidate;
import java.io.IOException;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 二维码扫描页面
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private final static String TAG = "CaptureFragment";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private InactivityTimer mInactivityTimer;

    private CameraManager cameraManager;
    private LocalValidate mLocalValidate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        cameraManager = new CameraManager((getApplicationContext()));
        hasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
        mLocalValidate = new LocalValidate();
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        viewfinderView.setCameraManager(cameraManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera();
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager)getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInactivityTimer.shutdown();
        super.onDestroy();
    }


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param resultString
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(String resultString, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (resultString == null) {
            Log.e(TAG, "handleDecode-> resultString is null");
            return;
        }
        Log.d(TAG, "handleDecode-> resultString = "+resultString);
        try{
            JSONObject jsonObject = new JSONObject(resultString);

            String appKey = jsonObject.optString("AppKey");
            String accessToken = jsonObject.optString("AccessToken");
            String global_AreanDomain = jsonObject.optString("Global_AreanDomain");
            String type = jsonObject.optString("Type");
            String apiUrl = jsonObject.optString("apiUrl");
            String url = null;
            JSONArray jsonArray =   jsonObject.optJSONArray("Url");
            JSONArray urlArray = null;
            if (jsonArray != null){
                Log.d(TAG, "jsonObject.optString(\"Url\"); = "+jsonArray.length());
                urlArray = jsonArray;
            }else{
                url = jsonObject.optString("Url");
                urlArray = new JSONArray();
                urlArray.put(url);
                Log.d(TAG, "jsonObject.optString(\"Url\"); = "+url);
            }
            if (!TextUtils.isEmpty(appKey)
                && !TextUtils.isEmpty(accessToken)
                && urlArray != null){
                Intent intent = new Intent();
                intent.putExtra(PlayActivity.APPKEY,appKey);
                intent.putExtra(PlayActivity.AccessToekn,accessToken);
                intent.putExtra(PlayActivity.Global_AreanDomain,apiUrl);
                intent.putExtra(DoublePlayActivity.TYPE,type);
                intent.putExtra(PlayActivity.PLAY_URL,urlArray.toString());
                setResult(MainActivity.RESULT_OK, intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initCamera() {
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    class PlayParams{
        public String AppKey;
        public String AccessToken;
        public String apiUrl;
        public String  Type;
    }
}
