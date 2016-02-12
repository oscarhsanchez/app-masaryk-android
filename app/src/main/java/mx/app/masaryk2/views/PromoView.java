package mx.app.masaryk2.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;


public class PromoView extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imgPromo;
    OnPromoClickListener listener;
    int position;

    public PromoView(View itemView, OnPromoClickListener l) {

        super(itemView);
        itemView.setOnClickListener(this);
        imgPromo = (ImageView)itemView.findViewById(R.id.img_promo);
        listener = l;

    }

    public void setInfo(JSONObject item, int pos, Context context) {

        String url = "";
        try {
            url = item.getJSONObject("thumb").getString("src");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        position = pos;
        Picasso.with(context).load(url).into(imgPromo);
    }

    @Override
    public void onClick(View view) {
        listener.onPromoClick(position);
    }

    //listener passed to viewHolder
    public interface OnPromoClickListener {
        void onPromoClick(int position);
    }
}