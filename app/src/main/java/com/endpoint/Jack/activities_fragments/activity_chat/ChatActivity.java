package com.endpoint.Jack.activities_fragments.activity_chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.ChatAdapter;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.ChatUserModel;
import com.endpoint.Jack.models.MessageDataModel;
import com.endpoint.Jack.models.MessageModel;
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
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView tv_name,tv_order_num;
    private CircleImageView image,image_chat_user;
    private ImageView image_send,image_back,image_upload_image;
    private EditText edt_msg_content;
    private ConstraintLayout cons_typing;
    private LinearLayout ll_back,ll_bill;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProgressBar progBar,progBarLoadMore;
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

    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG1 = 1,IMG2=2;
    private Uri imgUri = null;
    private int which_image_upload_selected = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up);

        setContentView(R.layout.activity_chat);
        initView();
        getDataFromIntent();
    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            chatUserModel = (ChatUserModel) intent.getSerializableExtra("data");
            preferences.saveChatUserData(this,chatUserModel);
            UpdateUI(chatUserModel);
            if (intent.hasExtra("from"))
            {
                from = true;
            }else
                {
                    from = false;
                }
        }
    }

    private void initView()
    {
        EventBus.getDefault().register(this);
        messageModelList = new ArrayList<>();
        Paper.init(this);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        image_back = findViewById(R.id.image_back);
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
        image_send =findViewById(R.id.image_send);
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
                if (dy<0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (total_item >= 20 && lastVisibleItemPos >= (total_item-5)&&!isLoading)
                    {
                        progBarLoadMore.setVisibility(View.VISIBLE);
                        int next_page = current_page+1;
                        isLoading = true;
                        loadMore(next_page);
                    }
                }
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
                which_image_upload_selected =1;
                CreateImageAlertDialog();
            }
        });


        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_msg_content.getText().toString().trim();
                if (!TextUtils.isEmpty(msg))
                {
                    edt_msg_content.setText("");
                    canSendTyping = true;
                    updateTyingState(Tags.END_TYPING);
                    SendMessage(msg,Tags.MESSAGE_TEXT);
                }
            }
        });

        edt_msg_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String msg = edt_msg_content.getText().toString();
                if (msg.length()>0)
                {
                    if (canSendTyping)
                    {
                        canSendTyping = false;
                        updateTyingState(Tags.START_TYPING);
                    }
                }else
                {
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

    ///////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToTyping(TypingModel typingModel)
    {
        Log.e("",typingModel.getTyping_value());
        if (typingModel.getTyping_value().equals("1"))
        {
            cons_typing.setVisibility(View.VISIBLE);

            new MyAsyncTask().execute();

        }else if (typingModel.getTyping_value().equals("2"))
        {

            cons_typing.setVisibility(View.GONE);
            if (mp!=null)
            {
                mp.release();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToNewMessage(MessageModel messageModel)
    {
        if (adapter==null)
        {
            messageModelList.add(messageModel);
            adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
            recView.setAdapter(adapter);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recView.scrollToPosition(messageModelList.size()-1);

                }
            },100);

        }else
            {
                messageModelList.add(messageModel);
                adapter.notifyItemInserted(messageModelList.size()-1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recView.scrollToPosition(messageModelList.size()-1);

                    }
                },100);

            }
    }

    ///////////////////////////////////////////////
    private void updateTyingState(int typingState)
    {

        Api.getService(Tags.base_url)
                .typing(chatUserModel.getRoom_id(),userModel.getData().getUser_id(),chatUserModel.getId(),typingState)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                        if (response.isSuccessful())
                        {
                            Log.e("success typing","true");
                        }else
                        {
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void UpdateUI(ChatUserModel chatUserModel)
    {
        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            ll_bill.setVisibility(View.GONE);
        }
        preferences.saveChatUserData(this,chatUserModel);
        tv_name.setText(chatUserModel.getName());
        tv_order_num.setText(getString(R.string.order_number)+"#"+chatUserModel.getOrder_id());
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image);
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image_chat_user);
        getChatMessages();
    }
    private void SendMessage(String msg,String msg_type)
    {

        if (msg_type.equals(Tags.MESSAGE_TEXT))
        {
           sendTextMessage(msg);
        }else
            {
                sendTextMessageWithImage(msg);
            }

    }
    private void sendTextMessageWithImage(String msg)
    {
        RequestBody room_part = Common.getRequestBodyText(chatUserModel.getRoom_id());
        RequestBody from_user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody to_user_id_part = Common.getRequestBodyText(chatUserModel.getId());

        RequestBody user_msg_part = Common.getRequestBodyText(msg);
        RequestBody user_msg_type_part = Common.getRequestBodyText(Tags.MESSAGE_IMAGE_TEXT);
        MultipartBody.Part image_part = Common.getMultiPart(this,imgUri,"file");

        Api.getService(Tags.base_url)
                .sendMessageWithImage(room_part,from_user_id_part,to_user_id_part,user_msg_part,user_msg_type_part,image_part)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                       Log.e("datttaa",response.body()+"_");
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (adapter==null)
                            {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
                                recView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size()-1);

                                    }
                                },100);
                            }else
                            {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size()-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size()-1);

                                    }
                                },100);
                            }

                        }else
                        {

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }
    private void sendTextMessage(String msg)
    {
        Api.getService(Tags.base_url)
                .sendMessage(chatUserModel.getRoom_id(),userModel.getData().getUser_id(),chatUserModel.getId(),msg,Tags.MESSAGE_TEXT)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (adapter==null)
                            {
                                messageModelList.add(response.body());
                                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
                                adapter.notifyDataSetChanged();
                                recView.setAdapter(adapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size()-1);

                                    }
                                },100);
                            }else
                            {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size()-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size()-1);

                                    }
                                },100);
                            }

                        }else
                        {

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void getChatMessages()
    {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(),1)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                messageModelList.addAll(response.body().getData());
                                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
                                recView.setAdapter(adapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recView.scrollToPosition(messageModelList.size()-1);

                                    }
                                },100);
                            }
                        }else
                        {

                            Toast.makeText(ChatActivity.this,R.string.failed, Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
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
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void loadMore(int next_page)
    {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(),next_page)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        if (response.isSuccessful())
                        {
                            progBarLoadMore.setVisibility(View.GONE);

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {


                                messageModelList.addAll(0,response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();
                                adapter.notifyItemRangeInserted(0,response.body().getData().size()-1);

                            }
                            isLoading = false;

                        }else
                        {
                            isLoading = false;
                            progBarLoadMore.setVisibility(View.VISIBLE);


                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
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
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void CreateImageAlertDialog()
    {

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
    private void Check_ReadPermission()
    {
        if (ContextCompat.checkSelfPermission(this, read_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{read_permission}, IMG1);
        } else {
            SelectImage(IMG1);
        }
    }
    private void Check_CameraPermission()
    {
        if (ContextCompat.checkSelfPermission(this, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, IMG2);
        } else {
            SelectImage(IMG2);

        }

    }

    private void SelectImage(int type)
    {

        Intent  intent = new Intent();

        if (type == IMG1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,IMG1);

        }else if (type ==IMG2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMG2);
            }catch (SecurityException e)
            {
                Toast.makeText(this,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

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
        }else if (requestCode == IMG2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(IMG2);

                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.getData();
            if (which_image_upload_selected ==1)
            {
                CreateBillAlertDialog(imgUri);

            }else if (which_image_upload_selected == 2)
            {
                SendMessage("0",Tags.MESSAGE_IMAGE_TEXT);

            }

        }else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgUri = getUriFromBitmap(bitmap);
            if (imgUri!=null)
            {
                if (which_image_upload_selected ==1)
                {
                    CreateBillAlertDialog(imgUri);

                }else if (which_image_upload_selected == 2)
                {
                    SendMessage("0",Tags.MESSAGE_IMAGE_TEXT);

                }

            }

        }
    }

    public  void CreateBillAlertDialog(Uri uri)
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bill_photo,null);
        ImageView image = view.findViewById(R.id.image);
        final EditText edt_order_cost = view.findViewById(R.id.edt_order_cost);

        File file = new File(Common.getImagePath(this, uri));
        Picasso.with(this).load(file).fit().into(image);

        Button btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost = edt_order_cost.getText().toString().trim();
                Currency currency = Currency.getInstance(new Locale(current_language,userModel.getData().getUser_country()));
                if (!TextUtils.isEmpty(cost))
                {
                    dialog.dismiss();
                    Common.CloseKeyBoard(ChatActivity.this,edt_order_cost);

                    double total = Double.parseDouble(cost)+Double.parseDouble(chatUserModel.getOffer_cost());
                    String msg = "تكلفة المشتريات: "+cost+" "+currency.getSymbol()+"\n"+"تكلفة التوصيل: "+chatUserModel.getOffer_cost()+" "+currency.getSymbol()+"\n"+"المجموع الكلي: "+total+" "+currency.getSymbol();
                    SendMessage(msg,Tags.MESSAGE_IMAGE_TEXT);


                }
            }
        });

        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private Uri getUriFromBitmap(Bitmap bitmap)
    {
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
    private void Back()
    {
        if (from)
        {
            finish();
        }else
        {
            Intent intent = new Intent(ChatActivity.this, ClientHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed()
    {
        Back();
    }
    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        preferences.clearChatUserData(ChatActivity.this);
        if (mp!=null)
        {
            mp.release();
        }

    }
    public class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mp = MediaPlayer.create(ChatActivity.this,R.raw.typing);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mp.start();
            mp.setLooping(false);
            return null;
        }
    }

}
