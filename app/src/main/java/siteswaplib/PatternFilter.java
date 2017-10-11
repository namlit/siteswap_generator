package siteswaplib;

public class PatternFilter extends Filter {

	public enum Type {EXCLUDE, INCLUDE}
	
	private Siteswap mPattern;
	private Type mType;
	
	public PatternFilter(Siteswap pattern, Type type, int number_of_jugglers) {
		this.mPattern = pattern;
		this.mType = type;
	}
	
	@Override
	public boolean matches_filter(Siteswap siteswap) {
		if (mType == Type.INCLUDE)
			return siteswap.testPattern(mPattern);
		return !siteswap.testPattern(mPattern);
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
		return mType == rhs.mType && mPattern == rhs.mPattern;
	}

}
