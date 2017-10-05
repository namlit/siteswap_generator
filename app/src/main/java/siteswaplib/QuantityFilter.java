package siteswaplib;

public class QuantityFilter extends Filter {

	public enum Type {GREATER_EQUAL, SMALLER_EQUAL, EQUAL}
	
	private Type mType;
	private char mFilterValue;
	private int mThresholdValue;
	
	public QuantityFilter(char filterValue, Type type, int threshold) {
		this.mFilterValue = filterValue;
		this.mType = type;
		this.mThresholdValue = threshold;
	}
	
	public QuantityFilter(int filterValue, Type type, int threshold) {
		this.mFilterValue = Siteswap.intToChar(filterValue);
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
		String str = new String("number of ");
		str += Character.toString(mFilterValue);
		
		if (mType == Type.GREATER_EQUAL)
			str += " ≥ ";
		else if (mType == Type.SMALLER_EQUAL)
			str += " ≤ ";
		else
			str += " = ";
		str += String.valueOf(mThresholdValue);
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
