package com.davenwu.csgobets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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

public class MainActivity extends ActionBarActivity {
    public static final String MATCH_URL = "com.davenwu.csgobets.MATCHURL";
    private ArrayList<MainMatch> matchesArrayList;
    private MatchesAdapter matchAdapter;
    private MatchesRetainedFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragments used to save data across state changes e.g. orientation changes
        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (MatchesRetainedFragment) fm.findFragmentByTag("data");

        if(dataFragment == null) {
            dataFragment = new MatchesRetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            matchesArrayList = new ArrayList<MainMatch>();
            dataFragment.setMatchesArrayList(matchesArrayList);
            matchAdapter = new MatchesAdapter(this, matchesArrayList);
            ((ListView) findViewById(R.id.matchesList)).setAdapter(matchAdapter);
            checkMatches();
        } else {
            findViewById(R.id.matchesProgressBar).setVisibility(View.INVISIBLE);
            matchesArrayList = dataFragment.getMatchesArrayList();
            matchAdapter = new MatchesAdapter(this, matchesArrayList);
            ((ListView) findViewById(R.id.matchesList)).setAdapter(matchAdapter);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataFragment.setMatchesArrayList(matchesArrayList);
    }

    public void checkMatches() {
        matchesArrayList.clear();
        matchAdapter.notifyDataSetChanged();
        findViewById(R.id.matchesProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.matchesNoConnection).setVisibility(View.INVISIBLE);

        new CheckMatchesTask().execute();
    }

    // Only for handling onClick
    public void checkMatches(View v) {
        checkMatches();
    }

    private class MatchesAdapter extends ArrayAdapter<MainMatch> {
        private Context context;
        private ArrayList<MainMatch> matches;

        public MatchesAdapter(Context context, ArrayList<MainMatch> matches) {
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
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    matchCard.findViewById(R.id.matchCardMainCard).setAlpha(0.5f);
                } else {
                    AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
                    alpha.setDuration(0);
                    alpha.setFillAfter(true);
                    matchCard.startAnimation(alpha);
                }
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
            findViewById(R.id.matchesProgressBar).setVisibility(View.INVISIBLE);

            if(doc == null) {
                findViewById(R.id.matchesNoConnection).setVisibility(View.VISIBLE);
                return;
            }

            matchesElement = doc.getElementById("bets").children();

            for(Element element : matchesElement) {
                MainMatch match = new MainMatch();

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
        MainMatch match;
        Bitmap eventBackground, teamOneImage, teamTwoImage;

        public DownloadTeamImagesTask(MainMatch match) {
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
            } catch (Exception e) {
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

    public static class MatchesRetainedFragment extends Fragment {
        private ArrayList<MainMatch> matchesArrayList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setMatchesArrayList(ArrayList<MainMatch> matchesArrayList) {
            this.matchesArrayList = matchesArrayList;
        }

        public ArrayList<MainMatch> getMatchesArrayList() {
            return matchesArrayList;
        }
    }
}
