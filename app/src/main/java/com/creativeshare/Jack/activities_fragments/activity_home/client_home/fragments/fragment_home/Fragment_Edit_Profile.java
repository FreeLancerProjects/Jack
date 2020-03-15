package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.preferences.Preferences;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.share.Common;
import com.creativeshare.Jack.singletone.UserSingleTone;
import com.creativeshare.Jack.tags.Tags;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Edit_Profile extends Fragment implements DatePickerDialog.OnDateSetListener{

    private final static  String TAG = "Data";
    private FloatingActionButton fab;
    private ClientHomeActivity activity;
    private EditText edt_name,edt_email,edt_address;
    private TextView tv_phone,tv_birth_date;
    private ImageView arrow;
    private CircleImageView image;
    private LinearLayout ll_back,ll_phone,ll_birth_date,ll_address;
    private SimpleRatingBar rateBar;
    private final int IMG1=1,IMG2=2;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;

    private SegmentedButtonGroup segmentGroup;
    private String current_language;
    private int gender;
    private UserModel userModel;
    private Preferences preferences;
    private UserSingleTone userSingleTone;
    private long date_of_birth=0;
    private String country_code,phone_code,phone,address="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Edit_Profile newInstance(UserModel userModel){

        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);

        Fragment_Edit_Profile fragment_edit_profile = new Fragment_Edit_Profile();
        fragment_edit_profile.setArguments(bundle);

        return fragment_edit_profile;
    }
    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        preferences = Preferences.getInstance();
        Paper.init(activity);

        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());
        ll_back  = view.findViewById(R.id.ll_back);
        ll_phone  = view.findViewById(R.id.ll_phone);
        ll_birth_date  = view.findViewById(R.id.ll_birth_date);
        ll_address  = view.findViewById(R.id.ll_address);
        tv_phone  = view.findViewById(R.id.tv_phone);
        tv_birth_date  = view.findViewById(R.id.tv_birth_date);
        edt_address  = view.findViewById(R.id.edt_address);



        arrow =view.findViewById(R.id.arrow);
        image =view.findViewById(R.id.image);
        edt_name = view.findViewById(R.id.edt_name);
        edt_email =view.findViewById(R.id.edt_email);
        rateBar =view.findViewById(R.id.rateBar);

        segmentGroup = view.findViewById(R.id.segmentGroup);
        segmentGroup.setVisibility(View.INVISIBLE);
        fab = view.findViewById(R.id.fab);


        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);


        }else
        {

            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);


        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateImageAlertDialog();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        segmentGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                if (position == 0)
                {
                    gender = Tags.MALE;
                }else
                    {
                        gender = Tags.FEMALE;
                    }
            }
        });

        ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentPhone();
            }
        });
        ll_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDateDialog();
            }
        });

        Bundle bundle = getArguments();

        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUI(userModel);
        }


    }

    private void UpdateUI(UserModel userModel)
    {

        if (userModel!=null)
        {
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+userModel.getData().getUser_image())).fit().placeholder(R.drawable.logo_only).into(image);
            edt_name.setText(userModel.getData().getUser_full_name());
            edt_email.setText(userModel.getData().getUser_email());
            //////////////////////////
            date_of_birth = Long.parseLong(userModel.getData().getUser_age());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy",Locale.ENGLISH);
            String date_birth = dateFormat.format(new Date(Long.parseLong(userModel.getData().getUser_age())*1000));
            tv_birth_date.setText(date_birth);
            //////////////////////////

            phone_code = userModel.getData().getUser_phone_code().replace("00","+");
            phone = userModel.getData().getUser_phone();
            tv_phone.setText(phone_code+" "+phone);
            country_code = userModel.getData().getUser_country();

            if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
            {
                ll_address.setVisibility(View.VISIBLE);
            }

            if(userModel.getData().getUser_gender().equals(String.valueOf(Tags.MALE)))
            {
                segmentGroup.setPosition(0);
                gender = Tags.MALE;
            }else
                {
                    segmentGroup.setPosition(1);
                    gender = Tags.FEMALE;

                }
                segmentGroup.setVisibility(View.VISIBLE);
            SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                    .setDuration(1000)
                    .setRatingTarget(0.0f)
                    .setRepeatCount(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator());

            builder.start();

        }
    }

    private void CreateDateDialog() {
        Calendar calendar = Calendar.getInstance(new Locale(current_language));
        Calendar calendar_now = Calendar.getInstance(new Locale(current_language));

        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.YEAR,(calendar_now.get(Calendar.YEAR)-20));

        DatePickerDialog dialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setOkText(getString(R.string.select));
        dialog.setCancelText(getString(R.string.cancel));
        dialog.setAccentColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        dialog.setOkColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        dialog.setCancelColor(ContextCompat.getColor(activity,R.color.gray4));
        dialog.setLocale(new Locale(current_language));
        dialog.setVersion(DatePickerDialog.Version.VERSION_1);
        dialog.setMaxDate(calendar);
        dialog.show(activity.getFragmentManager(),"");

    }

    private void CheckData()
    {
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();

        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()
                )
        {
            Common.CloseKeyBoard(activity,edt_name);
            edt_name.setError(null);
            edt_email.setError(null);

            if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
            {
                UploadUserDataToUpdate(m_name,m_email,address);

            }else
                {
                    address = edt_address.getText().toString().trim();

                    if (!TextUtils.isEmpty(m_name))
                    {
                        edt_address.setError(null);
                        UploadUserDataToUpdate(m_name,m_email,address);

                    }else
                        {
                            edt_address.setError(getString(R.string.field_req));
                        }
                }


        }else
        {
            if (TextUtils.isEmpty(m_name))
            {
                edt_name.setError(getString(R.string.field_req));
            }else
            {
                edt_name.setError(null);

            }

            if (TextUtils.isEmpty(m_email))
            {
                edt_email.setError(getString(R.string.field_req));
            }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
            {
                edt_email.setError(getString(R.string.inv_email));
            }
            else
            {
                edt_email.setError(null);

            }


        }
    }

    public void updatePhoneData(String country_code,String phone_code,String phone)
    {
        this.country_code = country_code;
        this.phone_code = phone_code;
        this.phone = phone;
        tv_phone.setText(phone_code+" "+phone);


    }
    private void Check_ReadPermission()
    {
        if (ContextCompat.checkSelfPermission(activity,read_permission)!= PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(activity,new String[]{read_permission},IMG1);
        }else
        {
            select_photo(1);

        }

    }


    private void Check_CameraPermission()
    {
        if (ContextCompat.checkSelfPermission(activity,camera_permission)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(activity,write_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{camera_permission,write_permission},IMG2);
        }else
        {
            select_photo(2);

        }

    }
    private void select_photo(int type)
    {
        Intent  intent = new Intent();

        if (type == 1)
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

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMG2);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }



        }



    }


    private void UpdateImage(Uri uri) {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        dialog.setCancelable(false);
        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody user_email_part = Common.getRequestBodyText(userModel.getData().getUser_email());
        RequestBody user_name_part = Common.getRequestBodyText(userModel.getData().getUser_full_name());
        RequestBody user_country_part = Common.getRequestBodyText(userModel.getData().getUser_country());
        RequestBody user_gender_part = Common.getRequestBodyText(userModel.getData().getUser_gender());
        RequestBody user_age_part = Common.getRequestBodyText(userModel.getData().getUser_age());
        RequestBody user_address_part = Common.getRequestBodyText(userModel.getData().getUser_address());
        RequestBody user_phone_code_part = Common.getRequestBodyText(userModel.getData().getUser_phone_code());
        RequestBody user_phone_part = Common.getRequestBodyText(userModel.getData().getUser_phone());

        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"user_image");


        Api.getService(Tags.base_url)
                .updateImage(user_id_part,user_email_part,user_name_part,user_country_part,user_gender_part,user_age_part,user_address_part,user_phone_code_part,user_phone_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            UpdateUserData(response.body());
                        }else
                            {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void UploadUserDataToUpdate(String m_name, String m_email, String address) {

        Log.e("date",date_of_birth+"_");
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        dialog.setCancelable(false);
        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody user_email_part = Common.getRequestBodyText(m_email);
        RequestBody user_name_part = Common.getRequestBodyText(m_name);
        RequestBody user_country_part = Common.getRequestBodyText(country_code);
        RequestBody user_gender_part = Common.getRequestBodyText(String.valueOf(gender));
        RequestBody user_age_part = Common.getRequestBodyText(String.valueOf(date_of_birth));
        RequestBody user_address_part = Common.getRequestBodyText(address);
        RequestBody user_phone_code_part = Common.getRequestBodyText(phone_code.replace("+","00"));
        RequestBody user_phone_part = Common.getRequestBodyText(phone);


        Api.getService(Tags.base_url)
                .updateProfile(user_id_part,user_email_part,user_name_part,user_country_part,user_gender_part,user_age_part,user_address_part,user_phone_code_part,user_phone_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            UpdateUserData(response.body());
                        }else
                        {
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }


    private void UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        UpdateUI(userModel);
        activity.updateUserData(userModel);
    }
    private void CreateImageAlertDialog()
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image,null);
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

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data!=null)
        {
            uri = data.getData();

            UpdateImage(uri);
        }else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data!=null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
            if (uri!=null)
            {
                UpdateImage(uri);

            }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

                }
        }
    }


    private Uri getUriFromBitmap(Bitmap bitmap)
    {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(),bitmap,"title",null);
            return Uri.parse(path);

        }catch (SecurityException e)
        {
            Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
            Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    select_photo(1);
                }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }else if (requestCode == IMG2)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    select_photo(2);

                }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy",Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,monthOfYear);

        String date = dateFormat.format(new Date(calendar.getTimeInMillis()));
        tv_birth_date.setText(date);


        date_of_birth = calendar.getTimeInMillis()/1000;
    }
}
