package SQL;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trabajo.ferreteriacartier.Productos;
import com.trabajo.ferreteriacartier.R;

import java.util.ArrayList;
import java.util.List;

import Firebase.ListaProductosFB;

public class ListaProductosSQL extends AppCompatActivity {

    //deeclarion de objetos
    private SearchView svBuscar;
    private RecyclerView recyclerView;
    private SQLiteAdapter adapter;
    private FirebaseDatabase database;
    private ProductosDAO productosDAO;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private List<Productos> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos_sql);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("productos");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("imagenes_productos");

        FloatingActionButton btnSubir=findViewById(R.id.btnSubirSQL);
        FloatingActionButton btnAgregarProducto=findViewById(R.id.btnAgregarProductoSQL);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutSQL);
        Toolbar toolbar = findViewById(R.id.toolbarSQL);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recIdSQL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SQLiteAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        productosDAO = new ProductosDAO(this);
        productList = new ArrayList<>();
        productList = productosDAO.obtenerTodosLosProductos();
        adapter.updateDataList(productList);

        svBuscar = findViewById(R.id.searchSQL);


        //funcion para el boton de buscar
        svBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });


        //funcion para actualizar por deslizamiento de recycler view
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                List<Productos> productList = productosDAO.obtenerTodosLosProductos();
                adapter.updateDataList(productList);
            }
        });

        //funcion para subir los productos locales a firebase y despus eliminarlos
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListaProductosSQL.this);
                builder.setMessage("¿Desea subir los productos offline al servidor?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (verificarInternet()) {
                                    List<Productos> productosOffline = productosDAO.obtenerTodosLosProductos();

                                    for (Productos producto : productosOffline) {
                                        subirProductoAFirebase(producto);
                                    }
                                    productosDAO.eliminarTodosLosProductos();
                                    Toast.makeText(ListaProductosSQL.this, "Productos subidos exitosamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ListaProductosSQL.this, "No hay conexión a Internet para subir el producto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });


//programacion boton regresar
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAgregarPro = new Intent(ListaProductosSQL.this, AgregarProductosSQL.class);
                startActivity(intentAgregarPro);
                finish();
            }
        });

    }

    //programacion boton regresar de android
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder confirmar = new android.app.AlertDialog.Builder(this);
        confirmar.setTitle("Regresar");
        confirmar.setMessage("¿Desea regresar a los productos online?");
        confirmar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent productosFB= new Intent(ListaProductosSQL.this, ListaProductosFB.class);
                startActivity(productosFB);
                finish();
            }
        });
        confirmar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmar.show();
    }

    //inflar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_lista_productos_sql, menu);
        return true;
    }

    //condicion para cuando seleccione el boton del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_productosFB) {
            Intent productosFB= new Intent(ListaProductosSQL.this,ListaProductosFB.class);
            startActivity(productosFB);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //funcion para comprobar inteernet
    private boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    //funcion para subir los productos de sql lite y subirlos a firebase
    private void subirProductoAFirebase(Productos producto) {
        String codigo = producto.getCodigo();
        String nombre = producto.getNombre();
        String precio = producto.getPrecio();
        String cantidad = producto.getCantidad();
        String urlFoto = producto.getUrlImg();

        StorageReference imagenRef = storageReference.child(codigo + ".jpg");

        if (urlFoto != null && !urlFoto.isEmpty()) {
            imagenRef.putFile(Uri.parse(urlFoto))
                    .addOnSuccessListener(taskSnapshot -> {
                        imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Productos productoFirebase = new Productos(codigo, nombre, precio, cantidad, uri.toString());
                            reference.child(codigo).setValue(productoFirebase)
                                    .addOnSuccessListener(aVoid -> {
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                    });
        } else {
            Productos productoFirebase = new Productos(codigo, nombre, precio, cantidad, "");
            reference.child(codigo).setValue(productoFirebase)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

    //funcion para buscar entro los productos locales
    public void searchList(String text){
        ArrayList<Productos> searchList = new ArrayList<>();
        for (Productos dataClass: productList){
            if (dataClass.getNombre().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

}


