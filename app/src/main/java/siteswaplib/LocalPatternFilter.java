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

/**
 * Created by tilman on 29.10.17.
 */

public class LocalPatternFilter extends PatternFilter {

    private Siteswap mLocalPattern;

    public LocalPatternFilter(Siteswap pattern, Type type, int numberOfJugglers) {
        super(pattern, type);

        mLocalPattern = pattern;

        byte[] globalPattern = new byte[numberOfJugglers * pattern.period_length() - (numberOfJugglers-1)];

        for (int i = 0; i < globalPattern.length; ++i) {
            if (i % numberOfJugglers == 0)
                globalPattern[i] = pattern.at(i / numberOfJugglers);
            else
                globalPattern[i] = Siteswap.DONT_CARE;
        }
        mPattern = new Siteswap(globalPattern);

    }

    @Override
    public String toString() {
        String str;

        if (mType == Type.INCLUDE)
            str = new String("Include Local: ");
        else
            str = new String("Exclude Local: ");
        str += mLocalPattern.toString();
        return str;
    }

    public Siteswap getGlobalPattern() {
        return mPattern;
    }

    @Override
    public Siteswap getPattern() {
        return mLocalPattern;
    }
}
