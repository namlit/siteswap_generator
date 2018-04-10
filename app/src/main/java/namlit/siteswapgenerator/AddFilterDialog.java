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

import android.content.Context;
import android.support.v4.app.DialogFragment;

import siteswaplib.Filter;

/**
 * Created by tilman on 29.10.17.
 */

public class AddFilterDialog extends DialogFragment {

    public interface FilterDialogListener {
        public void onAddSiteswapFilter(Filter filter);
        public void onRemoveSiteswapFilter(Filter filter);
        public void onChangeSiteswapFilter(Filter oldFilter, Filter newFilter);
    }

    // Use this instance of the interface to deliver action events
    FilterDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mListener = (FilterDialogListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
