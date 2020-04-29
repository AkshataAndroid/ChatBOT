package com.unfyd.unfydChatBot;

import android.content.Context;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unfyd.unfydChatBot.Activity.MainFragment;
import com.unfyd.unfydChatBot.Adapter.MessageAdapter;
import com.unfyd.unfydChatBot.Classes.clsCarousel;

import java.util.List;


public class CarouselRecyclerView extends RecyclerView.Adapter<CarouselRecyclerView.ViewHolder>{
    private LayoutInflater inflater;
    private List<clsCarousel> mListImages;
    Context _mContext;
    HorizontalListItemListener mHorizontalListItemListener;

    public CarouselRecyclerView(Context context, List<clsCarousel> messages,
                                HorizontalListItemListener horizontallistItemListener) {
        mListImages = messages;
        _mContext=context;

        mHorizontalListItemListener=horizontallistItemListener;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private TextView txtImagename;
        private ImageView imageView;

        public ViewHolder(View itemView) {


            super(itemView);

           txtImagename = (TextView) itemView.findViewById(R.id.txtImagename);
             imageView = (ImageView) itemView.findViewById(R.id.image);

            imageView.setOnClickListener(this);
        }

        public void setUsername(String username) {
            if (null == txtImagename) return;
            txtImagename.setText(username);

        }

        public void setCarouselImage(String username,int pos) {
            if (null == imageView) return;
              Glide.with(_mContext)
                .load(mListImages.get(pos).getImage())
                .into(imageView);

        }

        @Override
        public void onClick(View view) {

            try {
                mHorizontalListItemListener.HorizontalListItemClick(getAdapterPosition());
                Log.d("CAROUSEL",mListImages.get(getAdapterPosition()).getValue());

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public CarouselRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_corousel, parent, false);
        return new CarouselRecyclerView.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        clsCarousel message = mListImages.get(position);

       holder.setUsername(message.getValue());
       holder.setCarouselImage(message.getImage(),position);
    }


    @Override
    public int getItemCount() {
        return mListImages.size();
    }

    public interface HorizontalListItemListener {
        void HorizontalListItemClick(int position);
    }
}
