package namlit.siteswapgenerator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import siteswaplib.Filter;
import siteswaplib.Siteswap;

/**
 * Created by tilman on 29.10.17.
 */

public class AddFilterDialog extends DialogFragment {

    public interface FilterDialogListener {
        public void onAddSiteswapFilter(Filter filter);
        public void onRemoveSiteswapFilter(Filter filter);
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
