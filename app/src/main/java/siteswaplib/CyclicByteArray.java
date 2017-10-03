package siteswaplib;

import java.util.*;
import java.io.Serializable;

public class CyclicByteArray implements Iterable<Byte>, Iterator<Byte>, Serializable{
	private int first_element_index = 0;
	private int iterator_count = 0;
	private byte[] data;

	public CyclicByteArray(byte[] data) {
		this.data = new byte[data.length];
		for(int i = 0; i < data.length; ++i)
			this.data[i] = data[i];
	}

	public CyclicByteArray(CyclicByteArray array) {
		this(array.data);
		this.first_element_index = array.first_element_index;
	}
	
	public byte at(int index) {
		return data[((first_element_index + index) % length() + length()) % length()];
	}
	
	public void modify(int index, byte value) {
		data[((first_element_index + index) % length() + length()) % length()] = value;
	}
	
	public int length() {
		return data.length;
	}
	
	public void rotateRight(int positions) {
		first_element_index = (first_element_index - positions) % length();
		if (first_element_index < 0)
			first_element_index += length();
	}
	
	public void rotateLeft(int positions) {
		first_element_index = (first_element_index + positions) % length();
		if (first_element_index < 0)
			first_element_index += length();
	}

	@Override
	public Iterator<Byte> iterator() {
		iterator_count = 0;
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return iterator_count != length();
	}

	@Override
	public Byte next() {
		if (iterator_count >= length())
			throw new NoSuchElementException();
		
		return at(iterator_count++);
	}

	@Override
	public void remove() {
		// not implemented
	}


}
