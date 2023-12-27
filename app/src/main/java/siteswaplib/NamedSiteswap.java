package siteswaplib;

import androidx.annotation.NonNull;

public class NamedSiteswap extends Siteswap {

    public NamedSiteswap(String siteswap, int numJugglers, String name) {
        super(siteswap, numJugglers, name);
    }

    public NamedSiteswap(Siteswap siteswap) {
        super(siteswap);
    }

    @NonNull
    @Override
    public String toString() {

        String name = getSiteswapName() == "" ? "" : getSiteswapName() + ": ";
        return name + super.toString();
    }
}
