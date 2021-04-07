package com.bs.login.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bs.login.R;
import com.bs.login.databinding.RecyclerLayoutBinding;
import com.bs.login.databinding.LoadingBinding;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.BasicViewHolder> {

    //#region PROPERTIES & VALUES
    private List<Map<String, Object>> data;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    //#endregion

    //#region GETTERS, SETTERS & ETC.
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
        // recude clutter elsewhere, so it notifies here the dataset changed
        this.notifyDataSetChanged();
    }

    public RecyclerAdapter(List<Map<String, Object>> DB_dataset) {
        this.data = DB_dataset;
    }
    //#endregion

    @NonNull
    @Override
    public BasicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // verify if viewType is a loading one or a default one
        if (viewType == VIEW_TYPE_ITEM) {
            return new BasicViewHolder(RecyclerLayoutBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new BasicViewHolder(LoadingBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BasicViewHolder holder, int position) {
        // verifies if the data is not a instance of loading
        if (holder.binding instanceof RecyclerLayoutBinding) {
            Map<String, Object> data_i = data.get(position);
            if (data_i != null) {
                // setting data based on hashmap
                ((RecyclerLayoutBinding) holder.binding).nome.setText((String) data_i.get("Nome"));
                ((RecyclerLayoutBinding) holder.binding).data.setText(((Timestamp) data_i.get("Data")).toDate().toString());
                // get time of login
                double d = Double.parseDouble(((Timestamp) data_i.get("Data")).toDate()
                        .toString().split(" ")[3].replaceFirst(":", ".")
                        .split(":")[0]);
                // verify if login is in allowed time - 8.45 = 8h45m | 13.30 = 13h45m
                // thens sets the backgound to a specific color & adds the corresponding image
                if ((d > 8.45 && d < 10.00) || (d > 12.45 && d < 13.30)) {
                    ((RecyclerLayoutBinding) holder.binding).linearLayout
                            .setBackgroundColor(Color.parseColor("#80ff80"));
                    ((RecyclerLayoutBinding) holder.binding).imagem.setImageResource(R.drawable.check_mark);
                } else {
                    ((RecyclerLayoutBinding) holder.binding).linearLayout
                            .setBackgroundColor(Color.parseColor("#ff9999"));
                    ((RecyclerLayoutBinding) holder.binding).imagem.setImageResource(R.drawable.x_mark);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // return type if there is data inside or not
        return data.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //#region VIEW HOLDERS
    //#region CLASSES
    public class BasicViewHolder extends RecyclerView.ViewHolder {
        private ViewBinding binding;

        public BasicViewHolder(ViewBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }
    }
    //#endregion
    //#endregion

}
