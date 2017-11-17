package namlit.siteswapgenerator;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import siteswaplib.Filter;
import siteswaplib.NumberFilter;
import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class NumberFilterDialog extends AddFilterDialog {

    private RadioButton mAtLeastRadioButton;
    private RadioButton mNotMoreRadioButton;
    private RadioButton mExactlyRadioButton;
    private Spinner mNumberSpinner;
    private Spinner mHeightSpinner;
    Filter mOldFilter = null;
    int mMinThrow = 0;
    int mMaxThrow = 0;
    int mPeriodLength = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int positiveButtonStringId = R.string.filter__add_button;
        if (mOldFilter != null)
            positiveButtonStringId = R.string.filter__replace_button;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.number_filter_layout)
                .setPositiveButton(positiveButtonStringId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which
                    ) {
                        Filter filter = readNumberFilter();
                        if (filter != null) {
                            if (mOldFilter != null)
                                mListener.onChangeSiteswapFilter(mOldFilter, filter);
                            else
                                mListener.onAddSiteswapFilter(filter);
                        }
                    }
                })
                .setNegativeButton(R.string.filter__cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(R.string.filter__remove_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Filter filter = readNumberFilter();
                        if (filter != null)
                            mListener.onRemoveSiteswapFilter(filter);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAtLeastRadioButton = (RadioButton) getDialog().findViewById(R.id.at_least_radio_button);
        mNotMoreRadioButton = (RadioButton) getDialog().findViewById(R.id.not_more_radio_button);
        mExactlyRadioButton = (RadioButton) getDialog().findViewById(R.id.exactly_radio_button);
        mNumberSpinner = (Spinner) getDialog().findViewById(R.id.number_spinner);
        mHeightSpinner = (Spinner) getDialog().findViewById(R.id.height_spinner);


        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getString(R.string.number_filter__shared_preferences), Context.MODE_PRIVATE);

        boolean isAtLeast = sharedPref.getBoolean(getString(R.string.number_filter__at_least), true);
        boolean isNotMore = sharedPref.getBoolean(getString(R.string.number_filter__not_more), false);
        boolean isExactly  = sharedPref.getBoolean(getString(R.string.number_filter__exactly), false);
        int throwHeightIndex = sharedPref.getInt(getString(R.string.number_filter__shared_preferences_throw_height_index), 0);
        int numberIndex = sharedPref.getInt(getString(R.string.number_filter__shared_preferences_number_index), 1);

        if (mOldFilter != null) {
            if (mOldFilter instanceof NumberFilter) {
                NumberFilter filter = (NumberFilter) mOldFilter;
                isAtLeast = false;
                isNotMore = false;
                isExactly = false;
                switch (filter.getType()) {
                    case GREATER_EQUAL:
                        isAtLeast = true;
                        break;
                    case SMALLER_EQUAL:
                        isNotMore = true;
                        break;
                    case EQUAL:
                        isExactly = true;
                        break;
                }

                int throwHeight = filter.getFilterValue();
                int threshold = filter.getThresholdValue();

                if(throwHeight == Siteswap.PASS)
                    throwHeightIndex = 0;
                else if(throwHeight == Siteswap.SELF)
                    throwHeightIndex = 1;
                else
                    throwHeightIndex = throwHeight + 2 - mMinThrow;
                numberIndex = threshold;
            }

        }

        List<String> throwHeightArray =  new ArrayList<String>();
        throwHeightArray.add(Siteswap.intToString(Siteswap.PASS));
        throwHeightArray.add(Siteswap.intToString(Siteswap.SELF));
        int max = mMaxThrow;
        if (throwHeightIndex > max)
            max = throwHeightIndex;
        for (int i = mMinThrow; i <= max; ++i) {
            throwHeightArray.add(Siteswap.intToString(i));
        }

        ArrayAdapter<String> throwHeightAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, throwHeightArray);

        throwHeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHeightSpinner.setAdapter(throwHeightAdapter);

        List<String> numberArray =  new ArrayList<String>();
        for (int i = 0; i <= mPeriodLength; ++i) {
            numberArray.add(Integer.toString(i));
        }

        ArrayAdapter<String> NumberAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, numberArray);

        NumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNumberSpinner.setAdapter(NumberAdapter);

        if (throwHeightIndex >= mHeightSpinner.getCount() || throwHeightIndex < 0)
            throwHeightIndex = 0;
        if (numberIndex >= mNumberSpinner.getCount() || numberIndex < 0)
            numberIndex = 0;

        mAtLeastRadioButton.setChecked(isAtLeast);
        mNotMoreRadioButton.setChecked(isNotMore);
        mExactlyRadioButton.setChecked(isExactly);
        mHeightSpinner.setSelection(throwHeightIndex);
        mNumberSpinner.setSelection(numberIndex);
    }


    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getString(R.string.number_filter__shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean isAtLeast = mAtLeastRadioButton.isChecked();
        boolean isNotMore = mNotMoreRadioButton.isChecked();
        boolean isExactly = mExactlyRadioButton.isChecked();
        int throwHeightIndex = mHeightSpinner.getSelectedItemPosition();
        int numberIndex      = mNumberSpinner.getSelectedItemPosition();

        editor.putBoolean(getString(R.string.number_filter__at_least), isAtLeast);
        editor.putBoolean(getString(R.string.number_filter__not_more), isNotMore);
        editor.putBoolean(getString(R.string.number_filter__exactly), isExactly);
        editor.putInt(getString(R.string.number_filter__shared_preferences_throw_height_index), throwHeightIndex);
        editor.putInt(getString(R.string.number_filter__shared_preferences_number_index), numberIndex);

        editor.commit();
    }

    Filter readNumberFilter() {

        boolean isAtLeast = mAtLeastRadioButton.isChecked();
        boolean isNotMore = mNotMoreRadioButton.isChecked();
        boolean isExactly = mExactlyRadioButton.isChecked();
        int height = Siteswap.stringToInt((String) mHeightSpinner.getSelectedItem());
        int threshold = Integer.valueOf((String) mNumberSpinner.getSelectedItem());

        NumberFilter.Type type = NumberFilter.Type.GREATER_EQUAL;
        if (isNotMore)
            type = NumberFilter.Type.SMALLER_EQUAL;
        if (isExactly)
            type = NumberFilter.Type.EQUAL;

        return new NumberFilter(height, type, threshold);

    }

    public void show(FragmentManager manager, String tag, int minThrow, int maxThrow, int periodLength, Filter filter) {
        mOldFilter = filter;
        show(manager, tag, minThrow, maxThrow, periodLength);
    }

    public void show(FragmentManager manager, String tag, int minThrow, int maxThrow, int periodLength) {
        mMinThrow = minThrow;
        mMaxThrow = maxThrow;
        mPeriodLength = periodLength;
        show(manager, tag);
    }
}
