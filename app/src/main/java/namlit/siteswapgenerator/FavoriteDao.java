package namlit.siteswapgenerator;

import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY name")
    List<SiteswapEntity> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE siteswap IS :siteswap")
    List<SiteswapEntity> getSiteswaps(String siteswap);

    @Query("SELECT * FROM favorites WHERE siteswap IS :siteswap")
    SiteswapEntity getSiteswap(String siteswap);

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

    @RawQuery
    int checkpoint(SupportSQLiteQuery supportSQLiteQuery);

    @Insert
    void insertFavorites(SiteswapEntity... siteswap);

    @Delete
    void deleteFavorite(SiteswapEntity siteswap);
}
