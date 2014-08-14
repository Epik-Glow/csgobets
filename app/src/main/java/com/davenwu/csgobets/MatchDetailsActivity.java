package com.davenwu.csgobets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    private DetailedMatch match;
    private DetailedMatchRetainFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_match_details);

        matchUrl = "http://csgolounge.com/" + getIntent().getStringExtra(MainActivity.MATCH_URL);

        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (DetailedMatchRetainFragment) fm.findFragmentByTag("data");

        if(dataFragment == null) {
            dataFragment = new DetailedMatchRetainFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            match = new DetailedMatch();
            dataFragment.setDetailedMatch(match);
            new GetMatchDetailsTask().execute(matchUrl);
            new GetStreamTask().execute(matchUrl);
        } else {
            match = dataFragment.getDetailedMatch();
            displayMatchInfo(match);
        }
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

    private void displayMatchInfo(final DetailedMatch detailedMatch) {
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
        ImageView matchDetailsTeamOneImage = (ImageView) findViewById(R.id.matchDetailsTeamOneImage);
        ImageView matchDetailsTeamTwoImage = (ImageView) findViewById(R.id.matchDetailsTeamTwoImage);
        Button streamButton = (Button) findViewById(R.id.matchDetailsStreamButton);

        matchDetailsApproximateTime.setText(detailedMatch.getApproximateTime());
        matchDetailsBestOf.setText(detailedMatch.getBestOf());
        matchDetailsExactTime.setText(detailedMatch.getExactTime());
        matchDetailsTeamOneName.setText(detailedMatch.getTeamOneName());
        matchDetailsTeamOnePercentage.setText(detailedMatch.getTeamOnePercentage());
        matchDetailsTeamTwoName.setText(detailedMatch.getTeamTwoName());
        matchDetailsTeamTwoPercentage.setText(detailedMatch.getTeamTwoPercentage());
        matchDetailsTeamOnePotentialReward.setText(detailedMatch.getTeamOnePotentialReward());
        matchDetailsTeamTwoPotentialReward.setText(detailedMatch.getTeamTwoPotentialReward());
        matchDetailsTeamOneImage.setImageBitmap(detailedMatch.getTeamOneImage());
        matchDetailsTeamTwoImage.setImageBitmap(detailedMatch.getTeamTwoImage());

        if(detailedMatch.getStreamUrl() != null) {
            streamButton.setText("Twitch Stream Available!");
            streamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitch.tv/" + detailedMatch.getStreamUrl()));
                    startActivity(intent);
                }
            });
        } else {
            streamButton.setText("No Twitch Streams");
            streamButton.setEnabled(false);
            streamButton.setClickable(false);
        }

        findViewById(R.id.matchDetailsProgressBar).setVisibility(View.INVISIBLE);
        matchDetailsCard.setVisibility(View.VISIBLE);
        streamButton.setVisibility(View.VISIBLE);
    }

    private void refresh() {
        findViewById(R.id.matchDetailsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.matchDetailsMainCard).setVisibility(View.INVISIBLE);
        findViewById(R.id.matchDetailsStreamButton).setVisibility(View.INVISIBLE);
        new GetMatchDetailsTask().execute(matchUrl);
        new GetStreamTask().execute(matchUrl);
    }

    private class GetMatchDetailsTask extends AsyncTask<String, Void, Void> {
        Element element;

        @Override
        protected Void doInBackground(String... urls) {
            String matchUrl = urls[0];

            try {
                Document doc = Jsoup.connect(matchUrl).get();
                element = doc.body();
            } catch (Exception e) {
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

            match.setApproximateTime(element.select(".half").get(0).ownText());
            match.setBestOf(element.select(".half").get(1).ownText());
            match.setExactTime(element.select(".half").get(2).ownText());
            match.setTeamOneName(element.select("#main").select("a > span > b").get(0).ownText());
            match.setTeamOnePercentage(element.select("#main").select("a > span > i").get(0).ownText());
            match.setTeamTwoName(element.select("#main").select("a > span > b").get(1).ownText());
            match.setTeamTwoPercentage(element.select("#main").select("a > span > i").get(1).ownText());
            match.setTeamOnePotentialReward(element.select(".full > .half").get(0).text().substring("Value ".length()));
            match.setTeamTwoPotentialReward(element.select(".full > .half").get(1).text().substring("Value ".length()));

            matchDetailsApproximateTime.setText(match.getApproximateTime());
            matchDetailsBestOf.setText(match.getBestOf());
            matchDetailsExactTime.setText(match.getExactTime());
            matchDetailsTeamOneName.setText(match.getTeamOneName());
            matchDetailsTeamOnePercentage.setText(match.getTeamOnePercentage());
            matchDetailsTeamTwoName.setText(match.getTeamTwoName());
            matchDetailsTeamTwoPercentage.setText(match.getTeamTwoPercentage());
            matchDetailsTeamOnePotentialReward.setText(match.getTeamOnePotentialReward());
            matchDetailsTeamTwoPotentialReward.setText(match.getTeamTwoPotentialReward());

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
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            ImageView matchDetailsTeamOneImage = (ImageView) findViewById(R.id.matchDetailsTeamOneImage);
            ImageView matchDetailsTeamTwoImage = (ImageView) findViewById(R.id.matchDetailsTeamTwoImage);

            match.setTeamOneImage(teamOneImage);
            match.setTeamTwoImage(teamTwoImage);

            matchDetailsTeamOneImage.setImageBitmap(match.getTeamOneImage());
            matchDetailsTeamTwoImage.setImageBitmap(match.getTeamTwoImage());
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
                String streamAttr = Jsoup.connect(matchUrl).get().select("#live_embed_player_flash").attr("data");
                if((streamAttr != null) && (streamAttr.contains("twitch.tv"))) {
                    int channelStart = streamAttr.indexOf("channel=") + 8;

                    match.setStreamUrl(streamAttr.substring(channelStart));
                    return match.getStreamUrl();
                }
            } catch (Exception e) {
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String streamUrl) {
            Button streamButton = (Button) findViewById(R.id.matchDetailsStreamButton);

            if(streamUrl != null) {
                streamButton.setText("Twitch Stream Available!");
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitch.tv/" + streamUrl));
                        startActivity(intent);
                    }
                });
                streamButton.setEnabled(true);
                streamButton.setClickable(true);
            } else {
                streamButton.setText("No Twitch Streams");
                streamButton.setEnabled(false);
                streamButton.setClickable(false);
            }

            streamButton.setVisibility(View.VISIBLE);
        }
    }

    public static class DetailedMatchRetainFragment extends Fragment {
        private DetailedMatch match;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setDetailedMatch(DetailedMatch match) {
            this.match = match;
        }

        public DetailedMatch getDetailedMatch() {
            return match;
        }
    }
}
