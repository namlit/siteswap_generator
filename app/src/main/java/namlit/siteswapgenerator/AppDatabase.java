package namlit.siteswapgenerator;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;

@Database(entities = {SiteswapEntity.class, GenerationParameterEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public final static String database_name = "siteswap_generator_app_database";
    private static AppDatabase INSTANCE;

    public abstract FavoriteDao siteswapDao();
    public abstract GenerationParameterDao generationParameterDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            database_name)
                            .addMigrations(MIGRATION_1_2)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `generation_parameters` (" +
                    "`uid` INTEGER NOT NULL, " +
                    "`name` TEXT, " +
                    "`numberOfObjects` INTEGER NOT NULL, " +
                    "`periodLength` INTEGER NOT NULL, " +
                    "`maxThrow` INTEGER NOT NULL, " +
                    "`minThrow` INTEGER NOT NULL, " +
                    "`numberOfJugglers` INTEGER NOT NULL, " +
                    "`maxResults` INTEGER NOT NULL, " +
                    "`timeout` INTEGER NOT NULL, " +
                    "`isSynchronous` INTEGER NOT NULL, " +
                    "`isRandomMode` INTEGER NOT NULL, " +
                    "`isZips` INTEGER NOT NULL, " +
                    "`isZaps` INTEGER NOT NULL, " +
                    "`isHolds` INTEGER NOT NULL, " +
                    "`filterListString` TEXT, " +
                    "PRIMARY KEY(`uid`))");
        }
    };

}
