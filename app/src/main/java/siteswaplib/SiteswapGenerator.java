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

package siteswaplib;

import java.util.*;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SiteswapGenerator implements Serializable{

	public enum Status {GENERATING, ALL_SITESWAPS_FOUND, RANDOM_SITESWAP_FOUND, MAX_RESULTS_REACHED, TIMEOUT_REACHED, MEMORY_FULL, CANCELLED};

	private LinkedList<Siteswap> mSiteswaps;
	private FilterList mFilterList;
	private int mPeriodLength;
	private byte mMaxThrow;
	private byte mMinThrow;
	private byte mNumberOfObjects;
	private int mNumberOfJugglers;
	private int mMaxResults = 1000000000;
	private long mStartTime = 0;
    private int mTimeoutSeconds = 100;
	private boolean mCalculationComplete = false;
	private AtomicBoolean mIsCanceled;
	private int mBacktrackingCount = 0; // Just for algorithm performance analysis
	private int mNumberOfSynchronousHands = 1;
	private boolean mIsRandomGeneration = false;

	public SiteswapGenerator(int length, int max, int min, int objects, int number_of_jugglers) {
		this.mPeriodLength = length;
		if (length < 1)
			this.mPeriodLength = 1;
		this.mMaxThrow = (byte) max;
		this.mMinThrow = (byte) min;
		this.mNumberOfObjects = (byte) objects;
		if (objects < 1) {
			this.mNumberOfObjects = 1;
		}
		mIsCanceled = new AtomicBoolean(false);
		setNumberOfJugglers(number_of_jugglers);
        mFilterList = new FilterList();
        mFilterList.addDefaultFilters(number_of_jugglers, mNumberOfSynchronousHands);
	}

    public SiteswapGenerator(int length, int max, int min, int objects, int number_of_jugglers,
                             FilterList filterList) {

        this(length, max, min, objects, number_of_jugglers);
        mFilterList = filterList;
    }

	public LinkedList<Filter> getFilterList() {
		return mFilterList;
	}

	public void setFilterList(FilterList filterList) {
		this.mFilterList = filterList;
	}

	public Status generateSiteswaps() {
		mIsCanceled.set(false);
		mCalculationComplete = false;
		mBacktrackingCount = 0;
        mSiteswaps = new LinkedList<Siteswap>();
        mStartTime = System.currentTimeMillis();
		byte[] siteswapArray = new byte[mPeriodLength];
		byte[] interfaceArray = new byte[mPeriodLength];
		Arrays.fill(siteswapArray, Siteswap.FREE);
		Arrays.fill(interfaceArray, Siteswap.FREE);
		Siteswap siteswap;
		Siteswap siteswapInterface;
		Status status = Status.GENERATING;

		for (int i = 0; i < mNumberOfSynchronousHands; ++i) {
			siteswap = new Siteswap(siteswapArray, mNumberOfJugglers);
			siteswap.setNumberOfSynchronousHands(mNumberOfSynchronousHands);
			siteswap.setSynchronousStartPosition(i);
			siteswapInterface = new Siteswap(interfaceArray, mNumberOfJugglers);
			status = backtracking(siteswap, siteswapInterface, 0, 0);
			if (status != Status.GENERATING || mIsRandomGeneration)
				break;
            status = Status.ALL_SITESWAPS_FOUND;
		}

		if (mIsRandomGeneration) {
			while (status == Status.GENERATING || status == Status.RANDOM_SITESWAP_FOUND) {
				Arrays.fill(siteswapArray, Siteswap.FREE);
				Arrays.fill(interfaceArray, Siteswap.FREE);
				siteswap = new Siteswap(siteswapArray, mNumberOfJugglers);
				siteswap.setNumberOfSynchronousHands((mNumberOfSynchronousHands));
				siteswap.setSynchronousStartPosition((new Random().nextInt(mNumberOfSynchronousHands)));
				siteswapInterface = new Siteswap(interfaceArray, mNumberOfJugglers);
				status = backtracking(siteswap, siteswapInterface, 0, 0);
			}
		}
		mCalculationComplete = true;
		return status;
	}


	public void setNumberOfJugglers(int numberOfJugglers) {
		this.mNumberOfJugglers = numberOfJugglers;
		if (numberOfJugglers < 1)
			this.mNumberOfJugglers = 1;
	}

	public void setPeriodLength(int periodLength) {
		this.mPeriodLength = periodLength;
	}

	public void setMaxThrow(int maxThrow) {
		this.mMaxThrow = (byte) maxThrow;
	}

	public void setMinThrow(int minThrow) {
		this.mMinThrow = (byte) minThrow;
	}

	public void setNumberOfObjects(int numberOfObjects) {
		this.mNumberOfObjects = (byte) numberOfObjects;
	}

	public void setMaxResults(int maxResults) {
		this.mMaxResults = maxResults;
	}

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.mTimeoutSeconds = timeoutSeconds;
    }

	public void setSyncPattern(boolean isSyncPattern) {
		if (isSyncPattern)
            mNumberOfSynchronousHands = getNumberOfJugglers();
		else
			mNumberOfSynchronousHands = 1;
	}

    public void setRandomGeneration(boolean isRandomGeneration) {
		mIsRandomGeneration = isRandomGeneration;
	}

	public LinkedList<Siteswap> getSiteswaps() {
		return mSiteswaps;
	}

	public int getNumberOfGeneratedSiteswaps() {
		if (mSiteswaps == null)
			return 0;
		return mSiteswaps.size();
	}

	public int getPeriodLength() {
		return mPeriodLength;
	}

	public byte getMaxThrow() {
		return mMaxThrow;
	}

	public byte getMinThrow() {
		return mMinThrow;
	}

	public byte getNumberOfObjects() {
		return mNumberOfObjects;
	}

	public int getNumberOfJugglers() {
		return mNumberOfJugglers;
	}

	public int getMaxResults() {
		return mMaxResults;
	}

    public int getTimeoutSeconds() {
        return mTimeoutSeconds;
    }

	public int getBacktrackingCount() {
		return mBacktrackingCount;
	}

    public boolean isCalculationComplete() {
		return mCalculationComplete;
	}

	public void cancelGeneration() {
		mIsCanceled.set(true);
	}

	private int getMaxSumToGenerate(Siteswap siteswap, Siteswap siteswapInterface, int index) {
		int maxSum = 0;
		int interfaceIndex = siteswap.period_length() + mMaxThrow - 2;
		for(int i = siteswap.period_length() - 1; i >= index; --i) {
			while (siteswapInterface.at(interfaceIndex) != Siteswap.FREE) {
				interfaceIndex--;
				if (interfaceIndex < i)
					return 0;
			}
			maxSum += (interfaceIndex - i);
			interfaceIndex--;
		}
		return maxSum;
	}

	private int getMinSumToGenerate(Siteswap siteswap, Siteswap siteswapInterface, int index) {
		int minSum = 0;
		int interfaceIndex = index + mMinThrow;
		for(int i = index; i < siteswap.period_length(); ++i) {
			while (siteswapInterface.at(interfaceIndex) != Siteswap.FREE) {
				interfaceIndex++;
				if ((interfaceIndex - i) > mMaxThrow)
					return mMaxThrow;
			}
			minSum += (interfaceIndex - i);
			interfaceIndex++;
		}
		return minSum;
	}

	/**
	 * Returns false, if an timeout occured, the maximum number of siteswaps is
	 * reached or some error occurred. The siteswap calculation is then recursively
	 * aborted. Returns true on normal, to indicate, that the siteswap search
	 * shall be continued.
	 * */
	private Status backtracking(Siteswap siteswap, Siteswap siteswapInterface,
								 int currentIndex, int uniqueRepresentationIndex) {

		mBacktrackingCount++;
		if (mBacktrackingCount % 1000 == 0 &&
				System.currentTimeMillis() - mStartTime > mTimeoutSeconds * 1000)
			return Status.TIMEOUT_REACHED;

		if (mIsCanceled.get())
			return Status.CANCELLED;

		if (currentIndex == mPeriodLength) {

			if (uniqueRepresentationIndex != 0) {
				// Representation is not unique or siteswap has shorter period.
				// Go a step back and continue searching...
				return Status.GENERATING;
			}
			if (matchesFilters(siteswap)) {
				mSiteswaps.add(new Siteswap(siteswap));
				if(Runtime.getRuntime().maxMemory()-(Runtime.getRuntime().totalMemory() -
						Runtime.getRuntime().freeMemory()) < 1000)
					return Status.MEMORY_FULL;
				if (mSiteswaps.size() >= mMaxResults)
					return Status.MAX_RESULTS_REACHED;
				if (mIsRandomGeneration)
					return Status.RANDOM_SITESWAP_FOUND;
			}
			// A filter did not match. Go a step back and continue searching...
			return Status.GENERATING;
		}
		else { // Not last index

			if (currentIndex != 0) {
				if (!matchesFiltersPartialSitswap(siteswap, currentIndex - 1)) {
					// Go a step back and continue searching...
					return Status.GENERATING;
				}
			}
		}

		int min, max, uniqeMax;

		if (currentIndex == 0) {
			min = mNumberOfObjects;
			if (mIsRandomGeneration)
				min = mMaxThrow;
			max = mMaxThrow;
			uniqeMax = mMaxThrow + 1; // same value as max would result in wrong index calculation
			if (mPeriodLength == 1) {
				max = mNumberOfObjects;
			}
		}
		else {

			int partialSum = siteswap.getPartialSum(0, currentIndex - 1);
			int sum = mPeriodLength * mNumberOfObjects;

			// calculate minimum throw. The minimum throw must be hight enougth, that
			// the overall sum can be numberOfOjects * periodLength
			int minDeterminedByAverage = sum - partialSum - getMaxSumToGenerate(siteswap, siteswapInterface, currentIndex + 1);
			min = (minDeterminedByAverage > mMinThrow) ? minDeterminedByAverage : mMinThrow;

			// calculate max throw. The maximum throw can not be higher, than required
			// by the unique representation property. Additionally it must be possible,
			// that the overall sum is numberOfOjects * periodLength
			uniqeMax = siteswap.at(uniqueRepresentationIndex);
			int maxDeterminedByAverage = sum - partialSum - getMinSumToGenerate(siteswap, siteswapInterface, currentIndex + 1);
			max = (maxDeterminedByAverage < uniqeMax) ? maxDeterminedByAverage : uniqeMax;
		}

		for (int value = min; value <= max; ++value) {

			if (mIsRandomGeneration) {
				Random rand = new Random();
				value = rand.nextInt(max - min + 1) + min;
			}

			if (siteswapInterface.at(currentIndex + value) != Siteswap.FREE)
				continue;

			siteswap.set(currentIndex, value);
			siteswapInterface.set(currentIndex + value, value);
			int nextUniqueIndex = (value == uniqeMax) ? uniqueRepresentationIndex + 1 : 0;
			Status status = backtracking(siteswap, siteswapInterface, currentIndex + 1, nextUniqueIndex);
			if (status != Status.GENERATING)
				return status;
			siteswapInterface.set(currentIndex + value, Siteswap.FREE);
		}

		siteswap.set(currentIndex, Siteswap.FREE); // reset value for backtracking


		return Status.GENERATING;

	}

	private boolean matchesFilters(Siteswap siteswap) {
		if (mFilterList == null)
			return true;
		for (Filter filter : mFilterList) {
			if (!filter.isFulfilled(siteswap))
				return false;
		}
		return true;
	}

	private boolean matchesFiltersPartialSitswap(Siteswap siteswap, int index) {
		if (mFilterList == null)
			return true;
		for (Filter filter : mFilterList) {
			if (!filter.isPartlyFulfilled(siteswap, index))
				return false;
		}
		return true;
	}
}
