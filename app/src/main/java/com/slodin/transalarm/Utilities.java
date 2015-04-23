package com.slodin.transalarm;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by Filipe on 2015-03-30.
 */
public class Utilities extends Activity {



    protected Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Ringtone r = null;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        r = RingtoneManager.getRingtone(this, notification);
    }


    public void setOffAlarm () {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 1000 milliseconds
        v.vibrate(1000);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();
    }

    public void stopAlarm() {

        if (r != null){
            r.stop();
        }else{
            Toast.makeText(this, "Nothing is playing!", Toast.LENGTH_LONG).show();

        }



    }

}
