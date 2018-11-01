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
    SiteswapEntity getSiteswap(String siteswap);

    @Insert
    void insertFavorites(SiteswapEntity... siteswap);

    @Delete
    void deleteFavorite(SiteswapEntity siteswap);
}
