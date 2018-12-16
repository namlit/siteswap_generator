package namlit.siteswapgenerator;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GenerationParameterDao {

    @Query("SELECT * FROM generation_parameters ORDER BY name")
    List<GenerationParameterEntity> getAllGenerationParameters();

    @Query("SELECT * FROM generation_parameters WHERE name IS :name")
    GenerationParameterEntity getGenerationParamenters(String name);

    @Insert
    void insertGenerationParameters(GenerationParameterEntity... generationParameters);

    @Delete
    void deleteGenerationParameters(GenerationParameterEntity generationParameters);
}
