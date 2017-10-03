package siteswaplib;

import java.util.*;
import java.io.Serializable;

import siteswaplib.Siteswap;
import siteswaplib.UniqueSiteswap;

public class Pattern implements Serializable{

	public static final int SELF = -1;
	public static final int PASS = -2;
	public static final int DONT_CARE = -3;
	
	private int [] pattern;
	private int number_of_jugglers;
	
	public Pattern(String str, int number_of_jugglers) {
		pattern = new int[str.length()];
		
		for(int i = 0; i < str.length(); ++i) {
			pattern[i] = charToPattern(str.charAt(i));
		}
		
		this.number_of_jugglers = number_of_jugglers;
	}
	
	static int charToPattern(char value) {
		if (value == 's')
			return SELF;
		if (value == 'p')
			return PASS;
		if (value >= '0' && value <= '9')
			return value - '0';
		if (value >= 'a' && value <= 'z')
			return 10 + value - 'a';
		return DONT_CARE;
	}

	public boolean testPattern(Siteswap siteswap) {
		if (pattern.length == 0)
			return true;
		for(int start_pos = 0; start_pos < siteswap.period_length(); ++start_pos) {
			for(int i = 0; i < pattern.length; ++i) {
				if (!testPatternValue(pattern[i], siteswap.at(start_pos + i)))
					break;
				if (i == pattern.length - 1)
					return true;
			}
		}
		return false;
	}
	
	private boolean testPatternValue(int patternValue, byte siteswapValue) {
		if (patternValue == SELF)
			return siteswapValue % number_of_jugglers == 0;
		if (patternValue == PASS)
			return siteswapValue % number_of_jugglers != 0;
		if (patternValue == DONT_CARE)
			return true;
		return patternValue == siteswapValue;
	}
	
	static boolean testPatternValue(char patternValue, int siteswapValue, int numberOfJugglers) {
		int value = charToPattern(patternValue);
		
		if (value == SELF)
			return siteswapValue % numberOfJugglers == 0;
		if (value == PASS)
			return siteswapValue % numberOfJugglers != 0;
		if (value == DONT_CARE)
			return true;
		return value == siteswapValue;
	}
	
	static boolean isPass(int value, int numberOfJugglers) {
		return testPatternValue('p', value, numberOfJugglers);
	}
	
	static boolean isSelf(int value, int numberOfJugglers) {
		return !isPass(value, numberOfJugglers);
	}
	
	@Override
	public String toString() {
		String str = new String();
		for (int i : pattern) {
			if(i == SELF)
				str += 's';
			else if(i == PASS)
				str += 'p';
			else if(i == DONT_CARE)
				str += '?';
			else if(i < 10)
				str += String.valueOf(i);
			else
				str += Character.toString((char) (i - 10 + 'a'));
		}

		return str;
	}
}
