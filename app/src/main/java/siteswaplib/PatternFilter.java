package siteswaplib;

public class PatternFilter extends Filter {

	public enum Type {EXCLUDE, INCLUDE}
	
	protected Siteswap mPattern;
	protected Type mType;
	
	public PatternFilter(Siteswap pattern, Type type) {
		this.mPattern = pattern;
		this.mType = type;
	}
	
	@Override
	public boolean isFulfilled(Siteswap siteswap) {
		if (mType == Type.INCLUDE)
			return siteswap.isPattern(mPattern);
		return !siteswap.isPattern(mPattern);
	}


	@Override
	public boolean isPartlyFulfilled(Siteswap siteswap, int index) {
		if (mType == Type.INCLUDE)
			return true; // TODO return false, if it is impossible to fulfill pattern
		switch (mType)
		{
			case INCLUDE:
				break;
			case EXCLUDE:
				if (index < mPattern.period_length() - 1)
					return true;
				return !siteswap.isPattern(mPattern, index + 1 - mPattern.period_length());
		}
		return true;
	}

	@Override
	public String toString() {
		String str;
		
		if (mType == Type.INCLUDE)
			str = new String("Include: ");
		else
			str = new String("Exclude: ");
		str += mPattern.toString();
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof PatternFilter))
			return false;
		PatternFilter rhs = (PatternFilter) obj;
		return mType.equals(rhs.mType) && mPattern.equals(rhs.mPattern);
	}

	public Type getType() {
		return mType;
	}

	public Siteswap getPattern() {
		return mPattern;
	}

}
