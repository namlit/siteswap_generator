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
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private ExecutorService mExecutor;
    private Future<?> mFuture;
    private SiteswapGenerator mGenerator;
    private SiteswapGenerator.Status mGenerationStatus;
    private boolean mIsError = false;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (SiteswapGenerationCallbacks) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);

        // Create and execute the background task.
        mExecutor = Executors.newSingleThreadExecutor();
        startGeneration();
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
        if (mGenerator != null) {
            mGenerator.cancelGeneration();
        }
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
        mGenerator = null;
    }

    public void getSiteswapGenerator() {
        if (isError()) {
            startGeneration();
            return;
        }
        if (mFuture != null && mFuture.isDone()) {
            generationComplete();
        }
    }

    public boolean isError() {
        if(mGenerator == null)
            return true;
        return mIsError;
    }

    private void startGeneration() {
        mIsError = false;
        if (mCallbacks != null) {
            mGenerator = mCallbacks.getSiteswapGenerator();
        }

        mFuture = mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mGenerationStatus = mGenerator.generateSiteswaps();
                } catch (java.lang.RuntimeException e) {
                    mIsError = true;
                    // This exception occurs if the Android system recycles the memory of the
                    // activity, but the background task is still executed.
                }

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        generationComplete();
                    }
                });
            }
        });
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
