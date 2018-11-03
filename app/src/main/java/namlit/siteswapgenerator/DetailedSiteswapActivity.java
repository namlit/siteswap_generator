/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2017 Tilman Sinning
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package namlit.siteswapgenerator;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import siteswaplib.NamedSiteswaps;
import siteswaplib.Siteswap;

public class DetailedSiteswapActivity extends AppCompatActivity
        implements AddToFavoritesDialog.DatabaseTransactionComplete {

    private Siteswap mSiteswap;
    private TextView mGlobalSiteswapTextview;
    private TextView mLocalSiteswapTextview;
    private TextView mNameTextview;
    private TextView mInterfaceTextview;
    private TextView mInterfacePatternTextview;
    private TextView mNumberOfObjectsTextview;
    private TextView mPeriodLengthTextview;
    private TextView mLocalSiteswapLegendTextview;
    private CausalDiagram mCausalDiagram;
    private CausalDiagram mLadderDiagram;
    private ShareActionProvider mShareActionProvider;
    private SiteswapEntity mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_siteswap);
        setTitle(R.string.detailed_siteswap__title);

        Intent intent = getIntent();
        mSiteswap = (Siteswap) intent.getSerializableExtra(getString(R.string.intent_detailed_siteswap_view__siteswap));
        if (mSiteswap == null)
            mSiteswap = new Siteswap();

        if (mSiteswap.getSiteswapName() == "") {
            int index = NamedSiteswaps.getListOfNamedSiteswaps().indexOf(mSiteswap);
            if (index != -1) {
                mSiteswap.setSiteswapName(((Siteswap)
                        NamedSiteswaps.getListOfNamedSiteswaps().get(index)).getSiteswapName());
            }
        }
        mSiteswap.rotateToBestStartingPosition();

        mGlobalSiteswapTextview = (TextView) findViewById(R.id.global_siteswap_textview);
        mLocalSiteswapTextview = (TextView) findViewById(R.id.local_siteswap_textview);
        mNameTextview = (TextView) findViewById(R.id.name_textview);
        mInterfaceTextview = (TextView) findViewById(R.id.interface_textview);
        mInterfacePatternTextview = (TextView) findViewById(R.id.interface_pattern_text_view);
        mNumberOfObjectsTextview = (TextView) findViewById(R.id.number_of_objects_textview);
        mPeriodLengthTextview = (TextView) findViewById(R.id.period_length_textview);
        mLocalSiteswapLegendTextview = (TextView) findViewById(R.id.local_siteswap_legend_textview);
        mCausalDiagram = (CausalDiagram) findViewById(R.id.causal_diagram_view);
        mCausalDiagram.setSiteswap(mSiteswap);
        mLadderDiagram = (CausalDiagram) findViewById(R.id.ladder_diagram_view);
        mLadderDiagram.setSiteswap(mSiteswap);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mFavorite = db.siteswapDao().getSiteswap(mSiteswap.toParsableString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFavorite != null) {
                            mSiteswap.setSiteswapName(mFavorite.getName());
                        }
                        else {
                        }
                        updateTextViews();
                    }
                });
            }
        }).start();

        updateTextViews();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setShareIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_siteswap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share_detailed)
        {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            setShareIntent();
        }
        else if (id == R.id.action_add_to_favorites)
        {
            addToFavorites();
            updateTextViews();
        }
        else if (id == R.id.action_remove_from_favorites)
        {
            removeFromFavorites();
            updateTextViews();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String siteswapString = "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.detailed_siteswap__share_global) + " ");
        Siteswap getin = mSiteswap.calculateGetin();
        if (getin.period_length() != 0) {
            stringBuilder.append(getin.toString());
            stringBuilder.append(" | ");
        }
        stringBuilder.append(mSiteswap.toString());
        Siteswap getout = mSiteswap.calculateGetout();
        if (getout.period_length() != 0) {
            stringBuilder.append(" | ");
            stringBuilder.append(getout.toString());
        }
        stringBuilder.append("\n");
        stringBuilder.append(Html.fromHtml(createLocalHtmlString("|")).toString());

        siteswapString = stringBuilder.toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT, siteswapString);
        shareIntent.setType("text/plain");
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void updateTextViews() {

        String name = (mSiteswap.getSiteswapName() == "") ? "Unnamed" : mSiteswap.getSiteswapName();
        mNameTextview.setText(name);
        mInterfaceTextview.setText(mSiteswap.toInterface().toString());
        mInterfacePatternTextview.setText(mSiteswap.toInterface().toPattern().toString());
        mNumberOfObjectsTextview.setText(String.valueOf(mSiteswap.getNumberOfObjects()));
        mPeriodLengthTextview.setText(String.valueOf(mSiteswap.period_length()));


        String globalHtmlString = "<font color=\"grey\">" +
                mSiteswap.calculateGetin().toString() +
                "</font> " +
                " <big>" + mSiteswap.toString() + " </big> " +
                "<font color=\"grey\">" +
                mSiteswap.calculateGetout().toString() +
                "</font> ";
        mGlobalSiteswapTextview.setText(Html.fromHtml(globalHtmlString));

        String localHtmlString = createLocalHtmlString();

        mLocalSiteswapTextview.setText(Html.fromHtml(localHtmlString));
        //mLocalSiteswapTextview.setText(Html.fromHtml("Juggler A: 4 2.5 3.5"));

        mCausalDiagram.invalidate();
        mLadderDiagram.invalidate();
        setShareIntent();

        mLocalSiteswapLegendTextview.setText(Html.fromHtml(getString(R.string.detailed_siteswap__legend_html)));
    }

    private String createLocalHtmlString() {
        return createLocalHtmlString("");
    }

    private String createLocalHtmlString(String getinoutSeparation) {
        String localHtmlString = "<big>Local Siteswap:</big><br>";
        Vector<String> localSiteswapStrings = mSiteswap.toLocalString();
        Siteswap[] localGetins = mSiteswap.calculateLocalGetins();
        Siteswap[] localGetouts = mSiteswap.calculateLocalGetouts();

        Siteswap.ClubDistribution initialClubDistribution[] = mSiteswap.calculateInitialClubDistribution();

        for(int juggler = 0; juggler < mSiteswap.getNumberOfJugglers(); ++juggler) {
            localHtmlString += Character.toString((char) ('A' + juggler)) + " ";
            localHtmlString += "<small>" + initialClubDistribution[juggler].toString() + "</small>";
            // initial clubs in hands
            localHtmlString += ": ";
            localHtmlString += "<font color=\"grey\"><small>" +
                    localGetins[juggler].toDividedString() + "</small></font> ";
            if (localGetins[juggler].period_length() != 0)
                localHtmlString += getinoutSeparation + "&ensp;";
            localHtmlString += localSiteswapStrings.elementAt(juggler);
            if (localGetouts[juggler].period_length() != 0)
                localHtmlString += getinoutSeparation + "&ensp;";
            localHtmlString += "<font color=\"grey\"><small>" +
                    localGetouts[juggler].toDividedString() + "</small></font> ";
            localHtmlString += "<br>";
        }
        return localHtmlString;
    }

    public void rotateLeft(View view) {
        mSiteswap.rotateLeft(1);
        updateTextViews();
    }

    public void rotateRight(View view) {
        mSiteswap.rotateRight(1);
        updateTextViews();
    }

    private void addToFavorites() {

        new AddToFavoritesDialog().show(getSupportFragmentManager(),
                getString(R.string.add_to_favorites__dialog_tag), mSiteswap);
    }

    private void removeFromFavorites() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    FavoriteDao dao = db.siteswapDao();
                    List<SiteswapEntity> siteswapEntityList = dao.getSiteswaps(mSiteswap.toParsableString());
                    if (siteswapEntityList.size() >= 1) {
                        // TODO choose which to remove
                        dao.deleteFavorite(siteswapEntityList.get(0));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Toas success
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO does not work
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.detailed_siteswap__toast_not_in_favorites),
                                        Toast.LENGTH_LONG);
                            }
                        });
                    }
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                }
            }
        }).start();

    }

    @Override
    public void databaseTransactionComplete() {
        updateTextViews();
    }
}
