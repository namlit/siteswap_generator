package namlit.siteswapgenerator;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import java.util.LinkedList;

import siteswaplib.Siteswap;
import siteswaplib.SiteswapGenerator;

/**
 * Created by tilman on 22.11.17.
 */

public class SiteswapGenerationFragment extends Fragment {

    interface SiteswapGenerationCallbacks {
        SiteswapGenerator getSiteswapGenerator();
        void onGenerationComplete(SiteswapGenerator generator, boolean noTimeout);
    }

    private SiteswapGenerationCallbacks mCallbacks;
    private SiteswapGenerationTask mTask;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (SiteswapGenerationCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // Create and execute the background task.
        mTask = new SiteswapGenerationTask();
        mTask.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void getSiteswapGenerator() {
        if (mTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTask.onPostExecute(null);
        }
    }

    private class SiteswapGenerationTask extends AsyncTask<Void, Integer, Void> {

        private SiteswapGenerator mGenerator;
        private boolean mNoTimeout;

        @Override
        protected void onPreExecute() {
            publishProgress(0);
            if (mCallbacks != null) {
                mGenerator = mCallbacks.getSiteswapGenerator();
            }
        }

        @Override
        protected Void doInBackground(Void... ignore) {
            try {
                mNoTimeout = mGenerator.generateSiteswaps();
                publishProgress(100);
            }
            catch (java.lang.Exception e) {
                cancel(true);
                // TODO correct handling, the Task should automatically be restarted on an error here.
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void ignore) {
            if (mCallbacks != null) {
                mCallbacks.onGenerationComplete(mGenerator, mNoTimeout);
            }
        }
    }
}
