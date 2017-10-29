package siteswaplib;

/**
 * Created by tilman on 29.10.17.
 */

public class LocalPatternFilter extends PatternFilter {

    private Siteswap mLocalPattern;

    public LocalPatternFilter(Siteswap pattern, Type type) {
        super(pattern, type);

        mLocalPattern = pattern;

        byte[] globalPattern = new byte[2 * pattern.period_length() - 1];

        for (int i = 0; i < 2 * pattern.period_length() - 1; ++i) {
            if (i % 2 == 0)
                globalPattern[i] = pattern.at(i / 2);
            else
                globalPattern[i] = Siteswap.DONT_CARE;
        }
        mPattern = new Siteswap(globalPattern);

    }

    @Override
    public String toString() {
        String str;

        if (mType == Type.INCLUDE)
            str = new String("Include Local: ");
        else
            str = new String("Exclude Local: ");
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
