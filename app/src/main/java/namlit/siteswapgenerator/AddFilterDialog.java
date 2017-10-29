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
