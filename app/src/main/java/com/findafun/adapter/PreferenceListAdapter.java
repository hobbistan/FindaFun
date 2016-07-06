package com.findafun.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.activity.SelectPreferenceActivity;
import com.findafun.bean.categories.Category;

import java.util.ArrayList;

/**
 * Created by nandhakumar.k on 01/01/16.
 */
public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder> {
    private ArrayList<Category> categoryArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v, int viewType) {
            super(v);
            if (viewType == 1) {
                mTextView = (TextView) v.findViewById(R.id.txt_preference_name);
            } else {
                mTextView = (TextView) v;
            }

            mTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PreferenceListAdapter(Context context, ArrayList<Category> categoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PreferenceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        if (viewType == 1) {
            parentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preference_view_type1, parent, false);

        }
        else {
            parentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preference_view_type2, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(categoryArrayList.get(position).getCategory());
        GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
        if (categoryArrayList.get(position).getCategoryPreference().equals("no")) {
            // holder.tickImage.setVisibility(View.INVISIBLE);
            bgShape.setColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            if (context instanceof SelectPreferenceActivity) {
                ((SelectPreferenceActivity) context).onCategorySelected(position);
            }
            bgShape.setColor(context.getResources().getColor(R.color.preference_orange));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
     /*   if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if(categoryArrayList.get(position)!=null || categoryArrayList.get(position).getSize()>0)
            return categoryArrayList.get(position).getSize();
        else
            return 1;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}