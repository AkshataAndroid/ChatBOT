package com.unfyd.unfydChatBot.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
//import android.support.v4.view.ViewPager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.CellSignalStrength;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unfyd.unfydChatBot.Activity.ImageFragment;
import com.unfyd.unfydChatBot.Activity.MainActivity;
import com.unfyd.unfydChatBot.Activity.MainFragment;
import com.unfyd.unfydChatBot.CarouselRecyclerView;
import com.unfyd.unfydChatBot.Classes.Constants;
import com.unfyd.unfydChatBot.Classes.Message;
import com.unfyd.unfydChatBot.Database.DBHelper;
import com.unfyd.unfydChatBot.Database.clsChatDetail;
import com.unfyd.unfydChatBot.R;
import com.unfyd.unfydChatBot.RecyclerViewItemDecorator;
import com.unfyd.unfydChatBot.preferences.UserPreferences;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements CarouselRecyclerView.HorizontalListItemListener {

    private List<Message> mMessages;
    private int[] mUsernameColors;
    Context _mContext;
    byte[] response;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager HorizontalLayout;
    private ListItemListener mlistItemListener;
    File apkStorage = null;
    File outputFile = null;
    Bitmap bitFile = null;
    String fileName = "";
    String isFileDownloaded = "";
    MainFragment mainFragment;
    private final ProgressDialog dialog;

    public MessageAdapter(Context context, List<Message> messages, ListItemListener listItemListener, MainFragment fragment) {
        mMessages = messages;
        _mContext = context;
        mlistItemListener = listItemListener;
        mainFragment = fragment;
        dialog = new ProgressDialog(context);
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.item_message;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_action;
                break;
            //case Message.TYPE_OPTIONS:
            //      layout = R.layout.item_options;
            //     break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v, mlistItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());
        viewHolder.setOptionButton(message.getOptionValue());
        viewHolder.setType(message.getTypeValues(), position);
        viewHolder.setCarousel(message.getTypeValues(), position);
        viewHolder.setImage(message.getTypeValues(), position);

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    @Override
    public void HorizontalListItemClick(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CarouselRecyclerView.HorizontalListItemListener {

        private TextView mUsernameView;
        private TextView mMessageView, mUsermessage;
        private Button mOptionView;
        private GridView gridbuttons;
        private RecyclerView horizontal_recyler_view;
        private ImageButton imgBtnDownload;
        private ImageView imgLOGO;
        private LinearLayout linearLayoutDownload;
        private View layoutDownload;
        String url;
        ListItemListener onitemListener;

        public ViewHolder(View itemView, ListItemListener onItemListener) {
            super(itemView);
            try {
                this.onitemListener = onItemListener;
                // mUsernameView = (TextView) itemView.findViewById(R.id.username);
                mMessageView = (TextView) itemView.findViewById(R.id.message);
                imgLOGO = (ImageView) itemView.findViewById(R.id.imgLOGO);
                mUsermessage = (TextView) itemView.findViewById(R.id.Usermessage);
                gridbuttons = (GridView) itemView.findViewById(R.id.gridbuttons);
                horizontal_recyler_view = (RecyclerView) itemView.findViewById(R.id.horizontalRecyclerView);
                imgBtnDownload = (ImageButton) itemView.findViewById(R.id.imgBtnDownload);
                linearLayoutDownload = (LinearLayout) itemView.findViewById(R.id.linearLayoutDownload);
                layoutDownload = (View) itemView.findViewById(R.id.layoutDownload);
                imgBtnDownload.setOnClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        public void setType(String message, int pos) {
            if (null == gridbuttons) return;
            if (message.equalsIgnoreCase("button")) {
                gridbuttons.setVisibility(View.VISIBLE);
                CustomAdapter customAdapter = new CustomAdapter(gridbuttons.getContext(), mMessages.get(pos).getOptionLst(), mMessages, mainFragment);
                gridbuttons.setAdapter(customAdapter);
            } else {
                gridbuttons.setVisibility(View.GONE);
            }
        }

        public void changeBackgroundmMessageView(TextView view,int colorCode) {
            int color = 0;
            try {
                //String colorcode = UserPreferences.getPreferences(_mContext, "colorCode");
                //if (colorcode.equalsIgnoreCase("blue")) {
                   // color = Color.rgb(173, 216, 230); //red for example
                    color= ContextCompat.getColor(_mContext,colorCode);
                int radius = 70; //radius will be 5px
                int strokeWidth = 2;

                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setColor(color);
                gradientDrawable.setCornerRadius(radius);
                gradientDrawable.setStroke(strokeWidth, color);
              //  mMessageView.setBackground(gradientDrawable);
                view.setBackground(gradientDrawable);
                //mMessageView.setBackgroundResource(R.drawable.light_blue_layout);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }





        public void setCarousel(String message, int position) {
            if (null == horizontal_recyler_view) return;
            if (message.equalsIgnoreCase("carousel")) {
                int spaceInPixels = 10;
                HorizontalLayout = new LinearLayoutManager(_mContext, LinearLayoutManager.HORIZONTAL, false);
                horizontal_recyler_view.addItemDecoration(new RecyclerViewItemDecorator(spaceInPixels));
                horizontal_recyler_view.setLayoutManager(HorizontalLayout);
                horizontal_recyler_view.setVisibility(View.VISIBLE);
                if (mMessages.get(position).getcarouselLst() != null) {
                    horizontal_recyler_view.setAdapter(new CarouselRecyclerView(_mContext, mMessages.get(position).getcarouselLst(), this));
                }
            } else {
                horizontal_recyler_view.setVisibility(View.GONE);
            }
        }

        public void setImage(String message, int position) {
            UpdateDB("0", "");
            if (null == imgBtnDownload) return;
            if (message.equalsIgnoreCase("image")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.image_download));
                imgBtnDownload.setTag("1");
            } else if (message.equalsIgnoreCase("word")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.doc_download));
                imgBtnDownload.setTag("3");
            } else if (message.equalsIgnoreCase("pdf")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.pdf_download));
                imgBtnDownload.setTag("5");
            } else if (message.equalsIgnoreCase("excel")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.excel_download));
                imgBtnDownload.setTag("7");
            } else if (message.equalsIgnoreCase("ppt")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.ppt_download));
                imgBtnDownload.setTag("9");
            } else if (message.equalsIgnoreCase("audio")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.audio_download));
                imgBtnDownload.setTag("11");
            } else if (message.equalsIgnoreCase("video")) {
                layoutDownload.setVisibility(View.VISIBLE);
                imgBtnDownload.setImageDrawable(_mContext.getResources().getDrawable(R.drawable.video_download));
                imgBtnDownload.setTag("13");
            } else {
                layoutDownload.setVisibility(View.GONE);
            }
        }


        public void setOptionButton(String message) {
            //  if (null == mOptionView) return;
            //  mOptionView.setText(message);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            ////if(username!=null || username!="" && username.equalsIgnoreCase("server")) {
            // mUsernameView.setVisibility(View.VISIBLE);
            //mUsermessage.setVisibility(View.GONE);
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
//             }else
//             {
//                 mUsernameView.setVisibility(View.VISIBLE);
//                 mUsermessage.setVisibility(View.GONE);
//             }
        }


        public void setMessage(String message) {
            if (null == mMessageView) return;
            if (mMessages.get(getAdapterPosition()).getUsername() != null) {
                if (mMessages.get(getAdapterPosition()).getUsername().equalsIgnoreCase("server")) {
                    mMessageView.setText(message);
                    mUsermessage.setVisibility(View.GONE);
                    changeBackgroundmMessageView(mUsermessage,R.color.colorWhite);
                    imgLOGO.setVisibility(View.VISIBLE);
                } else {
                    mUsermessage.setVisibility(View.VISIBLE);
                    mMessageView.setVisibility(View.GONE);
                    mUsermessage.setText(message);
                    changeBackgroundmMessageView(mUsermessage,R.color.colorlightGreen);
                    imgLOGO.setVisibility(View.GONE);
                }
            }
        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }

        @Override
        public void onClick(View view) {
            try {
                onitemListener.ListItemClick(getAdapterPosition());
                if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("image")) {
                    ImageTask();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("word")) {
                    DocTask();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("pdf")) {
                    PDfTask();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("excel")) {
                    ExcelTAsk();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("ppt")) {
                    PPTTAsk();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("audio")) {
                    AudioTAsk();
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("video")) {
                    VideoTask();
                }
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage().toString());
                ex.printStackTrace();
            }
        }


        private void ImageTask() {
            try {

                url = mMessages.get(getAdapterPosition()).getImageLst().get(0);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
//                    Glide.with(_mContext)
//                            .load(R.drawable.loading)
//                            .into(imgBtnDownload);

                if (imgBtnDownload.getTag() == "1") {
                    Log.d("IMAGE", "SAME");
                    new DownloadTask().execute();
                } else {
                    Log.d("IMAGE", "DIFF");
                    ImageFragment fragment = new ImageFragment();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitFile.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("byteArray", bs.toByteArray());
                    fragment.setArguments(bundle);
                    switchContent(R.id.container, fragment);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void DocTask() {
            try {
                url = mMessages.get(getAdapterPosition()).getWordLst().get(0);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                //Glide.with(_mContext)
                //      .load(R.drawable.loading)
                //    .into(imgBtnDownload);
                if (imgBtnDownload.getTag() == "3") {
                    new DownloadTask().execute();
                } else {
                    openDocument("DOCUMENTS");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void PDfTask() {
            try {
                url = mMessages.get(getAdapterPosition()).getPdfLst().get(0);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
//                Glide.with(_mContext)
//                        .load(R.drawable.loading)
//                        .into(imgBtnDownload);
                if (imgBtnDownload.getTag() == "5") {
                    new DownloadTask().execute();

                } else {
                    openDocument("PDF");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void ExcelTAsk() {
            try {
                url = mMessages.get(getAdapterPosition()).getExcelLst().get(0);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
//                Glide.with(_mContext)
//                        .load(R.drawable.loading)
//                        .into(imgBtnDownload);
                if (imgBtnDownload.getTag() == "7") {
                    new DownloadTask().execute();
                } else {
                    openDocument("EXCEL");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void PPTTAsk() {
            try {
                url = mMessages.get(getAdapterPosition()).getPptLst().get(0);
//                Glide.with(_mContext)
//                        .load(R.drawable.loading)
//                        .into(imgBtnDownload);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                if (imgBtnDownload.getTag() == "9") {
                    new DownloadTask().execute();
                } else {
                    openDocument("PPT");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void AudioTAsk() {
            try {
                url = mMessages.get(getAdapterPosition()).getAudioLst().get(0);
//                Glide.with(_mContext)
//                        .load(R.drawable.loading)
//                        .into(imgBtnDownload);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                dialog.setMessage("Downloading...Please wait");
                dialog.show();
                if (imgBtnDownload.getTag() == "11") {
                    new DownloadTask().execute();
                } else {
                    openDocument("AUDIO");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void VideoTask() {
            try {
                url = mMessages.get(getAdapterPosition()).getVideoLst().get(0);
//                Glide.with(_mContext)
//                        .load(R.drawable.loading)
//                        .into(imgBtnDownload);
                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                dialog.setMessage("Downloading...Please wait");
                dialog.show();
                if (imgBtnDownload.getTag() == "13") {
                    new DownloadTask().execute();
                } else {
                    openDocument("VIDEO");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        public void switchContent(int id, Fragment fragment) {
            try {


                if (_mContext == null)
                    return;
                if (_mContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) _mContext;
                    Fragment frag = fragment;
                    mainActivity.switchContent(id, frag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void HorizontalListItemClick(int position) {

        }

        private class DownloadTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... arg0) {
                // do above Server call here
                Downloading();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("image")) {
                    openDocument("IMAGES");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("word")) {

                    imgBtnDownload.setImageResource(R.drawable.view_doc);
                    imgBtnDownload.setTag("4");
                    openDocument("DOCUMENTS");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("pdf")) {
                    imgBtnDownload.setImageResource(R.drawable.view_pdf);
                    imgBtnDownload.setTag("6");
                    openDocument("PDF");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("excel")) {
                    imgBtnDownload.setImageResource(R.drawable.view_excel);
                    imgBtnDownload.setTag("8");
                    openDocument("EXCEL");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("ppt")) {
                    imgBtnDownload.setImageResource(R.drawable.view_ppt);
                    imgBtnDownload.setTag("10");
                    openDocument("PPT");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("audio")) {
                    imgBtnDownload.setImageResource(R.drawable.view_audio);
                    imgBtnDownload.setTag("12");
                    openDocument("Audio");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("video")) {
                    imgBtnDownload.setImageResource(R.drawable.view_video);
                    imgBtnDownload.setTag("14");
                    openDocument("VIDEO");
                }
            }
        }

        public Bitmap Downloading() {
            try {
                InputStream in = null;

                try {
                    Log.i("URL", url);

                    URL strurl = new URL(url);//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) strurl.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                    c.connect();
                    //in = httpConn.getInputStream();
                    if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("image")) {
                        createDirectoryFIle("IMAGES");
                        in = new BufferedInputStream(strurl.openStream());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int n = in.read(buf);
                        while (n != -1) {
                            out.write(buf, 0, n);
                            n = in.read(buf);
                        }
                        out.close();
                        in.close();
                        response = out.toByteArray();

                        FileOutputStream fos = new FileOutputStream(apkStorage + "/" + fileName);
                        fos.write(response);
                        fos.close();
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("word")) {
                        createDirectoryFIle("DOCUMENTS");
                        fillStream(c);
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("pdf")) {
                        createDirectoryFIle("PDF");
                        fillStream(c);
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("excel")) {
                        createDirectoryFIle("EXCEL");
                        fillStream(c);
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("ppt")) {
                        createDirectoryFIle("PPT");
                        fillStream(c);
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("audio")) {
                        createDirectoryFIle("AUDIO");
                        fillStream(c);
                    } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("video")) {
                        createDirectoryFIle("VIDEO");
                        fillStream(c);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private void fillStream(HttpURLConnection c) {
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream inputStream = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                inputStream.close();
            } catch (Exception e) {

            }
        }

        private void createDirectoryFIle(String folderName) {
            try {
                apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "CHAT BOT/" + folderName);
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                }

                fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                outputFile = new File(apkStorage, fileName);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.d("File created", "created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void openDocument(String folderName) {
            try {
                // Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                String path = Environment.getExternalStorageDirectory() + "/" + "CHAT BOT/" + folderName + "/" + fileName;

                UpdateDB("1", path);
                if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("image")) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        bitFile = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imgBtnDownload.setImageBitmap(bitFile);
                        Drawable dr = new BitmapDrawable(_mContext.getResources(), bitFile);
                        imgBtnDownload.setImageDrawable(dr);
                        imgBtnDownload.setTag("2");

                    }
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("word")) {
                    OpenDocs(path, "application/msword");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("pdf")) {
                    OpenDocs(path, "application/pdf");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("excel")) {
                    OpenDocs(path, "application/vnd.ms-excel");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("ppt")) {
                    OpenDocs(path, "application/vnd.ms-powerpoint");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("audio")) {
                    dialog.dismiss();
                    OpenDocs(path, "audio/x-wav");
                } else if (mMessages.get(getAdapterPosition()).getTypeValues().equalsIgnoreCase("video")) {
                    dialog.dismiss();
                    OpenDocs(path, "video/*");
                }

                // custom message for the intent

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void UpdateDB(String isdownloaded, String path) {
            try {
                isFileDownloaded = isdownloaded;
                DBHelper dbHelper = new DBHelper(_mContext);
                clsChatDetail objclsChatDetail = new clsChatDetail();

                objclsChatDetail.setUserName(mMessages.get(getAdapterPosition()).getUsername());
                objclsChatDetail.setMessage(mMessages.get(getAdapterPosition()).getMessage());
                objclsChatDetail.setMessageType(mMessages.get(getAdapterPosition()).getTypeValues());
                objclsChatDetail.setIsLocationShared("0");
                objclsChatDetail.setLatitude("");
                objclsChatDetail.setLongitude("");
                objclsChatDetail.setResponseDateTime("");
                objclsChatDetail.setCreatedDateTime(Constants.df.format(Constants.cal.getTime()));
                objclsChatDetail.setIsFileDownloaded(isFileDownloaded);
                objclsChatDetail.setFilePath(path);
                dbHelper.updateFields(isFileDownloaded, path, mMessages.get(getAdapterPosition()).getTypeValues());
                //dbHelper.updateClsChatDetail(objclsChatDetail);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void OpenDocs(String path, String doctype) {
            try {
                File file = new File(path);
                final Uri data = FileProvider.getUriForFile(_mContext.getApplicationContext(), "myprovider", file);
                _mContext.grantUriPermission(_mContext.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(data, doctype)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                _mContext.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }


    public interface ListItemListener {
        void ListItemClick(int position);
    }
}
