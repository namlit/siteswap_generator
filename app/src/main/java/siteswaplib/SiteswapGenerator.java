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
        mSiteswaps = new LinkedList<Siteswap>();
        mStartTime = System.currentTimeMillis();
		byte[] arr = new byte[mPeriodLength];
		Arrays.fill(arr, (byte) 0);
		Siteswap siteswap = new Siteswap(arr, mNumberOfJugglers);
		TreeSet<Integer> freePositions = new TreeSet<Integer>();
		for(int i = 0; i < mPeriodLength; ++i)
			freePositions.add(i);

        System.out.println(mFilterList.size());
        for (Filter filter : mFilterList) {
            System.out.println(filter.toString());
        }
        System.out.println(mFilterList.size());
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

    public boolean isCalculationComplete() {
		return mCalculationComplete;
	}

	private boolean backtracking(Siteswap siteswap, TreeSet<Integer> freePositions,
								 int currentIndex, int uniqueRepresentationIndex) {
		if (System.currentTimeMillis() - mStartTime > mTimeoutSeconds * 1000)
			return false;
		if (currentIndex == mPeriodLength) {

			// filters shorter period and non unique representation siteswaps
			if (uniqueRepresentationIndex != 0)
				return true;
			if (testFilters(siteswap)) {
				mSiteswaps.add(new Siteswap(siteswap));
				if (mSiteswaps.size() >= mMaxResults)
					return false;
			}
			return true;
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


		TreeSet<Integer> freePositionsOld = new TreeSet<Integer>(freePositions);
		for (Integer pos : freePositionsOld) {
			int n = (int) Math.ceil((min + currentIndex - pos) / (double) mPeriodLength);
			while(pos - currentIndex + n * mPeriodLength <= max) {
				int value = pos - currentIndex + n * mPeriodLength;
				// TODO test filters and other conditions
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

	private boolean testFilters(Siteswap siteswap) {
		if (mFilterList == null)
			return true;
		for (Filter filter : mFilterList) {
			if (!filter.matches_filter(siteswap))
				return false;
		}
		return true;
	}
}
