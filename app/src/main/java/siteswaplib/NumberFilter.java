package siteswaplib;

public class NumberFilter extends Filter {

	public enum Type {GREATER_EQUAL, SMALLER_EQUAL, EQUAL}
	
	private Type mType;
	private byte mFilterValue;
	private int mThresholdValue;


	public NumberFilter(int filterValue, Type type, int threshold) {
		this.mFilterValue = (byte) filterValue;
		this.mType = type;
		this.mThresholdValue = threshold;
	}
	
	@Override
	public boolean isFulfilled(Siteswap siteswap) {
		if (mType == Type.GREATER_EQUAL)
			return siteswap.countValue(mFilterValue) >= mThresholdValue;
		if (mType == Type.SMALLER_EQUAL)
			return siteswap.countValue(mFilterValue) <= mThresholdValue;
		
		return siteswap.countValue(mFilterValue) == mThresholdValue;
	}

	@Override
    public boolean isPartlyFulfilled(Siteswap siteswap, int index) {

        int currentCount = siteswap.countValuePartitially(mFilterValue, index);

        switch (mType) {
            case GREATER_EQUAL:
                return currentCount + (siteswap.period_length() - index) > mThresholdValue;
            case SMALLER_EQUAL:
                return currentCount <= mThresholdValue;
            case EQUAL:
                return currentCount <= mThresholdValue &&
                        currentCount + (siteswap.period_length() - index) > mThresholdValue;
        }
        return true;
    }
	
	@Override
	public String toString() {
		String str = new String("");

		if (mType == Type.EQUAL) {
			if (mThresholdValue == 0)
				return "no " + Siteswap.intToString(mFilterValue);
			else
				str += "exactly " + String.valueOf(mThresholdValue);
		}

		else if (mType == Type.GREATER_EQUAL)
			str += "at least " + String.valueOf(mThresholdValue);
		else if (mType == Type.SMALLER_EQUAL)
			str += "not more than " + String.valueOf(mThresholdValue);
		else
			return "";

		if (mFilterValue < 0) { // Pass or Self: use String conversion of Siteswap class
			str += " " + Siteswap.intToString(mFilterValue);

			if (mThresholdValue != 1) { // Plural s on string representation needed
				if (mFilterValue == Siteswap.PASS)
					str += "es";
				else
					str += "s";
			}
		}
		else { // throw with height >= 0
			if (mThresholdValue == 1)
				str += " throw";
			else
				str += " throws";

			str += " with height " + Siteswap.intToString(mFilterValue);
		}

		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof NumberFilter))
			return false;
		NumberFilter rhs = (NumberFilter) obj;
		return mType == rhs.mType && mFilterValue == rhs.mFilterValue &&
				mThresholdValue == rhs.mThresholdValue;
	}

	public Type getType() {
		return mType;
	}


	public byte getFilterValue() {
		return mFilterValue;
	}

	public int getThresholdValue() {
		return mThresholdValue;
	}



}
