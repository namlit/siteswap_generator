package namlit.siteswapgenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Vector;

import siteswaplib.Siteswap;

public class DetailedSiteswapActivity extends AppCompatActivity {

    private Siteswap mSiteswap;
    private TextView mGlobalSiteswapTextview;
    private TextView mLocalSiteswapTextview;
    private TextView mIntefaceTextview;
    private TextView mIntefacePatternTextview;
    private TextView mNumberOfObjectsTextview;
    private TextView mPeriodLengthTextview;
    private TextView mIsValidTextview;
    private TextView mLocalSiteswapLegendTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_siteswap);
        setTitle(R.string.detailed_siteswap__title);

        Intent intent = getIntent();
        mSiteswap = (Siteswap) intent.getSerializableExtra(getString(R.string.intent_detailed_siteswap_view__siteswap));
        if (mSiteswap == null)
            mSiteswap = new Siteswap();

        mSiteswap.rotateToBestStartingPosition();

        mGlobalSiteswapTextview = (TextView) findViewById(R.id.global_siteswap_textview);
        mLocalSiteswapTextview = (TextView) findViewById(R.id.local_siteswap_textview);
        mIntefaceTextview = (TextView) findViewById(R.id.interface_textview);
        mIntefacePatternTextview = (TextView) findViewById(R.id.interface_pattern_text_view);
        mNumberOfObjectsTextview = (TextView) findViewById(R.id.number_of_objects_textview);
        mPeriodLengthTextview = (TextView) findViewById(R.id.period_length_textview);
        mIsValidTextview = (TextView) findViewById(R.id.is_valid_text_view);
        mLocalSiteswapLegendTextview = (TextView) findViewById(R.id.local_siteswap_legend_textview);

        updateTextViews();

    }

    public void updateTextViews() {

        mIntefaceTextview.setText(mSiteswap.toInterface().toString());
        mIntefacePatternTextview.setText(mSiteswap.toInterface().toPattern().toString());
        mNumberOfObjectsTextview.setText(String.valueOf(mSiteswap.getNumberOfObjects()));
        mPeriodLengthTextview.setText(String.valueOf(mSiteswap.period_length()));
        mIsValidTextview.setText(String.valueOf(mSiteswap.isValid()));


        String globalHtmlString = mSiteswap.calculateGetin().toString() +
                " <big>" + mSiteswap.toString() + " </big> " +
                mSiteswap.calculateGetout().toString();
        mGlobalSiteswapTextview.setText(Html.fromHtml(globalHtmlString));

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
            localHtmlString += "<small>" + localGetins[juggler].toDividedString() + "</small> ";
            localHtmlString += localSiteswapStrings.elementAt(juggler);
            localHtmlString += "<small>" + localGetouts[juggler].toDividedString() + "</small> ";
            localHtmlString += "<br>";
        }

        mLocalSiteswapTextview.setText(Html.fromHtml(localHtmlString));
        //mLocalSiteswapTextview.setText(Html.fromHtml("Juggler A: 4 2.5 3.5"));

        mLocalSiteswapLegendTextview.setText(Html.fromHtml(getString(R.string.detailed_siteswap__legend_html)));
    }

    public void rotateLeft(View view) {
        mSiteswap.rotateLeft(1);
        updateTextViews();
    }

    public void rotateRight(View view) {
        mSiteswap.rotateRight(1);
        updateTextViews();
    }
}
