package com.ablanco.zoomysample;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.ablanco.zoomy.ZoomListener;
import com.ablanco.zoomy.Zoomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Integer> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImages.addAll(Arrays.asList(R.drawable.img1, R.drawable.img2,
                R.drawable.img3, R.drawable.img4, R.drawable.img5,
                R.drawable.img6, R.drawable.img7, R.drawable.img8,
                R.drawable.img9, R.drawable.img10));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(5));
        recyclerView.setAdapter(new Adapter(mImages));

    }

    class Adapter extends RecyclerView.Adapter<ImageViewHolder> {

        private List<Integer> images;

        Adapter(List<Integer> images) {
            this.images = images;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new SquareImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            ((ImageView) holder.itemView).setImageResource(images.get(position));
            holder.itemView.setTag(holder.getAdapterPosition());
            Zoomy.Builder builder = new Zoomy.Builder(MainActivity.this)
                    .target(holder.itemView)
                    .interpolator(new OvershootInterpolator())
                    .listener(new ZoomListener() {
                        @Override
                        public void onViewStartedZooming(View view) {
                            Toast.makeText(MainActivity.this, "START ZOOM! "
                                    + view.getTag(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onViewEndedZooming(View view) {
                            Toast.makeText(MainActivity.this, "END ZOOM! "
                                    + view.getTag(), Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.register();
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class CommonItemSpaceDecoration extends RecyclerView.ItemDecoration {
        private int mSpace = 0;

        CommonItemSpaceDecoration(int space) {
            this.mSpace = space;
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(mSpace, mSpace, mSpace, mSpace);
        }

    }
}
