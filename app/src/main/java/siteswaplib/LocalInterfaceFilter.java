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

public class LocalInterfaceFilter extends InterfaceFilter {

    static final private String VERSION = "1";

    private Siteswap mLocalPattern;

    public LocalInterfaceFilter() {
    }

    public LocalInterfaceFilter(String str) {
        fromParsableString(str);
    }

    public LocalInterfaceFilter(Siteswap pattern, Type type, int numberOfJugglers) {
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
    public String toParsableString() {
        String str = new String();
        str += String.valueOf(VERSION) + ",";
        str += mLocalPattern.toParsableString() + ",";
        str += super.toParsableString();
        return str;
    }

    @Override
    public LocalInterfaceFilter fromParsableString(String str) {
        String[] splits = str.split(",");
        int begin_index = 0;
        if (splits.length < 3) {
            return this;
        }
        if (!splits[0].equals(VERSION))
            return this;
        begin_index += splits[0].length() + 1;
        mLocalPattern = new Siteswap(splits[1]);
        begin_index += splits[1].length() + 1;
        super.fromParsableString(str.substring(begin_index));
        return this;
    }

    @Override
    public String toString() {
        String str;

        if (mType == Type.INCLUDE)
            str = new String("Include Local Interface: ");
        else
            str = new String("Exclude Local Interface: ");
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
