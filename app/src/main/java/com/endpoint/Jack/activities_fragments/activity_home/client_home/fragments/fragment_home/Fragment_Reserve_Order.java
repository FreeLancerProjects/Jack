package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.Favourite_location;
import com.endpoint.Jack.models.OrderIdDataModel;
import com.endpoint.Jack.models.PlaceModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

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

public class Fragment_Reserve_Order extends Fragment {
    private static final String TAG = "DATA";
    private ClientHomeActivity activity;
    private ImageView image, arrow, image_reserve,image_details;
    private TextView tv_place_name, tv_place_address, tv_address;
    private LinearLayout ll_back, ll_delivery_location, ll_fav_address, ll_fav_map_loc, ll_choose_delivery_time;
    private CheckBox checkbox;
    private ExpandableLayout expandLayout;
    private TextView tv_fav_address_title, tv_fav_address, tv_delivery_time;
    private EditText edt_order_details;
    private PlaceModel placeModel;
    private String current_language;
    private Preferences preferences;
    private FloatingActionButton fab;
    private Favourite_location favourite_location;
    private String [] timesList;
    private UserSingleTone userSingleTone;
    private UserModel userModel;

    private final int IMG1=1,IMG2=2;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;

    ////////////////////////////////////////////////
    private Favourite_location selected_location = null;
    private long selected_time=0;
    private String order_details,delegate_id="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve_order, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Reserve_Order newInstance(PlaceModel placeModel) {
        Fragment_Reserve_Order fragment_reserve_order = new Fragment_Reserve_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, placeModel);
        fragment_reserve_order.setArguments(bundle);
        return fragment_reserve_order;

    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        timesList = new String[]{getString(R.string.hour1),
                getString(R.string.hour2),
                getString(R.string.hour3),
                getString(R.string.day1),
                getString(R.string.day2),
                getString(R.string.day3)

        };


        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);


        }
        ll_back = view.findViewById(R.id.ll_back);
        image = view.findViewById(R.id.image);
        image_details = view.findViewById(R.id.image_details);
        fab = view.findViewById(R.id.fab);
        image_reserve = view.findViewById(R.id.image_reserve);
        tv_place_name = view.findViewById(R.id.tv_place_name);
        tv_place_address = view.findViewById(R.id.tv_place_address);
        tv_address = view.findViewById(R.id.tv_address);
        ll_delivery_location = view.findViewById(R.id.ll_delivery_location);
        ll_fav_address = view.findViewById(R.id.ll_fav_address);
        ll_fav_map_loc = view.findViewById(R.id.ll_fav_map_loc);
        ll_choose_delivery_time = view.findViewById(R.id.ll_choose_delivery_time);
        checkbox = view.findViewById(R.id.checkbox);
        expandLayout = view.findViewById(R.id.expandLayout);
        tv_fav_address_title = view.findViewById(R.id.tv_fav_address_title);
        tv_fav_address = view.findViewById(R.id.tv_fav_address);
        tv_delivery_time = view.findViewById(R.id.tv_delivery_time);
        edt_order_details = view.findViewById(R.id.edt_order_details);






        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
        ll_delivery_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getFavouriteLocation(activity)!=null)
                {

                    if (expandLayout.isExpanded())
                    {
                        expandLayout.collapse(true);
                    }else
                        {
                            expandLayout.expand(true);
                        }
                }else
                    {
                        if (!checkbox.isChecked())
                        {
                            activity.DisplayFragmentMap("fragment_reserve_order");

                        }
                    }
            }
        });

        ll_fav_map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentMap("fragment_reserve_order");

            }
        });

        ll_fav_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favourite_location!=null)
                {
                    expandLayout.collapse(true);
                    selected_location = new Favourite_location(favourite_location.getPlace_id(),favourite_location.getName(),favourite_location.getStreet(),favourite_location.getAddress(),favourite_location.getLat(),favourite_location.getLng());
                    updateSelectedAddress(favourite_location,true);

                }
            }
        });


        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkbox.isChecked())
                {
                    DisplayAlertEnterAddressTitle();
                }else
                    {
                        preferences.ClearFavoriteLocation(activity);
                        expandLayout.collapse(true);
                    }
            }
        });

        ll_choose_delivery_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTimeDialog();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            updateUI(placeModel);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateImageAlertDialog();
            }
        });
        image_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });
    }

    private void CheckData() {
        order_details = edt_order_details.getText().toString().trim();
        if(!TextUtils.isEmpty(order_details)&&selected_location!=null&&selected_time!=0)
        {
            edt_order_details.setError(null);
            tv_delivery_time.setError(null);
            tv_address.setError(null);
            Common.CloseKeyBoard(activity,edt_order_details);
            /*if (TextUtils.isEmpty(delegate_id))
            {
                activity.DisplayFragmentDelegates(placeModel.getLat(),placeModel.getLng(),"reserve_order","","");

            }else
                {
                }*/

            if (uri==null)
            {
                sendOrder();

            }else
                {
                    sendOrderWithImage();
                }


        }else
            {
                if (TextUtils.isEmpty(order_details))
                {
                    edt_order_details.setError(getString(R.string.field_req));

                }else
                    {
                        edt_order_details.setError(null);
                    }

                if (selected_time == 0)
                {
                    tv_delivery_time.setError(getString(R.string.field_req));

                }else
                {
                    tv_delivery_time.setError(null);
                }

                if (selected_location == null)
                {
                    tv_address.setError(getString(R.string.field_req));

                }else
                {
                    tv_address.setError(null);
                }
            }

    }



    private void updateUI(PlaceModel placeModel) {
        if (placeModel!=null)
        {
            tv_place_name.setText(placeModel.getName());
            tv_place_address.setText(placeModel.getAddress());
            Picasso.with(activity).load(placeModel.getIcon()).fit().into(image);


        }
        favourite_location = preferences.getFavouriteLocation(activity);

        if (favourite_location !=null)
        {
            tv_fav_address_title.setText(favourite_location.getName());
            tv_fav_address.setText(favourite_location.getStreet()+"\n"+favourite_location.getAddress());
        }

    }

    public void updateSelectedAddress(Favourite_location selected_location,boolean checked)
    {

        checkbox.setVisibility(View.VISIBLE);


        if (!TextUtils.isEmpty(selected_location.getStreet()))
        {
            tv_address.setText(selected_location.getStreet()+"\n"+selected_location.getAddress());

        }else
            {
                tv_address.setText(selected_location.getAddress());

            }

        if (checked)
        {
            checkbox.setChecked(true);

        }else
            {
                checkbox.setChecked(false);

            }
    }

    private void DisplayAlertEnterAddressTitle()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_address_fav,null);
        TextView tv_address = view.findViewById(R.id.tv_address);
        final EditText edt_name = view.findViewById(R.id.edt_name);
        Button btn_save = view.findViewById(R.id.btn_save);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        tv_address.setText(selected_location.getStreet()+"\n"+selected_location.getAddress());

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString().trim();
                if (!TextUtils.isEmpty(name))
                {
                    edt_name.setError(null);
                    Common.CloseKeyBoard(activity,edt_name);

                    Favourite_location favourite_location = new Favourite_location(selected_location.getPlace_id(),name,selected_location.getStreet(),selected_location.getAddress(),selected_location.getLat(),selected_location.getLng());
                    Fragment_Reserve_Order.this.favourite_location =favourite_location;
                    preferences.SaveFavouriteLocation(activity,favourite_location);
                    if (!selected_location.getStreet().isEmpty())
                    {
                        tv_fav_address_title.setText(name+" : "+selected_location.getStreet());
                        tv_fav_address.setText(selected_location.getAddress());

                    }else
                        {
                            tv_fav_address_title.setText(name);
                            tv_fav_address.setText(selected_location.getAddress());
                        }
                    dialog.dismiss();

                }else
                    {
                        edt_name.setError(getString(R.string.field_req));
                    }
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

    public void updateSelectedLocation(Favourite_location favourite_location)
    {
        this.selected_location = favourite_location;
        tv_address.setError(null);
        updateSelectedAddress(favourite_location,false);
    }

    public void sendOrderWithImage()
    {
        //this.delegate_id = delegate_id;

        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody client_address_part = Common.getRequestBodyText(selected_location.getStreet()+" "+selected_location.getAddress());
        RequestBody client_lat_part = Common.getRequestBodyText(String.valueOf(selected_location.getLat()));
        RequestBody client_lng_part = Common.getRequestBodyText(String.valueOf(selected_location.getLng()));
        RequestBody order_details_part = Common.getRequestBodyText(order_details);
        RequestBody place_id_part = Common.getRequestBodyText(placeModel.getPlace_id());
        RequestBody place_address_part = Common.getRequestBodyText(placeModel.getAddress());
        RequestBody order_type_part = Common.getRequestBodyText("1");
        RequestBody place_lat_part = Common.getRequestBodyText(String.valueOf(placeModel.getLat()));
        RequestBody place_lng_part = Common.getRequestBodyText(String.valueOf(placeModel.getLng()));
        RequestBody selected_time_part = Common.getRequestBodyText(String.valueOf(selected_time));
        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"order_image");

        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .sendOrderWithImage(user_id_part,client_address_part,client_lat_part,client_lng_part,order_details_part,place_id_part,place_address_part,order_type_part,place_lat_part,place_lng_part,selected_time_part,image_part)
                .enqueue(new Callback<OrderIdDataModel>() {
                    @Override
                    public void onResponse(Call<OrderIdDataModel> call, Response<OrderIdDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            uri = null;
                            CreateAlertDialog(response.body().getData().getOrder_id());
                        }else
                            {
                                try {
                                    Log.e("Error_code",response.code()+""+response.errorBody().string());
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
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    public void sendOrder()
    {
        //this.delegate_id = delegate_id;


        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .sendOrder(userModel.getData().getUser_id(),selected_location.getStreet()+" "+selected_location.getAddress(),selected_location.getLat(),selected_location.getLng(),order_details,placeModel.getPlace_id(),placeModel.getAddress(),"1",placeModel.getLat(),placeModel.getLng(),selected_time)
                .enqueue(new Callback<OrderIdDataModel>() {
                    @Override
                    public void onResponse(Call<OrderIdDataModel> call, Response<OrderIdDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            CreateAlertDialog(response.body().getData().getOrder_id());
                        }else
                        {
                            try {
                                Log.e("Error_code",response.code()+""+response.errorBody().string());
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
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void CreateTimeDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_delivery_time,null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(timesList.length-1);
        numberPicker.setDisplayedValues(timesList);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(1);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = timesList[numberPicker.getValue()];
                tv_delivery_time.setText(val);
                setTime(numberPicker.getValue());
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
        switch (value)
        {
            case 0:
                calendar.add(Calendar.HOUR_OF_DAY,1);
                break;
            case 1:
                calendar.add(Calendar.HOUR_OF_DAY,2);

                break;
            case 2:
                calendar.add(Calendar.HOUR_OF_DAY,3);

                break;
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH,1);

                break;
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH,2);

                break;
            case 5:
                calendar.add(Calendar.DAY_OF_MONTH,3);

                break;
        }

        selected_time = calendar.getTimeInMillis()/1000;
    }

    public  void CreateAlertDialog(String order_id)
    {


        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_order_id,null);
        Button btn_follow = view.findViewById(R.id.btn_follow);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(getString(R.string.order_sent_successfully_order_number_is)+" #"+order_id);
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.FollowOrder();





            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.Back();


            }
        });

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
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
                Picasso.with(activity).load(file).into(image_details);

            }catch (Exception e)
            {
                Picasso.with(activity).load(uri).into(image_details);
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

}
