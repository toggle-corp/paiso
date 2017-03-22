package com.togglecorp.paiso.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.PendingTransactionsAdapter;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.helpers.ThemeUtils;

public class PendingTransactionsActivity extends AppCompatActivity {

    private DbHelper mDbHelper;

    private RecyclerView mNotificationsRecyclerView;
    private PendingTransactionsAdapter mPendingTransactionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mDbHelper = new DbHelper(this);

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle("Pending Approval");

        // Recycler view and adapter
        mPendingTransactionsAdapter = new PendingTransactionsAdapter(this, mDbHelper);
        mNotificationsRecyclerView = (RecyclerView) findViewById(R.id.notifications_recyclerview);
        mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNotificationsRecyclerView.setAdapter(mPendingTransactionsAdapter);

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mNotificationsRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private ItemTouchHelper.Callback mItemTouchHelperCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return  makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            ((PendingTransactionsAdapter.ViewHolder) viewHolder).approveSelected();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            c.clipRect(0, 0, dX, itemView.getBottom());

            Paint paint = new Paint();
            paint.setColor(ThemeUtils.getThemeColor(PendingTransactionsActivity.this, R.attr.colorAccent));
            c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);

            Drawable drawable = ContextCompat.getDrawable(PendingTransactionsActivity.this, R.drawable.ic_check);
            drawable.setBounds(dpToPx(28), dpToPx(28), itemView.getBottom() - dpToPx(28), itemView.getBottom() - dpToPx(28));
            drawable.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private int dpToPx(int dp) {
        return ThemeUtils.convertDpToPx(this, dp);
    }
}