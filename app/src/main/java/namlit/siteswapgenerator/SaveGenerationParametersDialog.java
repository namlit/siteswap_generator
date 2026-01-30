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
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tilman on 29.10.17.
 */

public class SaveGenerationParametersDialog extends DialogFragment {

    private static final String STATE_GENERATION_PARAMETER_ENTITY = "STATE_GENERATION_PARAMETER_ENTITY";
    private EditText mGenerationParameterNameTextEdit;
    private GenerationParameterEntity mGenerationParameterEntity;

    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mGenerationParameterEntity = (GenerationParameterEntity) savedInstanceState.getSerializable(STATE_GENERATION_PARAMETER_ENTITY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_save_generation_parameters)
                .setTitle(getString(R.string.save_generation_parameters__title))
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
                        mGenerationParameterEntity.setName(mGenerationParameterNameTextEdit.getText().toString());
                        insertEntityInDatabase();
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    public void insertEntityInDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getContext());
                    db.generationParameterDao().insertGenerationParameters(mGenerationParameterEntity);
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                }
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGenerationParameterNameTextEdit = (EditText) getDialog().findViewById(R.id.generation_parameter_name_text_edit_text_edit);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_GENERATION_PARAMETER_ENTITY, mGenerationParameterEntity);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void show(FragmentManager manager, String tag, GenerationParameterEntity entity) {
        mGenerationParameterEntity = entity;
        show(manager, tag);
    }
}
