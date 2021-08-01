package namlit.siteswapgenerator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

import siteswaplib.FilterList;
import siteswaplib.Siteswap;

@Entity(tableName = "generation_parameters")
public class GenerationParameterEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "numberOfObjects")
    private int numberOfObjects;

    @ColumnInfo(name = "periodLength")
    private int periodLength;

    @ColumnInfo(name = "maxThrow")
    private int maxThrow;

    @ColumnInfo(name = "minThrow")
    private int minThrow;

    @ColumnInfo(name = "numberOfJugglers")
    private int numberOfJugglers;

    @ColumnInfo(name = "maxResults")
    private int maxResults;

    @ColumnInfo(name = "timeout")
    private int timeout;

    @ColumnInfo(name = "isSynchronous")
    private boolean isSynchronous;

    @ColumnInfo(name = "isRandomMode")
    private boolean isRandomMode;

    @ColumnInfo(name = "isZips")
    private boolean isZips;

    @ColumnInfo(name = "isZaps")
    private boolean isZaps;

    @ColumnInfo(name = "isHolds")
    private boolean isHolds;

    @ColumnInfo(name = "filterListString")
    private String filterListString;

    public GenerationParameterEntity() {
        name = "";
        numberOfObjects = 7;
        periodLength = 5;
        maxThrow = 10;
        minThrow = 2;
        numberOfJugglers = 2;
        maxResults = 100;
        timeout = 5;
        isSynchronous = false;
        isRandomMode = false;
        isZips = false;
        isZaps = false;
        isHolds = false;
        setFilterList(new FilterList(numberOfJugglers, 1));
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    public void setNumberOfObjects(int numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }

    public int getMaxThrow() {
        return maxThrow;
    }

    public void setMaxThrow(int maxThrow) {
        this.maxThrow = maxThrow;
    }

    public int getMinThrow() {
        return minThrow;
    }

    public void setMinThrow(int minThrow) {
        this.minThrow = minThrow;
    }

    public int getNumberOfJugglers() {
        return numberOfJugglers;
    }

    public void setNumberOfJugglers(int numberOfJugglers) {
        this.numberOfJugglers = numberOfJugglers;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isSynchronous() {
        return isSynchronous;
    }

    public void setSynchronous(boolean is_synchronous) {
        this.isSynchronous = is_synchronous;
    }

    public boolean isRandomMode() {
        return isRandomMode;
    }

    public void setRandomMode(boolean is_random_mode) {
        this.isRandomMode = is_random_mode;
    }

    public boolean isZips() {
        return isZips;
    }

    public void setZips(boolean zips) {
        isZips = zips;
    }

    public boolean isZaps() {
        return isZaps;
    }

    public void setZaps(boolean zaps) {
        isZaps = zaps;
    }

    public boolean isHolds() {
        return isHolds;
    }

    public void setHolds(boolean holds) {
        isHolds = holds;
    }

    public void setFilterListString(String filterListString) {
        this.filterListString = filterListString;
    }

    public String getFilterListString() {
        return filterListString;
    }

    public void setFilterList(FilterList filter_list) {
        setFilterListString(filter_list.toParsableString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Siteswap toSiteswap() {
        Siteswap siteswap = new Siteswap(getName());
        return siteswap;
    }

    @Override
    public String toString() {
        return getName();
    }
}
