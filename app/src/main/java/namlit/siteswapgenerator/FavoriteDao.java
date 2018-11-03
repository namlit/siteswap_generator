package namlit.siteswapgenerator;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY name")
    List<SiteswapEntity> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE siteswap IS :siteswap")
    List<SiteswapEntity> getSiteswaps(String siteswap);

    @Query("SELECT * FROM favorites WHERE siteswap IS :siteswap")
    SiteswapEntity getSiteswap(String siteswap);

    // TODO Why is the wrong column returned?
    @Query("SELECT DISTINCT juggler_names FROM favorites ORDER BY juggler_names")
    List<String> getJugglers();

    @Query("SELECT DISTINCT location FROM favorites ORDER BY location")
    List<String> getLocations();

    @Query("SELECT DISTINCT date FROM favorites ORDER BY date")
    List<String> getDates();

    @Query("SELECT * FROM favorites WHERE juggler_names IS :juggler")
    List<SiteswapEntity> getSiteswapsOfJuggler(String juggler);

    @Query("SELECT * FROM favorites WHERE location IS :location")
    List<SiteswapEntity> getSiteswapsOfLocation(String location);

    @Query("SELECT * FROM favorites WHERE date IS :date")
    List<SiteswapEntity> getSiteswapsOfDate(String date);

    @Insert
    void insertFavorites(SiteswapEntity... siteswap);

    @Delete
    void deleteFavorite(SiteswapEntity siteswap);
}
