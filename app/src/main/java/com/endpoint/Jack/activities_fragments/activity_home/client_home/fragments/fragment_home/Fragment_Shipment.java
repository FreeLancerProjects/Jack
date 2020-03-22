package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.OrderIdDataModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Shipment extends Fragment {

    private ClientHomeActivity activity;
    private ImageView image_send,img_delete1,img_delete2,img_delete3,image_details;
    private CardView cardView_location_pickup, cardView_location_dropoff, cardView_delivery_time;
    private TextView tv_location_pickup, tv_location_dropoff, tv_delivery_time;
    private EditText edt_order_details;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private String place_id = "", place_pickup_address = "", place_dropoff_address = "", order_details = "";
    private double place_pickup_lat = 0.0, place_pickup_long = 0.0, place_dropoff_lat = 0.0, place_dropoff_long = 0.0;
    private long selected_time = 0;
    private String delegate_id = "";
    private FloatingActionButton fab;

    private String current_language;
    private String[] timesList;


    private final int IMG1=1,IMG2=2;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;


    public static Fragment_Shipment newInstance() {
        Fragment_Shipment fragment_delegate_comments = new Fragment_Shipment();
        return fragment_delegate_comments;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shipment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        timesList = new String[]{getString(R.string.hour1),
                getString(R.string.hour2),
                getString(R.string.hour3),
                getString(R.string.day1),
                getString(R.string.day2),
                getString(R.string.day3)

        };

        img_delete1 = view.findViewById(R.id.img_delete1);
        img_delete2 = view.findViewById(R.id.img_delete2);
        img_delete3 = view.findViewById(R.id.img_delete3);

        image_send = view.findViewById(R.id.image_send);
        image_details = view.findViewById(R.id.image_details);
        fab = view.findViewById(R.id.fab);
        cardView_location_pickup = view.findViewById(R.id.cardView_location_pickup);
        cardView_location_dropoff = view.findViewById(R.id.cardView_location_dropoff);
        cardView_delivery_time = view.findViewById(R.id.cardView_delivery_time);
        tv_location_pickup = view.findViewById(R.id.tv_location_pickup);
        tv_location_dropoff = view.findViewById(R.id.tv_location_dropoff);
        tv_delivery_time = view.findViewById(R.id.tv_delivery_time);
        edt_order_details = view.findViewById(R.id.edt_order_details);
        cardView_location_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentMap("fragment_shipment_pickup_location");
            }
        });

        cardView_location_dropoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentMap("fragment_shipment_dropoff_location");
            }
        });

        cardView_delivery_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTimeDialog();
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel!=null) {
                    if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
                    {
                        CheckData();

                    }else
                        {
                            Common.CreateSignAlertDialog(activity,getString(R.string.serv_aval_client));

                        }

                }
            }
        });

        img_delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_location_pickup.setText("");
                tv_location_pickup.setHint(getString(R.string.receiving_location));
                place_pickup_address = "";
                place_pickup_lat = 0.0;
                place_pickup_long = 0.0;
                place_id="";
                img_delete1.setVisibility(View.GONE);

            }
        });

        img_delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_location_dropoff.setText("");
                tv_location_dropoff.setHint(getString(R.string.dropoff_location));
                place_dropoff_address = "";
                place_dropoff_lat = 0.0;
                place_dropoff_long = 0.0;
                place_id="";
                img_delete2.setVisibility(View.GONE);


            }
        });
        img_delete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_time = 0;
                tv_delivery_time.setHint(getString(R.string.deliver_within));
                tv_delivery_time.setText("");
                img_delete3.setVisibility(View.GONE);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateImageAlertDialog();
            }
        });

    }

    private void CheckData()
    {

        order_details = edt_order_details.getText().toString().trim();
        if (!TextUtils.isEmpty(place_pickup_address) &&
                !TextUtils.isEmpty(place_dropoff_address)&&
                !TextUtils.isEmpty(place_dropoff_address)&&
                selected_time!=0)

        {
            tv_location_pickup.setError(null);
            tv_location_dropoff.setError(null);
            tv_delivery_time.setError(null);
            edt_order_details.setError(null);
            Common.CloseKeyBoard(activity,edt_order_details);

//            if (TextUtils.isEmpty(delegate_id))
//            {
//                activity.DisplayFragmentDelegates(place_pickup_lat,place_dropoff_long,"reserve_shipment","","");
//
//            }else
//                {
//                }

            sendOrder();


        }else
            {
                if (TextUtils.isEmpty(place_pickup_address))
                {
                    tv_location_pickup.setError(getString(R.string.field_req));

                }else
                    {
                        tv_location_pickup.setError(null);
                    }

                if (TextUtils.isEmpty(place_dropoff_address))
                {
                    tv_location_dropoff.setError(getString(R.string.field_req));

                }else
                {
                    tv_location_dropoff.setError(null);
                }

                if (selected_time==0)
                {
                    tv_delivery_time.setError(getString(R.string.field_req));

                }else
                {
                    tv_delivery_time.setError(null);
                }

                if (TextUtils.isEmpty(order_details))
                {
                    edt_order_details.setError(getString(R.string.field_req));

                }else
                {

                    edt_order_details.setError(null);
                }




            }

    }
    private void sendOrder()
    {
        //this.delegate_id = delegate_id;
        int coupon_id=0;
        if(userModel.getCoupon_data()!=null){
            coupon_id=userModel.getCoupon_data().getId();
        }
        else {
            coupon_id=-1;
        }
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .sendOrder(userModel.getData().getUser_id(),place_dropoff_address, place_dropoff_lat, place_dropoff_long, order_details, place_id, place_pickup_address, "2", place_pickup_lat, place_pickup_long, selected_time,coupon_id+"")
                .enqueue(new Callback<OrderIdDataModel>() {
                    @Override
                    public void onResponse(Call<OrderIdDataModel> call, Response<OrderIdDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            CreateAlertDialog(response.body().getData().getOrder_id());
                        } else {
                            try {
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderIdDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }
    private void sendOrderWithImage()
    {
        //this.delegate_id = delegate_id;
        int coupon_id=0;
        if(userModel.getCoupon_data()!=null){
            coupon_id=userModel.getCoupon_data().getId();
        }
        else {
            coupon_id=-1;
        }

        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody place_dropoff_address_part = Common.getRequestBodyText(place_dropoff_address);
        RequestBody place_dropoff_lat_part = Common.getRequestBodyText(String.valueOf(place_dropoff_lat));
        RequestBody place_dropoff_long_part = Common.getRequestBodyText(String.valueOf(place_dropoff_long));
        RequestBody order_details_part = Common.getRequestBodyText(order_details);
        RequestBody place_id_part = Common.getRequestBodyText(place_id);
        RequestBody place_pickup_address_part = Common.getRequestBodyText(place_pickup_address);
        RequestBody order_type_part = Common.getRequestBodyText("2");
        RequestBody place_pickup_lat_part = Common.getRequestBodyText(String.valueOf(place_pickup_lat));
        RequestBody place_pickup_long_part = Common.getRequestBodyText(String.valueOf(place_pickup_long));
        RequestBody selected_time_part = Common.getRequestBodyText(String.valueOf(selected_time));
        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"order_image");
        RequestBody copun_part = Common.getRequestBodyText(coupon_id+"");




        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .sendOrderWithImage(user_id_part,place_dropoff_address_part, place_dropoff_lat_part, place_dropoff_long_part, order_details_part, place_id_part, place_pickup_address_part, order_type_part, place_pickup_lat_part, place_pickup_long_part, selected_time_part,copun_part,image_part)
                .enqueue(new Callback<OrderIdDataModel>() {
                    @Override
                    public void onResponse(Call<OrderIdDataModel> call, Response<OrderIdDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            CreateAlertDialog(response.body().getData().getOrder_id());
                        } else {
                            try {
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderIdDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }
    public void setLocationData(String place_id, String place_address, double place_lat, double place_long, String type) {
        if (type.equals("pickup_location")) {
            this.place_pickup_address = place_address;
            this.place_pickup_lat = place_lat;
            this.place_pickup_long = place_long;
            this.place_id = place_id;
            tv_location_pickup.setText(place_address);
            tv_location_pickup.setError(null);
            img_delete1.setVisibility(View.VISIBLE);

        } else if (type.equals("dropoff_location")) {
            this.place_dropoff_address = place_address;
            this.place_dropoff_lat = place_lat;
            this.place_dropoff_long = place_long;
            this.place_id = place_id;
            tv_location_dropoff.setText(place_address);
            tv_location_dropoff.setError(null);
            img_delete2.setVisibility(View.VISIBLE);


        }
    }

    private void CreateTimeDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_delivery_time, null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(timesList.length - 1);
        numberPicker.setDisplayedValues(timesList);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(1);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = timesList[numberPicker.getValue()];
                tv_delivery_time.setText(val);
                tv_delivery_time.setError(null);
                setTime(numberPicker.getValue());
                img_delete3.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setView(view);
        dialog.show();
    }

    private void setTime(int value) {
        Calendar calendar = Calendar.getInstance(new Locale(current_language));
        switch (value) {
            case 0:
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case 1:
                calendar.add(Calendar.HOUR_OF_DAY, 2);

                break;
            case 2:
                calendar.add(Calendar.HOUR_OF_DAY, 3);

                break;
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                break;
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH, 2);

                break;
            case 5:
                calendar.add(Calendar.DAY_OF_MONTH, 3);

                break;
        }

        selected_time = calendar.getTimeInMillis() / 1000;
    }

    public void CreateAlertDialog(String order_id)
    {


        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_order_id, null);
        Button btn_follow = view.findViewById(R.id.btn_follow);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(getString(R.string.order_sent_successfully_order_number_is) + " #" + order_id);
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();

                dialog.dismiss();
                activity.FollowOrderFromShipment();


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                dialog.dismiss();


            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }

    private void clearUI() {
        edt_order_details.setText("");
        edt_order_details.setHint(getString(R.string.write_package_details));
        tv_delivery_time.setText("");
        tv_delivery_time.setHint(getString(R.string.deliver_within));

        tv_location_pickup.setText("");
        tv_location_pickup.setHint(getString(R.string.receiving_location));
        tv_location_dropoff.setText("");
        tv_location_dropoff.setHint(getString(R.string.dropoff_location));
        place_id = "";
        place_pickup_address = "";
        place_dropoff_address = "";
        order_details = "";
        place_pickup_lat = 0.0;
        place_pickup_long = 0.0;
        place_dropoff_lat = 0.0;
        place_dropoff_long = 0.0;
        selected_time = 0;
        uri = null;
    }

    private void CreateImageAlertDialog()
    {

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data!=null)
        {
            uri = data.getData();
            File file = new File(Common.getImagePath(activity,uri));
            try {
                Picasso.with(activity).load(file).fit().into(image_details);

            }catch (Exception e)
            {
                Picasso.with(activity).load(uri).fit().into(image_details);
            }
            image_details.setVisibility(View.VISIBLE);


        }else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data!=null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image_details.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
            image_details.setVisibility(View.VISIBLE);


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
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
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
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    select_photo(2);

                }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

}
