package mx.app.masaryk2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.views.PromoView;


/**
 * Created by noisedan on 2/6/16.
 */
public class PromoAdapter extends RecyclerView.Adapter<PromoView> implements PromoView.OnPromoClickListener {

    JSONArray data;
    Context context;
    OnItemClickListener listener;

    public PromoAdapter(Context c, JSONArray d, OnItemClickListener l) {
        data     = d;
        context  = c;
        listener = l;
    }

    @Override
    public PromoView onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_promo, null);
        PromoView rcv = new PromoView(layoutView, this);


        return rcv;
    }

    @Override
    public void onBindViewHolder(PromoView holder, int position) {

        JSONObject item = new JSONObject();
        try {
            item = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.setInfo(item, position, context);

    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    @Override
    public void onPromoClick(int position) {
        try {
            listener.onItemClick(position, data.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //listener passed to viewHolder
    public interface OnItemClickListener {
        void onItemClick(int position, JSONObject data);
    }

}
