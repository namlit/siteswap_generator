package siteswaplib;

import java.io.Serializable;

public class QuantityFilter extends Filter {

	public enum Type {GREATER_EQUAL, SMALLER_EQUAL, EQUAL}
	
	private Type type;
	private char filterValue;
	private int thresholdValue;
	
	public QuantityFilter(char filterValue, Type type, int threshold) {
		this.filterValue = filterValue;
		this.type = type;
		this.thresholdValue = threshold;
	}
	
	public QuantityFilter(int filterValue, Type type, int threshold) {
		this.filterValue = Siteswap.intToChar(filterValue);
		this.type = type;
		this.thresholdValue = threshold;
	}
	
	@Override
	public boolean matches_filter(Siteswap siteswap) {
		if (type == Type.GREATER_EQUAL)
			return siteswap.getValueOccurence(filterValue) >= thresholdValue;
		if (type == Type.SMALLER_EQUAL)
			return siteswap.getValueOccurence(filterValue) <= thresholdValue;
		
		return siteswap.getValueOccurence(filterValue) == thresholdValue;
	}
	
	@Override
	public String toString() {
		String str = new String("number of ");
		str += Character.toString(filterValue);
		
		if (type == Type.GREATER_EQUAL)
			str += " ≥ ";
		else if (type == Type.SMALLER_EQUAL)
			str += " ≤ ";
		else
			str += " = ";
		str += String.valueOf(thresholdValue);
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof QuantityFilter))
			return false;
		QuantityFilter rhs = (QuantityFilter) obj;
		return type == rhs.type && filterValue == rhs.filterValue && 
				thresholdValue == rhs.thresholdValue;
	}

}
