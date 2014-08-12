package com.davenwu.csgobets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends Activity {
    public static final String MATCH_URL = "com.davenwu.csgobets.MATCHURL";

    private LinearLayout matchesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchesList = (LinearLayout) findViewById(R.id.matchesListView);
        checkMatches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        } else if(id == R.id.action_refresh) {
            checkMatches();
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkMatches() {
        matchesList.removeAllViews();
        findViewById(R.id.matchesProgressBar).setVisibility(View.VISIBLE);
        new CheckMatchesTask().execute();
    }

    private class CheckMatchesTask extends AsyncTask<Void, Void, Void> {
        Document doc;
        Elements matches;
        String time, additionalInfo, event, teamOneName, teamTwoName, teamOnePercentage, teamTwoPercentage, matchUrl;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                doc = Jsoup.connect("http://csgolounge.com").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            matches = doc.getElementById("bets").children();
            findViewById(R.id.matchesProgressBar).setVisibility(View.INVISIBLE);
            for(Element element : matches) {
                View matchCard = getLayoutInflater().inflate(R.layout.match_card, null);
                TextView matchCardTime = (TextView) matchCard.findViewById(R.id.matchCardTime);
                TextView matchCardAdditionalInfo = (TextView) matchCard.findViewById(R.id.matchCardAdditionalInfo);
                TextView matchCardEvent = (TextView) matchCard.findViewById(R.id.matchCardEvent);
                TextView matchCardTeamOneName = (TextView) matchCard.findViewById(R.id.matchCardTeamOneName);
                TextView matchCardTeamOnePercentage = (TextView) matchCard.findViewById(R.id.matchCardTeamOnePercentage);
                TextView matchCardTeamTwoName = (TextView) matchCard.findViewById(R.id.matchCardTeamTwoName);
                TextView matchCardTeamTwoPercentage = (TextView) matchCard.findViewById(R.id.matchCardTeamTwoPercentage);

                time = element.select(".matchheader").select(".whenm").get(0).ownText();
                additionalInfo = element.select(".matchheader").select(".whenm").select("span").text();
                event = element.select(".matchheader").select(".whenm").get(1).ownText();
                teamOneName = element.select(".match").select(".matchleft").select("a").select("div").select("div").get(2).select(".teamtext").select("b").text();
                teamOnePercentage = element.select(".match").select(".matchleft").select("a").select("div").get(2).select(".teamtext").select("i").first().ownText();
                teamTwoName = element.select(".match").select(".matchleft").select("a").select("div").get(6).select(".teamtext").select("b").text();
                teamTwoPercentage = element.select(".match").select(".matchleft").select("a").select("div").get(6).select(".teamtext").select("i").first().ownText();
                matchUrl = element.select(".match").select(".matchleft").select("a").attr("href");

                matchCardTime.setText(time);
                matchCardAdditionalInfo.setText(additionalInfo);
                matchCardEvent.setText(event);
                matchCardTeamOneName.setText(teamOneName);
                matchCardTeamOnePercentage.setText(teamOnePercentage);
                matchCardTeamTwoName.setText(teamTwoName);
                matchCardTeamTwoPercentage.setText(teamTwoPercentage);

                new DownloadTeamImagesTask(matchCard).execute(element);

                matchCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), MatchDetailsActivity.class);
                        intent.putExtra(MainActivity.MATCH_URL, matchUrl);
                        startActivity(intent);
                    }
                });

                matchesList.addView(matchCard);
            }
        }
    }

    private class DownloadTeamImagesTask extends AsyncTask<Element, Void, Void> {
        View matchCard;
        Bitmap eventBackground, teamOneImage, teamTwoImage;

        public DownloadTeamImagesTask(View matchCard) {
            this.matchCard = matchCard;
        }

        @Override
        protected Void doInBackground(Element... elements) {
            Element element = elements[0];
            String bgAttr = element.select(".match").attr("style");
            String bgUrl = bgAttr.substring(bgAttr.indexOf("background-image: url('") + 23, bgAttr.length() - 2);
            String teamOneAttr = element.select(".match").select(".matchleft").select("a").select("div").get(1).attr("style").replace("\\", "");
            String teamOneUrl = teamOneAttr.substring(teamOneAttr.indexOf("background: url('") + 17, teamOneAttr.length() - 2);
            String teamTwoAttr = element.select(".match").select(".matchleft").select("a").select("div").get(5).attr("style").replace("\\", "");
            String teamTwoUrl = teamTwoAttr.substring(teamTwoAttr.indexOf("background: url('") + 17, teamTwoAttr.length() - 2);

            try {
                eventBackground = getBitmap(bgUrl);
                teamOneImage = getBitmap(teamOneUrl);
                teamTwoImage = getBitmap(teamTwoUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            ImageView matchCardEventBackground = (ImageView) matchCard.findViewById(R.id.matchCardEventBackground);
            ImageView matchCardTeamOneImage = (ImageView) matchCard.findViewById(R.id.matchCardTeamOneImage);
            ImageView matchCardTeamTwoImage = (ImageView) matchCard.findViewById(R.id.matchCardTeamTwoImage);

            matchCardEventBackground.setImageBitmap(eventBackground);
            matchCardTeamOneImage.setImageBitmap(teamOneImage);
            matchCardTeamTwoImage.setImageBitmap(teamTwoImage);
        }

        private Bitmap getBitmap(String url) throws IOException {
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
    }
}
