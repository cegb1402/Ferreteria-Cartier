package SQL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trabajo.ferreteriacartier.Productos;
import com.trabajo.ferreteriacartier.R;

public class AgregarProductosSQL extends AppCompatActivity {

    //declarion de objetos
    private EditText etCodigo, etNombre, etPrecio, etCantidad;
    private ImageView imagenProducto;
    private FloatingActionButton btnRegresar, btnGuardar,btnActualizar,btnEliminar;
    private String urlFoto;
    private ProductosDAO productosDAO;
    int REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_productos_sql);

        etCodigo = findViewById(R.id.etCodigoSQL);
        etNombre = findViewById(R.id.etNombreSQL);
        etPrecio = findViewById(R.id.etPrecioSQL);
        etCantidad = findViewById(R.id.etCantidadSQL);
        imagenProducto = findViewById(R.id.imagenProductoSQL);
        btnRegresar = findViewById(R.id.btnRegresarSQL);
        btnGuardar=findViewById(R.id.btnGuardarSQL);
        btnEliminar=findViewById(R.id.btnEliminarSQL);
        btnActualizar=findViewById(R.id.btnActualizarSQL);

        solicitarPermisos();
        productosDAO = new ProductosDAO(this);



//verificar si se entro por medio del view holder para activar botones de liminar y actualizar
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String nombreIntent = intent.getStringExtra("Nombre");
            String codigoIntent = intent.getStringExtra("Codigo");
            String precioIntent = intent.getStringExtra("Precio");
            String cantidadIntent = intent.getStringExtra("Cantidad");
            urlFoto =    intent.getStringExtra("Imagen");

            etNombre.setText(nombreIntent);
            etCodigo.setText(codigoIntent);
            etCantidad.setText(cantidadIntent);
            etPrecio.setText(precioIntent);
            etCodigo.setEnabled(false);
            btnGuardar.setVisibility(View.INVISIBLE);
            btnRegresar.setVisibility(View.INVISIBLE);
            btnActualizar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(urlFoto);
            imagenProducto.setImageURI(uri);
        }   else {
            btnGuardar.setVisibility(View.VISIBLE);
            btnRegresar.setVisibility(View.VISIBLE);
            btnActualizar.setVisibility(View.INVISIBLE);
            btnEliminar.setVisibility(View.INVISIBLE);
        }


        //programacion boton regresar
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(AgregarProductosSQL.this, ListaProductosSQL.class);
                startActivity(intentLogin);
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
                    Toast.makeText(AgregarProductosSQL.this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                } else {
                        productosDAO.insertarProducto(codigo,nombre, precio,cantidad,imagen);
                        limpiarTF();
                        Toast.makeText(AgregarProductosSQL.this, "Producto Subido Exitosamente", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AgregarProductosSQL.this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                } else {
                        productosDAO.actualizarProducto(codigo,nombre, precio,cantidad,imagen);
                        finish();
                        Toast.makeText(AgregarProductosSQL.this, "Producto Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        //programacion boton eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = etCodigo.getText().toString();

                    productosDAO.eliminarProducto(codigo);
                    finish();
                    Toast.makeText(AgregarProductosSQL.this, "Producto Eliminado Exitosamente", Toast.LENGTH_SHORT).show();

            }
        });

        //funcion para cargar imagen al presionar el image view
        imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

    }

    //limpiar text fields
    public void limpiarTF() {
        etCodigo.setText("");
        etCantidad.setText("");
        etNombre.setText("");
        etPrecio.setText("");
        imagenProducto.setImageResource(R.drawable.cargar_img);
    }

//funcion para cargar imagen
    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione una apicacion"), 10);
    }

    //funcion para solicitar los permisos para las imagenes

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

    //funcion boton regresar de android
    @Override
    public void onBackPressed() {
        Intent intentLogin = new Intent(AgregarProductosSQL.this, ListaProductosSQL.class);
        startActivity(intentLogin);
        finish();
    }



}