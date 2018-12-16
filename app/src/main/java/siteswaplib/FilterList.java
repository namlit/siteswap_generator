/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2017 Tilman Sinning
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package siteswaplib;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import siteswaplib.NumberFilter.Type;

public class FilterList extends LinkedList<Filter> implements Serializable {


    // TODO add and use method updateNumberOfJugglersAndSynchronousHands

	public void addDefaultFilters(int numberOfJugglers, int minThrow,
										 int numberOfSynchronousHands) {
		if (numberOfJugglers <= 1)
			return;

		// remove Filters first, to avoid duplicate filters
		removeDefaultFilters(numberOfJugglers, minThrow, numberOfSynchronousHands);
		addFirst(new NumberFilter(Siteswap.PASS, Type.GREATER_EQUAL, 1, numberOfSynchronousHands));
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers)) {
			    NumberFilter filter = new NumberFilter(i, Type.EQUAL, 0, numberOfSynchronousHands);
			    if (!contains(filter))
                    addFirst(filter);
			}
		}
	}

	public void addDefaultFilters(int numberOfJugglers,
										 int numberOfSynchronousHands) {
		addDefaultFilters(numberOfJugglers, 0, numberOfSynchronousHands);
	}

	public void addZips(int numberOfJugglers, int numberOfSynchronousHands) {

		while (remove(new NumberFilter(numberOfJugglers, Type.EQUAL, 0, numberOfSynchronousHands)))
			;
	}

	public void addZaps(int numberOfJugglers, int numberOfSynchronousHands) {

		int max = 3*numberOfJugglers;
		if (numberOfSynchronousHands > 1 && numberOfJugglers > 1)
			max = 2 * numberOfJugglers + 2; // Where is just one zap in synchronous patters "2p"
		for (int i = 2 * numberOfJugglers + 1; i < max; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				while (remove(new NumberFilter(i, Type.EQUAL, 0,
						numberOfSynchronousHands)))
					;
		}
	}

	public void addHolds(int numberOfJugglers, int numberOfSynchronousHands) {
		remove(new NumberFilter(2 * numberOfJugglers, Type.EQUAL, 0, numberOfSynchronousHands));
	}

	public void removeDefaultFilters(int numberOfJugglers, int minThrow,
											int numberOfSynchronousHands) {
		if (numberOfJugglers <= 1)
			return;
		while (remove(new NumberFilter(Siteswap.PASS, Type.GREATER_EQUAL, 1,
				numberOfSynchronousHands)))
			;
		for (int i = minThrow; i < 2*numberOfJugglers; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				while (remove(new NumberFilter(i, Type.EQUAL, 0,
						numberOfSynchronousHands)))
					;
		}
	}

	public void removeDefaultFilters(int numberOfJugglers,
										 int numberOfSynchronousHands) {
		removeDefaultFilters(numberOfJugglers, 0, numberOfSynchronousHands);
	}

	public void removeZips(int numberOfJugglers, int numberOfSynchronousHands) {

		addZips(numberOfJugglers, numberOfSynchronousHands);
		addFirst(new NumberFilter(numberOfJugglers, Type.EQUAL, 0, 1));
	}

	public void removeZaps(int numberOfJugglers, int numberOfSynchronousHands) {

		addZaps(numberOfJugglers, numberOfSynchronousHands);
		int max = 3*numberOfJugglers;
		if (numberOfSynchronousHands > 1 && numberOfJugglers > 1)
			max = 2 * numberOfJugglers + 2; // Where is just one zap in synchronous patters "2p"
		for (int i = 2 * numberOfJugglers + 1; i < max; ++i) {
			if ( Siteswap.isPass(i, numberOfJugglers))
				addFirst(new NumberFilter(i, Type.EQUAL, 0,
						numberOfSynchronousHands));
		}
	}

	public void removeHolds(int numberOfJugglers, int numberOfSynchronousHands) {

		addHolds(numberOfJugglers, numberOfSynchronousHands);
		addFirst(new NumberFilter(2 * numberOfJugglers, Type.EQUAL, 0, 1));
	}

    public String toParsableString() {
        String str = new String();
        for (Filter filter : this) {
            if (filter instanceof LocalPatternFilter) {
                str += Filter.FilterType.LOCAL_PATTERN_FILTER.toString() + ":";
                str += filter.toParsableString() + ";";
            }
            else if (filter instanceof LocalInterfaceFilter) {
                str += Filter.FilterType.LOCAL_INTERFACE_FILTER.toString() + ":";
                str += filter.toParsableString() + ";";
            }
            else if (filter instanceof PatternFilter) {
                str += Filter.FilterType.PATTERN_FILTER.toString() + ":";
                str += filter.toParsableString() + ";";
            }
            else if (filter instanceof InterfaceFilter) {
                str += Filter.FilterType.INTERFACE_FILTER.toString() + ":";
                str += filter.toParsableString() + ";";
            }
            else if (filter instanceof NumberFilter) {
                str += Filter.FilterType.NUMBER_FILTER.toString() + ":";
                str += filter.toParsableString() + ";";
            }
        }
        return str;
    }

    public FilterList fromParsableString(String str) {
		clear();
        String[] filters = str.split(";");
        for (String f : filters) {
            String[] filter = f.split(":");
            if (filter.length != 2)
                continue;
            if (Filter.FilterType.valueOf(filter[0]).equals(Filter.FilterType.LOCAL_PATTERN_FILTER)) {
                add(new LocalPatternFilter(filter[1]));
            }
            else if (Filter.FilterType.valueOf(filter[0]).equals(Filter.FilterType.LOCAL_INTERFACE_FILTER)) {
                add(new LocalInterfaceFilter(filter[1]));
            }
            else if (Filter.FilterType.valueOf(filter[0]).equals(Filter.FilterType.PATTERN_FILTER)) {
                add(new PatternFilter(filter[1]));
            }
            else if (Filter.FilterType.valueOf(filter[0]).equals(Filter.FilterType.INTERFACE_FILTER)) {
                add(new InterfaceFilter(filter[1]));
            }
            else if (Filter.FilterType.valueOf(filter[0]).equals(Filter.FilterType.NUMBER_FILTER)) {
                add(new NumberFilter(filter[1]));
            }
        }
        return this;
    }
}
