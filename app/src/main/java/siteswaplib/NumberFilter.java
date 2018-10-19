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
import java.util.Objects;

public class NumberFilter extends Filter {

	public class FilterValue implements Serializable {
		private int mValue;

		public FilterValue(int filterValue) {
			this.mValue = filterValue;
		}

		public FilterValue(String filterValue) {
			fromString(filterValue);
		}

		public void setValue(int value) {
			this.mValue = value;
		}

		// A 6p in a synchronous pattern can be either a 5 or a 7
		// This function returns all possible numbers at a given
		// synchronous position index
		public int[] getValues(int synchronousPosition) {
			if (isSpecialThrow()) {
				return new int[]{mValue};
			}
			if (mNumberOfSynchronousHands == 1 || isSelf()) {
				return new int[]{mValue};
			}

			int landingPosition = 0;
			int length = mNumberOfSynchronousHands - 1;
			if (getCorrectedValue() == 0) {
			    length -= synchronousPosition;
			    landingPosition = synchronousPosition + 1;
			}
			int values[] = new int[length];
			for (int i = 0; i < length; ++i) {
				if (landingPosition == synchronousPosition)
					++landingPosition;
				values[i] = getCorrectedValue() + (landingPosition - synchronousPosition);
				++landingPosition;
			}
			return values;
		}

		public int getCorrectedValue() {

			if (isSpecialThrow())
				return mValue;
			int diffToValidThrow = mValue % mNumberOfSynchronousHands;
			return mValue - diffToValidThrow;
		}

		public void fromString(String strValue) {
			if (mNumberOfSynchronousHands > 1 &&
					strValue.length() == 2 && strValue.charAt(1) == 'p') {
				mValue = Siteswap.charToInt(strValue.charAt(0)) + 1;
				if (mValue % mNumberOfSynchronousHands != 1)
					mValue = Siteswap.INVALID;
				return;
			}
			mValue = Siteswap.stringToInt(strValue);
		}

		@Override
		public String toString() {
			if (mValue < 0) { // Pass or Self: use String conversion of Siteswap class
				return Siteswap.intToString(mValue);
			}
			int diffToValidThrow = mValue % mNumberOfSynchronousHands;
			String str = Siteswap.intToString(mValue - diffToValidThrow);
			if (diffToValidThrow != 0)
				str += "p";
			return str;
		}
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof FilterValue))
				return false;
			FilterValue rhs = (FilterValue) obj;
			return toString().equals(rhs.toString());
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		public boolean isPass() {
			return mValue == Siteswap.PASS;
		}

		public boolean isSelf() {
			return mValue == Siteswap.SELF;
		}

		public boolean isSpecialThrow() {
			return mValue < 0;
		}
	}
	public enum Type {GREATER_EQUAL, SMALLER_EQUAL, EQUAL}

	private Type mType;
	private FilterValue mFilterValue;
	private int mThresholdValue;
	private int mNumberOfSynchronousHands;


	public NumberFilter(int filterValue, Type type, int threshold, int numberOfSynchronousHands) {
		this.mType = type;
		this.mThresholdValue = threshold;
		this.mNumberOfSynchronousHands = numberOfSynchronousHands;
		this.mFilterValue = new FilterValue(filterValue);
	}

	public NumberFilter(String filterValue, Type type, int threshold, int numberOfSynchronousHands) {
		this.mType = type;
		this.mThresholdValue = threshold;
		this.mNumberOfSynchronousHands = numberOfSynchronousHands;
		this.mFilterValue = new FilterValue(filterValue);
	}


	public void setNumberOfSynchronousHands(int numberOfSynchronousHands) {
	    this.mNumberOfSynchronousHands = numberOfSynchronousHands;
	}

	@Override
	public boolean isFulfilled(Siteswap siteswap) {
		if (mType == Type.GREATER_EQUAL)
			return siteswap.countFilterValue(mFilterValue) >= mThresholdValue;
		if (mType == Type.SMALLER_EQUAL)
			return siteswap.countFilterValue(mFilterValue) <= mThresholdValue;

		return siteswap.countFilterValue(mFilterValue) == mThresholdValue;
	}

	@Override
    public boolean isPartlyFulfilled(Siteswap siteswap, int index) {

        int currentCount = siteswap.countFilterValuePartitially(mFilterValue, index);

        switch (mType) {
            case GREATER_EQUAL:
                return currentCount + (siteswap.global_period_length() - index) > mThresholdValue;
            case SMALLER_EQUAL:
                return currentCount <= mThresholdValue;
            case EQUAL:
                return currentCount <= mThresholdValue &&
                        currentCount + (siteswap.global_period_length() - index) > mThresholdValue;
        }
        return true;
    }

	@Override
	public String toString() {
		String str = new String("");

		if (mType == Type.EQUAL) {
			if (mThresholdValue == 0)
				return "no " + mFilterValue.toString();
			else
				str += "exactly " + Siteswap.intToString(mThresholdValue);
		}

		else if (mType == Type.GREATER_EQUAL)
			str += "at least " + Siteswap.intToString(mThresholdValue);
		else if (mType == Type.SMALLER_EQUAL)
			str += "not more than " + Siteswap.intToString(mThresholdValue);
		else
			return "";

		if (mFilterValue.isSpecialThrow()) { // Pass or Self: use String conversion of Siteswap class
			str += " " + mFilterValue.toString();

			if (mThresholdValue != 1) { // Plural s on string representation needed
				if (mFilterValue.isPass())
					str += "es";
				else
					str += "s";
			}
		}
		else { // throw with height >= 0
			if (mThresholdValue == 1)
				str += " throw";
			else
				str += " throws";

			str += " with height " + mFilterValue.toString();
		}

		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof NumberFilter))
			return false;
		NumberFilter rhs = (NumberFilter) obj;
		return toString().equals(rhs.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public Type getType() {
		return mType;
	}


	public FilterValue getFilterValue() {
		return mFilterValue;
	}

	public int getThresholdValue() {
		return mThresholdValue;
	}

	static public String[] getPossibleValues(int minThrow, int maxThrow,
											 int numberOfSynchronousHands) {
		if (numberOfSynchronousHands == 1) {
			int length = maxThrow - minThrow + 1;
			String arr[] = new String[length];
			for(int i = 0; i < length; ++i) {
				arr[i] = Siteswap.intToString(i + minThrow);
			}
			return arr;
		}
		int distMinToValidThrow = minThrow % numberOfSynchronousHands;
		int distMaxToValidThrow = maxThrow % numberOfSynchronousHands;
		int length = 2 * ((maxThrow - distMaxToValidThrow - minThrow) / numberOfSynchronousHands + 1);
		if (distMinToValidThrow != 0) {
			length += 1;
		}
		if (distMaxToValidThrow != 0) {
			length += 1;
		}
		String[] arr = new String[length];
		int i = 0;
		int currentThrow = minThrow;
		if (distMinToValidThrow != 0) {
			arr[i] = Siteswap.intToString(minThrow - distMinToValidThrow) + "p";
			currentThrow = minThrow + (numberOfSynchronousHands - distMinToValidThrow);
			++i;
		}
		for(; i < length - 1; i += 2) {
			arr[i] = Siteswap.intToString(currentThrow);
			arr[i+1] = Siteswap.intToString(currentThrow) + "p";
			currentThrow += numberOfSynchronousHands;
		}
		if (distMaxToValidThrow != 0) {
			arr[i] = Siteswap.intToString(currentThrow) + "p";
		}
		return arr;
	}


}
