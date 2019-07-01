package com.zhhli.sip.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * des:
 *
 * @date 2018/2/9
 * <p>
 * AudioFocus
 * https://juejin.im/post/5a153ee7f265da432717e070
 */

public class SipAudioManager {
    private static SipAudioManager instance;

    private static final String TAG = "SipAudioManager";

    private Context context;
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    private AudioFocusRequest mAudioFocusRequest;


    private Ringtone mRingTone;
    private Vibrator mVibrator;
    private static final long[] VIBRATOR_PATTERN = {0, 1000, 1000};


    public static SipAudioManager getInstance() {
        if (instance == null) {
            synchronized (SipAudioManager.class) {
                instance = new SipAudioManager();
            }
        }
        int i = 0;
        return instance;
    }


    private SipAudioManager() {
        context = SipUtils.getContext().getApplicationContext();
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void reset() {
        if (mAudioManager != null) {
            stopRingtone();
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.stopBluetoothSco();
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setWiredHeadsetOn(false);
            releaseAudioFocus();
        }
    }


    /**
     * 请求音频焦点 设置监听
     */
    public int requestAudioFocus(final AudioListener audioListener) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioFocusChangeListener == null) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    Log.d(TAG, "onAudioFocusChange: " + focusChange);
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                            audioListener.play();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                            audioListener.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT");
                            audioListener.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // ... pausing or ducking depends on your app
                            Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                            audioListener.pause();
                            break;
                        default:
                    }
                }
            };
        }

        int requestFocusResult = 0;
        if (mAudioManager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                requestFocusResult = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                Log.d(TAG, "requestAudioFocus: SDK_INT < 26 =" + requestFocusResult);
            } else {
                if (mAudioFocusRequest == null) {
                    mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                    .build())
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(mAudioFocusChangeListener)
                            .build();
                }
                requestFocusResult = mAudioManager.requestAudioFocus(mAudioFocusRequest);
                Log.d(TAG, "requestAudioFocus: SDK_INT >= 26 =" + requestFocusResult);
            }
        }
        return requestFocusResult;
    }

    /**
     * 暂停、播放完成或退到后台释放音频焦点
     * 应该先请求音频焦点
     */
    public int releaseAudioFocus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        int abandonFocusResult = 0;
        if (mAudioManager != null && mAudioFocusChangeListener != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                abandonFocusResult = mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                Log.d(TAG, "releaseAudioFocus: SDK_INT < 26 =" + abandonFocusResult);
            } else {
                if (mAudioFocusRequest != null) {
                    abandonFocusResult = mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                    Log.d(TAG, "releaseAudioFocus: SDK_INT >= 26 =" + abandonFocusResult);
                }
            }
        }
        return abandonFocusResult;
    }

    public interface AudioListener {
        void play();

        void pause();
    }


    public synchronized void startRingtone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createWaveform(VIBRATOR_PATTERN, 0));
        } else {
            mVibrator.vibrate(VIBRATOR_PATTERN, 0);
        }


        try {
            Uri mRingtoneUri = Settings.System.DEFAULT_RINGTONE_URI;
            mRingTone = RingtoneManager.getRingtone(context, mRingtoneUri);

            mRingTone.setStreamType(AudioManager.STREAM_RING);
            setRingtoneRepeat(mRingTone);

            mRingTone.play();
        } catch (Exception exc) {
            Log.e("AppAudioManager", "Error while trying to play ringtone!", exc);
        }
    }

    private void setRingtoneRepeat(Ringtone ringtone) {
        Class<Ringtone> clazz = Ringtone.class;
        try {
            Field audio = clazz.getDeclaredField("mLocalPlayer");
            audio.setAccessible(true);
            MediaPlayer target = (MediaPlayer) audio.get(ringtone);
            target.setLooping(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopRingtone() {
        mVibrator.cancel();

        if (mRingTone != null) {
            try {
                if (mRingTone.isPlaying()) {
                    mRingTone.stop();
                }
            } catch (Exception ignored) {
            }
        }
    }


    public void setSpeaker(boolean isSpeaker) {
        mAudioManager.setSpeakerphoneOn(isSpeaker);
    }

    public void setVoIPStreamMode() {
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.FLAG_PLAY_SOUND);
    }


    public boolean isPSTNCalling() {
        return mAudioManager.getMode() == AudioManager.MODE_IN_CALL;
    }


    /**
     * 切换到外放
     */
    public void changeToSpeaker() {
        //注意此处，蓝牙未断开时使用MODE_IN_COMMUNICATION而不是MODE_NORMAL
        //    mAudioManager.setMode(bluetoothIsConnected ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_NORMAL);

        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到蓝牙音箱
     */
    public void changeToBluetooth() {
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.startBluetoothSco();
        mAudioManager.setBluetoothScoOn(true);
        mAudioManager.setSpeakerphoneOn(false);
    }

/************************************************************/
//注意：以下两个方法还未验证
/************************************************************/

    /**
     * 切换到耳机模式
     */
    public void changeToHeadset() {
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到听筒
     */
    public void changeToReceiver() {
        mAudioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

}

//afChangeListener =
//                new AudioManager.OnAudioFocusChangeListener() {
//                    @Override
//                    public void onAudioFocusChange(int focusChange) {
//                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//                            // Permanent loss of audio focus
//                            // Pause playback immediately
//                           /* mediaController.getTransportControls().pause();*/
//                            // Wait 30 seconds before stopping playback
//                           /* mHandler.postDelayed(mDelayedStopRunnable,
//                                    TimeUnit.SECONDS.toMillis(30));*/
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
//                            // Pause playback
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//                            // Lower the volume, keep playing
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                            // Your app has been granted audio focus again
//                            // Raise volume to normal, restart playback if necessary
//                        }
//                    }
//                };
