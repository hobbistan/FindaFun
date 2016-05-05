package com.findafun.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class BannerList extends AppCompatActivity {

    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private Event event;
    private ArrayList<String> imgList = new ArrayList<>();




    Gallery gallery;
    ImageView selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);
        event = (Event) getIntent().getSerializableExtra("eventObj");

        imgList.add(0,event.getEventLogo());
        imgList.add(1,"http://placehold.it/120x120&text=image2");
        imgList.add(2,"http://placehold.it/120x120&text=image3");

        Gallery gallery = (Gallery) findViewById(R.id.gallery_list);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ImageView imageView = (ImageView) findViewById(R.id.banner_list);
              //  imageView.setImageResource(imgList[position]);

                String thumbnailUrl = getThumbnailImageUrl(imgList.get(position),0,0);

                uImageLoader.displayImage(thumbnailUrl, imageView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(android.R.color.darker_gray)
                                .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

                imageView.setAdjustViewBounds(true);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
          /*  TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();*/
        }
        // returns the number of images
        public int getCount() {
            return imgList.size();
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
          /*  ImageView imageView = new ImageView(context);
           // imageView.setImageResource(imgList[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            imageView.setBackgroundResource(R.color.bg_gray);




            imageView.setPadding(5,2,5,2);*/

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.image_banner, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.banner);

            String thumbnailUrl = getThumbnailImageUrl(imgList.get(position),0,0);

            uImageLoader.displayImage(thumbnailUrl, imageView,
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(android.R.color.darker_gray)
                            .cacheInMemory(true).cacheOnDisk(true).build(), loadingListener);

            imageView.setAdjustViewBounds(true);

            return convertView;
        }
    }


    public String getThumbnailImageUrl(String imgUrl,int width,int height){
        String url="http://imgsize.ph.126.net/?imgurl=data1_data2xdata3x0x85.jpg&enlarge=true";
        width = (int) (getResources().getDisplayMetrics().density * 100);
        height = (int) (getResources().getDisplayMetrics().density * 100); //just for convenient
        url=url.replaceAll("data1", imgUrl).replaceAll("data2", width+"").replaceAll("data3", height+"");
        return url;
    }

    private ImageLoadingListener loadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            view.setEnabled(true);//only loadedImage is available we can click item
        }
    };

}
