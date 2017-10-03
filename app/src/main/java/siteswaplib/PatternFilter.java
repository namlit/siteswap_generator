package siteswaplib;

import java.io.Serializable;
import siteswaplib.Pattern;

public class PatternFilter extends Filter {

	public enum Type {EXCLUDE, INCLUDE}
	
	private Pattern pattern;
	private Type type;
	
	public PatternFilter(String pattern, Type type, int number_of_jugglers) {
		this.pattern = new Pattern(pattern, number_of_jugglers);
		this.type = type;
	}
	
	@Override
	public boolean matches_filter(Siteswap siteswap) {
		if (type == Type.INCLUDE)
			return pattern.testPattern(siteswap);
		return !pattern.testPattern(siteswap);
	}

	@Override
	public String toString() {
		String str;
		
		if (type == Type.INCLUDE)
			str = new String("Include: ");
		else
			str = new String("Exclude: ");
		str += pattern.toString();
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof PatternFilter))
			return false;
		PatternFilter rhs = (PatternFilter) obj;
		return type == rhs.type && pattern == rhs.pattern;
	}

}
