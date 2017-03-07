package com.togglecorp.paiso.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.togglecorp.paiso.R;

import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ItemViewHolder> {
    private Context mContext;
    private int mSelectedItem = 0;
    private NavigationChangeListener mChangeListener;

    public static class Item {
        public String name;
        public Drawable icon;

//        public Item() {}
        public Item(String name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }
    };

    private List<Item> mItems;

    public NavigationDrawerAdapter(Context context, List<Item> items, NavigationChangeListener changeListener) {
        mContext = context;
        mItems = items;
        mChangeListener = changeListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_navigation_drawer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = mItems.get(position);

        if (mSelectedItem == position) {
            DrawableCompat.setTint(item.icon, Color.WHITE);
            holder.itemNameView.setTextColor(Color.WHITE);
            holder.rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            DrawableCompat.setTint(item.icon, Color.BLACK);
            holder.itemNameView.setTextColor(Color.BLACK);
            holder.rootView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.iconView.setImageDrawable(item.icon);
        holder.itemNameView.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected class ItemViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iconView;
        protected TextView itemNameView;
        protected View rootView;

        public ItemViewHolder(final View itemView) {
            super(itemView);

            iconView = (ImageView) itemView.findViewById(R.id.icon);
            itemNameView = (TextView) itemView.findViewById(R.id.item_name);
            rootView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedItem = getLayoutPosition();
                    notifyDataSetChanged();
                    mChangeListener.onChange(mSelectedItem);
                }
            });
        }
    }
}
