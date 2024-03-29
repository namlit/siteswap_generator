package namlit.siteswapgenerator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

import siteswaplib.Siteswap;

@Entity(tableName = "favorites")
public class SiteswapEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "siteswap")
    private String siteswap;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "juggler_names")
    private String juggerNames;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "date")
    private String date;

    public SiteswapEntity() {

    }

    public SiteswapEntity(Siteswap siteswap, String name, String juggerNames,
                          String location, String date) {
        fromSiteswap(siteswap);
        setName(name);
        setJuggerNames(juggerNames);
        setLocation(location);
        setDate(date);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSiteswap() {
        return siteswap;
    }

    public void setSiteswap(String siteswap) {
        this.siteswap = siteswap;
    }

    public String getName() {
        return name;
    }

    public void setName(String favoriteName) {
        this.name = favoriteName;
    }

    public String getJuggerNames() {
        return juggerNames;
    }

    public void setJuggerNames(String juggerNames) {
        this.juggerNames = juggerNames;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Siteswap toSiteswap() {
        Siteswap siteswap = new Siteswap(getSiteswap());
        return siteswap;
    }

    public void fromSiteswap(Siteswap siteswap) {
        setSiteswap(siteswap.toParsableString());
        setName(siteswap.getSiteswapName());
    }

    @Override
    public String toString() {
        return getName() + ", " +
                getJuggerNames() + ", " +
                getLocation() + ", " +
                getDate();
    }

}
