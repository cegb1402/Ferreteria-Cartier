package SQL;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.trabajo.ferreteriacartier.Productos;

import java.util.ArrayList;
import java.util.List;


public class ProductosDAO {
    private ProductosDBHelper dbHelper;

    public ProductosDAO(Context context) {
        dbHelper = new ProductosDBHelper(context);
    }
    public void close() {
        dbHelper.close();
    }

    //funcion insertar
    public void insertarProducto(String codigo, String nombre, String precio, String cantidad, String urlImg) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProductosDBHelper.COLUMN_CODIGO, codigo);
        values.put(ProductosDBHelper.COLUMN_NOMBRE, nombre);
        values.put(ProductosDBHelper.COLUMN_PRECIO, precio);
        values.put(ProductosDBHelper.COLUMN_CANTIDAD, cantidad);
        values.put(ProductosDBHelper.COLUMN_URL_IMG, urlImg);

        try {
            db.insertOrThrow(ProductosDBHelper.TABLE_PRODUCTOS, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    //funcion eliminar
    public void eliminarProducto(String codigo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.delete(ProductosDBHelper.TABLE_PRODUCTOS, ProductosDBHelper.COLUMN_CODIGO + " = ?", new String[]{codigo});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //funcion actualizar
    public void actualizarProducto(String codigo, String nuevoNombre, String nuevoPrecio, String nuevaCantidad, String nuevaUrlImg) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProductosDBHelper.COLUMN_NOMBRE, nuevoNombre);
        values.put(ProductosDBHelper.COLUMN_PRECIO, nuevoPrecio);
        values.put(ProductosDBHelper.COLUMN_CANTIDAD, nuevaCantidad);
        values.put(ProductosDBHelper.COLUMN_URL_IMG, nuevaUrlImg);

        try {
            db.update(ProductosDBHelper.TABLE_PRODUCTOS, values, ProductosDBHelper.COLUMN_CODIGO + " = ?", new String[]{codigo});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    //funcion eliminar
    public void eliminarTodosLosProductos() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM productos");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //funcion obtener
    public List<Productos> obtenerTodosLosProductos() {
        List<Productos> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ProductosDBHelper.COLUMN_CODIGO,
                ProductosDBHelper.COLUMN_NOMBRE,
                ProductosDBHelper.COLUMN_PRECIO,
                ProductosDBHelper.COLUMN_CANTIDAD,
                ProductosDBHelper.COLUMN_URL_IMG
        };

        Cursor cursor = db.query(
                ProductosDBHelper.TABLE_PRODUCTOS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String codigo = cursor.getString(cursor.getColumnIndexOrThrow(ProductosDBHelper.COLUMN_CODIGO));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ProductosDBHelper.COLUMN_NOMBRE));
            String precio = cursor.getString(cursor.getColumnIndexOrThrow(ProductosDBHelper.COLUMN_PRECIO));
            String cantidad = cursor.getString(cursor.getColumnIndexOrThrow(ProductosDBHelper.COLUMN_CANTIDAD));
            String urlImg = cursor.getString(cursor.getColumnIndexOrThrow(ProductosDBHelper.COLUMN_URL_IMG));
            Productos producto = new Productos(codigo, nombre, precio, cantidad, urlImg);
            productList.add(producto);
        }
        cursor.close();
        db.close();
        return productList;
    }


}
