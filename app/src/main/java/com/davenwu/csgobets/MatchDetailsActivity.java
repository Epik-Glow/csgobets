package com.davenwu.csgobets;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.davenwu.csgobets.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MatchDetailsActivity extends Activity {
    private String matchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        matchUrl = "http://csgolounge.com/" + getIntent().getStringExtra(MainActivity.MATCH_URL);
        new GetStreamTask().execute(matchUrl);
        setContentView(R.layout.activity_match_details);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.match_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetStreamTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String matchUrl = urls[0];

            try {
                String matchPage = Jsoup.connect(matchUrl).get().toString();
                int channelStart = matchPage.indexOf("channel=") + 8;
                int channelEnd = matchPage.indexOf("\"", channelStart);
                return matchPage.substring(channelStart, channelEnd);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String streamUrl) {
            if(streamUrl != null) {

            }
        }
    }
}
