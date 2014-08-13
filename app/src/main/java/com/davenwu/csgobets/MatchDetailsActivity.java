package com.davenwu.csgobets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MatchDetailsActivity extends ActionBarActivity {
    private String matchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        matchUrl = "http://csgolounge.com/" + getIntent().getStringExtra(MainActivity.MATCH_URL);
        new GetMatchDetailsTask().execute(matchUrl);
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
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if(id == R.id.action_open_csgolounge) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(matchUrl));
            startActivity(intent);
            return true;
        } else if(id == R.id.action_refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        findViewById(R.id.matchDetailsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.matchDetailsMainCard).setVisibility(View.INVISIBLE);
        new GetMatchDetailsTask().execute(matchUrl);
    }

    private class GetMatchDetailsTask extends AsyncTask<String, Void, Void> {
        Element element;

        @Override
        protected Void doInBackground(String... urls) {
            String matchUrl = urls[0];

            try {
                Document doc = Jsoup.connect(matchUrl).get();
                element = doc.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            View matchDetailsCard = findViewById(R.id.matchDetailsMainCard);
            TextView matchDetailsApproximateTime = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsApproximateTime);
            TextView matchDetailsBestOf = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsBestOf);
            TextView matchDetailsExactTime = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsExactTime);
            TextView matchDetailsTeamOneName = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamOneName);
            TextView matchDetailsTeamOnePercentage = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamOnePercentage);
            TextView matchDetailsTeamTwoName = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamTwoName);
            TextView matchDetailsTeamTwoPercentage = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamTwoPercentage);
            TextView matchDetailsTeamOnePotentialReward = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamOnePotentialReward);
            TextView matchDetailsTeamTwoPotentialReward = (TextView) matchDetailsCard.findViewById(R.id.matchDetailsTeamTwoPotentialReward);

            matchDetailsApproximateTime.setText(element.select(".half").get(0).ownText());
            matchDetailsBestOf.setText(element.select(".half").get(1).ownText());
            matchDetailsExactTime.setText(element.select(".half").get(2).ownText());
            matchDetailsTeamOneName.setText(element.select("#main").select("a > span > b").get(0).ownText());
            matchDetailsTeamOnePercentage.setText(element.select("#main").select("a > span > i").get(0).ownText());
            matchDetailsTeamTwoName.setText(element.select("#main").select("a > span > b").get(1).ownText());
            matchDetailsTeamTwoPercentage.setText(element.select("#main").select("a > span > i").get(1).ownText());
            matchDetailsTeamOnePotentialReward.setText(element.select(".full > .half").get(0).text().substring("Value ".length()));
            matchDetailsTeamTwoPotentialReward.setText(element.select(".full > .half").get(1).text().substring("Value ".length()));

            new GetTeamImagesTask(element).execute();

            findViewById(R.id.matchDetailsProgressBar).setVisibility(View.INVISIBLE);
            matchDetailsCard.setVisibility(View.VISIBLE);
        }
    }

    private class GetTeamImagesTask extends AsyncTask<Void, Void, Void> {
        private Element element;
        private Bitmap teamOneImage, teamTwoImage;

        public GetTeamImagesTask(Element element) {
            this.element = element;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String teamOneAttr = element.select(".team").get(0).attr("style");
            String teamOneUrl = teamOneAttr.substring(teamOneAttr.indexOf("background: url('") + 17, teamOneAttr.length() - 2);
            String teamTwoAttr = element.select(".team").get(1).attr("style");
            String teamTwoUrl = teamTwoAttr.substring(teamTwoAttr.indexOf("background: url('") + 17, teamTwoAttr.length() - 2);

            try {
                teamOneImage = getBitmap(teamOneUrl);
                teamTwoImage = getBitmap(teamTwoUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            ImageView matchDetailsTeamOneImage = (ImageView) findViewById(R.id.matchDetailsTeamOneImage);
            ImageView matchDetailsTeamTwoImage = (ImageView) findViewById(R.id.matchDetailsTeamTwoImage);

            matchDetailsTeamOneImage.setImageBitmap(teamOneImage);
            matchDetailsTeamTwoImage.setImageBitmap(teamTwoImage);
        }

        private Bitmap getBitmap(String url) throws IOException {
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
    }

    private class GetStreamTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String matchUrl = urls[0];

            try {
                String matchPage = Jsoup.connect(matchUrl).get().select("#mainstream").toString();
                int channelStart = matchPage.indexOf("channel=") + 8;
                int channelEnd = matchPage.indexOf("\"", channelStart);

                return matchPage.substring(channelStart, channelEnd);
            } catch (IOException e) {
                e.printStackTrace();
            } catch(IndexOutOfBoundsException e) {
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String streamUrl) {
            if(streamUrl != null) {
                Button streamButton = (Button) findViewById(R.id.matchDetailsStreamButton);
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitch.tv/" + streamUrl));
                        startActivity(intent);
                    }
                });
                streamButton.setVisibility(View.VISIBLE);
                findViewById(R.id.matchDetailsProgressBar).setVisibility(View.INVISIBLE);
            }
        }
    }
}
