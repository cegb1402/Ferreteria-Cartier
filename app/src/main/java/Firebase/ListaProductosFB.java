
package Firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trabajo.ferreteriacartier.Login;
import com.trabajo.ferreteriacartier.Productos;
import com.trabajo.ferreteriacartier.R;

import java.util.ArrayList;
import java.util.List;

import SQL.ListaProductosSQL;

public class ListaProductosFB extends AppCompatActivity {


    //declaracion de objetos
    private RecyclerView recyclerView;
    private SearchView svBuscar;
    private Adapter productosAdapter;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private List<Productos> productosLista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos_fb);

        FloatingActionButton btnLogout = findViewById(R.id.btnLogoutFB);
        FloatingActionButton btnAgregarProducto = findViewById(R.id.btnAgregarProductoFB);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutFB);

        Toolbar toolbar = findViewById(R.id.toolbarFB);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recIdFB);

        svBuscar = findViewById(R.id.searchFB);
        svBuscar.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListaProductosFB.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        productosLista = new ArrayList<>();
        productosAdapter = new Adapter(ListaProductosFB.this, productosLista);
        recyclerView.setAdapter(productosAdapter);


//verificar internet al entrar a la aplicacion
        if (verificarInternet()==false){
            Toast.makeText(this, "No tienes acceso a internet para mostrar los productos", Toast.LENGTH_SHORT).show();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("productos");

        //obtener lista de productos y insertarlos al adapter
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productosLista.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Productos productos = itemSnapshot.getValue(Productos.class);
                    productos.setCodigo(itemSnapshot.getKey());
                    productosLista.add(productos);
                }
                productosAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //funcion para buscar
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

        //funcion para actualizar listar deslizando hacia abajo
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (verificarInternet()) {
                    recyclerView.setAdapter(productosAdapter);
                    productosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListaProductosFB.this, "Debes tener conexión a internet para mostrar los productos", Toast.LENGTH_SHORT).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //funcion boton para cerrar sesion
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListaProductosFB.this);
                builder.setMessage("¿Desea cerrar sesión?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intentLogin = new Intent(ListaProductosFB.this, Login.class);
                                startActivity(intentLogin);
                                finish();
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

        //funcion para boton agregar
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAgregarPro = new Intent(ListaProductosFB.this, AgregarProductosFB.class);
                startActivity(intentAgregarPro);
            }
        });
    }

    //funcion para el boton regresar de android
    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
        confirmar.setTitle("Salir");
        confirmar.setMessage("¿Desea salir de la aplicación?");
        confirmar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        getMenuInflater().inflate(R.menu.menu_lista_productos_fb, menu);
        return true;
    }

    //accion para acceder a los productos locales por medio del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_productosSQL) {
            Intent productosSQL = new Intent(this, ListaProductosSQL.class);
            startActivity(productosSQL);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //funcion para comprobar interneet
    private boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //funcion para buscar entre todos los productos
    public void searchList(String text){
        ArrayList<Productos> searchList = new ArrayList<>();
        for (Productos dataClass: productosLista){
            if (dataClass.getNombre().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        productosAdapter.searchDataList(searchList);
    }



}