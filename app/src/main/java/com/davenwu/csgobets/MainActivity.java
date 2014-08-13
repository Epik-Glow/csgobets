package com.davenwu.csgobets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    public static final String MATCH_URL = "com.davenwu.csgobets.MATCHURL";
    private ArrayList<Match> matchesArrayList;
    private MatchAdapter matchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchesArrayList = new ArrayList<Match>();
        matchAdapter = new MatchAdapter(this, matchesArrayList);
        ((ListView) findViewById(R.id.matchesList)).setAdapter(matchAdapter);
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
        if(id == R.id.action_refresh) {
            checkMatches();
        } else if(id == R.id.action_open_csgolounge) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://csgolounge.com"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkMatches() {
        matchesArrayList.clear();
        matchAdapter.notifyDataSetChanged();
        findViewById(R.id.matchesProgressBar).setVisibility(View.VISIBLE);
        new CheckMatchesTask().execute();
    }

    private class MatchAdapter extends ArrayAdapter<Match> {
        private Context context;
        private ArrayList<Match> matches;

        public MatchAdapter(Context context, ArrayList<Match> matches) {
            super(context, R.layout.match_card, matches);
            this.context = context;
            this.matches = matches;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View matchCard = inflater.inflate(R.layout.match_card, parent, false);
            TextView matchCardTime = (TextView) matchCard.findViewById(R.id.matchCardTime);
            TextView matchCardAdditionalInfo = (TextView) matchCard.findViewById(R.id.matchCardAdditionalInfo);
            TextView matchCardEvent = (TextView) matchCard.findViewById(R.id.matchCardEvent);
            TextView matchCardTeamOneName = (TextView) matchCard.findViewById(R.id.matchCardTeamOneName);
            TextView matchCardTeamOnePercentage = (TextView) matchCard.findViewById(R.id.matchCardTeamOnePercentage);
            TextView matchCardTeamTwoName = (TextView) matchCard.findViewById(R.id.matchCardTeamTwoName);
            TextView matchCardTeamTwoPercentage = (TextView) matchCard.findViewById(R.id.matchCardTeamTwoPercentage);
            ImageView matchCardEventBackground = (ImageView) matchCard.findViewById(R.id.matchCardEventBackground);
            ImageView matchCardTeamOneImage = (ImageView) matchCard.findViewById(R.id.matchCardTeamOneImage);
            ImageView matchCardTeamTwoImage = (ImageView) matchCard.findViewById(R.id.matchCardTeamTwoImage);
            ImageView matchCardTeamOneWin = (ImageView) matchCard.findViewById(R.id.matchCardTeamOneWin);
            ImageView matchCardTeamTwoWin = (ImageView) matchCard.findViewById(R.id.matchCardTeamTwoWin);

            matchCardTime.setText(matches.get(position).getTime());
            matchCardAdditionalInfo.setText(matches.get(position).getAdditionalInfo());
            matchCardEvent.setText(matches.get(position).getEvent());
            matchCardTeamOneName.setText(matches.get(position).getTeamOneName());
            matchCardTeamOnePercentage.setText(matches.get(position).getTeamOnePercentage());
            matchCardTeamTwoName.setText(matches.get(position).getTeamTwoName());
            matchCardTeamTwoPercentage.setText(matches.get(position).getTeamTwoPercentage());
            matchCardEventBackground.setImageBitmap(matches.get(position).getEventBackground());
            matchCardTeamOneImage.setImageBitmap(matches.get(position).getTeamOneImage());
            matchCardTeamTwoImage.setImageBitmap(matches.get(position).getTeamTwoImage());

            if(matches.get(position).isMatchOver()) {
                matchCard.findViewById(R.id.matchCardMainCard).setAlpha(0.5f);
            }
            if(matches.get(position).isTeamOneWin()) {
                matchCardTeamOneWin.setVisibility(View.VISIBLE);
            }
            if(matches.get(position).isTeamTwoWin()) {
                matchCardTeamTwoWin.setVisibility(View.VISIBLE);
            }

            matchCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MatchDetailsActivity.class);
                    intent.putExtra(MainActivity.MATCH_URL, matches.get(position).getMatchUrl());
                    startActivity(intent);
                }
            });

            return matchCard;
        }
    }

    private class CheckMatchesTask extends AsyncTask<Void, Void, Void> {
        Document doc;
        Elements matchesElement;

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
            matchesElement = doc.getElementById("bets").children();
            findViewById(R.id.matchesProgressBar).setVisibility(View.INVISIBLE);
            for(Element element : matchesElement) {
                Match match = new Match();

                match.setTime(element.select(".matchheader").select(".whenm").get(0).ownText());
                match.setAdditionalInfo(element.select(".matchheader").select(".whenm").select("span").text());
                match.setEvent(element.select(".matchheader").select(".whenm").get(1).ownText());
                match.setTeamOneName(element.select(".match").select(".matchleft").select("a").select("div").select("div").get(2).select(".teamtext").select("b").text());
                match.setTeamOnePercentage(element.select(".match").select(".matchleft").select("a").select("div").get(2).select(".teamtext").select("i").first().ownText());
                match.setTeamTwoName(element.select(".match").select(".matchleft").select("a").select("div").get(6).select(".teamtext").select("b").text());
                match.setTeamTwoPercentage(element.select(".match").select(".matchleft").select("a").select("div").get(6).select(".teamtext").select("i").first().ownText());
                match.setMatchUrl(element.select(".match").select(".matchleft").select("a").attr("href"));
                match.setMatchOver(element.select(".match").hasClass("notaviable"));
                match.setTeamOneWin(!element.select(".team").get(0).select("img").isEmpty());
                match.setTeamTwoWin(!element.select(".team").get(1).select("img").isEmpty());

                new DownloadTeamImagesTask(match).execute(element);

                matchAdapter.add(match);
                matchAdapter.notifyDataSetChanged();
            }
        }
    }

    private class DownloadTeamImagesTask extends AsyncTask<Element, Void, Void> {
        Match match;
        Bitmap eventBackground, teamOneImage, teamTwoImage;

        public DownloadTeamImagesTask(Match match) {
            this.match = match;
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
            match.setEventBackground(eventBackground);
            match.setTeamOneImage(teamOneImage);
            match.setTeamTwoImage(teamTwoImage);
            matchAdapter.notifyDataSetChanged();
        }

        private Bitmap getBitmap(String url) throws IOException {
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
    }
}
