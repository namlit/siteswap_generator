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

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import siteswaplib.SiteswapGenerator;

/**
 * Created by tilman on 22.11.17.
 */

public class SiteswapGenerationFragment extends Fragment {

    interface SiteswapGenerationCallbacks {
        SiteswapGenerator getSiteswapGenerator();
        void onGenerationComplete(SiteswapGenerator generator, SiteswapGenerator.Status status);
    }

    private SiteswapGenerationCallbacks mCallbacks;
    private SiteswapGenerationTask mTask;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (SiteswapGenerationCallbacks) context;
    }

    @Override
    public void onAttach(Activity context) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTask.mGenerator.cancelGeneration();
        mTask.cancel(true);
        mTask.mGenerator = null;
        mTask = null;
    }

    public void getSiteswapGenerator() {
        if (isError()) {
            mTask = new SiteswapGenerationTask();
            mTask.execute();
            return;
        }
        if (mTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTask.generationComplete();
        }
    }

    public boolean isError() {
        if(mTask.mGenerator == null)
            return true;
        return mTask.mIsError;
    }

    private class SiteswapGenerationTask extends AsyncTask<Void, Integer, Void> {

        private SiteswapGenerator mGenerator;
        private SiteswapGenerator.Status mGenerationStatus;
        private boolean mIsError = false;

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mGenerator = mCallbacks.getSiteswapGenerator();
            }
        }

        @Override
        protected Void doInBackground(Void... ignore) {
            try {
                mGenerationStatus = mGenerator.generateSiteswaps();
            }
            catch (java.lang.RuntimeException e) {
                mIsError = true;
                // This exceptions occurs, if the Andoid system recycles the memory of the
                // activity, but doInBackgound is still executed in background.
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void ignore) {

            generationComplete();
        }

        private void generationComplete() {
            if (mIsError) {
                return;
            }
            if (mCallbacks != null) {
                mCallbacks.onGenerationComplete(mGenerator, mGenerationStatus);
            }
        }
    }
}
