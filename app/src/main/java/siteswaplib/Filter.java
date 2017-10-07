package siteswaplib;

import java.io.Serializable;
import java.util.LinkedList;

import siteswaplib.Siteswap;
import siteswaplib.QuantityFilter.Type;

public abstract class Filter implements Serializable {

	public abstract boolean matches_filter(Siteswap siteswap);
	
	public static void addDefaultFilters(LinkedList<Filter> filterList,
										 int numberOfJugglers, int minThrow) {
		
		if (numberOfJugglers <= 1)
			return;
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				filterList.addFirst(new QuantityFilter(i, Type.EQUAL, 0));
		}
		filterList.addFirst(new QuantityFilter('p', Type.GREATER_EQUAL, 1));
	}

	public static void addDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		addDefaultFilters(filterList, numberOfJugglers, 0);
	}

	public static void addZips(LinkedList<Filter> filterList, int numberOfJugglers) {

		while (filterList.remove(new QuantityFilter(numberOfJugglers, Type.EQUAL, 0)))
			;
	}

	public static void addZaps(LinkedList<Filter> filterList, int numberOfJugglers) {

		for (int i = 2 * numberOfJugglers + 1; i < 3*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				while (filterList.remove(new QuantityFilter(i, Type.EQUAL, 0)))
					;
		}
	}

	public static void addHolds(LinkedList<Filter> filterList, int numberOfJugglers) {
		filterList.remove(new QuantityFilter(2 * numberOfJugglers, Type.EQUAL, 0));
	}
	
	public static void removeDefaultFilters(LinkedList<Filter> filterList,
											int numberOfJugglers, int minThrow) {
		if (numberOfJugglers <= 1)
			return;
		while (filterList.remove(new QuantityFilter('p', Type.GREATER_EQUAL, 1)))
			;
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				while (filterList.remove(new QuantityFilter(i, Type.EQUAL, 0)))
					;
		}
	}

	public static void removeDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		removeDefaultFilters(filterList, numberOfJugglers, 0);
	}

	public static void removeZips(LinkedList<Filter> filterList, int numberOfJugglers) {

		filterList.addFirst(new QuantityFilter(numberOfJugglers, Type.EQUAL, 0));
	}

	public static void removeZaps(LinkedList<Filter> filterList, int numberOfJugglers) {

		for (int i = 2 * numberOfJugglers + 1; i < 3*numberOfJugglers; ++i) {
			if ( Pattern.isPass(i, numberOfJugglers))
				filterList.addFirst(new QuantityFilter(i, Type.EQUAL, 0));
		}
	}

	public static void removeHolds(LinkedList<Filter> filterList, int numberOfJugglers) {
		filterList.addFirst(new QuantityFilter(2 * numberOfJugglers, Type.EQUAL, 0));
	}

}
