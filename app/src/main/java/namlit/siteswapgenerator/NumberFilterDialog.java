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


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import siteswaplib.Filter;
import siteswaplib.NumberFilter;
import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class NumberFilterDialog extends AddFilterDialog {

    private static final String STATE_OLD_FILTER = "STATE_OLD_FILTER";
    private static final String STATE_MIN_THROW = "STATE_MIN_THROW";
    private static final String STATE_MAX_THROW = "STATE_MAX_THROW";
    private static final String STATE_PERIOD_LENGTH = "STATE_PERIOD_LENGTH";
    private static final String STATE_NUMBER_OF_SYNCHRONOUS_HANDS = "STATE_NUMBER_OF_SYNCHRONOUS_HANDS";
    private RadioButton mAtLeastRadioButton;
    private RadioButton mNotMoreRadioButton;
    private RadioButton mExactlyRadioButton;
    private Spinner mNumberSpinner;
    private Spinner mHeightSpinner;
    Filter mOldFilter = null;
    int mMinThrow = 0;
    int mMaxThrow = 0;
    int mPeriodLength = 0;
    int mNumberOfSynchronousHands = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mOldFilter = (Filter) savedInstanceState.getSerializable(STATE_OLD_FILTER);
            mMinThrow = savedInstanceState.getInt(STATE_MIN_THROW);
            mMaxThrow = savedInstanceState.getInt(STATE_MAX_THROW);
            mPeriodLength = savedInstanceState.getInt(STATE_PERIOD_LENGTH);
            mNumberOfSynchronousHands = savedInstanceState.getInt(STATE_NUMBER_OF_SYNCHRONOUS_HANDS);
        }

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

        List<String> throwHeightArray =  new ArrayList<String>();

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

                NumberFilter.FilterValue throwHeight = filter.getFilterValue();
                int threshold = filter.getThresholdValue();
                if(throwHeight.isGenericPass())
                    throwHeightIndex = 0;
                else if(throwHeight.isGenericSelf())
                    throwHeightIndex = 1;
                else {
                    throwHeightArray.add(throwHeight.toString());
                    throwHeightIndex = 0;
                }
                numberIndex = threshold;
            }

        }

        throwHeightArray.add(Siteswap.intToString(Siteswap.PASS));
        throwHeightArray.add(Siteswap.intToString(Siteswap.SELF));
        for (String str : NumberFilter.getPossibleValues(mMinThrow, mMaxThrow,
                mNumberOfSynchronousHands)) {
            throwHeightArray.add(str);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_OLD_FILTER, mOldFilter);
        outState.putInt(STATE_MIN_THROW, mMinThrow);
        outState.putInt(STATE_MAX_THROW, mMaxThrow);
        outState.putInt(STATE_PERIOD_LENGTH, mPeriodLength);
        outState.putInt(STATE_NUMBER_OF_SYNCHRONOUS_HANDS, mNumberOfSynchronousHands);
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
        String height = (String) mHeightSpinner.getSelectedItem();
        int threshold = Integer.valueOf((String) mNumberSpinner.getSelectedItem());

        NumberFilter.Type type = NumberFilter.Type.GREATER_EQUAL;
        if (isNotMore)
            type = NumberFilter.Type.SMALLER_EQUAL;
        if (isExactly)
            type = NumberFilter.Type.EQUAL;

        return new NumberFilter(height, type, threshold, mNumberOfSynchronousHands);

    }

    public void show(FragmentManager manager, String tag, int minThrow, int maxThrow,
                     int periodLength, int numberOfSynchronousHands, Filter filter) {
        mOldFilter = filter;
        show(manager, tag, minThrow, maxThrow, periodLength, numberOfSynchronousHands);
    }

    public void show(FragmentManager manager, String tag, int minThrow, int maxThrow,
                     int periodLength, int numberOfSynchronousHands) {
        mMinThrow = minThrow;
        mMaxThrow = maxThrow;
        mPeriodLength = periodLength;
        mNumberOfSynchronousHands = numberOfSynchronousHands;
        show(manager, tag);
    }
}
