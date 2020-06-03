package com.endpoint.Jack.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.models.MessageModel;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ChatAdapter extends RecyclerView.Adapter {

    private final int ITEM_MESSAGE_LEFT = 1;
    private final int ITEM_MESSAGE_RIGHT = 2;
    private final int ITEM_MESSAGE_IMAGE_LEFT = 3;
    private final int ITEM_MESSAGE_IMAGE_RIGHT = 4;
    private final int ITEM_MESSAGE_ٍSound_LEFT = 5;
    private final int ITEM_MESSAGE_Sound_RIGHT = 6;
    private List<MessageModel> messageModelList;
    private String current_user_id;
    private String chat_user_image;
    private Context context;
    private int pos = -1;

    public ChatAdapter(List<MessageModel> messageModelList, String current_user_id, String chat_user_image, Context context) {
        this.messageModelList = messageModelList;
        this.current_user_id = current_user_id;
        this.chat_user_image = chat_user_image;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_MESSAGE_LEFT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_left_row, parent, false);
            return new MsgLeftHolder(view);

        } else if (viewType == ITEM_MESSAGE_RIGHT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_right_row, parent, false);
            return new MsgRightHolder(view);

        } else if (viewType == ITEM_MESSAGE_IMAGE_LEFT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_image_left_row, parent, false);
            return new ImageLeftHolder(view);

        } else if (viewType == ITEM_MESSAGE_ٍSound_LEFT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_audio_left_row, parent, false);
            return new SoundLeftHolder(view);

        } else if (viewType == ITEM_MESSAGE_Sound_RIGHT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_audio_right_row, parent, false);
            return new SoundRightHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_image_right_row, parent, false);
            return new ImageRightHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MsgLeftHolder) {

            Log.e("1", "1");

            MsgLeftHolder msgLeftHolder = (MsgLeftHolder) holder;
            MessageModel messageModel = messageModelList.get(msgLeftHolder.getAdapterPosition());

            msgLeftHolder.BindData(messageModel);

        } else if (holder instanceof MsgRightHolder) {
            MsgRightHolder msgRightHolder = (MsgRightHolder) holder;
            MessageModel messageModel = messageModelList.get(msgRightHolder.getAdapterPosition());

            msgRightHolder.BindData(messageModel);
            Log.e("2", "2");

        }
        else if (holder instanceof ImageLeftHolder) {
            Log.e("3", "3");

            ImageLeftHolder imageLeftHolder = (ImageLeftHolder) holder;
            MessageModel messageModel = messageModelList.get(imageLeftHolder.getAdapterPosition());

            imageLeftHolder.BindData(messageModel);

        }
        else if (holder instanceof ImageRightHolder) {
            Log.e("4", "4");
            ImageRightHolder imageRightHolder = (ImageRightHolder) holder;
            MessageModel messageModel = messageModelList.get(imageRightHolder.getAdapterPosition());

            imageRightHolder.BindData(messageModel);
        }
        else if (holder instanceof SoundRightHolder) {
            Log.e("3", "3");

            SoundRightHolder imageLeftHolder = (SoundRightHolder) holder;
            MessageModel messageModel = messageModelList.get(imageLeftHolder.getAdapterPosition());
            imageLeftHolder.imagePlay.setOnClickListener(view -> {
                pos = position;
                notifyDataSetChanged();

            });
            imageLeftHolder.BindData(messageModel);
            if (pos == position) {
                if (imageLeftHolder.mediaPlayer != null && imageLeftHolder.mediaPlayer.isPlaying()) {
                    imageLeftHolder.mediaPlayer.pause();
                    imageLeftHolder.imagePlay.setImageResource(R.drawable.ic_play);

                } else {

                    if (imageLeftHolder.mediaPlayer != null) {
                        imageLeftHolder.imagePlay.setImageResource(R.drawable.ic_pause);

                        imageLeftHolder.mediaPlayer.start();
                        imageLeftHolder.updateProgress();


                    }
                }
            } else {
                if (imageLeftHolder.mediaPlayer != null && imageLeftHolder.mediaPlayer.isPlaying()) {
                    imageLeftHolder.mediaPlayer.pause();
                    imageLeftHolder.imagePlay.setImageResource(R.drawable.ic_play);

                }
            }
        }
        else if (holder instanceof SoundLeftHolder) {
            Log.e("4", "4");
            SoundLeftHolder imageRightHolder = (SoundLeftHolder) holder;
            MessageModel messageModel = messageModelList.get(imageRightHolder.getAdapterPosition());
            imageRightHolder.imagePlay.setOnClickListener(view -> {

                pos = position;
                notifyDataSetChanged();
            });
            imageRightHolder.BindData(messageModel);
            if (pos == position) {
                if (imageRightHolder.mediaPlayer != null && imageRightHolder.mediaPlayer.isPlaying()) {
                    imageRightHolder.mediaPlayer.pause();
                    imageRightHolder.imagePlay.setImageResource(R.drawable.ic_play);

                } else {

                    if (imageRightHolder.mediaPlayer != null) {
                        imageRightHolder.imagePlay.setImageResource(R.drawable.ic_pause);

                        imageRightHolder.mediaPlayer.start();
                        imageRightHolder.updateProgress();


                    }
                }
            } else {
                if (imageRightHolder.mediaPlayer != null && imageRightHolder.mediaPlayer.isPlaying()) {
                    imageRightHolder.mediaPlayer.pause();
                    imageRightHolder.imagePlay.setImageResource(R.drawable.ic_play);

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MsgLeftHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tv_message_content, tv_time;

        public MsgLeftHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);

        }


        public void BindData(MessageModel messageModel) {

            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + chat_user_image)).placeholder(R.drawable.logo_only).fit().priority(Picasso.Priority.HIGH).into(image);
            tv_message_content.setText(messageModel.getMessage());

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
        }
    }

    public class MsgRightHolder extends RecyclerView.ViewHolder {
        private TextView tv_message_content, tv_time;

        public MsgRightHolder(View itemView) {
            super(itemView);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

        public void BindData(MessageModel messageModel) {

            tv_message_content.setText(messageModel.getMessage());

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
        }
    }


    public class ImageLeftHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private ImageView image_bill;
        private TextView tv_message_content, tv_time;

        public ImageLeftHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            image_bill = itemView.findViewById(R.id.image_bill);
        }


        public void BindData(final MessageModel messageModel) {

            if (!messageModel.getMessage().equals("0")) {
                tv_message_content.setText(messageModel.getMessage());
            }
            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);

            //Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+messageModel.getFile())).resizeDimen(R.dimen.chat_image_width,R.dimen.chat_image_height).into(image_bill);
            new MyAsyncTask().execute(messageModel.getFile());
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + chat_user_image)).placeholder(R.drawable.logo_only).fit().priority(Picasso.Priority.HIGH).into(image);

        }


        public class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + strings[0])).priority(Picasso.Priority.HIGH).transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int size = Math.min(source.getWidth(), source.getHeight());
                            int x = (source.getWidth() - size) / 2;
                            int y = (source.getHeight() - size) / 2;
                            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
                            if (result != source) {
                                source.recycle();
                            }
                            return result;

                        }

                        @Override
                        public String key() {
                            return "square()";
                        }
                    }).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Log.e("nknk", "nknk");
                image_bill.setImageBitmap(bitmap);
            }
        }

    }


    public class ImageRightHolder extends RecyclerView.ViewHolder {
        private TextView tv_message_content, tv_time;
        private ImageView image_bill;

        public ImageRightHolder(View itemView) {
            super(itemView);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            image_bill = itemView.findViewById(R.id.image_bill);

        }

        public void BindData(final MessageModel messageModel) {

            if (!messageModel.getMessage().equals("0")) {
                tv_message_content.setText(messageModel.getMessage());
            }

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);


            new MyAsyncTask().execute(messageModel.getFile());


        }

        public class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + strings[0])).priority(Picasso.Priority.HIGH).transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int size = Math.min(source.getWidth(), source.getHeight());
                            int x = (source.getWidth() - size) / 2;
                            int y = (source.getHeight() - size) / 2;
                            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
                            if (result != source) {
                                source.recycle();
                            }
                            return result;

                        }

                        @Override
                        public String key() {
                            return "square()";
                        }
                    }).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                image_bill.setImageBitmap(bitmap);
            }
        }

    }

    public class SoundRightHolder extends RecyclerView.ViewHolder {
        private TextView recordDuration, tv_time;
        private ImageView imagePlay;
        private SeekBar seekBar;
        private MediaPlayer mediaPlayer;
        private Handler handler;
        private Runnable runnable;

        public SoundRightHolder(View itemView) {
            super(itemView);
            imagePlay = itemView.findViewById(R.id.imagePlay);
            recordDuration = itemView.findViewById(R.id.recordDuration);
            tv_time = itemView.findViewById(R.id.tv_time);

            seekBar = itemView.findViewById(R.id.seekBar);

        }

        private void initAudio(MessageModel messageModel) {
            try {


                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(Tags.IMAGE_URL + messageModel.getFile());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(100.0f, 100.0f);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();

                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    seekBar.setMax(mediaPlayer.getDuration());
                    imagePlay.setImageResource(R.drawable.ic_play);
                });

                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    imagePlay.setImageResource(R.drawable.ic_play);
                    seekBar.setProgress(0);
                    handler.removeCallbacks(runnable);

                });

            } catch (IOException e) {
                Log.e("eeeex", e.getMessage());
                mediaPlayer.release();
                mediaPlayer = null;
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }

            }
        }

        private void updateProgress() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler = new Handler();
            runnable = this::updateProgress;
            handler.postDelayed(runnable, 1000);


        }

        public void BindData(final MessageModel messageModel) {


            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
            initAudio(messageModel);


        }


    }

    public class SoundLeftHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView recordDuration, tv_time;
        private ImageView imagePlay;
        private SeekBar seekBar;
        private MediaPlayer mediaPlayer;
        private Handler handler;
        private Runnable runnable;

        public SoundLeftHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            imagePlay = itemView.findViewById(R.id.imagePlay);
            recordDuration = itemView.findViewById(R.id.recordDuration);
            tv_time = itemView.findViewById(R.id.tv_time);

            seekBar = itemView.findViewById(R.id.seekBar);
        }

        private void initAudio(MessageModel messageModel) {
            try {


                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(Tags.IMAGE_URL + messageModel.getFile());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(100.0f, 100.0f);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();

                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    seekBar.setMax(mediaPlayer.getDuration());
                    imagePlay.setImageResource(R.drawable.ic_play);
                });

                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    imagePlay.setImageResource(R.drawable.ic_play);
                    seekBar.setProgress(0);
                    handler.removeCallbacks(runnable);

                });

            } catch (IOException e) {
                Log.e("eeeex", e.getMessage());
                mediaPlayer.release();
                mediaPlayer = null;
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }

            }
        }

        private void updateProgress() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler = new Handler();
            runnable = this::updateProgress;
            handler.postDelayed(runnable, 1000);


        }

        public void BindData(final MessageModel messageModel) {


            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
            initAudio(messageModel);
            //Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+messageModel.getFile())).resizeDimen(R.dimen.chat_image_width,R.dimen.chat_image_height).into(image_bill);

        }


    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = messageModelList.get(position);
        Log.e("type", messageModel.getMessage_type() + "_");
        if (messageModel.getMessage_type().equals(Tags.MESSAGE_TEXT)) {
            if (messageModel.getTo_user().equals(current_user_id)) {
                return ITEM_MESSAGE_LEFT;
            } else {
                return ITEM_MESSAGE_RIGHT;
            }
        } else if (messageModel.getMessage_type().equals("3")) {
            if (messageModel.getTo_user().equals(current_user_id)) {
                return ITEM_MESSAGE_ٍSound_LEFT;
            } else {
                return ITEM_MESSAGE_Sound_RIGHT;
            }
        } else {
            if (messageModel.getTo_user().equals(current_user_id)) {
                return ITEM_MESSAGE_IMAGE_LEFT;
            } else {
                Log.e("v", "ggg");
                return ITEM_MESSAGE_IMAGE_RIGHT;
            }
        }


    }


}
