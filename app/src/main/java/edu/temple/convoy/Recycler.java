package edu.temple.convoy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Recycler extends RecyclerView.Adapter<Recycler.MyViewHolder>{
    private ArrayList<Record> list;
    RecyclerViewClickListener listener;
    public Recycler(ArrayList<Record> list,RecyclerViewClickListener listener){
        this.list = list;
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView recordinginformation;

            public MyViewHolder(final View view){
                super(view);
                recordinginformation = view.findViewById(R.id.recordinginformation);
            }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public Recycler.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler, parent,false);
            return new MyViewHolder(itemview);

    }

    @Override
    public void onBindViewHolder(@NonNull Recycler.MyViewHolder holder, int position) {
        String information = list.get(position).getUsername() + "    " + list.get(position).getLocalDate();
        holder.recordinginformation.setText(information);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

}
