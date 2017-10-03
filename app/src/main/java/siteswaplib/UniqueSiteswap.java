package siteswaplib;

public class UniqueSiteswap extends Siteswap {

	public UniqueSiteswap(Siteswap s) {
		super(s);
		make_unique_representation();
	}
	
	public UniqueSiteswap(UniqueSiteswap s) {
		super(s);
	}
	
	public UniqueSiteswap(byte[] data, int numberOfJugglers) {
		super(data, numberOfJugglers);
		make_unique_representation();
	}
	
	public void swap(int index) {
		super.swap(index);
		make_unique_representation();
	}
	
	public void rotateRight(int positions) {
		make_unique_representation();
	}
	
	public void rotateLeft(int positions) {
		make_unique_representation();
	}
	
}
