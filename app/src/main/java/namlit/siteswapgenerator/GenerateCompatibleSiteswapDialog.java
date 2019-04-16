/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2018 Tilman Sinning
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.EditText;

import siteswaplib.FilterList;
import siteswaplib.InterfaceFilter;
import siteswaplib.PatternFilter;
import siteswaplib.Siteswap;
import siteswaplib.SiteswapGenerator;

/**
 * Created by tilman on 22.07.18.
 */

public class GenerateCompatibleSiteswapDialog extends DialogFragment {

    public interface DatabaseTransactionComplete {
        public void databaseTransactionComplete();
    }

    private EditText mNumberOfObjectsTextEdit;
    private EditText mMinThrow;
    private EditText mMaxThrow;
    private CheckBox mIsZipsCheckbox;
    private CheckBox mIsZapscheckbox;
    private CheckBox mIsHoldsCheckbox;
    private Siteswap mSiteswap;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_generate_compatible_siteswap_dialog)
                .setTitle(getString(R.string.detailed_siteswap__dialog_generate_compatible_title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.generate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generateCompatibleSiteswaps();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mNumberOfObjectsTextEdit = (EditText) getDialog().findViewById(R.id.number_of_objects_compatible_text_edit);
        mMinThrow = (EditText) getDialog().findViewById(R.id.min_throw_compatible_text_edit);
        mMaxThrow = (EditText) getDialog().findViewById(R.id.max_throw_compatible_text_edit);
        mIsZipsCheckbox = (CheckBox) getDialog().findViewById(R.id.include_zips_compatible_checkbox);
        mIsZapscheckbox = (CheckBox) getDialog().findViewById(R.id.include_zaps_compatible_checkbox);
        mIsHoldsCheckbox = (CheckBox) getDialog().findViewById(R.id.include_holds_compatible_checkbox);
        mNumberOfObjectsTextEdit.setText(String.valueOf(mSiteswap.getNumberOfObjects()));
        mMinThrow.setText(String.valueOf(mSiteswap.getNumberOfJugglers()));
        mMaxThrow.setText(String.valueOf(mSiteswap.getMaxThrow()));
        mIsZipsCheckbox.setChecked(true);
        mIsZapscheckbox.setChecked(false);
        mIsHoldsCheckbox.setChecked(false);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void generateCompatibleSiteswaps()
    {
        try {
            int numberOfObjects = Integer.valueOf(mNumberOfObjectsTextEdit.getText().toString());
            int minThrow = Integer.valueOf(mMinThrow.getText().toString());
            int maxThrow = Integer.valueOf(mMaxThrow.getText().toString());
            int numberOfJugglers = mSiteswap.getNumberOfJugglers();
            int periodLength = mSiteswap.period_length();
            int maxResults = 1000;
            int timeout = 10;
            boolean isSyncPattern = mSiteswap.isSynchronous();
            int numberOfSynchronousHands = 1;
            if (isSyncPattern) {
                numberOfSynchronousHands = numberOfJugglers;
            }
            boolean isZips = mIsZipsCheckbox.isChecked();
            boolean isZaps = mIsZapscheckbox.isChecked();
            boolean isHolds = mIsHoldsCheckbox.isChecked();

            if (periodLength < 1)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_period_length));

            if (numberOfObjects < 1)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_number_of_objects));

            if (numberOfJugglers < 1 || numberOfJugglers > 10)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_number_of_jugglers));

            if (maxThrow < numberOfObjects)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_max_throw_smaller_average));

            if (minThrow > numberOfObjects)
                throw new IllegalArgumentException(getString(R.string.main_activity__invalid_min_throw_greater_average));

            if ((numberOfObjects + mSiteswap.getNumberOfObjects()) % numberOfJugglers != 0)
                throw new IllegalArgumentException(getString(R.string.generate_compatible__invalid_number_of_objects));

            FilterList filters = new FilterList();
            filters.addDefaultFilters(numberOfJugglers, minThrow, numberOfSynchronousHands);
            if (!isZips) {
                filters.removeZips(numberOfJugglers, numberOfSynchronousHands);
            }
            if (!isZaps) {
                filters.removeZaps(numberOfJugglers, numberOfSynchronousHands);
            }
            if (!isHolds) {
                filters.removeHolds(numberOfJugglers, numberOfSynchronousHands);
            }
            filters.add(new InterfaceFilter(mSiteswap.toInterface().toPattern(), PatternFilter.Type.INCLUDE));

            SiteswapGenerator siteswapGenerator = new SiteswapGenerator(periodLength, maxThrow,
                    minThrow, numberOfObjects, numberOfJugglers, filters);
            siteswapGenerator.setMaxResults(maxResults);
            siteswapGenerator.setTimeoutSeconds(timeout);
            siteswapGenerator.setSyncPattern(isSyncPattern);
            siteswapGenerator.setRandomGeneration(false);
            siteswapGenerator.setCompatibleSiteswap(mSiteswap);

            Intent intent = new Intent(getContext(), ShowSiteswaps.class);
            intent.putExtra(getString(R.string.intent__siteswap_generator), siteswapGenerator);
            startActivity(intent);
        }
        catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.main_activity__invalid_input_value) + " " +
                            String.format(getString(R.string.main_activity__could_not_convert_to_int), e.getMessage()))
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return;
        }
        catch (IllegalArgumentException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.main_activity__invalid_input_value) + " " + e.getMessage())
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
            return;
        }

    }

    public void show(FragmentManager manager, String tag, Siteswap siteswap) {
        mSiteswap = siteswap;
        show(manager, tag);
    }
}
