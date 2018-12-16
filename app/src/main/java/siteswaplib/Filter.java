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

import siteswaplib.NumberFilter.Type;

public abstract class Filter implements Serializable {

	public enum FilterType {NUMBER_FILTER, PATTERN_FILTER, INTERFACE_FILTER,
		LOCAL_PATTERN_FILTER, LOCAL_INTERFACE_FILTER};

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

    public abstract String toParsableString();
	public abstract Filter fromParsableString(String str);
}
