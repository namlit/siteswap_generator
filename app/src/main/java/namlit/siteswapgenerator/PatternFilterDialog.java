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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import siteswaplib.Filter;
import siteswaplib.InterfaceFilter;
import siteswaplib.LocalInterfaceFilter;
import siteswaplib.LocalPatternFilter;
import siteswaplib.PatternFilter;
import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class PatternFilterDialog extends AddFilterDialog {

    private static final String STATE_OLD_FILTER = "STATE_OLD_FILTER";
    private static final String STATE_NUMBER_OF_JUGGLERS = "STATE_NUMBER_OF_JUGGLERS";
    private RadioButton mPatternRadioButton;
    private RadioButton mInterfaceRadioButton;
    private RadioButton mGlobalRadioButton;
    private RadioButton mLocalRadioButton;
    private RadioButton mIncludeRadioButton;
    private RadioButton mExcludeRadioButton;
    private EditText mPatternEditText;
    private TextView mDescriptionTextView;
    private Filter mOldFilter = null;
    private int mNumberOfJugglers;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if(savedInstanceState != null) {
            mOldFilter = (Filter) savedInstanceState.getSerializable(STATE_OLD_FILTER);
            mNumberOfJugglers = savedInstanceState.getInt(STATE_NUMBER_OF_JUGGLERS);
        }
        int positiveButtonStringId = R.string.filter__add_button;
        if (mOldFilter != null)
            positiveButtonStringId = R.string.filter__replace_button;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.pattern_filter_layout)
                .setPositiveButton(positiveButtonStringId, null)
                .setNegativeButton(R.string.filter__cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(R.string.filter__remove_button, null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Filter filter = readPatternFilter();
                        if (filter == null)
                            return;
                        if (mOldFilter != null)
                            mListener.onChangeSiteswapFilter(mOldFilter, filter);
                        else
                            mListener.onAddSiteswapFilter(filter);
                        dialog.dismiss();
                    }
                });
                button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Filter filter = readPatternFilter();
                        if (filter == null)
                            return;
                        mListener.onRemoveSiteswapFilter(filter);
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        mPatternRadioButton = (RadioButton) getDialog().findViewById(R.id.pattern_radio_button);
        mInterfaceRadioButton = (RadioButton) getDialog().findViewById(R.id.interface_radio_button);
        mGlobalRadioButton = (RadioButton) getDialog().findViewById(R.id.global_radio_button);
        mLocalRadioButton = (RadioButton) getDialog().findViewById(R.id.local_radio_button);
        mIncludeRadioButton = (RadioButton) getDialog().findViewById(R.id.include_radio_button);
        mExcludeRadioButton = (RadioButton) getDialog().findViewById(R.id.exclude_radio_button);
        mPatternEditText = (EditText) getDialog().findViewById(R.id.pattern_text_edit);
        mDescriptionTextView = (TextView) getDialog().findViewById(R.id.description_text_view);

        mDescriptionTextView.setText(Html.fromHtml(getString(R.string.pattern_filter__description_html)));

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getString(R.string.pattern_filter__shared_preferences), Context.MODE_PRIVATE);

        boolean isPattern = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_pattern_checked), true);
        boolean isInterface = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_interface_checked), false);
        boolean isGlobal  = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_global_checked), true);
        boolean isLocal = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_local_checked), false);
        boolean isInclude = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_include_checked), true);
        boolean isExclude = sharedPref.getBoolean(getString(R.string.pattern_filter__shared_preferences_exclude_checked), false);

        if (mOldFilter != null) {
            if (mOldFilter instanceof PatternFilter) {
                PatternFilter filter = (PatternFilter) mOldFilter;
                isPattern = true;
                isInterface = false;
                isGlobal = true;
                isLocal = false;
                isInclude = filter.getType() == PatternFilter.Type.INCLUDE;
                isExclude = !isInclude;
                mPatternEditText.setText(filter.getPattern().toString());
            }

            if (mOldFilter instanceof LocalPatternFilter) {
                isGlobal = false;
                isLocal = true;
            }

            if (mOldFilter instanceof InterfaceFilter) {
                isPattern = false;
                isInterface = true;
            }

            if (mOldFilter instanceof LocalInterfaceFilter) {
                isGlobal = false;
                isLocal = true;
            }

        }

        mPatternRadioButton.setChecked(isPattern);
        mInterfaceRadioButton.setChecked(isInterface);
        mGlobalRadioButton.setChecked(isGlobal);
        mLocalRadioButton.setChecked(isLocal);
        mIncludeRadioButton.setChecked(isInclude);
        mExcludeRadioButton.setChecked(isExclude);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_OLD_FILTER, mOldFilter);
        outState.putInt(STATE_NUMBER_OF_JUGGLERS, mNumberOfJugglers);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getString(R.string.pattern_filter__shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean isPattern = mPatternRadioButton.isChecked();
        boolean isInterface = mInterfaceRadioButton.isChecked();
        boolean isGlobal = mGlobalRadioButton.isChecked();
        boolean isLocal = mLocalRadioButton.isChecked();
        boolean isInclude = mIncludeRadioButton.isChecked();
        boolean isExclude = mExcludeRadioButton.isChecked();

        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_pattern_checked), isPattern);
        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_interface_checked), isInterface);
        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_global_checked), isGlobal);
        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_local_checked), isLocal);
        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_include_checked), isInclude);
        editor.putBoolean(getString(R.string.pattern_filter__shared_preferences_exclude_checked), isExclude);

        editor.commit();
    }

    Filter readPatternFilter() {

        boolean isPattern = mPatternRadioButton.isChecked();
        boolean isInterface = mInterfaceRadioButton.isChecked();
        boolean isGlobal = mGlobalRadioButton.isChecked();
        boolean isLocal = mLocalRadioButton.isChecked();
        boolean isInclude = mIncludeRadioButton.isChecked();
        boolean isExclude = mExcludeRadioButton.isChecked();

        PatternFilter.Type filterType = PatternFilter.Type.INCLUDE;
        Siteswap pattern = new Siteswap( mPatternEditText.getText().toString() );
        if (pattern.isParsingError()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.pattern_filter__parsing_error) + " " +
                                    pattern.getInvalidCharactersFromParsing())
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return null;
        }
        if (pattern.period_length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.pattern_filter__toast_no_input))
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return null;
        }

        if (isExclude)
            filterType = PatternFilter.Type.EXCLUDE;

        if (isPattern) {
            if (isGlobal) {
                return new PatternFilter(pattern, filterType);
            }
            else {
                return new LocalPatternFilter(pattern, filterType, mNumberOfJugglers);
            }
        }
        else { // Interface Filter

            if (isGlobal) {

                return new InterfaceFilter(pattern, filterType);
            }
            else {

                return new LocalInterfaceFilter(pattern, filterType, mNumberOfJugglers);
            }
        }

    }

    public void show(FragmentManager manager, String tag, int numberOfJugglers) {
        mNumberOfJugglers = numberOfJugglers;
        show(manager, tag);
    }

    public void show(FragmentManager manager, String tag, int numberOfJugglers, Filter filter) {
        mOldFilter = filter;
         show(manager, tag, numberOfJugglers);
    }
}
