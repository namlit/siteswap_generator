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

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static siteswaplib.SiteswapGenerator.Status.ALL_SITESWAPS_FOUND;

public class SiteswapGeneratorTest {

    @Test
    public void testGeneration() {
        int numberOfJugglers = 2;
        int period = 7;
        int minThrow = 2;
        int maxThrow = 10;
        int numberOfObjects = 6;
        int numberOfSynchronousHands = 1;
        FilterList filters = new FilterList();
        filters.addDefaultFilters(numberOfJugglers, minThrow, numberOfSynchronousHands);
        filters.add(new LocalInterfaceFilter(
                new Siteswap("ppspsps"), PatternFilter.Type.INCLUDE, numberOfJugglers));
        filters.add(new LocalPatternFilter(
                new Siteswap("22"), PatternFilter.Type.EXCLUDE, numberOfJugglers));
        filters.add(new LocalPatternFilter(
                new Siteswap("44"), PatternFilter.Type.EXCLUDE, numberOfJugglers));

        SiteswapGenerator gen = new SiteswapGenerator(period, maxThrow, minThrow, numberOfObjects, numberOfJugglers, filters);
        gen.setSyncPattern(false);

        SiteswapGenerator.Status status = gen.generateSiteswaps();
        assertEquals(ALL_SITESWAPS_FOUND, status);
        String expected = new String("[7567566, 7746675, 7746756, 7747746, 8456757, 8457567, 8457747, 8556756, 8557566, 8557746, 8557845, 8558556, 8627577, 8852757, 9245778, 9457467, 9458457, 9525678, 9527478, 9527892, 9529458, 9557466, 9557862, 9558456, 9558852, 9625677, 9627477, 9629457, 9645675, 9645774, 9685275, 9695274, 9724677, 9729627, 9744675, 9744774, 9749625, 9749724, 9784275, 9789225, 9794274, 9922497, 9924477, 9924972, 9929427, 9929922, 9952467, 9952962, a524579, a525578, a529529, a625577, a645575, a675275, a724577, a729527, a744575, a749525, a756275, a759245, a774275, a779225, a855275]");
        assertEquals(expected, gen.getSiteswaps().toString());
        return;
    }

}
