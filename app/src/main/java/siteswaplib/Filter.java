package siteswaplib;

import java.io.Serializable;
import java.util.LinkedList;

import siteswaplib.NumberFilter.Type;

public abstract class Filter implements Serializable {

	public abstract boolean isFulfilled(Siteswap siteswap);

    /**
     * The filter is only tested at/up to index position. This function can be
     * used during siteswap generation to test, if a partly generated siteswap
     * would fulfill the filter condition. Returns true, if the filter is currently
     * fulfilled or might be fulfilled later, when the complete siteswap is
     * generated. Returns False, if it is not possible anymore, to fulfill the filter
     * condition.
     * */
    public abstract boolean isPartlyFulfilled(Siteswap siteswap, int index);
	
	public static void addDefaultFilters(LinkedList<Filter> filterList,
										 int numberOfJugglers, int minThrow) {
		if (numberOfJugglers <= 1)
			return;

		// remove Filters first, to avoid duplicate filters
		removeDefaultFilters(filterList, numberOfJugglers, minThrow);
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				filterList.addFirst(new NumberFilter(i, Type.EQUAL, 0));
		}
		filterList.addFirst(new NumberFilter(Siteswap.PASS, Type.GREATER_EQUAL, 1));
	}

	public static void addDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		addDefaultFilters(filterList, numberOfJugglers, 0);
	}

	public static void addZips(LinkedList<Filter> filterList, int numberOfJugglers) {

		while (filterList.remove(new NumberFilter(numberOfJugglers, Type.EQUAL, 0)))
			;
	}

	public static void addZaps(LinkedList<Filter> filterList, int numberOfJugglers) {

		for (int i = 2 * numberOfJugglers + 1; i < 3*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				while (filterList.remove(new NumberFilter(i, Type.EQUAL, 0)))
					;
		}
	}

	public static void addHolds(LinkedList<Filter> filterList, int numberOfJugglers) {
		filterList.remove(new NumberFilter(2 * numberOfJugglers, Type.EQUAL, 0));
	}
	
	public static void removeDefaultFilters(LinkedList<Filter> filterList,
											int numberOfJugglers, int minThrow) {
		if (numberOfJugglers <= 1)
			return;
		while (filterList.remove(new NumberFilter(Siteswap.PASS, Type.GREATER_EQUAL, 1)))
			;
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				while (filterList.remove(new NumberFilter(i, Type.EQUAL, 0)))
					;
		}
	}

	public static void removeDefaultFilters(LinkedList<Filter> filterList, int numberOfJugglers) {
		removeDefaultFilters(filterList, numberOfJugglers, 0);
	}

	public static void removeZips(LinkedList<Filter> filterList, int numberOfJugglers) {

		filterList.addFirst(new NumberFilter(numberOfJugglers, Type.EQUAL, 0));
	}

	public static void removeZaps(LinkedList<Filter> filterList, int numberOfJugglers) {

		for (int i = 2 * numberOfJugglers + 1; i < 3*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				filterList.addFirst(new NumberFilter(i, Type.EQUAL, 0));
		}
	}

	public static void removeHolds(LinkedList<Filter> filterList, int numberOfJugglers) {
		filterList.addFirst(new NumberFilter(2 * numberOfJugglers, Type.EQUAL, 0));
	}

}
