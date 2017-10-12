package siteswaplib;

import java.text.DecimalFormat;
import java.util.*;
import java.io.Serializable;

public class Siteswap implements Comparable<Siteswap>, Iterable<Byte>, Serializable {

	public static final int SELF = -1;
	public static final int PASS = -2;
	public static final int DONT_CARE = -3;
	public static final int FREE = -4;
	public static final int INVALID = -5;

	protected CyclicByteArray mData;
	protected int mNumberOfJugglers = 1;
	
	public Siteswap(Siteswap s) {
		this.mData = new CyclicByteArray(s.mData);
		this.mNumberOfJugglers = s.mNumberOfJugglers;
	}
	
	public Siteswap(byte[] data, int numberOfJugglers) {
		this.mData = new CyclicByteArray(data);
		this.mNumberOfJugglers = numberOfJugglers;
	}

	public Siteswap(String siteswap, int numberOfJugglers) {
		this.mData = new CyclicByteArray(parseString(siteswap));
		this.mNumberOfJugglers = numberOfJugglers;
	}
	
	public Siteswap(byte[] data) {
		this(data, 1);
	}
	
	public byte at(int index) {
		return mData.at(index);
	}
	
	public void set(int index, int value) {
		mData.modify(index, (byte) value);
	}
	
	public int period_length() {
		return mData.length();
	}
	
	public void swap(int index) {
		byte temp = mData.at(index + 1);
		mData.modify(index+1, (byte) (mData.at(index) - 1));
		mData.modify(index, (byte) (temp + 1));
	}
	
	public int getNumberOfObjects() {
		return getPartialSum(0, period_length()-1) / period_length();
	}
	
	public int getPartialSum(int startIndex, int stopIndex) {
		int sum = 0;
		for (int i = startIndex; i <= stopIndex; ++i) {
			sum += mData.at(i);
		}
		return sum;
	}
	
	public boolean is_in_range(byte min, byte max) {
		for (Byte value : mData) {
			if(value < min || value > max)
				return false;
		}
		return true;
	}
	
	public int countValue(byte value) {
		int counter = 0;
		for (byte i : mData) {
			if(isPatternSingleValue(value, i))
				counter++;
		}
		return counter;
	}

    public int countValuePartitially(byte value, int index) {
        int counter = 0;
        for (int i = 0; i < index; ++i) {
            if(isPatternSingleValue(value, mData.at(i)))
                counter++;
        }
        return counter;
    }
	
	public void rotateRight(int positions) {
		mData.rotateRight(positions);
	}
	
	public void rotateLeft(int positions) {
		mData.rotateLeft(positions);
	}
	
	public void make_unique_representation()
	{

		Siteswap temp = new Siteswap(this);
		int rotate_counter = 0;
		
		for(int i = 0; i < period_length(); ++i) {
			if (compareTo(temp) < 0) {
				mData.rotateRight(rotate_counter);
				rotate_counter = 0;
			}
			temp.rotateRight(1);
			rotate_counter++;
		}
	}
	
	public boolean rotateGetinFree() {
		for (int i = 0; i < period_length(); ++i) {
			if (isGetinFree())
				return true;
			rotateRight(1);
		}
		return false;
	}
	
	public Siteswap calculateGetin() {
		
		byte[] getIn = new byte[0];
		TreeSet<Integer> fallDownPositions = new TreeSet<Integer>();
		for (int i = 0; i < 3 * getNumberOfObjects(); ++i) {
			fallDownPositions.add(i + at(i));
		}
		
		int getInPos = 0;
		int value = 0;
		for (int i = 0; i < getNumberOfObjects(); ++i) {
			value = 0;
			if (fallDownPositions.contains(i)) {
				if (getIn.length == 0) {
					getIn = new byte[getNumberOfObjects() - i];
				}
				while(fallDownPositions.contains(i + value)) {
					value++;
				}
			}
			if (getIn.length != 0) {
				getIn[getInPos] = (byte) (getNumberOfObjects() + value);
				getInPos++;
				fallDownPositions.add(i + value);
			}
		}
		return new Siteswap(getIn, mNumberOfJugglers);
	}
	
	public Siteswap calculateGetout() {

		byte[] getOut = new byte[0];
		
		// pos zero is the first throw after end of siteswap
		TreeSet<Integer> fallDownPositions = new TreeSet<Integer>();
		for (int i = -1; i > -3 * getNumberOfObjects(); --i) {
			fallDownPositions.add(i + at(i));
		}
		
		int getOutPos = 0;
		int value = getNumberOfObjects();
		for (int i = getNumberOfObjects() - 1; i >= 0; --i) {
			value = getNumberOfObjects();
			if (fallDownPositions.contains(i + value)) {
				if (getOut.length == 0) {
					getOutPos = i;
					getOut = new byte[i + 1];
				}
				while(fallDownPositions.contains(i + value)) {
					value--;
				}
			}
			if (getOut.length != 0) {
				getOut[getOutPos] = (byte) (value);
				getOutPos--;
				fallDownPositions.add(i + value);
			}
		}
		return new Siteswap(getOut, mNumberOfJugglers);
	}
	
	public boolean isGetinFree() {
		return calculateGetin().period_length() == 0;
	}


	@Override
	public Iterator<Byte> iterator() {
		return mData.iterator();
	}

	@Override
	public int compareTo(Siteswap o) {
		int length = period_length() < o.period_length() ? period_length() : o.period_length();
		
		for(int i = 0; i < length; ++i) {
			if(at(i) < o.at(i))
				return -1;
			if(at(i) > o.at(i))
				return 1;
		}
		
		if(period_length() < o.period_length())
			return -1;
		if(period_length() > o.period_length())
			return 1;
		return 0;
	}

	public byte[] toArray() {
		byte[] arr = new byte[period_length()];
		for (int i = 0; i < period_length(); ++i) {
			arr[i] = mData.at(i);
		}
		return arr;
	}
	
	public String toLocalString() {
		return toLocalString(false);
	}
	
	public String toLocalString(boolean isHtml) {
		if (mNumberOfJugglers == 1)
			return toString();
		String str = new String();
		
		for(int juggler = 0; juggler < mNumberOfJugglers; ++juggler) {
			str += Character.toString((char) ('A' + juggler)) + ": ";
			DecimalFormat formatter = new DecimalFormat("0.#");
			for(int i = 0; i < period_length(); ++i) {
				int position = juggler + i*mNumberOfJugglers;
				str += formatter.format(mData.at(position) / (double) mNumberOfJugglers);
				if (Siteswap.isPass(mData.at(position), mNumberOfJugglers)) {
					if (isHtml)
						str += "<sub><small>";
					if (mNumberOfJugglers >= 3)
						str += Character.toString((char) ('A' + position % mNumberOfJugglers));
					if (((juggler + mData.at(position)) / mNumberOfJugglers) % 2 == 0)
						str += "x";
					else
						str += "s";
					if (isHtml)
						str += "</small></sub>";
				}
				str += " ";
			}
			str += "\n";
		}
		
		return str;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((Siteswap) obj) == 0;
	}

	@Override
	public String toString() {
		String str = new String();
		for (byte value : mData) {
			str += Character.toString(intToChar(value));
		}
		return str;
	}

	public static String intToString(int value) {
		if (value == SELF)
			return "self";
		if (value == PASS)
			return "pass";
		if (value == DONT_CARE)
			return "do not care";
		if (value == FREE)
			return "free";
		if (value < 0)
			return "invalid";

		if(value >= 0 && value < 10)
			return Integer.toString(value);

		return Character.toString((char) (value - 10 + 'a'));
	}

	public static char intToChar(int value) {

		if (value == SELF)
			return 's';
		if (value == PASS)
			return 'p';
		if (value == DONT_CARE)
			return '?';
		if (value == FREE)
			return '*';
		if (value < 0)
			return '!';

		if(value >= 0 && value < 10)
			return (char) (value + '0');

		return (char) (value - 10 + 'a');
	}

	public static int charToInt(char value) {
		if (value == 'p')
			return PASS;
		if (value == 's')
			return SELF;
		if (value == '?')
			return DONT_CARE;
		if (value == 'O')
			return FREE;
		if(value < '9' && value > '0')
			return (int) (value - '0');
		if(value < 'z' && value > 'a')
			return (int) (value + 10 - 'a');
		return INVALID;
	}

	static public boolean isPatternSingleValue(byte patternValue, byte siteswapValue, int numberOfJugglers) {
		if (siteswapValue >= 0) {
			if (patternValue == SELF)
				return siteswapValue % numberOfJugglers == 0;
			if (patternValue == PASS)
				return siteswapValue % numberOfJugglers != 0;
			if (patternValue == DONT_CARE)
				return true;
			if (patternValue == FREE)
				return false;
		}
		else {
			if (siteswapValue == DONT_CARE)
				return true;
			if (siteswapValue == FREE)
				return false;
		}

		return patternValue == siteswapValue;
	}

    public boolean isPatternSingleValue(byte patternValue, byte siteswapValue) {
        return isPatternSingleValue(patternValue, siteswapValue, mNumberOfJugglers);
    }

    public boolean isPattern(Siteswap pattern) {
        if (pattern.period_length() == 0)
            return true;
        for(int i = 0; i < period_length(); ++i) {
            if (isPattern(pattern, i))
                return true;
        }
        return false;
    }

    public boolean isPattern(Siteswap pattern, int patternStartPosition) {
        for (int i = 0; i < pattern.period_length(); ++i) {
            if (!isPatternSingleValue(pattern.at(i), at(i + patternStartPosition)))
                return false;
        }
        return true;
    }

	static public boolean isPass(int value, int numberOfJugglers) {
        return value % numberOfJugglers != 0;
	}

	static public boolean isSelf(int value, int numberOfJugglers) {
		return !isPass(value, numberOfJugglers);
	}

	private byte[] parseString(String str) {
		byte[] siteswap = new byte[str.length()];
		for (int i = 0; i < str.length(); ++i) {
			siteswap[i] = (byte) charToInt(str.charAt(i));
		}
		return siteswap;
	}
	
}
