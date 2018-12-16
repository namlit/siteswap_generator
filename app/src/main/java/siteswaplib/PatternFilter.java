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

public class PatternFilter extends Filter {

	public enum Type {EXCLUDE, INCLUDE}
	static final private String VERSION = "1";

	protected Siteswap mPattern;
	protected Type mType;

	public PatternFilter() {
	}

	public PatternFilter(String str) {
		fromParsableString(str);
	}

	public PatternFilter(Siteswap pattern, Type type) {
		this.mPattern = pattern;
		this.mType = type;
	}

	@Override
	public boolean isFulfilled(Siteswap siteswap) {
		if (mType == Type.INCLUDE)
			return siteswap.isPattern(mPattern);
		return !siteswap.isPattern(mPattern);
	}


	@Override
	public boolean isPartlyFulfilled(Siteswap siteswap, int index) {
		if (mType == Type.INCLUDE)
			return true; // TODO return false, if it is impossible to fulfill pattern
		switch (mType)
		{
			case INCLUDE:
				break;
			case EXCLUDE:
				if (index < mPattern.period_length() - 1)
					return true;
				return !siteswap.isPattern(mPattern, index + 1 - mPattern.period_length());
		}
		return true;
	}

	@Override
	public String toParsableString() {
		String str = new String();
		str += String.valueOf(VERSION) + ",";
		str += mPattern.toParsableString() + ",";
		str += mType.toString() + ",";
		return str;
	}

	@Override
	public PatternFilter fromParsableString(String str) {
		String[] splits = str.split(",");
		if (splits.length < 3) {
			return this;
		}
        if (!splits[0].equals(VERSION))
            return this;
        mPattern = new Siteswap(splits[1]);
        mType = Type.valueOf(splits[2]);
        return this;
	}

	@Override
	public String toString() {
		String str;
		if (mType == Type.INCLUDE)
			str = new String("Include: ");
		else
			str = new String("Exclude: ");
		str += mPattern.toString();
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof PatternFilter))
			return false;
		PatternFilter rhs = (PatternFilter) obj;
		return mType.equals(rhs.mType) && mPattern.equals(rhs.mPattern);
	}

	public Type getType() {
		return mType;
	}

	public Siteswap getPattern() {
		return mPattern;
	}

}
