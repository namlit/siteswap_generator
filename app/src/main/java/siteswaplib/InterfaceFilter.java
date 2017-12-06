package siteswaplib;

/**
 * Created by tilman on 29.10.17.
 */

public class InterfaceFilter extends PatternFilter {

    public InterfaceFilter(Siteswap pattern, Type type) {
        super(pattern, type);
    }

    @Override
    public String toString() {
        String str;

        if (mType == Type.INCLUDE)
            str = new String("Include Interface: ");
        else
            str = new String("Exclude Interface: ");
        str += mPattern.toString();
        return str;
    }

    @Override
    public boolean isFulfilled(Siteswap siteswap) {

        Siteswap siteswapInterface = siteswap.toInterface(Siteswap.FREE);

        if (mType == Type.INCLUDE)
            return siteswapInterface.isPattern(mPattern);
        return !siteswapInterface.isPattern(mPattern);
    }


    @Override
    public boolean isPartlyFulfilled(Siteswap siteswap, int index) {

        if (mType == Type.EXCLUDE) {
            return isFulfilled(siteswap);
        }

        // Intefaces that need to be included are not checked for partly generated siteswaps
        // at the moment
        return true;
    }
}
