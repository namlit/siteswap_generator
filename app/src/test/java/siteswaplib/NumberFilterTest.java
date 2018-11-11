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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class NumberFilterTest {

    @Test
    public void testCreateSynchronousFromString() {
        String expected = new String("6p");
        NumberFilter filter = new NumberFilter(expected, NumberFilter.Type.EQUAL, 1, 2);
        assertEquals(expected, filter.getFilterValue().toString());
    }

    @Test
    public void testEquals() {
        NumberFilter filter1 = new NumberFilter("6p", NumberFilter.Type.EQUAL, 1, 2);
        NumberFilter filter2 = new NumberFilter("6p", NumberFilter.Type.EQUAL, 1, 2);
        NumberFilter filter3 = new NumberFilter("6", NumberFilter.Type.EQUAL, 1, 2);
        NumberFilter filter4 = new NumberFilter("8p", NumberFilter.Type.EQUAL, 1, 2);
        assertEquals(filter1, filter2);
        assertNotEquals(filter1, filter3);
        assertNotEquals(filter1, filter4);
    }

    @Test
    public void testSynchronousPassConversion() {
        NumberFilter filter = new NumberFilter("9p", NumberFilter.Type.EQUAL, 1, 3);
        assertArrayEquals(new int[]{10, 11}, filter.getFilterValue().getValues(0));
        assertArrayEquals(new int[]{8, 10}, filter.getFilterValue().getValues(1));
        assertArrayEquals(new int[]{7, 8}, filter.getFilterValue().getValues(2));
    }

    @Test
    public void testCountValue() {
        NumberFilter filter1 = new NumberFilter("9p", NumberFilter.Type.EQUAL, 1, 3);
        NumberFilter filter2 = new NumberFilter("9", NumberFilter.Type.EQUAL, 1, 3);
        Siteswap siteswap1 = new Siteswap("89a4945", 3);
        Siteswap siteswap2 = new Siteswap("8859a57889a059a49a5919a3783", 3);
        siteswap1.setNumberOfSynchronousHands(3);
        siteswap2.setNumberOfSynchronousHands(3);
        siteswap2.setSynchronousStartPosition(2);
        int expected1 = 4;
        int expected2 = 9;
        int expected3 = 6; // Attention: counted over global period length
        int expected4 = 6;
        assertEquals(expected1, siteswap1.countFilterValue(filter1.getFilterValue()));
        assertEquals(expected2, siteswap2.countFilterValue(filter1.getFilterValue()));
        assertEquals(expected3, siteswap1.countFilterValue(filter2.getFilterValue()));
        assertEquals(expected4, siteswap2.countFilterValue(filter2.getFilterValue()));
    }

}
