package com.endpoint.Jack.activities_fragments.activity_chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.activities_fragments.telr_activity.TelrActivity;
import com.endpoint.Jack.adapters.ChatAdapter;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.BillDataModel;
import com.endpoint.Jack.models.ChatUserModel;
import com.endpoint.Jack.models.MessageDataModel;
import com.endpoint.Jack.models.MessageModel;
import com.endpoint.Jack.models.OrderModel;
import com.endpoint.Jack.models.PayPalLinkModel;
import com.endpoint.Jack.models.SocialMediaModel;
import com.endpoint.Jack.models.TypingModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
//import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView tv_name, tv_order_num, recordDuration;
    private CircleImageView image, image_chat_user;
    private ImageView image_send, image_back, image_upload_image, imageMic, imageDelete, imagePlay;
   // private GifImageView images;
    private Handler handler;
    private Runnable runnable;
    private EditText edt_msg_content;
    private ConstraintLayout cons_typing;
    private LinearLayout ll_back, ll_bill;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProgressBar progBar, progBarLoadMore;
    private List<MessageModel> messageModelList;
    private ChatAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private ChatUserModel chatUserModel;
    private String current_language;
    private Preferences preferences;
    private boolean canSendTyping = true;
    private boolean from = true;
    private MediaPlayer mp;
    private final String audio_perm = Manifest.permission.RECORD_AUDIO;
    private final int write_req = 100;
private TextView tv_title;
    private boolean isPermissionGranted = false;
    private MediaPlayer mediaPlayer;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG1 = 1, IMG2 = 2;
    private Uri imgUri = null;
    private int which_image_upload_selected = 0;
    private MediaRecorder recorder;
    private String path;
    private SeekBar seekBar;
    private CardView cardView;
private String bill_amount;
    private int network_per;
    private double network_per_totla;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base, Language_Helper.getLanguage(base)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up);

        setContentView(R.layout.activity_chat);
        initView();

        checkWritePermission();

        getDataFromIntent();
        getOrder();
        getSocialMedia();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            chatUserModel = (ChatUserModel) intent.getSerializableExtra("data");
            preferences.saveChatUserData(this, chatUserModel);
            UpdateUI(chatUserModel);
            if (intent.hasExtra("from")) {
                from = true;
            } else {
                from = false;
            }
        }
    }

    private void initView() {
        EventBus.getDefault().register(this);
        messageModelList = new ArrayList<>();
        Paper.init(this);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
tv_title=findViewById(R.id.tvTitle);
        image_back = findViewById(R.id.image_back);
        imageMic = findViewById(R.id.imageMic);
        imagePlay = findViewById(R.id.imagePlay);
        imageDelete = findViewById(R.id.imageDelete);
        recordDuration = findViewById(R.id.recordDuration);
        cardView = findViewById(R.id.cardView);
        //images = findViewById(R.id.images);
        seekBar = findViewById(R.id.seekBar);
        if (current_language.equals("ar")) {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        } else {
            image_back.setImageResource(R.drawable.ic_left_arrow);

        }


        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        progBarLoadMore = findViewById(R.id.progBarLoadMore);
        progBarLoadMore.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        tv_name = findViewById(R.id.tv_name);
        tv_order_num = findViewById(R.id.tv_order_num);
        image_upload_image = findViewById(R.id.image_upload_image);

        image = findViewById(R.id.image);
        //image_call =findViewById(R.id.image_call);
        image_send = findViewById(R.id.image_send);
        edt_msg_content = findViewById(R.id.edt_msg_content);
        ll_back = findViewById(R.id.ll_back);
        ll_bill = findViewById(R.id.ll_bill);

        cons_typing = findViewById(R.id.cons_typing);
        image_chat_user = findViewById(R.id.image_chat_user);


        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recView.setLayoutManager(manager);
        recView.setNestedScrollingEnabled(true);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (total_item >= 20 && lastVisibleItemPos >= (total_item - 5) && !isLoading) {
                        progBarLoadMore.setVisibility(View.VISIBLE);
                        int next_page = current_page + 1;
                        isLoading = true;
                        loadMore(next_page);
                    }
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {

                    mediaPlayer.seekTo(i);


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Back();
            }
        });

       /* image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+chatUserModel.getPhone()));
                startActivity(intent);
            }
        });*/

        image_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which_image_upload_selected = 2;
                CreateImageAlertDialog();
            }
        });

        ll_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)){
                which_image_upload_selected = 1;
                CreateImageAlertDialog();}
                else {
pay();
                }
            }
        });
        imagePlay.setOnClickListener(view -> {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                recordDuration.setText(getDuration(mediaPlayer.getCurrentPosition()));
                mediaPlayer.pause();
                imagePlay.setImageResource(R.drawable.ic_play);

            } else {

                if (mediaPlayer != null) {
                    imagePlay.setImageResource(R.drawable.ic_pause);

                    mediaPlayer.start();
                    updateProgress();


                }
            }

        });
        imageDelete.setOnClickListener(view -> {
            cardView.setVisibility(View.GONE);
            deleteFile();
            // model.setAudio_name("");
            //  model.setSound_path("");
            //  binding.setModel(model);
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
        });
        imageMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isPermissionGranted) {
                        if (recorder != null) {
                            recorder.release();
                            recorder = null;

                        }
                        initRecorder();

                    } else {
                        Toast.makeText(ChatActivity.this, "Cannot access mic", Toast.LENGTH_SHORT).show();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isPermissionGranted) {

                        try {
                            recorder.stop();
                            //  Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                            mediaPlayer = null;
                            initAudio();

                        } catch (Exception e) {
                            // binding.imageWave.setVisibility(View.GONE);
                        }


                    } else {
                        Toast.makeText(ChatActivity.this, "Cannot access mic", Toast.LENGTH_SHORT).show();
                    }


                }
                return true;
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_msg_content.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    edt_msg_content.setText("");
                    canSendTyping = true;
                    updateTyingState(Tags.END_TYPING);
                    SendMessage(msg, Tags.MESSAGE_TEXT);
                } else {
                    if (path != null) {
                        sendmeassgeWithSound();
                    }
                }
            }
        });

        edt_msg_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String msg = edt_msg_content.getText().toString();
                if (msg.length() > 0) {
                    if (canSendTyping) {
                        canSendTyping = false;
                        updateTyingState(Tags.START_TYPING);
                    }
                } else {
                    canSendTyping = true;
                    updateTyingState(Tags.END_TYPING);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void deleteFile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private String getDuration(long duration) {

        String total_duration = "00:00";

        if (mediaPlayer != null) {
            total_duration = String.format(Locale.ENGLISH, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );


        }

        return total_duration;

    }

    private void sendmeassgeWithSound() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody room_part = Common.getRequestBodyText(chatUserModel.getRoom_id());
        RequestBody from_user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody to_user_id_part = Common.getRequestBodyText(chatUserModel.getId());

        RequestBody user_msg_part = Common.getRequestBodyText("0");
        RequestBody user_msg_type_part = Common.getRequestBodyText("3");
        MultipartBody.Part sound_file_part = Common.getMultiPartSound(path, "file");

        Api.getService(Tags.base_url)
                .sendMessageWithImage(room_part, from_user_id_part, to_user_id_part, user_msg_part, user_msg_type_part, sound_file_part)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        dialog.dismiss();
                        Log.e("datttaa", response.body() + "_");
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (adapter == null) {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
                                recView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);
                                        setdata();

                                    }
                                }, 100);
                            } else {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size() - 1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);
                                        setdata();

                                    }
                                }, 100);
                            }

                        } else {

                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void setdata() {
        cardView.setVisibility(View.GONE);
        deleteFile();
        // model.setAudio_name("");
        //  model.setSound_path("");
        //  binding.setModel(model);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }

    private void checkWritePermission() {

        if (ContextCompat.checkSelfPermission(this, audio_perm) != PackageManager.PERMISSION_GRANTED) {


            isPermissionGranted = false;

            ActivityCompat.requestPermissions(this, new String[]{write_permission, audio_perm}, write_req);


        } else {
            isPermissionGranted = true;
        }
    }

    private void initAudio() {
        try {


            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(100.0f, 100.0f);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            recordDuration.setText(getDuration(mediaPlayer.getDuration()));

            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                cardView.setVisibility(View.VISIBLE);
                seekBar.setMax(mediaPlayer.getDuration());
                imagePlay.setImageResource(R.drawable.ic_play);
            });

            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                recordDuration.setText(getDuration(mediaPlayer.getDuration()));
                imagePlay.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                handler.removeCallbacks(runnable);

            });
            //images.setVisibility(View.GONE);


        } catch (IOException e) {
            Log.e("eeeex", e.getMessage());
            mediaPlayer.release();
            mediaPlayer = null;
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
            cardView.setVisibility(View.GONE);

        }
    }

    private void updateProgress() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        recordDuration.setText(getDuration(mediaPlayer.getCurrentPosition()));
        handler = new Handler();
        runnable = this::updateProgress;
        handler.postDelayed(runnable, 1000);


    }

    ///////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToTyping(TypingModel typingModel) {
        Log.e("", typingModel.getTyping_value());
        if (typingModel.getTyping_value().equals("1")) {
            cons_typing.setVisibility(View.VISIBLE);

            new MyAsyncTask().execute();

        } else if (typingModel.getTyping_value().equals("2")) {

            cons_typing.setVisibility(View.GONE);
            if (mp != null) {
                mp.release();
            }
        }
    }

    private void initRecorder() {
      //  images.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        isPermissionGranted = true;
        String audioName = "AUD" + calendar.getTimeInMillis() + ".mp3";

        File folder_done = new File(Tags.local_folder_path);

        if (!folder_done.exists()) {
            folder_done.mkdir();
        }

        path = folder_done.getAbsolutePath() + "/" + audioName;


        recorder = new MediaRecorder();


        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(1);
        recorder.setOutputFile(path);
        try {
            recorder.prepare();
            recorder.start();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Failed", "Failed");


            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }


        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToNewMessage(MessageModel messageModel) {
        if(messageModel.getBill_step()!=null){
            if(messageModel.getBill_step().equals("bill_paid")){
                ll_bill.setVisibility(View.GONE);

            }
        else if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)&&messageModel.getBill_step().equals("not_attach")) {
            ll_bill.setVisibility(View.GONE);
        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)&&messageModel.getBill_step().equals("not_attach"))
        {
            ll_bill.setVisibility(View.VISIBLE);

        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)&&!messageModel.getBill_step().equals("not_attach"))
        {
            ll_bill.setVisibility(View.GONE);

        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)&&!messageModel.getBill_step().equals("not_attach"))
        {
            tv_title.setText(getResources().getString(R.string.pay));

            ll_bill.setVisibility(View.VISIBLE);

        }}
        if(messageModel.getTotal_cost()!=null){
            chatUserModel.setTotla_cost(messageModel.getTotal_cost());
        }
        if (adapter == null) {
            messageModelList.add(messageModel);
            adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
            recView.setAdapter(adapter);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recView.scrollToPosition(messageModelList.size() - 1);

                }
            }, 100);

        } else {
            messageModelList.add(messageModel);
            adapter.notifyItemInserted(messageModelList.size() - 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recView.scrollToPosition(messageModelList.size() - 1);

                }
            }, 100);

        }
    }

    ///////////////////////////////////////////////
    private void updateTyingState(int typingState) {

        Api.getService(Tags.base_url)
                .typing(chatUserModel.getRoom_id(), userModel.getData().getUser_id(), chatUserModel.getId(), typingState)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                        if (response.isSuccessful()) {
                            Log.e("success typing", "true");
                        } else {
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void UpdateUI(ChatUserModel chatUserModel) {
        if(chatUserModel.getBill_step().equals("bill_paid")){
            ll_bill.setVisibility(View.GONE);

        }

        else if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)&&chatUserModel.getBill_step().equals("not_attach")) {
            ll_bill.setVisibility(View.GONE);
        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)&&chatUserModel.getBill_step().equals("not_attach"))
        {
            ll_bill.setVisibility(View.VISIBLE);

        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)&&!chatUserModel.getBill_step().equals("not_attach"))
        {
            ll_bill.setVisibility(View.GONE);

        }
        else if(userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)&&!chatUserModel.getBill_step().equals("not_attach"))
        {
            ll_bill.setVisibility(View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.pay));

        }
        preferences.saveChatUserData(this, chatUserModel);
        tv_name.setText(chatUserModel.getName());
        tv_order_num.setText(getString(R.string.order_number) + "#" + chatUserModel.getOrder_id());
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL + chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image);
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL + chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image_chat_user);
        getChatMessages();
    }

    private void SendMessage(String msg, String msg_type) {

        if (msg_type.equals(Tags.MESSAGE_TEXT)) {
            sendTextMessage(msg);
        } else {
            sendTextMessageWithImage(msg);
        }

    }

    private void sendTextMessageWithImage(String msg) {
        RequestBody room_part = Common.getRequestBodyText(chatUserModel.getRoom_id());
        RequestBody from_user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody to_user_id_part = Common.getRequestBodyText(chatUserModel.getId());

        RequestBody user_msg_part = Common.getRequestBodyText(msg);
        RequestBody user_msg_type_part = Common.getRequestBodyText(Tags.MESSAGE_IMAGE_TEXT);
        MultipartBody.Part image_part = Common.getMultiPart(this, imgUri, "file");

        Api.getService(Tags.base_url)
                .sendMessageWithImage(room_part, from_user_id_part, to_user_id_part, user_msg_part, user_msg_type_part, image_part)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                        Log.e("llllsss", response.code() + "" );
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (adapter == null) {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
                                recView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            } else {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size() - 1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            }

                        } else {

                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }
    private void sendلاbillMessageWithImage(String msg) {
        RequestBody room_part = Common.getRequestBodyText(chatUserModel.getRoom_id());
        RequestBody from_user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody to_user_id_part = Common.getRequestBodyText(chatUserModel.getId());

        RequestBody user_msg_part = Common.getRequestBodyText(msg);
        RequestBody user_msg_type_part = Common.getRequestBodyText(Tags.MESSAGE_IMAGE_TEXT);
        RequestBody bill_type_part = Common.getRequestBodyText(bill_amount);
        RequestBody network_per_part = Common.getRequestBodyText(network_per_totla+"");

        RequestBody order_type_part = Common.getRequestBodyText(chatUserModel.getOrder_id());

        MultipartBody.Part image_part = Common.getMultiPart(this, imgUri, "file");

        Api.getService(Tags.base_url)
                .sendbillWithImage(room_part, from_user_id_part, to_user_id_part, user_msg_part, user_msg_type_part,bill_type_part,order_type_part,network_per_part,image_part)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                        Log.e("datttaa", response.body() + "_");
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            ll_bill.setVisibility(View.GONE);
                           // chatUserModel.setBill_step("bill_attach");
                            if (adapter == null) {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
                                recView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            } else {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size() - 1);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            }
getOrder();
                        } else {

                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void sendTextMessage(String msg) {
        Api.getService(Tags.base_url)
                .sendMessage(chatUserModel.getRoom_id(), userModel.getData().getUser_id(), chatUserModel.getId(), msg, Tags.MESSAGE_TEXT)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        progBar.setVisibility(View.GONE);
                        Log.e("llllsss", response.code() + "");
                        if (response.isSuccessful()) {
                            if (adapter == null) {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
                                adapter.notifyDataSetChanged();
                                recView.setAdapter(adapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            } else {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size() - 1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            }

                        } else {

                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Errorsss", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void getChatMessages() {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(), 1)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getData().size() > 0) {
                                messageModelList.addAll(response.body().getData());
                                adapter = new ChatAdapter(messageModelList, userModel.getData().getUser_id(), chatUserModel.getImage(), ChatActivity.this);
                                recView.setAdapter(adapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size() - 1);

                                    }
                                }, 100);
                            }
                        } else {

                            Toast.makeText(ChatActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void loadMore(int next_page) {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(), next_page)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        if (response.isSuccessful()) {
                            progBarLoadMore.setVisibility(View.GONE);

                            if (response.body() != null && response.body().getData().size() > 0) {


                                messageModelList.addAll(0, response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();
                                adapter.notifyItemRangeInserted(0, response.body().getData().size() - 1);

                            }
                            isLoading = false;

                        } else {
                            isLoading = false;
                            progBarLoadMore.setVisibility(View.VISIBLE);


                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            isLoading = false;
                            progBarLoadMore.setVisibility(View.VISIBLE);
                            Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void CreateImageAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_image, null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_CameraPermission();

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_ReadPermission();


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }

    private void Check_ReadPermission() {
        if (ContextCompat.checkSelfPermission(this, read_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{read_permission}, IMG1);
        } else {
            SelectImage(IMG1);
        }
    }

    private void Check_CameraPermission() {
        if (ContextCompat.checkSelfPermission(this, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, IMG2);
        } else {
            SelectImage(IMG2);

        }

    }

    private void SelectImage(int type) {

        Intent intent = new Intent();

        if (type == IMG1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, IMG1);

        } else if (type == IMG2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMG2);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG1);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMG2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(IMG2);

                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == write_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.getData();
            if (which_image_upload_selected == 1) {
                CreateBillAlertDialog(imgUri);

            } else if (which_image_upload_selected == 2) {
                SendMessage("0", Tags.MESSAGE_IMAGE_TEXT);


            }

        } else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgUri = getUriFromBitmap(bitmap);
            if (imgUri != null) {
                if (which_image_upload_selected == 1) {
                    CreateBillAlertDialog(imgUri);

                } else if (which_image_upload_selected == 2) {
                    SendMessage("0", Tags.MESSAGE_IMAGE_TEXT);

                }

            }

        }
        else if(requestCode==100){
            ll_bill.setVisibility(View.GONE);
            BillDataModel.setBill_step("bill_paid");
            BillDataModel.setTotla_Cost(chatUserModel.getTotla_cost());
        }

    }

    public void CreateBillAlertDialog(Uri uri) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bill_photo, null);
        ImageView image = view.findViewById(R.id.image);
        final EditText edt_order_cost = view.findViewById(R.id.edt_order_cost);

        File file = new File(Common.getImagePath(this, uri));
        Picasso.with(this).load(file).fit().into(image);

        Button btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost = edt_order_cost.getText().toString().trim();
                Currency currency = Currency.getInstance(new Locale(current_language, userModel.getData().getUser_country()));
                if (!TextUtils.isEmpty(cost)) {
                    dialog.dismiss();
                    Common.CloseKeyBoard(ChatActivity.this, edt_order_cost);

                    double total = Double.parseDouble(cost) + Double.parseDouble(chatUserModel.getOffer_cost());
                     network_per_totla=(total*network_per)/100;
                     network_per_totla=0;
                  //   total+=network_per_totla;
                    String msg = "تكلفة المشتريات: " + cost + " " + currency.getSymbol() + "\n" + "تكلفة التوصيل شامل ضريبة القيمه المضافه: " + chatUserModel.getOffer_cost()  + currency.getSymbol() +"\n" + "المجموع الكلي : " + total + " " + currency.getSymbol() ;
                    bill_amount=total+"";
                    sendلاbillMessageWithImage(msg);

                }
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    private void Back() {
        BillDataModel.setBill_step(chatUserModel.getBill_step());
        BillDataModel.setTotla_Cost(chatUserModel.getTotla_cost());
        if (from) {
            finish();
        } else {
            Intent intent = new Intent(ChatActivity.this, ClientHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        preferences.clearChatUserData(ChatActivity.this);
        if (mp != null) {
            mp.release();
        }

    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mp = MediaPlayer.create(ChatActivity.this, R.raw.typing);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mp.start();
            mp.setLooping(false);
            return null;
        }
    }
    public void pay() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .getbillpay(userModel.getData().getUser_id(),chatUserModel.getOrder_id(),chatUserModel.getTotla_cost())
                    .enqueue(new Callback<PayPalLinkModel>() {
                        @Override
                        public void onResponse(Call<PayPalLinkModel> call, Response<PayPalLinkModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body()!=null)
                                {
                                    // Log.e("body",response.body().getData()+"______");
                                    Intent intent = new Intent(ChatActivity.this, TelrActivity.class);
                                    intent.putExtra("data",response.body());
                                    startActivityForResult(intent,100);



                                }else
                                {
                                    try {
                                        Log.e("jhhhhhh",response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(ChatActivity.this,R.string.failed, Toast.LENGTH_SHORT).show();
                                }


                            }else
                            {
                                try {
                                    Log.e("dlldldl",response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 500) {
                                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PayPalLinkModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                        Toast.makeText(ChatActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(ChatActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }

    }
    private void getSocialMedia() {

//        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
//        dialog.setCancelable(false);
//        dialog.show();
        Api.getService(Tags.base_url)
                .getSocialMedia()
                .enqueue(new Callback<SocialMediaModel>() {
                    @Override
                    public void onResponse(Call<SocialMediaModel> call, Response<SocialMediaModel> response) {
                     //   dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                           network_per=response.body().getNetwork_per();


                        } else {
                     //       dialog.dismiss();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SocialMediaModel> call, Throwable t) {
                        try {
                       //     dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }
    private void getOrder() {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getClientOrders(chatUserModel.getOrder_id())
                .enqueue(new Callback<OrderModel>() {
                    @Override
                    public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
update(response.body());


                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void update(OrderModel order) {
        chatUserModel = new ChatUserModel(order.getDriver_user_full_name(),order.getDriver_user_image(),order.getDriver_id(),order.getRoom_id_fk(),order.getDriver_user_phone_code(),order.getDriver_user_phone(),order.getOrder_id(),order.getDriver_offer(),order.getBill_step(),order.getBill_amount());

        UpdateUI(chatUserModel);

    }

}
