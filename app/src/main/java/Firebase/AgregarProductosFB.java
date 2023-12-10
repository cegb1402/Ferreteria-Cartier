package Firebase;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trabajo.ferreteriacartier.Productos;
import com.trabajo.ferreteriacartier.R;

public class AgregarProductosFB extends AppCompatActivity {

    //declaracion de objetos
    private EditText etCodigo, etNombre, etPrecio, etCantidad;
    private ImageView imagenProducto;
    private FloatingActionButton btnRegresar, btnGuardar,btnActualizar,btnEliminar;
    private String urlFoto;
    int REQUEST_CODE = 200;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_productos_fb);
        solicitarPermisos();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("productos");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("imagenes_productos");

        etCodigo = findViewById(R.id.etCodigoFB);
        etNombre = findViewById(R.id.etNombreFB);
        etPrecio = findViewById(R.id.etPrecioFB);
        etCantidad = findViewById(R.id.etCantidadFB);
        imagenProducto = findViewById(R.id.imagenProductoFB);
        btnRegresar = findViewById(R.id.btnRegresarFB);
        btnGuardar=findViewById(R.id.btnGuardarFB);
        btnActualizar = findViewById(R.id.btnActualizarFB);
        btnEliminar=findViewById(R.id.btnEliminarFB);


//comprobacion si se  accedio por medio del view holder
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String nombre = intent.getStringExtra("Nombre");
            String codigo = intent.getStringExtra("Codigo");
            String precio = intent.getStringExtra("Precio");
            String cantidad = intent.getStringExtra("Cantidad");
            urlFoto=intent.getStringExtra("Imagen");

            etNombre.setText(nombre);
            etCodigo.setText(codigo);
            etCantidad.setText(cantidad);
            etPrecio.setText(precio);
            etCodigo.setEnabled(false);
            btnGuardar.setVisibility(View.INVISIBLE);
            btnRegresar.setVisibility(View.INVISIBLE);
            btnActualizar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
            Glide.with(this).load(urlFoto).into(imagenProducto);
        }
        else {
            btnGuardar.setVisibility(View.VISIBLE);
            btnRegresar.setVisibility(View.VISIBLE);
            btnActualizar.setVisibility(View.INVISIBLE);
            btnEliminar.setVisibility(View.INVISIBLE);
        }

        // programacion boton regresar
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AgregarProductosFB.this,ListaProductosFB.class);
                finish();
            }
        });

        //programacion boton guardar

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = etCodigo.getText().toString();
                String nombre = etNombre.getText().toString();
                String precio = etPrecio.getText().toString();
                String cantidad = etCantidad.getText().toString();
                String imagen = urlFoto;

                if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || cantidad.isEmpty() || urlFoto == null) {
                    Toast.makeText(AgregarProductosFB.this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                } else {
                if (verificarInternet()){
                    insertarProducto(codigo,nombre, precio,cantidad,imagen);
                    limpiarTF();
                    Toast.makeText(AgregarProductosFB.this, "Producto Subido Exitosamente", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AgregarProductosFB.this, "No hay conexión a Internet para subir el producto", Toast.LENGTH_SHORT).show();
                }
                }
            }
        });

        //programacion boton actualizar

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Productos productos=new Productos();
                String codigo = etCodigo.getText().toString();
                String nombre = etNombre.getText().toString();
                String precio = etPrecio.getText().toString();
                String cantidad = etCantidad.getText().toString();
                String imagen = urlFoto;

                if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || cantidad.isEmpty() || imagen == null) {
                    Toast.makeText(AgregarProductosFB.this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificarInternet()){
                        insertarProducto(codigo,nombre, precio,cantidad,imagen);
                        finish();
                        Toast.makeText(AgregarProductosFB.this, "Producto Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AgregarProductosFB.this, "No hay conexión a Internet para modificar el producto", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //programacion boton eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = etCodigo.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(AgregarProductosFB.this);
                builder.setMessage("¿Desea eliminar este producto del servido?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (verificarInternet()) {
                                    eliminarProducto(codigo);
                                    finish();
                                    Toast.makeText(AgregarProductosFB.this, "Producto Eliminado Exitosamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AgregarProductosFB.this, "No hay conexión a Internet para eliminar el producto", Toast.LENGTH_SHORT).show();
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

        //programacion para insertar imagen
        imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Seleccione una apicacion"), 10);
            }
        });

    }

    //funcion para limpiar text fields
    public void limpiarTF() {
        etCodigo.setText("");
        etCantidad.setText("");
        etNombre.setText("");
        etPrecio.setText("");
        imagenProducto.setImageResource(R.drawable.cargar_img);
    }

    //funcion para solicitar permisos al entrar a la actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            imagenProducto.setImageURI(path);
            urlFoto = path.toString();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisos() {
        int permisos = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);

        if (permisos == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE);
        }
    }

    //programacion boton regresar de android

    @Override
    public void onBackPressed() {
        finish();
    }

    //funcion para comprobar conexion a internet
    private boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //funcion para insertar producto en firebase
    private void insertarProducto(String codigo, String nombre, String precio, String cantidad, String urlFoto) {

        String urlFotoActual = getIntent().getStringExtra("Imagen");

        if (urlFoto != null && !urlFoto.equals(urlFotoActual)) {
            StorageReference imagenRef = storageReference.child(codigo + ".jpg");
            imagenRef.putFile(Uri.parse(urlFoto))
                    .addOnSuccessListener(taskSnapshot -> {
                        imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Productos producto = new Productos(codigo, nombre, precio, cantidad, uri.toString());

                            reference.child(codigo).setValue(producto)
                                    .addOnSuccessListener(aVoid -> {
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                    });
        } else {
            Productos producto = new Productos(codigo, nombre, precio, cantidad, urlFoto);
            reference.child(codigo).setValue(producto).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> {
                    });
        }
    }

//funcion para eliminar producto en firebase
    private void eliminarProducto(String codigo) {
        DatabaseReference productoRef = reference.child(codigo);
        productoRef.removeValue();
        storageReference.child(codigo + ".jpg").delete();
    }

}