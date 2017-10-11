package siteswaplib;

public class QuantityFilter extends Filter {

	public enum Type {GREATER_EQUAL, SMALLER_EQUAL, EQUAL}
	
	private Type mType;
	private byte mFilterValue;
	private int mThresholdValue;


	public QuantityFilter(int filterValue, Type type, int threshold) {
		this.mFilterValue = (byte) filterValue;
		this.mType = type;
		this.mThresholdValue = threshold;
	}
	
	@Override
	public boolean matches_filter(Siteswap siteswap) {
		if (mType == Type.GREATER_EQUAL)
			return siteswap.countValue(mFilterValue) >= mThresholdValue;
		if (mType == Type.SMALLER_EQUAL)
			return siteswap.countValue(mFilterValue) <= mThresholdValue;
		
		return siteswap.countValue(mFilterValue) == mThresholdValue;
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

		if (mFilterValue < 0) {
			str += " " + Siteswap.intToString(mFilterValue);
		}
		else {
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
		if (! (obj instanceof QuantityFilter))
			return false;
		QuantityFilter rhs = (QuantityFilter) obj;
		return mType == rhs.mType && mFilterValue == rhs.mFilterValue &&
				mThresholdValue == rhs.mThresholdValue;
	}

}
