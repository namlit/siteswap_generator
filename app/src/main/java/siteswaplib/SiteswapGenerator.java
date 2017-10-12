package siteswaplib;

import java.util.*;
import java.io.Serializable;

public class SiteswapGenerator implements Serializable{

	private LinkedList<Siteswap> mSiteswaps;
	private LinkedList<Filter> mFilterList;
	private int mPeriodLength;
	private byte mMaxThrow;
	private byte mMinThrow;
	private byte mNumberOfObjects;
	private int mNumberOfJugglers;
	private int mMaxResults = 1000000000;
	private long mStartTime = 0;
    private int mTimeoutSeconds = 100;
	private boolean mCalculationComplete = false;
	private int mBacktrackingCount = 0; // Just for algorithm performance analysis

	public SiteswapGenerator(int length, int max, int min, int objects, int number_of_jugglers) {
		this.mPeriodLength = length;
		this.mMaxThrow = (byte) max;
		this.mMinThrow = (byte) min;
		this.mNumberOfObjects = (byte) objects;
		setNumberOfJugglers(number_of_jugglers);
        mFilterList = new LinkedList<Filter>();
        Filter.addDefaultFilters(mFilterList, number_of_jugglers);
	}

    public SiteswapGenerator(int length, int max, int min, int objects, int number_of_jugglers,
                             LinkedList<Filter> filterList) {

        this(length, max, min, objects, number_of_jugglers);
        mFilterList = filterList;
    }
	
	public LinkedList<Filter> getFilterList() {
		return mFilterList;
	}
	
	public void setFilterList(LinkedList<Filter> filterList) {
		this.mFilterList = filterList;
	}

	public boolean generateSiteswaps() {
		mCalculationComplete = false;
		mBacktrackingCount = 0;
        mSiteswaps = new LinkedList<Siteswap>();
        mStartTime = System.currentTimeMillis();
		byte[] arr = new byte[mPeriodLength];
		Arrays.fill(arr, (byte) 0);
		Siteswap siteswap = new Siteswap(arr, mNumberOfJugglers);
		TreeSet<Integer> freePositions = new TreeSet<Integer>();
		for(int i = 0; i < mPeriodLength; ++i)
			freePositions.add(i);

		boolean result = backtracking(siteswap, freePositions, 0, 0);
		mCalculationComplete = true;
		return result;
	}
	
	public void setNumberOfJugglers(int numberOfJugglers) {
		this.mNumberOfJugglers = numberOfJugglers;
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
	
	public LinkedList<Siteswap> getSiteswaps() {
		return mSiteswaps;
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

	/**
	 * Returns false, if an timeout occured, the maximum number of siteswaps is
	 * reached or some error occurred. The siteswap calculation is then recursively
	 * aborted. Returns true on normal, to indicate, that the siteswap search
	 * shall be continued.
	 * */
	private boolean backtracking(Siteswap siteswap, TreeSet<Integer> freePositions,
								 int currentIndex, int uniqueRepresentationIndex) {

		if (System.currentTimeMillis() - mStartTime > mTimeoutSeconds * 1000)
			return false;

		if (currentIndex == mPeriodLength) {

			if (uniqueRepresentationIndex != 0) {
				// Representation is not unique or siteswap has shorter period.
				// Continue Backtracking...
				mBacktrackingCount++;
				return true;
			}
			if (matchesFilters(siteswap)) {
				mSiteswaps.add(new Siteswap(siteswap));
				if (mSiteswaps.size() >= mMaxResults)
					return false; // Abort if max_results reached
			}
			// A filter did not match. Continue Backtracking...
			mBacktrackingCount++;
			return true;
		}
		else { // Not last index

			if (currentIndex != 0) {
				if (!matchesFiltersPartialSitswap(siteswap, currentIndex - 1)) {
					mBacktrackingCount++;
					return true;
				}
			}
		}

		int partialSum = siteswap.getPartialSum(0, currentIndex - 1);
		int sum = mPeriodLength * mNumberOfObjects;
		int averMin = sum - partialSum - (mMaxThrow - 1) * (mPeriodLength - currentIndex - 1);
		int min = (averMin > mMinThrow) ? averMin : mMinThrow;
		if(currentIndex == 0)
			min = mNumberOfObjects;

		int uniqeMax = siteswap.at(uniqueRepresentationIndex);
		int minTemp = (averMin - 1 > mMinThrow) ? averMin - 1 : mMinThrow;
		int averMax = sum - partialSum - minTemp * (mPeriodLength - currentIndex - 1);
		int max = (averMax < uniqeMax) ? averMax : uniqeMax;
		if(currentIndex == 0)
			max = mMaxThrow;
		if(currentIndex == (mPeriodLength - 1) && max == siteswap.at(0) &&
				uniqueRepresentationIndex != (currentIndex - 1))
			max--;


		// TODO use siteswap as interface with free positions instead of TreeSet
		TreeSet<Integer> freePositionsOld = new TreeSet<Integer>(freePositions);
		for (Integer pos : freePositionsOld) {
			int n = (int) Math.ceil((min + currentIndex - pos) / (double) mPeriodLength);
			while(pos - currentIndex + n * mPeriodLength <= max) {
				int value = pos - currentIndex + n * mPeriodLength;
				siteswap.set(currentIndex, value);
				freePositions.remove(pos);
				int nextUniqueIndex = (value == uniqeMax) ? uniqueRepresentationIndex + 1 : 0;
				if (!backtracking(siteswap, freePositions, currentIndex + 1, nextUniqueIndex))
					return false;
				freePositions.add(pos);
				n++;
			}
			siteswap.set(currentIndex, 0); // reset value for backtracking
		}


		return true;

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
