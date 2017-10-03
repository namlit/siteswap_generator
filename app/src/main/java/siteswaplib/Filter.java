package siteswaplib;

import java.io.Serializable;
import java.util.LinkedList;

import siteswaplib.Siteswap;
import siteswaplib.QuantityFilter.Type;

public abstract class Filter implements Serializable {

	public abstract boolean matches_filter(Siteswap siteswap);
	
	public static void addDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		
		if (numberOfJugglers <= 1)
			return;
		for (int i = 0; i < 2*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				filterList.addFirst(new QuantityFilter(i, Type.EQUAL, 0));
		}
		filterList.addFirst(new QuantityFilter('p', Type.GREATER_EQUAL, 1));
	}
	
	public static void removeDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		if (numberOfJugglers <= 1)
			return;
		filterList.remove(new QuantityFilter('p', Type.GREATER_EQUAL, 1));
		for (int i = 0; i < 2*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				filterList.remove(new QuantityFilter(i, Type.EQUAL, 0));
		}
	}
}
