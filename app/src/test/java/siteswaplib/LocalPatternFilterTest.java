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

public class LocalPatternFilterTest {

    @Test
    public void testParsableStringConversion() {
        LocalPatternFilter filter1 = new LocalPatternFilter(new Siteswap("ppsps"), LocalPatternFilter.Type.EXCLUDE, 3);
        LocalPatternFilter filter2 = new LocalPatternFilter(new Siteswap("972"), LocalPatternFilter.Type.INCLUDE, 5);

        String filter1str = filter1.toParsableString();
        String filter2str = filter2.toParsableString();

        LocalPatternFilter filter1conv = new LocalPatternFilter().fromParsableString(filter1str);
        LocalPatternFilter filter2conv = new LocalPatternFilter().fromParsableString(filter2str);

        assertEquals(filter1, filter1conv);
        assertEquals(filter2, filter2conv);

    }

}
