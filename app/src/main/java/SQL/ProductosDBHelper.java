package SQL;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductosDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "productos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCTOS = "productos";
    public static final String COLUMN_CODIGO = "codigo";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_CANTIDAD = "cantidad";
    public static final String COLUMN_URL_IMG = "urlImg";

    // Constructor
    public ProductosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTOS + " (" +
                COLUMN_CODIGO + " TEXT PRIMARY KEY," +
                COLUMN_NOMBRE + " TEXT," +
                COLUMN_PRECIO + " TEXT," +
                COLUMN_CANTIDAD + " TEXT," +
                COLUMN_URL_IMG + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
