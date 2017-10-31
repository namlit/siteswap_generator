package siteswaplib;

import java.text.DecimalFormat;
import java.util.*;
import java.io.Serializable;

public class Siteswap implements Comparable<Siteswap>, Iterable<Byte>, Serializable {

	public static final byte SELF = -1;
	public static final byte PASS = -2;
	public static final byte DONT_CARE = -3;
	public static final byte FREE = -4;
	public static final byte INVALID = -5;

	protected CyclicByteArray mData;
	protected int mNumberOfJugglers = 1;

	public Siteswap() {
		this(new byte[0]);
	}

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

	public Siteswap(String siteswap) {
		this(siteswap, 1);
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

	public int getMaxThrow() {
		int max = 0;
		for(int i = 0; i < period_length(); ++i) {
			if (at(i) > max)
				max = at(i);
		}
		return max;
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
        for (int i = 0; i <= index; ++i) {
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

		Siteswap siteswapInterface = toInterface(Siteswap.FREE, period_length() + getMaxThrow());

		int getinLength = 0;

		// calculate getin length
		for (int i = 0; i < getNumberOfObjects(); ++i) {
			if (siteswapInterface.at(i) != FREE) {
				getinLength = getNumberOfObjects() - i;
				break;
			}
		}

		// create getin Siteswap
		byte[] getinArray = new byte[getinLength];
		Arrays.fill(getinArray, (byte) getNumberOfObjects());
		Siteswap getin = new Siteswap(getinArray, mNumberOfJugglers);

		// calculate getin values
		for(int i = 0; i < getin.period_length(); ++i) {
			int offset = i - getin.period_length();
			while(siteswapInterface.at(getin.at(i) + offset) != FREE)
				getin.set(i, getin.at(i) + 1);
			siteswapInterface.set(getin.at(i) + offset, getin.at(i));
		}
		
		return getin;
	}
	
	public Siteswap calculateGetout() {

		Siteswap siteswapInterface = toInterface(Siteswap.FREE, period_length() + getMaxThrow());

		int getoutLength = 0;

		// calculate getout length
		for (int i = 0; i < getMaxThrow() - getNumberOfObjects(); ++i) {
			if (siteswapInterface.at(period_length() + getMaxThrow() - i - 1) != FREE) {
				getoutLength = getMaxThrow() - getNumberOfObjects() - i;
				break;
			}
		}

		// create getout Siteswap
		byte[] getoutArray = new byte[getoutLength];
		Arrays.fill(getoutArray, (byte) getNumberOfObjects());
		Siteswap getout = new Siteswap(getoutArray, mNumberOfJugglers);

		// calculate getout values
		for (int i = getout.period_length() - 1; i >= 0; --i) {
			int offset = period_length() + i;
			while (siteswapInterface.at(getout.at(i) + offset) != FREE)
				getout.set(i, getout.at(i) - 1);
			siteswapInterface.set(getout.at(i) + offset, getout.at(i));
		}

		return getout;
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
        if (! (obj instanceof Siteswap))
            return false;
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

	public Siteswap toInterface(byte defaultValue) {

		return toInterface(defaultValue, period_length());
	}

	public Siteswap toInterface(byte defaultValue, int interfaceLength) {

		byte[] interfaceArray = new byte[interfaceLength];
		Arrays.fill(interfaceArray, defaultValue);
		Siteswap siteswapInterface = new Siteswap(interfaceArray, mNumberOfJugglers);
		for (int i = 0; i < period_length(); ++i) {
			if (at(i) < 0)
				continue;
			siteswapInterface.set(i + at(i), at(i));
		}
		return siteswapInterface;
	}

	public static boolean isValid(Siteswap siteswap) {

		byte[] interfaceArray = new byte[siteswap.period_length()];
		Arrays.fill(interfaceArray, Siteswap.FREE);
		Siteswap siteswapInterface = new Siteswap(interfaceArray);
		for (int i = 0; i < siteswap.period_length(); ++i) {
			if (siteswap.at(i) < 0)
				continue;
			if (siteswapInterface.at(i + siteswap.at(i)) != Siteswap.FREE)
				return false;
			siteswapInterface.set(i + siteswap.at(i), siteswap.at(i));
		}

		return true;
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

	public static int stringToInt(String value) {
		if (value == "pass")
			return PASS;
		else if (value == "self")
			return SELF;
		else if (value == "do not care")
			return DONT_CARE;
		else if (value == "free")
			return FREE;
		else if (value.length() == 1)
			return charToInt(value.charAt(0));
		return INVALID;
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
		if(value <= '9' && value >= '0')
			return (int) (value - '0');
		if(value <= 'z' && value >= 'a')
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
			// the Pattern should not have any free positions. Therefore false is returned in this case
			if (patternValue == FREE)
				return false;
		}
		else {
			if (siteswapValue == DONT_CARE)
				return true;
			// When a position in the siteswap is free, it is not known, if the pattern will be
			// matched. In this case false will be assumed
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
