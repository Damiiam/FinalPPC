package abonnu.edu.ar.finalppc.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import abonnu.edu.ar.finalppc.DTO.Propiedad;
import abonnu.edu.ar.finalppc.R;

public class PropiedadesAdapter extends RecyclerView.Adapter<PropiedadesAdapter.ViewHolder>{

    private ArrayList<Propiedad> propiedades;

    public PropiedadesAdapter(ArrayList<Propiedad> listaPropiedades) {
        this.propiedades = listaPropiedades;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_propiedad, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getDescripcion().setText(propiedades.get(position).getDescripcion());
        holder.getDomicilio().setText(propiedades.get(position).getTelefono());
        holder.getTipo().setText(propiedades.get(position).getTipo());
        holder.getValor().setText("$ " + String.valueOf(propiedades.get(position).getValor()));

    }

    @Override
    public int getItemCount() {
        return propiedades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDescripcion, tvDomicilio, tvTipo, tvValor;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDescripcion = itemView.findViewById(R.id.tvDescipcion);
            tvDomicilio = itemView.findViewById(R.id.tvDomicilio);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvValor = itemView.findViewById(R.id.tvValor);
        }

        public TextView getDescripcion() {
            return tvDescripcion;
        }

        public TextView getDomicilio() {
            return tvDomicilio;
        }

        public TextView getTipo() {
            return tvTipo;
        }

        public TextView getValor() {
            return tvValor;
        }
    }
}
