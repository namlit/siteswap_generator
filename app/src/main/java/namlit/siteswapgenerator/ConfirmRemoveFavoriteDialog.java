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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class ConfirmRemoveFavoriteDialog extends DialogFragment {

    private static final String STATE_SITESWAP_ENTIIY = "STATE_SITESWAP_ENTIIY";
    private TextView mFavoriteTextView;
    private SiteswapEntity mSiteswapEntity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mSiteswapEntity = (SiteswapEntity) savedInstanceState.getSerializable(STATE_SITESWAP_ENTIIY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.layout_confirm_remove_favorite)
                .setTitle(getString(R.string.confirm_remove_favorite__title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(getString(R.string.remove), null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteSiteswapFromFavorites();
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

        mFavoriteTextView = (TextView) getDialog().findViewById(R.id.favorite_text_view);
        mFavoriteTextView.setText(
                getString(R.string.confirm_remove_favorite__name) + " " + mSiteswapEntity.getName() + "\n" +
                getString(R.string.confirm_remove_favorite__jugglers) + " " + mSiteswapEntity.getJuggerNames() + "\n" +
                getString(R.string.confirm_remove_favorite__location) + " " + mSiteswapEntity.getLocation() + "\n" +
                getString(R.string.confirm_remove_favorite__date) + " " + mSiteswapEntity.getDate());

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SITESWAP_ENTIIY, mSiteswapEntity);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void deleteSiteswapFromFavorites() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getContext());
                    FavoriteDao dao = db.siteswapDao();
                    dao.deleteFavorite(mSiteswapEntity);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.detailed_siteswap__toast_favorite_removed), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                } catch (NullPointerException e) {
                }
            }
        }).start();

    }

    public void show(FragmentManager manager, String tag, SiteswapEntity siteswapEntity) {
        mSiteswapEntity = siteswapEntity;
        show(manager, tag);
    }
}
