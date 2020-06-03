package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.share.Common;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Delegate_Register extends Fragment {

    private ImageView image_id, image_id_icon, image_license, image_license_icon, arrow, image_register, car_front_image, car_front_icon, car_back_image, car_back_icon;
    private LinearLayout ll_back;
    private FrameLayout fl_id_image, fl_license_image, fl_car_front_image, fl_car_back_image;
    private EditText edt_national_num, edt_address, edt_banknumber;
    private ClientHomeActivity activity;
    private String current_language;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2, IMG_REQ3 = 3, IMG_REQ4 = 4;
    private Uri imgUri1 = null, imgUri2 = null, imgUri3 = null, imgUri4 = null;
    private int selectedType = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_register, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Delegate_Register newInstance() {
        return new Fragment_Delegate_Register();
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);


        }

        image_id = view.findViewById(R.id.image_id);
        image_id_icon = view.findViewById(R.id.image_id_icon);
        image_license = view.findViewById(R.id.image_license);
        image_license_icon = view.findViewById(R.id.image_license_icon);
        image_register = view.findViewById(R.id.image_register);
        ll_back = view.findViewById(R.id.ll_back);
        edt_national_num = view.findViewById(R.id.edt_national_num);
        edt_address = view.findViewById(R.id.edt_address);
        edt_banknumber = view.findViewById(R.id.edt_banknumber);
        fl_id_image = view.findViewById(R.id.fl_id_image);
        fl_license_image = view.findViewById(R.id.fl_license_image);


        fl_car_front_image = view.findViewById(R.id.fl_car_front_image);
        fl_car_back_image = view.findViewById(R.id.fl_car_back_image);
        car_front_image = view.findViewById(R.id.car_front_image);
        car_front_icon = view.findViewById(R.id.car_front_icon);
        car_back_image = view.findViewById(R.id.car_back_image);
        car_back_icon = view.findViewById(R.id.car_back_icon);


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        fl_id_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ1);
            }
        });

        fl_license_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ2);
            }
        });

        fl_car_front_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ3);
            }
        });
        fl_car_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ4);
            }
        });


        image_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

    }

    private void CheckData() {
        String m_national_id = edt_national_num.getText().toString().trim();
        String m_address = edt_address.getText().toString().trim();
String m_banknumber=edt_banknumber.getText().toString().trim();
        if (!TextUtils.isEmpty(m_national_id) &&
                !TextUtils.isEmpty(m_address) &&
                !TextUtils.isEmpty(m_banknumber)&&
                imgUri1 != null &&
                imgUri2 != null &&
                imgUri3 != null &&
                imgUri4 != null

        ) {
            edt_address.setError(null);
            edt_national_num.setError(null);
            edt_banknumber.setError(null);
            Common.CloseKeyBoard(activity, edt_national_num);
            activity.registerDelegate(m_national_id, m_address,m_banknumber, imgUri1, imgUri2, imgUri3, imgUri4);

        } else {
            if (TextUtils.isEmpty(m_national_id)) {
                edt_national_num.setError(getString(R.string.field_req));

            } else {
                edt_national_num.setError(null);

            }
            if (TextUtils.isEmpty(m_banknumber)) {
                edt_banknumber.setError(getString(R.string.field_req));

            } else {
                edt_banknumber.setError(null);

            }
            if (TextUtils.isEmpty(m_address)) {
                edt_address.setError(getString(R.string.field_req));

            } else {
                edt_address.setError(null);

            }

            if (imgUri1 == null) {
                Toast.makeText(activity, R.string.choose_identity_card_image, Toast.LENGTH_SHORT).show();
            }

            if (imgUri2 == null) {
                Toast.makeText(activity, R.string.choose_license_image, Toast.LENGTH_SHORT).show();
            }

            if (imgUri3 == null) {
                Toast.makeText(activity, R.string.ch_img_front, Toast.LENGTH_SHORT).show();
            }

            if (imgUri4 == null) {
                Toast.makeText(activity, R.string.ch_img_behind, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void CreateImageAlertDialog(final int img_req) {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image, null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 2;
                Check_CameraPermission(img_req);

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 1;
                CheckReadPermission(img_req);


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

    private void CheckReadPermission(int img_req) {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1, img_req);
        }
    }

    private void Check_CameraPermission(int img_req) {
        if (ContextCompat.checkSelfPermission(activity, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, write_permission}, img_req);
        } else {
            SelectImage(2, img_req);

        }

    }

    private void SelectImage(int type, int img_req) {

        Intent intent = new Intent();

        if (type == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, img_req);

        } else if (type == 2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, img_req);
            } catch (SecurityException e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {

            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == IMG_REQ2) {
            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ2);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ2);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == IMG_REQ3) {

            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ3);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ3);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == IMG_REQ4) {
            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ4);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ4);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1) {
                imgUri1 = data.getData();
                image_id_icon.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri1));
                Picasso.with(activity).load(file).fit().into(image_id);
            } else if (selectedType == 2) {
                image_id_icon.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                if (imgUri1 != null) {
                    String path = Common.getImagePath(activity, imgUri1);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(image_id);

                    } else {
                        Picasso.with(activity).load(imgUri1).fit().into(image_id);

                    }
                }
            }


        } else if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1) {
                imgUri2 = data.getData();
                image_license_icon.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri2));

                Picasso.with(activity).load(file).fit().into(image_license);
            } else if (selectedType == 2) {

                image_license_icon.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri2 = getUriFromBitmap(bitmap);
                if (imgUri2 != null) {
                    String path = Common.getImagePath(activity, imgUri2);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(image_license);

                    } else {
                        Picasso.with(activity).load(imgUri2).fit().into(image_license);

                    }
                }
            }


        } else if (requestCode == IMG_REQ3 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1) {
                imgUri3 = data.getData();
                car_front_icon.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri3));

                Picasso.with(activity).load(file).fit().into(car_front_image);
            } else if (selectedType == 2) {

                car_front_icon.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri3 = getUriFromBitmap(bitmap);
                if (imgUri3 != null) {
                    String path = Common.getImagePath(activity, imgUri3);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(car_front_image);

                    } else {
                        Picasso.with(activity).load(imgUri3).fit().into(car_front_image);

                    }
                }


            }


        } else if (requestCode == IMG_REQ4 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1) {
                imgUri4 = data.getData();
                car_back_icon.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri4));

                Picasso.with(activity).load(file).fit().into(car_back_image);
            } else if (selectedType == 2) {

                car_back_icon.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri4 = getUriFromBitmap(bitmap);
                if (imgUri4 != null) {
                    String path = Common.getImagePath(activity, imgUri4);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(car_back_image);

                    } else {
                        Picasso.with(activity).load(imgUri4).fit().into(car_back_image);

                    }
                }


            }


        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }

}
