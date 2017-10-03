package siteswaplib;

import java.util.*;
import java.io.Serializable;

import siteswaplib.QuantityFilter.Type;

public class SiteswapGenerator implements Serializable{

	private transient TreeSet<UniqueSiteswap> mFoundSiteswaps;
	private transient LinkedList<UniqueSiteswap> mQueue;
	private transient LinkedList<UniqueSiteswap> mFilteredSiteswaps2;
	private LinkedList<Siteswap> mFilteredSiteswaps;
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
		resetDataStructures();
		setNumberOfJugglers(number_of_jugglers);
	}
	
	public LinkedList<Filter> getFilterList() {
		return mFilterList;
	}
	
	public void setFilterList(LinkedList<Filter> filterList) {
		this.mFilterList = filterList;
	}

	public boolean generateSiteswaps() {
		mCalculationComplete = false;
		resetDataStructures();
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
	
	private boolean backtracking(Siteswap siteswap, TreeSet<Integer> freePositions, 
			int currentIndex, int uniqueRepresentationIndex) {
		if (System.currentTimeMillis() - mStartTime > mTimeoutSeconds * 1000)
			return false;
		if (currentIndex == mPeriodLength) {
			// filters shorter period and non unique representation siteswaps
			if (uniqueRepresentationIndex != 0)
				return true;
			if (testFilters(siteswap)) {
				mFilteredSiteswaps.add(new Siteswap(siteswap));
				if (mFilteredSiteswaps.size() >= mMaxResults)
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
	
	public boolean generateSiteswaps2() {
		resetDataStructures();
        long startTime = System.currentTimeMillis();
		byte[] arr = new byte[mPeriodLength];
		Arrays.fill(arr, mNumberOfObjects);
		UniqueSiteswap temp = new UniqueSiteswap(arr, mNumberOfJugglers);
		mFoundSiteswaps.add(temp);
		mQueue.add(temp);
		if (testFilters(temp))
			mFilteredSiteswaps2.add(temp);
		outerloop:
		while (!mQueue.isEmpty()) {
            if((System.currentTimeMillis() - startTime) >= 1000 * mTimeoutSeconds)
                return false;
			Siteswap current = mQueue.remove();
			for (int i = 0; i < current.period_length(); ++i) {
				temp = new UniqueSiteswap(current);
				temp.swap(i);
				if (temp.is_in_range(mMinThrow, mMaxThrow)) { // TODO more efficient bound for filter rules
					if(mFoundSiteswaps.add(temp)) {
						mQueue.add(temp);
						if (testFilters(temp)) {
							mFilteredSiteswaps2.add(temp);
							if (mFilteredSiteswaps2.size() >= mMaxResults)
								break outerloop;
						}
					}
				}
			}
		}
		return true;
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
	
	public LinkedList<Siteswap> getFilteredSiteswaps() {
		return mFilteredSiteswaps;
	}

	public int getPeriodLength() {
		return mPeriodLength;
	}

	public LinkedList<UniqueSiteswap> getFilteredSiteswaps2() {
		return mFilteredSiteswaps2;
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

	private boolean testFilters(Siteswap siteswap) {
		if (mFilterList == null)
			return true;
		for (Filter filter : mFilterList) {
			if (!filter.matches_filter(siteswap))
				return false;
		}
		return true;
	}
	
	private void resetDataStructures() {

		mFoundSiteswaps = new TreeSet<UniqueSiteswap>();
		mQueue = new LinkedList<UniqueSiteswap>();
		mFilteredSiteswaps2 = new LinkedList<UniqueSiteswap>();
		mFilteredSiteswaps = new LinkedList<Siteswap>();
	}
}
