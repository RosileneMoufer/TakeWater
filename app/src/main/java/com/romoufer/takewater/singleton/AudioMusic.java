package com.romoufer.takewater.singleton;

import android.media.Ringtone;

public class AudioMusic {

    Ringtone ringtone;
    static AudioMusic instance;

    public static synchronized AudioMusic getInstance() {
        if (instance == null)
            instance = new AudioMusic();

        return instance;
    }

    public Ringtone getRingtone() {
        return ringtone;
    }

    public void setRingtone(Ringtone ringtone) {
        this.ringtone = ringtone;
    }

}
