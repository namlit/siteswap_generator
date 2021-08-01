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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tilman on 29.10.17.
 */

public class ChooseRemoveFavoriteDialog extends DialogFragment {

    private static final String STATE_SITESWAP_ENTITY_LIST = "STATE_SITESWAP_ENTITY_LIST";
    private ListView mListView;
    private List<SiteswapEntity> mSiteswapEntityList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mSiteswapEntityList = (List<SiteswapEntity>) savedInstanceState.getSerializable(STATE_SITESWAP_ENTITY_LIST);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_choose_remove_favorite)
                .setTitle(getString(R.string.choose_remove_favorite__title))
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

        mListView = (ListView) getDialog().findViewById(R.id.favorite_choose_remove_list);
        ArrayAdapter adapter = new ArrayAdapter<SiteswapEntity>(
                getContext(), android.R.layout.simple_list_item_1, mSiteswapEntityList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteswapEntity siteswapEntity = ((SiteswapEntity) parent.getItemAtPosition(position));

                new ConfirmRemoveFavoriteDialog().show(getFragmentManager(),
                        getString(R.string.confirm_remove_favorite__dialog_tag),
                        siteswapEntity);
                dismiss();
            }
        });


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SITESWAP_ENTITY_LIST, (Serializable) mSiteswapEntityList);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void show(FragmentManager manager, String tag, List<SiteswapEntity> siteswapEntityList) {
        mSiteswapEntityList = siteswapEntityList;
        show(manager, tag);
    }
}
