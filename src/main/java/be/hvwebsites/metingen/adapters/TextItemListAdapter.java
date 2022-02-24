package be.hvwebsites.metingen.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.EditEntity;
import be.hvwebsites.metingen.EditMeasurement;
import be.hvwebsites.metingen.R;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Measurement;

public class TextItemListAdapter extends RecyclerView.Adapter<TextItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;
    private List<ListItemHelper> itemList;
    private String entityType;

    public TextItemListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textItemView;

        private ListViewHolder(View itemView){
            super(itemView);

            if (entityType.equals(SpecificData.ENTITY_TYPE_3)){
                textItemView = itemView.findViewById(R.id.msrmnt_item);
            }else {
                textItemView = itemView.findViewById(R.id.manage_entities_item);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // er is geclicked op een item, dit betekent dat er nr detail vd item vr evt update wordt gegaan
            // daarvoor gaan we nr de update activity
            int positionToUpdate = getAdapterPosition();
            // Bepaal de ID vh currentitem
            IDNumber itemIDToUpdate = itemList.get(positionToUpdate).getItemID();
//            String currentItem = itemList.get(positionToUpdate).getItemtext();

            Intent intent;

            if (entityType == SpecificData.ENTITY_TYPE_3){
                intent = new Intent(mContext, EditMeasurement.class);
            }else {
                intent = new Intent(mContext, EditEntity.class);
            }

            intent.putExtra(SpecificData.ENTITY_TYPE, entityType);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, "currentItem");
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, itemIDToUpdate.getId());
            mContext.startActivity(intent);
        }
    }

//    public List<String> getReusableList() {
//        return reusableList;
//    }

    public List<ListItemHelper> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemHelper> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

//    public void setReusableList(List<String> reusableList) {
//        this.reusableList = reusableList;
//        notifyDataSetChanged();
//    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        if (entityType.equals(SpecificData.ENTITY_TYPE_3)){
            itemView = inflater.inflate(R.layout.list_measurement_item, parent, false);
        }else {
            itemView = inflater.inflate(R.layout.list_manage_entities_item, parent, false);
        }
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if (itemList != null){
            String currentLine = itemList.get(position).getItemtext();
            holder.textItemView.setText(currentLine);
        }else {
            holder.textItemView.setText("No data !");
        }
        if (entityType.equals(SpecificData.ENTITY_TYPE_3)){
            holder.textItemView.setTypeface(Typeface.MONOSPACE);
        }
    }

    @Override
    public int getItemCount() {
//        if (reusableList != null) return reusableList.size();
//        else return 0;
        if (itemList != null) return itemList.size();
        else return 0;
    }

}
