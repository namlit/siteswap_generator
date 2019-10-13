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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class LoadGenerationParametersDialog extends DialogFragment {

    private static final String STATE_GENERATION_PARAMETER_ENTITY = "STATE_GENERATION_PARAMETER_ENTITY";
    private ListView mListView;
    private List<GenerationParameterEntity> mGenerationParameterEntityList;
    private UpdateGenerationParameters updateGenerationParameters;

    public interface UpdateGenerationParameters {
        public void updateGenerationParameters(GenerationParameterEntity entity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mGenerationParameterEntityList = (List<GenerationParameterEntity>) savedInstanceState.getSerializable(STATE_GENERATION_PARAMETER_ENTITY);
        }

        updateGenerationParameters = (UpdateGenerationParameters) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_generation_parameters_list)
                .setTitle(getString(R.string.load_generation_parameters__title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        mListView = (ListView) getDialog().findViewById(R.id.generation_parameters_list);
        ArrayAdapter adapter = new ArrayAdapter<GenerationParameterEntity>(
                getContext(), android.R.layout.simple_list_item_1, mGenerationParameterEntityList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GenerationParameterEntity generationParameterEntity = ((GenerationParameterEntity) parent.getItemAtPosition(position));
                updateGenerationParameters.updateGenerationParameters(generationParameterEntity);
                dismiss();
            }
        });


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_GENERATION_PARAMETER_ENTITY, (Serializable) mGenerationParameterEntityList);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void show(FragmentManager manager, String tag, List<GenerationParameterEntity> siteswapEntityList) {
        mGenerationParameterEntityList = siteswapEntityList;
        show(manager, tag);
    }
}
