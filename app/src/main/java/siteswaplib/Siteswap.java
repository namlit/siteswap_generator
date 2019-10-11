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

import java.text.DecimalFormat;
import java.util.*;
import java.io.Serializable;

public class Siteswap implements Comparable<Siteswap>, Iterable<Byte>, Serializable {

	public static final byte SELF = -1;
	public static final byte PASS = -2;
	public static final byte DONT_CARE = -3;
	public static final byte FREE = -4;
	public static final byte INVALID = -5;

	private CyclicByteArray mData;
	private int mNumberOfJugglers = 1;

	private String mSiteswapName = "";
	private String mInvalidCharacters = "";
	private boolean mIsParsingError = false;
	private int mNumberOfSynchronousHands = 1;
    // when there are n throws at the same time, each siteswap number can be at position
	// 0, ... n-1 within the synchronous throws. The position of siteswap.at(0) is coded
	// in mSynchronousStartPosition and is adapted on every rotation of the siteswap.
	private int mSynchronousStartPosition = 0;

	public Siteswap() {
		this(new byte[0]);
	}

	public Siteswap(Siteswap s) {
		this.mData = new CyclicByteArray(s.mData);
		setNumberOfJugglers(s.getNumberOfJugglers());
		setSiteswapName(s.getSiteswapName());
		setNumberOfSynchronousHands(s.getNumberOfSynchronousHands());
		setSynchronousStartPosition(s.getSynchronousStartPosition());
	}

	public Siteswap(byte[] data, int numberOfJugglers) {
		this.mData = new CyclicByteArray(data);
		setNumberOfJugglers(numberOfJugglers);
	}

	public Siteswap(String siteswap, int numberOfJugglers, String name) {
	    this(siteswap, numberOfJugglers);
		setSiteswapName(name);
	}

	public Siteswap(String siteswap, int numberOfJugglers) {
		setNumberOfJugglers(numberOfJugglers);
		fromParsableString(siteswap);
	}

	public Siteswap(String siteswap) {
		this(siteswap, 2);
	}

	public Siteswap(byte[] data) {
		this(data, 1);
	}

	public byte at(int index) {
		return mData.at(index);
	}

	public byte atSyncCorrected(int index) {
		return (byte) (getNumberOfSynchronousHands() *
				((getSynchronousPosition(index) + at(index)) / getNumberOfSynchronousHands()));
	}

	public void set(int index, int value) {
		mData.modify(index, (byte) value);
	}

	public int period_length() {
		return mData.length();
	}

	public int local_period_length() {
		if (period_length() % getNumberOfJugglers() == 0)
			return period_length() / getNumberOfJugglers();
		return period_length();
	}

	// TODO rename
    public int global_period_length() {
        int length = period_length();
        if (period_length() % getNumberOfSynchronousHands() != 0) {
            while (length % getNumberOfSynchronousHands() != 0) {
                length += period_length();
            }
        }
		return length;
	}

	public int getNonMirroredPeriod() {
		int length = period_length();
		int number_of_hands = getNumberOfHands();
		while (length % number_of_hands != 0) {
			length += period_length();
		}
		return length;
	}

	public int getNumberOfHands() {
		return 2 * getNumberOfJugglers();
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
		if (period_length() == 0)
			return 0;
		return getPartialSum(0, period_length()-1) / period_length();
	}

	public int getNumberOfJugglers() {
		return mNumberOfJugglers;
	}

	private void setNumberOfJugglers(int numberOfJugglers) {
		this.mNumberOfJugglers = numberOfJugglers;
		if (numberOfJugglers < 1)
			this.mNumberOfJugglers = 1;
	}

	public boolean setNumberOfSynchronousHands(int numberOfSynchronousHands) {
		if (getNumberOfHands() % numberOfSynchronousHands != 0)
			return false;
		mNumberOfSynchronousHands = numberOfSynchronousHands;
		return true;
	}

	public int getNumberOfSynchronousHands() {
		return mNumberOfSynchronousHands;
	}

	public void setSynchronousStartPosition(int synchronousStartPosition) {
		mSynchronousStartPosition = synchronousStartPosition % getNumberOfSynchronousHands();
	}

	public int getSynchronousStartPosition() {
		return mSynchronousStartPosition;
	}

	public String getSiteswapName() {
		return mSiteswapName;
	}

	public void setSiteswapName(String name) {
		mSiteswapName = name;
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

	public boolean isSynchronous() {
		return mNumberOfSynchronousHands != 1;
	}

	public int getSynchronousPosition(int position) {
		return (getSynchronousStartPosition() + position) % getNumberOfSynchronousHands();
	}

	public int countFilterValue(NumberFilter.FilterValue siteswapValue) {
		int counter = 0;
        for (int i = 0; i < global_period_length(); ++i) {
			for (int value : siteswapValue.getValues(getSynchronousPosition(i))) {
				if (isPatternSingleValue((byte) value, at(i)))
					counter++;
			}
		}
		return counter;
	}

    public int countFilterValuePartitially(NumberFilter.FilterValue siteswapValue, int index) {
		int counter = 0;
		for (int i = 0; i <= index; ++i) {
			for (int value : siteswapValue.getValues(getSynchronousPosition(i))) {
				if (isPatternSingleValue((byte) value, mData.at(i)))
					counter++;
			}
		}
		return counter;
	}

	public void rotateRight(int positions) {
		mData.rotateRight(positions);
		mSynchronousStartPosition = (mSynchronousStartPosition - positions) % getNumberOfSynchronousHands();
		if (mSynchronousStartPosition < 0)
			mSynchronousStartPosition += getNumberOfSynchronousHands();
	}

	public void rotateLeft(int positions) {
		mData.rotateLeft(positions);
		mSynchronousStartPosition = (mSynchronousStartPosition + positions) % getNumberOfSynchronousHands();
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

		Siteswap siteswapInterface = toInterface(Siteswap.FREE, period_length() + getMaxThrow(),
				period_length() + getMaxThrow());

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

		Siteswap siteswapInterface = toInterface(Siteswap.FREE, period_length() + getMaxThrow(), period_length());

		int getoutLength = 0;

		// calculate getout length
		for (int i = 0; i < getMaxThrow() - getNumberOfObjects(); ++i) {
			int interfaceIndex = period_length() + getMaxThrow() - i - 1;
			if (siteswapInterface.at(interfaceIndex) != FREE) {
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

	public int calculateMandatoreyGetinLength() {
		Siteswap getin = calculateGetin();
		Siteswap siteswapInterface = toInterface(Siteswap.FREE, period_length() + getMaxThrow(),
				period_length() + getMaxThrow());

		int mandatoryGetinLength = 0;

		for (int i = 0; i < getin.period_length(); ++i) {
			int fallDownPosition = getin.at(i) + i - getin.period_length();
			int sameHandPreviousPosition = fallDownPosition - 2 * getNumberOfJugglers();

			// if Siteswap is Mandatory
			if( sameHandPreviousPosition >= 0 && siteswapInterface.at(sameHandPreviousPosition) != Siteswap.FREE ) {
				mandatoryGetinLength = getin.period_length() - i;
				break;
			}

			if(fallDownPosition >= 0) {
				siteswapInterface.set(fallDownPosition, getin.at(i));
			}

		}
		return mandatoryGetinLength;
	}

	public Siteswap calculateMandatoryGetin() {

		Siteswap getin = calculateGetin();
		int mandatoryGetinLength = calculateMandatoreyGetinLength();

		Siteswap mandatorySiteswap = new Siteswap(new byte[mandatoryGetinLength], mNumberOfJugglers);

		for(int i = 0; i < mandatorySiteswap.period_length(); ++i) {
			int getinPos = i + getin.period_length() - mandatorySiteswap.period_length();
			mandatorySiteswap.set(i, getin.at(getinPos));
		}

		return mandatorySiteswap;
	}

	public Siteswap calculateNonMandatoryGetins() {

		Siteswap getin = calculateGetin();
		int mandatoryGetinLength = calculateMandatoreyGetinLength();
		int nonMandatoryGetinLength = getin.period_length() - mandatoryGetinLength;

		Siteswap nonMandatorySiteswap = new Siteswap(new byte[nonMandatoryGetinLength], mNumberOfJugglers);

		for (int i = 0; i < nonMandatoryGetinLength; ++i) {
			nonMandatorySiteswap.set(i, getin.at(i));
		}

		return nonMandatorySiteswap;
	}

	public Siteswap[] calculateLocalGetins() {

		Siteswap[] localGetins = new Siteswap[getNumberOfJugglers()];
		Siteswap mandatoryGetin = calculateMandatoryGetin();

		// initialize getin Siteswaps
		for(int juggler = 0; juggler < getNumberOfJugglers(); ++juggler) {
			int localGetinLength = mandatoryGetin.period_length() / getNumberOfJugglers();
			if (mandatoryGetin.period_length() % getNumberOfJugglers() >= (getNumberOfJugglers() - juggler))
				localGetinLength++;

			localGetins[juggler] = new Siteswap(new byte[localGetinLength], getNumberOfJugglers());
		}

		int localGetinIndex = 0;
		for (int i = 0; i < mandatoryGetin.period_length(); ++i) {
			int juggler = (i + getNumberOfJugglers() -
					mandatoryGetin.period_length() % getNumberOfJugglers()) % getNumberOfJugglers();

			localGetins[juggler].set(localGetinIndex, mandatoryGetin.at(i));

			if ((i + 1) % getNumberOfJugglers() == 0)
				localGetinIndex++;
		}

		return localGetins;
	}


	public Siteswap[] calculateLocalGetouts() {

		Siteswap[] localGetouts = new Siteswap[getNumberOfJugglers()];
		Siteswap globalGetout = calculateGetout();

		// Calculate getout lenght for each juggler and allocate getout Siteswaps
		for(int i = 0; i < localGetouts.length; ++i) {
			int startPos = i - period_length() % getNumberOfJugglers();
			if (i < period_length() % getNumberOfJugglers())
				startPos += getNumberOfJugglers();
			int length = (globalGetout.period_length() - startPos - 1) / getNumberOfJugglers() + 1;
			if (globalGetout.period_length() - startPos <= 0)
				length = 0;
			localGetouts[i] = new Siteswap(new byte[length], mNumberOfJugglers);
		}

		for (int i = 0; i < globalGetout.period_length(); ++i) {
			int juggler = (period_length() % getNumberOfJugglers() + i) % getNumberOfJugglers();
			localGetouts[juggler].set(i / getNumberOfJugglers(), globalGetout.at(i));
		}

		return localGetouts;
	}

	public class ClubDistribution {
		public int leftHandNumberOfClubs;
		public int rightHandNumberOfClubs;

		public ClubDistribution(int left, int right) {
			leftHandNumberOfClubs = left;
			rightHandNumberOfClubs = right;
		}

		@Override
		public String toString() {
			return String.valueOf(leftHandNumberOfClubs) + "|" +
					String.valueOf(rightHandNumberOfClubs);
		}

		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof ClubDistribution))
				return false;
			ClubDistribution rhs = (ClubDistribution) obj;
			return this.leftHandNumberOfClubs == rhs.leftHandNumberOfClubs &&
					this.rightHandNumberOfClubs == rhs.rightHandNumberOfClubs;
		}
	}

	// Calculates the club Distribution for a pattern started from ground state
	public ClubDistribution[] calculateGroundStateClubDistribution() {
		ClubDistribution groundStateClubDistribution[] = new ClubDistribution[getNumberOfJugglers()];

		for (int juggler = 0; juggler < getNumberOfJugglers(); ++juggler) {
			int numberOfClubsForJuggler = getNumberOfObjects() / getNumberOfJugglers();
			if (juggler < getNumberOfObjects() % getNumberOfJugglers())
				numberOfClubsForJuggler++;
			int numberOfClubsRightHand = numberOfClubsForJuggler / 2 + numberOfClubsForJuggler % 2;
			int numberOfClubsLeftHand = numberOfClubsForJuggler / 2;

			groundStateClubDistribution[juggler] =
					new ClubDistribution(numberOfClubsLeftHand, numberOfClubsRightHand);
		}
		return groundStateClubDistribution;
	}

	public ClubDistribution[] calculateInitialClubDistribution() {
		ClubDistribution initialClubDistribution[] = new ClubDistribution[getNumberOfJugglers()];
		Siteswap getin = calculateGetin();
		Siteswap localGetins[] = calculateLocalGetins();
		Siteswap nonMandatoryGetin = calculateNonMandatoryGetins();

		// Calculate initial start position including all getins. It is assumed,
		// that the regular periodig Siteswap is started from the right Hand
		// for all jugglers.
		for(int juggler = 0; juggler < getNumberOfJugglers(); ++juggler) {
			int numberOfClubsForJuggler = getNumberOfObjects() / getNumberOfJugglers();
			int jugglerPosition = (getin.period_length() % getNumberOfJugglers() + juggler) %
					getNumberOfJugglers();
			if( jugglerPosition < getNumberOfObjects() % getNumberOfJugglers())
				numberOfClubsForJuggler++;
			int numberOfClubsStartingHand = numberOfClubsForJuggler / 2 + numberOfClubsForJuggler % 2;
			int numberOfClubsSecondHand = numberOfClubsForJuggler / 2;
			int numberOfGetinsForJuggler = getin.period_length() / getNumberOfJugglers();
			if (getin.period_length() % getNumberOfJugglers() >= (getNumberOfJugglers() - juggler))
				numberOfGetinsForJuggler++;
			boolean isStartRight = (numberOfGetinsForJuggler % 2) == 0;
			int right = numberOfClubsStartingHand;
			int left = numberOfClubsSecondHand;
			if (!isStartRight) {
				right = numberOfClubsSecondHand;
				left = numberOfClubsStartingHand;
			}
			initialClubDistribution[juggler] = new ClubDistribution(left, right);
		}

		// Calculate new starting position, when only mandatory getins are thrown
		for (int i = 0; i < nonMandatoryGetin.period_length(); ++i) {
			int throwingJuggler = (i + getNumberOfJugglers() -
					getin.period_length() % getNumberOfJugglers()) % getNumberOfJugglers();
			int numberOfThrows = (getin.period_length() - i) / getNumberOfJugglers();
			if (throwingJuggler >= getNumberOfJugglers() - (getin.period_length()-i) % getNumberOfJugglers())
				numberOfThrows++;
			boolean isRightHandThrowing = (numberOfThrows % 2) == 0;
			int catchingJuggler = (throwingJuggler + nonMandatoryGetin.at(i)) % getNumberOfJugglers();
			boolean isRightHandCatching = ((throwingJuggler + nonMandatoryGetin.at(i)) /
					getNumberOfJugglers()) % 2 == 0;
			if (!isRightHandThrowing)
				isRightHandCatching = !isRightHandCatching;

			if (isRightHandThrowing)
				initialClubDistribution[throwingJuggler].rightHandNumberOfClubs--;
			else
				initialClubDistribution[throwingJuggler].leftHandNumberOfClubs--;

			if (isRightHandCatching)
				initialClubDistribution[catchingJuggler].rightHandNumberOfClubs++;
			else
				initialClubDistribution[catchingJuggler].leftHandNumberOfClubs++;
		}

		return initialClubDistribution;
	}

	public boolean isMandatoryGetin() {
		return calculateMandatoryGetin().period_length() != 0;
	}

	public boolean isGroundStateClubDistribution() {
		ClubDistribution groundstateClubDistribution[] = calculateGroundStateClubDistribution();
		ClubDistribution actualClubDistribution[] = calculateInitialClubDistribution();

		for (int i = 0; i < actualClubDistribution.length; ++i) {
			if (!groundstateClubDistribution[i].equals(actualClubDistribution[i]))
				return false;
		}
		return true;
	}

	// The higher the returned integer value, the better the current rotation can be
	// used as a starting position.
	public int measureSuitabilityForStartingPosition() {
		int measure = 0;

		if (!isMandatoryGetin())
			measure += 1000;
		if (isGroundStateClubDistribution())
			measure += 10;
		if (isGetinFree())
			measure += 1;
		if (Siteswap.isPass(at(0), getNumberOfJugglers()))
			measure += 100;
		for(int i = 1; i < getNumberOfJugglers(); ++i) {
			if (Siteswap.isPass(at(i), getNumberOfJugglers()))
				measure += 2;
		}
		return measure;
	}

	public void rotateToBestStartingPosition() {
		int max = 0;
		int rot = 0;
		for (int i = 0; i < period_length(); ++i) {
			if (measureSuitabilityForStartingPosition() > max) {
				max = measureSuitabilityForStartingPosition();
				rot = i;
			}
			rotateRight(1);
		}
		rotateRight(rot);
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

	public Vector<Siteswap> toLocal() {

		Vector<Siteswap> localSiteswaps = new Vector<Siteswap>(mNumberOfJugglers);
		for (int i = 0; i < mNumberOfJugglers; ++i) {
			localSiteswaps.add(new Siteswap(new byte[period_length()]));
			for(int j = 0; j < period_length(); ++j) {
				localSiteswaps.elementAt(i).set(j, at(mNumberOfJugglers * j + i));
			}
		}
		return localSiteswaps;
	}

	/**
	 * Returns the Siteswap as a string, where the individual numbers are
	 * divided by the number of jugglers (local notation). The order of the
	 * number is left as is.
	 * @return String representation of the siteswap
	 */
	public String toDividedString() {

		if (mNumberOfJugglers == 1)
			return toString();

		String str = new String();
		DecimalFormat formatter = new DecimalFormat("0.#");
		for(int i = 0; i < period_length(); ++i) {
			str += formatter.format(at(i) / (double) mNumberOfJugglers);
			str += "&ensp;";
		}
		return str;
	}

	public Vector<String> toLocalString() {

		Vector<String> localSiteswapStrings = new Vector<String>(mNumberOfJugglers);
		if (mNumberOfJugglers == 1) {
			localSiteswapStrings.add(toString());
			return localSiteswapStrings;
		}

		for(int juggler = 0; juggler < mNumberOfJugglers; ++juggler) {
			String str = new String();
			DecimalFormat formatter = new DecimalFormat("0.#");
			for(int i = 0; i < local_period_length(); ++i) {
				int position = juggler + i*mNumberOfJugglers;
				str += formatter.format(atSyncCorrected(position) / (double) mNumberOfJugglers);
				if (Siteswap.isPass(at(position), mNumberOfJugglers)) {
					str += "<sub><small>";
					if (mNumberOfJugglers >= 3)
						str += Character.toString((char) ('A' + (position + at(position)) % mNumberOfJugglers));
					if (((juggler + at(position)) / mNumberOfJugglers) % 2 == 0)
						str += "x";
					else
						str += "||";
					str += "</small></sub>";
				}
				str += "&ensp;";
			}
			localSiteswapStrings.add(str);
		}

		return localSiteswapStrings;

	}

	@Override
	public boolean equals(Object obj) {
        if (! (obj instanceof Siteswap))
            return false;
        if (getNumberOfSynchronousHands() != ((Siteswap) obj).getNumberOfSynchronousHands())
            return false;
        // TODO synchronous start position: attention: use same rotation for comparison
		return compareTo((Siteswap) obj) == 0;
	}

	@Override
	public String toString() {
		if (isSynchronous()) {
			return toSyncStringLocalVsLocal();
		}
		else {
			return toAsyncString();
		}
	}

    public String toGlobalString() {
        if (isSynchronous()) {
            return toSyncStringGlobalRepresentation();
        }
        else {
            return toAsyncString();
        }
    }

	public String toAsyncString() {
		String str = new String();
		for (byte value : mData) {
			str += Character.toString(intToChar(value));
		}
		return str;
	}

	public String toSyncStringLocalVsLocal() {
		String str = new String();
		for (int juggler = 0; juggler < getNumberOfJugglers(); ++juggler) {
			str += Character.toString((char) ('A' + juggler)) + ": ";
			for (int i = 0; i < local_period_length(); ++i) {
				int index = i*getNumberOfJugglers() + juggler;
				int value = atSyncCorrected(index) / getNumberOfJugglers();
				str += Character.toString(intToChar(value));
				if (isPass(at(index), getNumberOfJugglers())) {
					str += "p";
				}
			}
			str += "  ";
		}
		return str;
	}

	public String toSyncStringGlobalRepresentation() {
		String str = new String();
		for (int i = 0; i < global_period_length(); ++i) {
			byte value = atSyncCorrected(i);
			if (getSynchronousPosition(i) == 0)
				str += "(";
			str += Character.toString(intToChar(value));
			if (isPass(at(i), getNumberOfJugglers()))
				str += "p";
			if (getSynchronousPosition(i) == (getNumberOfSynchronousHands() - 1))
				str += ")";
		}
		return str;
	}

	static public String getCurrentStringVersion() {
		return new String("v1.0.0");
	}

	// /v1.0.0/86277.[nr_jugglers].[nr_sync_hands].[sync_start_pos]"
	// async two jugglers: 86277.2.1.0
	public boolean fromParsableString(String str) {
		String[] version_splitted_arr = str.split("/");

		if (version_splitted_arr.length == 0) {
			return false;
		}
		if (version_splitted_arr.length == 1) {
			return parseStringLatestVersion(version_splitted_arr[0]);
		}
		if (version_splitted_arr.length == 2) {
			return parseStringLatestVersion(version_splitted_arr[1]);
		}
		return false; // length > 2
	}

	public boolean parseVersionedString(String version, String siteswapStr) {
		if (version.equals("v1.0.0")) {
			return parseStringVersion_1_0_0(siteswapStr);
		}
		return false; // invalid version
	}

	private boolean parseStringLatestVersion(String str) {
		return parseStringVersion_1_0_0(str);
	}

	private boolean parseStringVersion_1_0_0(String str) {
		String[] arr = str.split("\\.");
		int i = 0;
		try {
			if (arr.length >= 1) {
				this.mData = new CyclicByteArray(parseString(arr[0]));
			}
			if (arr.length >= 2) {
				i = 1;
				setNumberOfJugglers(Integer.valueOf(arr[1]));
			}
			if (arr.length >= 3) {
				i = 2;
				setNumberOfSynchronousHands(Integer.valueOf(arr[2]));
			}
			if (arr.length >= 4) {
				i = 3;
				setSynchronousStartPosition(Integer.valueOf(arr[3]));
			}
		}
        catch (NumberFormatException e) {
			mIsParsingError = true;
			mInvalidCharacters += arr[i];
			return false;
		}
		return true;
	}

	public String toParsableString() {
		return toAsyncString() + "." +
				String.valueOf(getNumberOfJugglers()) + "." +
				String.valueOf(getNumberOfSynchronousHands()) + "." +
				String .valueOf(getSynchronousStartPosition());
	}

	public Siteswap toPattern() {
		Siteswap pattern = new Siteswap(this);
		for (int i = 0; i < period_length(); ++i) {
			if(isPass(at(i), mNumberOfJugglers))
				pattern.set(i, PASS);
			if(isSelf(at(i), mNumberOfJugglers))
				pattern.set(i, SELF);
		}
		return pattern;
	}

	public Siteswap toInterface() {

		return toInterface(Siteswap.FREE);
	}

	public Siteswap toInterface(byte defaultValue) {

		byte[] interfaceArray = new byte[period_length()];
		Arrays.fill(interfaceArray, defaultValue);
		Siteswap siteswapInterface = new Siteswap(interfaceArray, mNumberOfJugglers);
		for (int i = 0; i < period_length(); ++i) {
			if (at(i) < 0)
				continue;
			siteswapInterface.set(i + at(i), at(i));
		}
		return siteswapInterface;
	}

	/**
	 * converts the siteswap to an interface of a specific length. The interface will not be
	 * filled cyclic and throws coming down after the specified length will be ignored. The
	 * length parameters specifies, how many throws of the siteswaps are considered for the
	 * interface. Typically length is the interfaceLength in the context of getin calculation
	 * and the period length of the siteswap in the context of getout calculation.
	 *
	 *
	 * @param defaultValue
	 * @param interfaceLength
	 * @param length
	 * @return
	 */
	public Siteswap toInterface(byte defaultValue, int interfaceLength, int length) {

		byte[] interfaceArray = new byte[interfaceLength];
		Arrays.fill(interfaceArray, defaultValue);
		Siteswap siteswapInterface = new Siteswap(interfaceArray, mNumberOfJugglers);
		for (int i = 0; i < length; ++i) {
			if (at(i) < 0 || i + at(i) >= interfaceLength)
				continue;
			siteswapInterface.set(i + at(i), at(i));
		}
		return siteswapInterface;
	}

	public boolean isValid() {
		return isValid(this);
	}

	public static Siteswap mergeCompatible(Siteswap s1, Siteswap s2) {
		Siteswap i1 = s1.toInterface().toPattern();
		Siteswap i2 = s2.toInterface().toPattern();
		if (!s2.rotateToInterface(i1)) {
			return null;
		}
		int length = 2 * s1.period_length();
		Siteswap mergedSiteswap = new Siteswap(new byte[length]);
		mergedSiteswap.setSynchronousStartPosition(s1.getSynchronousStartPosition());
		mergedSiteswap.setNumberOfSynchronousHands(s1.getNumberOfSynchronousHands());
		mergedSiteswap.setNumberOfJugglers(s1.getNumberOfJugglers());
		for (int i = 0; i < s1.period_length(); ++i) {
			mergedSiteswap.set(2*i, s1.at(2*i));
			mergedSiteswap.set(2*i+1, s2.at(2*i+1));
		}
		if (!mergedSiteswap.isValid()) {
			return null;
		}
		return mergedSiteswap;
	}

	public static boolean isValid(Siteswap siteswap) {

		byte[] interfaceArray = new byte[siteswap.period_length()];
		Arrays.fill(interfaceArray, Siteswap.FREE);
		Siteswap siteswapInterface = new Siteswap(interfaceArray);
		for (int i = 0; i < siteswap.period_length(); ++i) {
			if (siteswap.at(i) < 0)
				return false;
			if (siteswapInterface.at(i + siteswap.at(i)) != Siteswap.FREE)
				return false;
			siteswapInterface.set(i + siteswap.at(i), siteswap.at(i));
		}

		return true;
	}

	public String stringAt(int index) {
		return Character.toString(intToChar(atSyncCorrected(index)));
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

	public boolean isPatternSingleValue(byte patternValue, byte siteswapValue) {
		if (siteswapValue >= 0) {
			if (patternValue == SELF)
				return siteswapValue % getNumberOfJugglers() == 0;
			if (patternValue == PASS)
				return siteswapValue % getNumberOfJugglers() != 0;
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

    public boolean rotateToInterface(Siteswap siteswapInterface) {
		if (siteswapInterface.period_length() != period_length()) {
			return false;
		}
		for(int i = 0; i < period_length(); ++i) {
		    rotateLeft(1);
			if (toInterface().isPattern(siteswapInterface, 0))
				return true;
		}
		return false;

	}

    public boolean isParsingError() {
		return mIsParsingError;
	}

    public String getInvalidCharactersFromParsing() {
		return mInvalidCharacters;
	}

	static public boolean isPass(int value, int numberOfJugglers) {
        return value % numberOfJugglers != 0;
	}

	static public boolean isSelf(int value, int numberOfJugglers) {
		return !isPass(value, numberOfJugglers);
	}

	private byte[] parseString(String str) {
		byte[] siteswap = new byte[str.length()];
		mInvalidCharacters = "";
		mIsParsingError = false;
		for (int i = 0; i < str.length(); ++i) {
			siteswap[i] = (byte) charToInt(str.charAt(i));
			if (siteswap[i] == INVALID) {
				mIsParsingError = true;
				mInvalidCharacters += str.charAt(i);
			}
		}
		return siteswap;
	}

}
