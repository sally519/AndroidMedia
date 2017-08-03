package com.uumedia.utils;

import android.content.Context;
import android.media.AudioManager;


public class VoiceUtils {

    public static AudioManager audiomanage;

    public static int getMaxVolume(Context context) {
        audiomanage = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        return audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getCurrentVolume(Context context) {
        audiomanage = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        return audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setVolume(Context context, int arg) {
        audiomanage = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, arg, 0);
    }
}
