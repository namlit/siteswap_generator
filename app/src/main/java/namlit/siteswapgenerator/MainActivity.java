package namlit.siteswapgenerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import siteswaplib.*;

public class MainActivity extends AppCompatActivity {

    private LinkedList<Filter> mFilterList;

    private EditText mNumberOfObjects;
    private EditText mPeriodLength;
    private EditText mMaxThrow;
    private EditText mMinThrow;
    private EditText mNumberOfJugglers;
    private EditText mMaxResults;
    private EditText mTimeout;
    private LinearLayout mFilterListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberOfObjects    = (EditText) findViewById(R.id.number_of_objects);
        mPeriodLength       = (EditText) findViewById(R.id.period_length);
        mMaxThrow           = (EditText) findViewById(R.id.max_throw);
        mMinThrow           = (EditText) findViewById(R.id.min_throw);
        mNumberOfJugglers   = (EditText) findViewById(R.id.number_of_jugglers);
        mMaxResults         = (EditText) findViewById(R.id.max_results);
        mTimeout            = (EditText) findViewById(R.id.timeout);
        mFilterListLayout   = (LinearLayout) findViewById(R.id.filter_list_layout);

        if (savedInstanceState != null) {
            mFilterList = (LinkedList<Filter>) savedInstanceState.getSerializable(
                    getString(R.string.main_activity__saved_filter_list));
        }
        else {
            mFilterList = new LinkedList<Filter>();

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            int numberOfObjects  = sharedPref.getInt(getString(R.string.main_activity__settings_number_of_objects),  7);
            int periodLength     = sharedPref.getInt(getString(R.string.main_activity__settings_period_length),      5);
            int maxThrow         = sharedPref.getInt(getString(R.string.main_activity__settings_max_throw),         10);
            int minThrow         = sharedPref.getInt(getString(R.string.main_activity__settings_min_throw),          2);
            int numberOfJugglers = sharedPref.getInt(getString(R.string.main_activity__settings_number_of_jugglers), 2);
            int maxResults       = sharedPref.getInt(getString(R.string.main_activity__settings_max_results),      100);
            int timeout          = sharedPref.getInt(getString(R.string.main_activity__settings_timeout),            5);

            mNumberOfObjects.setText(String.valueOf(numberOfObjects));
            mPeriodLength.setText(String.valueOf(periodLength));
            mMaxThrow.setText(String.valueOf(maxThrow));
            mMinThrow.setText(String.valueOf(minThrow));
            mNumberOfJugglers.setText(String.valueOf(numberOfJugglers));
            mMaxResults.setText(String.valueOf(maxResults));
            mTimeout.setText(String.valueOf(timeout));
        }

        //Filter.addDefaultFilters(mFilterList, 2);
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            int numberOfObjects = Integer.valueOf(mNumberOfObjects.getText().toString());
            int periodLength = Integer.valueOf(mPeriodLength.getText().toString());
            int maxThrow = Integer.valueOf(mMaxThrow.getText().toString());
            int minThrow = Integer.valueOf(mMinThrow.getText().toString());
            int numberOfJugglers = Integer.valueOf(mNumberOfJugglers.getText().toString());
            int maxResults = Integer.valueOf(mMaxResults.getText().toString());
            int timeout = Integer.valueOf(mTimeout.getText().toString());

            editor.putInt(getString(R.string.main_activity__settings_number_of_objects), numberOfObjects);
            editor.putInt(getString(R.string.main_activity__settings_period_length), periodLength);
            editor.putInt(getString(R.string.main_activity__settings_max_throw), maxThrow);
            editor.putInt(getString(R.string.main_activity__settings_min_throw), minThrow);
            editor.putInt(getString(R.string.main_activity__settings_number_of_jugglers), numberOfJugglers);
            editor.putInt(getString(R.string.main_activity__settings_max_results), maxResults);
            editor.putInt(getString(R.string.main_activity__settings_timeout), timeout);

            editor.commit();
        }
        catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.main_activity__invalid_input_value),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFilterList() {
        mFilterListLayout.removeAllViews();
        for(Filter filter: mFilterList) {
            TextView view = new TextView(this);
            view.setText(filter.toString());
            view.setPadding(20, 5, 20, 5);
            mFilterListLayout.addView(view);
        }
    }

    public void generateSiteswaps(View view) {
        Intent intent = new Intent(this, ShowSiteswaps.class);

        try {
            int numberOfObjects = Integer.valueOf(mNumberOfObjects.getText().toString());
            int periodLength = Integer.valueOf(mPeriodLength.getText().toString());
            int maxThrow = Integer.valueOf(mMaxThrow.getText().toString());
            int minThrow = Integer.valueOf(mMinThrow.getText().toString());
            int numberOfJugglers = Integer.valueOf(mNumberOfJugglers.getText().toString());
            int maxResults = Integer.valueOf(mMaxResults.getText().toString());
            int timeout = Integer.valueOf(mTimeout.getText().toString());


            SiteswapGenerator siteswapGenerator = new SiteswapGenerator(periodLength, maxThrow, minThrow, numberOfObjects, numberOfJugglers);
            siteswapGenerator.setMaxResults(maxResults);
            siteswapGenerator.setTimeoutSeconds(timeout);

            intent.putExtra(getString(R.string.intent__siteswap_generator), siteswapGenerator);
            startActivity(intent);

        }
        catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.main_activity__invalid_input_value),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
