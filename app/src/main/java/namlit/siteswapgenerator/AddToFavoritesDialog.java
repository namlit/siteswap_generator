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


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 22.07.18.
 */

public class AddToFavoritesDialog extends DialogFragment {

    public interface DatabaseTransactionComplete {
        public void databaseTransactionComplete();
    }

    private EditText mSiteswapNameTextEdit;
    private Siteswap mSiteswap;
    private DatabaseTransactionComplete mDatabaseTransactionComplete;
    private Activity mActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDatabaseTransactionComplete = (DatabaseTransactionComplete) getActivity();
        mActivity = getActivity();

        builder.setView(R.layout.layout_add_to_favorites)
                .setTitle(getString(R.string.add_to_favorites__title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addSiteswapToFavorites();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSiteswapNameTextEdit = (EditText) getDialog().findViewById(R.id.siteswap_name_text_edit);
        mSiteswapNameTextEdit.setText(mSiteswap.getSiteswapName());
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void addSiteswapToFavorites()
    {
        String name = mSiteswapNameTextEdit.getText().toString();
        mSiteswap.setSiteswapName(name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getContext());
                    db.siteswapDao().insertFavorites(new SiteswapEntity(mSiteswap));
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDatabaseTransactionComplete.databaseTransactionComplete();
                        }
                    });
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                }
            }
        }).start();

    }

    public void show(FragmentManager manager, String tag, Siteswap siteswap) {
        mSiteswap = siteswap;
        show(manager, tag);
    }
}
