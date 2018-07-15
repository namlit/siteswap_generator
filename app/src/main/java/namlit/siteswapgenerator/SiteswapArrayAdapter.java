package namlit.siteswapgenerator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import siteswaplib.Siteswap;

public class SiteswapArrayAdapter extends ArrayAdapter<Siteswap> {

    private final LayoutInflater mInflater;
    private final int mResource;

    public SiteswapArrayAdapter (Context context, int resource, List<Siteswap> siteswaps) {

        super(context, resource, siteswaps);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view;
        final TextView text;
        LayoutInflater inflater = mInflater;
        int resource = mResource;
        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        try {
            text = (TextView) view;

        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }
        final Siteswap siteswap = getItem(position);
        String name = siteswap.getSiteswapName() == "" ? "" : siteswap.getSiteswapName() + ": ";
        text.setText(name + siteswap.toString());
        return view;
    }
}
