package namlit.siteswapgenerator;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
