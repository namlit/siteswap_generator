package namlit.siteswapgenerator;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import siteswaplib.Filter;
import siteswaplib.PatternFilter;
import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class PatternFilterDialog extends AddFilterDialog {

    private RadioButton mPatternRadioButton;
    private RadioButton mInterfaceRadioButton;
    private RadioButton mGlobalRadioButton;
    private RadioButton mLocalRadioButton;
    private RadioButton mIncludeRadioButton;
    private RadioButton mExcludeRadioButton;
    private EditText mPatternEditText;
    private TextView mDescriptionTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.pattern_filter_layout)
                .setPositiveButton(R.string.filter__add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which
                    ) {
                        Filter filter = readPatternFilter();
                        if (filter != null)
                            mListener.onAddSiteswapFilter(filter);
                    }
                })
                .setNegativeButton(R.string.filter__cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(R.string.filter__remove_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Filter filter = readPatternFilter();
                        if (filter != null)
                            mListener.onRemoveSiteswapFilter(filter);
                    }
                });

        return builder.create();
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

        mPatternRadioButton.setChecked(isPattern);
        mInterfaceRadioButton.setChecked(isInterface);
        mGlobalRadioButton.setChecked(isGlobal);
        mLocalRadioButton.setChecked(isLocal);
        mIncludeRadioButton.setChecked(isInclude);
        mExcludeRadioButton.setChecked(isExclude);


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
        if (pattern.period_length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.pattern_filter__toast_no_input),
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        if (isExclude)
            filterType = PatternFilter.Type.EXCLUDE;

        if (isPattern) {
            if (isGlobal) {
                return new PatternFilter(pattern, filterType);
            }
            else { // Local Pattern Filter
                Toast.makeText(getActivity(), "Unsupported Filter Type", Toast.LENGTH_SHORT).show();
            }
        }
        else { // Interface Filter
            if (isGlobal) {

                Toast.makeText(getActivity(), "Unsupported Filter Type", Toast.LENGTH_SHORT).show();
            }
            else { // Local Interface Filter

                Toast.makeText(getActivity(), "Unsupported Filter Type", Toast.LENGTH_SHORT).show();
            }
        }
        return null;

    }
}
