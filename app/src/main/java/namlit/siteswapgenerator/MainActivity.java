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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.text.TextWatcher;
import android.text.Editable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import siteswaplib.*;

public class MainActivity extends AppCompatActivity
        implements AddFilterDialog.FilterDialogListener {

    final static int PATTERN_FILTER_ITEM_NUMBER = 0;

    private LinkedList<Filter> mFilterList;

    private int mNumberOfObjects;
    private int mPeriodLength;
    private int mMaxThrow;
    private int mMinThrow;
    private int mNumberOfJugglers;
    private int mMaxResults;
    private int mTimeout;
    private boolean mIsRandomGenerationMode;
    private boolean mIsZips;
    private boolean mIsZaps;
    private boolean mIsHolds;
    private int mFilterSpinnerPosition;
    private EditText mNumberOfObjectsEditText;
    private EditText mPeriodLengthEditText;
    private EditText mMaxThrowEditText;
    private EditText mMinThrowEditText;
    private EditText mNumberOfJugglersEditText;
    private EditText mMaxResultsEditText;
    private EditText mTimeoutEditText;
    private CheckBox mRandomGenerationModeCheckbox;
    private CheckBox mZipsCheckbox;
    private CheckBox mZapsCheckbox;
    private CheckBox mHoldsCheckbox;
    private Spinner mFilterTypeSpinner;
    private NonScrollListView mFilterListView;
    private ArrayAdapter<Filter> mFilterListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberOfObjectsEditText = (EditText) findViewById(R.id.number_of_objects);
        mPeriodLengthEditText = (EditText) findViewById(R.id.period_length);
        mMaxThrowEditText = (EditText) findViewById(R.id.max_throw);
        mMinThrowEditText = (EditText) findViewById(R.id.min_throw);
        mNumberOfJugglersEditText = (EditText) findViewById(R.id.number_of_jugglers);
        mMaxResultsEditText = (EditText) findViewById(R.id.max_results);
        mTimeoutEditText = (EditText) findViewById(R.id.timeout);
        mZipsCheckbox       = (CheckBox) findViewById(R.id.include_zips_checkbox);
        mZapsCheckbox       = (CheckBox) findViewById(R.id.include_zaps_checkbox);
        mHoldsCheckbox      = (CheckBox) findViewById(R.id.include_holds_checkbox);
        mFilterTypeSpinner  = (Spinner) findViewById(R.id.filter_type_spinner);
        mFilterListView     = (NonScrollListView) findViewById(R.id.filter_list);
        mRandomGenerationModeCheckbox = (CheckBox) findViewById(R.id.random_generation_mode_checkbox);

        mFilterList = new LinkedList<Filter>();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mNumberOfObjects  = sharedPref.getInt(getString(R.string.main_activity__settings_number_of_objects),  7);
        mPeriodLength     = sharedPref.getInt(getString(R.string.main_activity__settings_period_length),      5);
        mMaxThrow         = sharedPref.getInt(getString(R.string.main_activity__settings_max_throw),         10);
        mMinThrow         = sharedPref.getInt(getString(R.string.main_activity__settings_min_throw),          2);
        mNumberOfJugglers = sharedPref.getInt(getString(R.string.main_activity__settings_number_of_jugglers), 2);
        mMaxResults       = sharedPref.getInt(getString(R.string.main_activity__settings_max_results),      100);
        mTimeout          = sharedPref.getInt(getString(R.string.main_activity__settings_timeout),            5);
        mIsZips       = sharedPref.getBoolean(getString(R.string.main_activity__settings_is_zips), true);
        mIsZaps       = sharedPref.getBoolean(getString(R.string.main_activity__settings_is_zaps), false);
        mIsHolds      = sharedPref.getBoolean(getString(R.string.main_activity__settings_is_holds), false);
        mIsRandomGenerationMode = sharedPref.getBoolean(
                getString(R.string.main_activity__settings_is_random_generation_mode), false);
        mFilterSpinnerPosition = sharedPref.getInt(getString(R.string.main_activity__settings_filter_spinner_position), 0);
        String serializedFilterList = sharedPref.getString(getString(R.string.main_activity__settings_filter_list), "");

        if (serializedFilterList != "") {
            try {
                byte b[] = Base64.decode(serializedFilterList, Base64.DEFAULT);
                ByteArrayInputStream bi = new ByteArrayInputStream(b);
                ObjectInputStream si = new ObjectInputStream(bi);
                mFilterList = (LinkedList<Filter>) si.readObject();
                si.close();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.main_activity__deserialization_error_toast),
                        Toast.LENGTH_SHORT).show();
            }
        }


        mNumberOfObjectsEditText.setText(String.valueOf(mNumberOfObjects));
        mPeriodLengthEditText.setText(String.valueOf(mPeriodLength));
        mMaxThrowEditText.setText(String.valueOf(mMaxThrow));
        mMinThrowEditText.setText(String.valueOf(mMinThrow));
        mNumberOfJugglersEditText.setText(String.valueOf(mNumberOfJugglers));
        mMaxResultsEditText.setText(String.valueOf(mMaxResults));
        mTimeoutEditText.setText(String.valueOf(mTimeout));
        mRandomGenerationModeCheckbox.setChecked(mIsRandomGenerationMode);
        mZipsCheckbox.setChecked(mIsZips);
        mZapsCheckbox.setChecked(mIsZaps);
        mHoldsCheckbox.setChecked(mIsHolds);
        mFilterTypeSpinner.setSelection(mFilterSpinnerPosition);


        mFilterListAdapter = new ArrayAdapter<Filter>(
                this, android.R.layout.simple_list_item_1, mFilterList);
        mFilterListView.setAdapter(mFilterListAdapter);
        mFilterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Filter filter = (Filter) parent.getItemAtPosition(position);

                updateFromTextEdits();
                if (filter instanceof NumberFilter) {

                        new NumberFilterDialog().show(getSupportFragmentManager(),
                                getString(R.string.number_filter__dialog_tag),
                                mMinThrow, mMaxThrow, mPeriodLength, filter);
                }
                else if (filter instanceof PatternFilter)
                    new PatternFilterDialog().show(getSupportFragmentManager(),
                            getString(R.string.pattern_filter__dialog_tag), mNumberOfJugglers, filter);

            }
        });

        updateAutoFilters();

        mNumberOfJugglersEditText.addTextChangedListener(new TextWatcher() {
            private boolean was_invalid = false;
            private int invalid_filter_list_length = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    was_invalid = true;
                    invalid_filter_list_length = 0;
                    return;
                }
                if (!updateAutoFilters()) {
                    was_invalid = true;
                    invalid_filter_list_length = mFilterList.size();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (was_invalid && invalid_filter_list_length == mFilterList.size()) {
                    was_invalid = false;
                    return;
                }
                updateFromTextEdits();
                Filter.removeDefaultFilters(mFilterList, mNumberOfJugglers);
                Filter.addZips(mFilterList, mNumberOfJugglers);
                Filter.addZaps(mFilterList, mNumberOfJugglers);
                Filter.addHolds(mFilterList, mNumberOfJugglers);
                mFilterListAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_named_siteswaps)
        {
            showNamedSiteswaps();
        }
        else if (id == R.id.action_about)
        {
            showAboutDialog();
        }
        else if (id == R.id.action_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml(getString(R.string.help_activity__help_html_text)))
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean updateFromTextEdits() {

        try {
            mNumberOfObjects = Integer.valueOf(mNumberOfObjectsEditText.getText().toString());
            mPeriodLength = Integer.valueOf(mPeriodLengthEditText.getText().toString());
            mMaxThrow = Integer.valueOf(mMaxThrowEditText.getText().toString());
            mMinThrow = Integer.valueOf(mMinThrowEditText.getText().toString());
            mNumberOfJugglers = Integer.valueOf(mNumberOfJugglersEditText.getText().toString());
            mMaxResults = Integer.valueOf(mMaxResultsEditText.getText().toString());
            mTimeout = Integer.valueOf(mTimeoutEditText.getText().toString());
            mIsRandomGenerationMode = mRandomGenerationModeCheckbox.isChecked();
            mIsZips = mZipsCheckbox.isChecked();
            mIsZaps = mZapsCheckbox.isChecked();
            mIsHolds = mHoldsCheckbox.isChecked();
            mFilterSpinnerPosition = mFilterTypeSpinner.getSelectedItemPosition();

            if (mPeriodLength < 1)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_period_length));

            if (mNumberOfObjects < 1)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_number_of_objects));

            if (mNumberOfJugglers < 1 || mNumberOfJugglers > 10)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_number_of_jugglers));

            if (mMaxThrow < mNumberOfObjects)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_max_throw_smaller_average));

            if (mMinThrow > mNumberOfObjects)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_min_throw_greater_average));
        }
        catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.main_activity__invalid_input_value) + " " +
                            String.format(getString(R.string.main_activity__could_not_convert_to_int), e.getMessage()))
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return false;
        }
        catch (IllegalArgumentException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.main_activity__invalid_input_value) + " " + e.getMessage())
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        updateFromTextEdits();

        editor.putInt(getString(R.string.main_activity__settings_number_of_objects), mNumberOfObjects);
        editor.putInt(getString(R.string.main_activity__settings_period_length), mPeriodLength);
        editor.putInt(getString(R.string.main_activity__settings_max_throw), mMaxThrow);
        editor.putInt(getString(R.string.main_activity__settings_min_throw), mMinThrow);
        editor.putInt(getString(R.string.main_activity__settings_number_of_jugglers), mNumberOfJugglers);
        editor.putInt(getString(R.string.main_activity__settings_max_results), mMaxResults);
        editor.putInt(getString(R.string.main_activity__settings_timeout), mTimeout);
        editor.putBoolean(getString(R.string.main_activity__settings_is_random_generation_mode), mIsRandomGenerationMode);
        editor.putBoolean(getString(R.string.main_activity__settings_is_zips), mIsZips);
        editor.putBoolean(getString(R.string.main_activity__settings_is_zaps), mIsZaps);
        editor.putBoolean(getString(R.string.main_activity__settings_is_holds), mIsHolds);
        editor.putInt(getString(R.string.main_activity__settings_filter_spinner_position), mFilterSpinnerPosition);

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(mFilterList);
            so.close();
            editor.putString(getString(R.string.main_activity__settings_filter_list), Base64.encodeToString(bo.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.main_activity__serialization_error_toast),
                    Toast.LENGTH_SHORT).show();
        }

        editor.commit();
    }



    public void addFilter(View view) {

        if (!updateFromTextEdits())
            return;

        if (mFilterTypeSpinner.getSelectedItemPosition() == PATTERN_FILTER_ITEM_NUMBER) {

            new PatternFilterDialog().show(getSupportFragmentManager(),
                    getString(R.string.pattern_filter__dialog_tag), mNumberOfJugglers);
        } else {
            new NumberFilterDialog().show(getSupportFragmentManager(),
                    getString(R.string.number_filter__dialog_tag), mMinThrow, mMaxThrow, mPeriodLength);

        }
    }

    public void resetFilters(View view) {
        mFilterList.clear();
        updateAutoFilters();
    }


    public void onAddSiteswapFilter(Filter filter)
    {
        if (!mFilterList.contains(filter))
            mFilterList.add(filter);
        mFilterListAdapter.notifyDataSetChanged();
    }

    public void onRemoveSiteswapFilter(Filter filter)
    {
        // Remove all occurences of Filter
        while (mFilterList.remove(filter))
            ;
        mFilterListAdapter.notifyDataSetChanged();
    }

    public void onChangeSiteswapFilter(Filter oldFilter, Filter newFilter)
    {
        onRemoveSiteswapFilter(oldFilter);
        onAddSiteswapFilter(newFilter);
    }

    public void enterSiteswap(View button) {

        if (!updateFromTextEdits())
            return;

        new EnterSiteswapDialog().show(getSupportFragmentManager(),
                getString(R.string.enter_siteswap__dialog_tag), mNumberOfJugglers);
    }

    public void generateSiteswaps(View view) {

        if (!updateFromTextEdits())
            return;

        SiteswapGenerator siteswapGenerator = new SiteswapGenerator(mPeriodLength, mMaxThrow,
                mMinThrow, mNumberOfObjects, mNumberOfJugglers, mFilterList);
        siteswapGenerator.setMaxResults(mMaxResults);
        siteswapGenerator.setTimeoutSeconds(mTimeout);
        siteswapGenerator.setRandomGeneration(mIsRandomGenerationMode);

        Intent intent = new Intent(this, ShowSiteswaps.class);
        intent.putExtra(getString(R.string.intent__siteswap_generator), siteswapGenerator);
        startActivity(intent);
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        updateFromTextEdits();

        switch (view.getId()) {
            case R.id.include_zips_checkbox:
                if (checked)
                    Filter.addZips(mFilterList, mNumberOfJugglers);
                else
                    Filter.removeZips(mFilterList, mNumberOfJugglers);
                break;
            case R.id.include_zaps_checkbox:
                if (checked)
                    Filter.addZaps(mFilterList, mNumberOfJugglers);
                else
                    Filter.removeZaps(mFilterList, mNumberOfJugglers);
                break;
            case R.id.include_holds_checkbox:
                if (checked)
                    Filter.addHolds(mFilterList, mNumberOfJugglers);
                else
                    Filter.removeHolds(mFilterList, mNumberOfJugglers);
                break;
        }
        mFilterListAdapter.notifyDataSetChanged();

    }

    private boolean updateAutoFilters() {
        if (!updateFromTextEdits())
            return false;
        Filter.addDefaultFilters(mFilterList, mNumberOfJugglers, mMinThrow);
        onCheckboxClicked(mZipsCheckbox);   // Updates the filter list corresponding to checkbox
        onCheckboxClicked(mZapsCheckbox);   // Updates the filter list corresponding to checkbox
        onCheckboxClicked(mHoldsCheckbox);  // Updates the filter list corresponding to checkbox
        mFilterListAdapter.notifyDataSetChanged();
        return true;
    }

    private void showNamedSiteswaps() {

        Intent intent = new Intent(this, NamedSiteswapActivity.class);
        startActivity(intent);
    }

    private void showAboutDialog()
    {

        try
        {

            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String appVersion = info.versionName;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View view = getLayoutInflater().inflate(R.layout.layout_about_page, null);
            TextView appNameVersion = (TextView) view.findViewById(R.id.appNameVersion);
            appNameVersion.setText(getString(R.string.app_name) + " " + appVersion);

            builder.setView(view);
            builder.setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Throwable t) {

            t.printStackTrace();
        }
    }
}
