package com.slodin.transalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class calPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal_page);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Receives the Intent passed on MainActivity and handles it
        //It also gets the Extra (which is a parameter - the station id and the station name (alt in the HTML) and the
        //distance to the destination
        String stationId;
        String stationLabel;
        String estimatedTime;



        //Get the parameters and update the UI
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(intent.EXTRA_SHORTCUT_NAME)){
            stationId = intent.getStringExtra(Intent.EXTRA_TEXT);
            stationLabel = intent.getStringExtra(intent.EXTRA_SHORTCUT_NAME);
            estimatedTime = intent.getStringExtra(intent.EXTRA_TITLE);

            ((TextView) findViewById(R.id.stationSelect)).setText("Destination: " + stationLabel);
            ((TextView) findViewById(R.id.estimateTimeLabel)).setText(estimatedTime + " mins");

        }

    }

    @Override
    protected void onResume(){
        super.onResume();

        String stationLabel;

        //Get the parameters and update the UI
        Intent intent = this.getIntent();
        if (intent.hasExtra(intent.EXTRA_SHORTCUT_NAME)){
            stationLabel = intent.getStringExtra(intent.EXTRA_SHORTCUT_NAME);

            ((TextView) findViewById(R.id.stationSelect)).setText(stationLabel);
            String estimatedTime = intent.getStringExtra(intent.EXTRA_TITLE);
            //((TextView) findViewById(R.id.estimateTimeLabel)).setText(estimatedTime + " mins");

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cal_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
