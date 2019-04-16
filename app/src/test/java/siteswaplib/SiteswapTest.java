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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SiteswapTest {

    @Test
    public void testCreateSynchronousFromString() {
        String expected = new String("6p");
        NumberFilter filter = new NumberFilter(expected, NumberFilter.Type.EQUAL, 1, 2);
        assertEquals(expected, filter.getFilterValue().toString());
    }

    @Test
    public void testParseString() {
        String siteswap_str_1 = "86277.2.1.0";
        Siteswap siteswap_1 = new Siteswap(siteswap_str_1);
        assertEquals("86277", siteswap_1.toAsyncString());
        assertEquals(2, siteswap_1.getNumberOfJugglers());
        assertEquals(1, siteswap_1.getNumberOfSynchronousHands());
        assertEquals(0, siteswap_1.getSynchronousStartPosition());
        assertEquals(siteswap_str_1, siteswap_1.toParsableString());

        String siteswap_str_2 = "c9ec99bc39ac99a.3.3.1";
        Siteswap siteswap_2 = new Siteswap(siteswap_str_2);
        assertEquals("c9ec99bc39ac99a", siteswap_2.toAsyncString());
        assertEquals(3, siteswap_2.getNumberOfJugglers());
        assertEquals(3, siteswap_2.getNumberOfSynchronousHands());
        assertEquals(1, siteswap_2.getSynchronousStartPosition());
        assertEquals(siteswap_str_2, siteswap_2.toParsableString());
    }

    @Test
    public void testMergeSyncSiteswapsFailOnInvalidSiteswap() {
        Siteswap s1 = new Siteswap("86277");
        Siteswap s2 = new Siteswap("6787a");
        Siteswap merged = Siteswap.mergeCompatible(s1, s2);
        assertEquals(merged, null);
    }

    @Test
    public void testMergeSyncSiteswapsFailOnIncompatibility() {
        Siteswap s1 = new Siteswap("86277");
        Siteswap s2 = new Siteswap("86727");
        Siteswap merged = Siteswap.mergeCompatible(s1, s2);
        assertEquals(merged, null);
    }

}
