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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class EnterSiteswapDialog extends DialogFragment {

    private EditText mSiteswapTextEdit;
    private EditText mNumberOfJugglersTextEdit;
    private CheckBox mIsSynchronouscheckbox;
    private int mNumberOfJugglersDefault;
    private boolean mIsSyncDefault;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_enter_siteswap)
                .setTitle(getString(R.string.enter_siteswap__title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(getString(R.string.ok), null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateAndShowSiteswap())
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

        mSiteswapTextEdit = (EditText) getDialog().findViewById(R.id.enter_siteswap_siteswap_text_edit);
        mNumberOfJugglersTextEdit = (EditText) getDialog().findViewById(R.id.enter_siteswap_number_of_jugglers_text_edit);
        mIsSynchronouscheckbox = (CheckBox) getDialog().findViewById(R.id.enter_siteswap_sync_mode_checkbox);
        mNumberOfJugglersTextEdit.setText(String.valueOf(mNumberOfJugglersDefault));
        mIsSynchronouscheckbox.setChecked(mIsSyncDefault);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean validateAndShowSiteswap() {
        try {
            int numberOfJugglers = Integer.valueOf(mNumberOfJugglersTextEdit.getText().toString());
            boolean isSync = mIsSynchronouscheckbox.isChecked();
            int numberOfSynchronousHands = 1;
            if (isSync) {
                numberOfSynchronousHands = numberOfJugglers;
            }
            Siteswap siteswap = new Siteswap(mSiteswapTextEdit.getText().toString(), numberOfJugglers);
            siteswap.setNumberOfSynchronousHands(numberOfSynchronousHands);
            if (!siteswap.isValid() )
                throw new IllegalArgumentException(siteswap.toString());
            Intent intent = new Intent(getContext(), DetailedSiteswapActivity.class);
            intent.putExtra(getString(R.string.intent_detailed_siteswap_view__siteswap), siteswap);
            startActivity(intent);
            return true;
        }
        catch (IllegalArgumentException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.main_activity__invalid_site_swap) + e.getMessage())
                    .setNeutralButton(getString(R.string.back), null);
            builder.create().show();
        }
        return false;
    }

    public void show(FragmentManager manager, String tag, int numberOfJugglers,
                     boolean isSynchrounous) {
        mNumberOfJugglersDefault = numberOfJugglers;
        mIsSyncDefault = isSynchrounous;
        show(manager, tag);
    }
}
