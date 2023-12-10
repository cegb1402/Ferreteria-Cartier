package Firebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.trabajo.ferreteriacartier.Productos;
import com.trabajo.ferreteriacartier.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ProductosViewHolder> {

    private Context context;
    private List<Productos> productList;

    public Adapter(Context context, List<Productos> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_card, parent, false);
        return new ProductosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        final int currentPosition = position;

        //uso de glide para tomar el url de la imagen y insertarlo en el image view en el card del producto
        Glide.with(context).load(productList.get(currentPosition).getUrlImg()).into(holder.fotoProducto);
        holder.txtNombreProducto.setText(productList.get(currentPosition).getNombre());
        holder.txtCodigoProducto.setText("Código: " + productList.get(currentPosition).getCodigo());
        holder.txtCantidadProducto.setText("Cantidad: " + productList.get(currentPosition).getCantidad());
        holder.txtPrecioProducto.setText("Precio: $" + productList.get(currentPosition).getPrecio());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AgregarProductosFB.class);
                intent.putExtra("Imagen", productList.get(currentPosition).getUrlImg());
                intent.putExtra("Nombre", productList.get(currentPosition).getNombre());
                intent.putExtra("Codigo", productList.get(currentPosition).getCodigo());
                intent.putExtra("Cantidad", productList.get(currentPosition).getCantidad());
                intent.putExtra("Precio", productList.get(currentPosition).getPrecio());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void searchDataList(ArrayList<Productos> searchList){
        productList = searchList;
        notifyDataSetChanged();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder {

        ImageView fotoProducto;
        TextView txtNombreProducto, txtCodigoProducto, txtCantidadProducto, txtPrecioProducto;
        CardView cardView;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoProducto = itemView.findViewById(R.id.fotoProducto);
            cardView = itemView.findViewById(R.id.cv);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            txtCodigoProducto = itemView.findViewById(R.id.txtCodigoProducto);
            txtCantidadProducto = itemView.findViewById(R.id.txtCantidadProducto);
            txtPrecioProducto = itemView.findViewById(R.id.txtPrecioProducto);
        }
    }
}


