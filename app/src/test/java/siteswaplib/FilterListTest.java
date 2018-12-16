/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2018 Tilman Sinning
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilterListTest {

    @Test
    public void testParsableStringConversion() {
        FilterList list1 = new FilterList();
        list1.addDefaultFilters(5, 1);
        FilterList list2 = new FilterList();
        list2.addDefaultFilters(3, 3);

        list1.add(new NumberFilter("9p", NumberFilter.Type.EQUAL, 1, 3));
        list1.add(new NumberFilter("9", NumberFilter.Type.EQUAL, 1, 3));
        list1.add(new LocalInterfaceFilter(new Siteswap("ppsps"), LocalInterfaceFilter.Type.EXCLUDE, 3));

        String list1str = list1.toParsableString();
        String list2str = list2.toParsableString();

        FilterList list1conv = new FilterList().fromParsableString(list1str);
        FilterList list2conv = new FilterList().fromParsableString(list2str);

        assertEquals(list1, list1conv);
        assertEquals(list2, list2conv);

    }

}
