package testing.gps_service;
/*
import android.content.Context;
import android.databinding.DataBindingUtil;*/

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import testing.gps_service.carousellayoutmanager.CarouselLayoutManager;
import testing.gps_service.carousellayoutmanager.CarouselZoomPostLayoutListener;
import testing.gps_service.carousellayoutmanager.CenterScrollListener;
import testing.gps_service.carousellayoutmanager.DefaultChildSelectionListener;
/*import testing.gps_service.databinding.ActivityCarouselPreviewBinding;
import testing.gps_service.databinding.ItemViewBinding;*/

public class CarouselPreviewActivity extends AppCompatActivity {

    private FloatingActionButton fab_scroll, fab_change_data;
    private RecyclerView list_vertical, list_horizontal;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   final ActivityCarouselPreviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_carousel_preview);
        // setSupportActionBar(binding.toolbar);
        setContentView(R.layout.activity_carousel_preview);

        fab_scroll = (FloatingActionButton) findViewById(R.id.fab_scroll);
        fab_change_data = (FloatingActionButton) findViewById(R.id.fab_change_data);
        list_vertical = (RecyclerView) findViewById(R.id.list_vertical);
        list_horizontal = (RecyclerView) findViewById(R.id.list_horizontal);
        final TestAdapter adapter = new TestAdapter();

        // create layout manager with needed params: vertical, cycle
        initRecyclerView(list_horizontal, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false), adapter);
        initRecyclerView(list_vertical, new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true), adapter);

        // fab button will add element to the end of the list
        fab_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int itemToRemove = adapter.mItemsCount;
                if (adapter.mItemsCount != itemToRemove) {
                    adapter.mItemsCount++;
                    adapter.notifyItemInserted(itemToRemove);
                }
            }
        });

        // fab button will remove element from the end of the list
        fab_change_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int itemToRemove = adapter.mItemsCount - 1;
                if (0 <= itemToRemove) {
                    adapter.mItemsCount--;
                    adapter.notifyItemRemoved(itemToRemove);
                }
            }
        });
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, final TestAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                final int position = recyclerView.getChildLayoutPosition(v);
                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
                Toast.makeText(CarouselPreviewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    final int value = adapter.mPosition[adapterPosition];
              /*      adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
                    adapter.notifyItemChanged(adapterPosition);*/
                }
            }
        });
    }

    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {

        private final Random mRandom = new Random();
        private final int[] mColors;
        private final int[] mPosition;
        private int mItemsCount = 5;

        TestAdapter() {
            mColors = new int[mItemsCount];
            mPosition = new int[mItemsCount];
            for (int i = 0; mItemsCount > i; ++i) {
                //noinspection MagicNumber
                mColors[i] = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
                mPosition[i] = i;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView c_item_1, c_item_2;

            public MyViewHolder(View view) {
                super(view);

                c_item_1 = (TextView) view.findViewById(R.id.c_item_1);
                c_item_2 = (TextView) view.findViewById(R.id.c_item_2);
            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.c_item_1.setText(String.valueOf(mPosition[position]));
            holder.c_item_2.setText(String.valueOf(mPosition[position]));
            holder.itemView.setBackgroundColor(mColors[position]);
        }

        @Override
        public int getItemCount() {
            return mItemsCount;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return Adapter.IGNORE_ITEM_VIEW_TYPE;
        }
    }


}