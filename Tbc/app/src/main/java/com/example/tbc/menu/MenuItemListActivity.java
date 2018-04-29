package com.example.tbc.menu;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tbc.R;


public class MenuItemListActivity extends ListActivity {

    private ListAddLayoutAdapter mAdapter;
    MenuItem[] menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //NoteManager noteManager = new NoteManager(this,Config.TABLE_NAME_NOTE);
        //notes = (ArrayList<Note>) noteManager.query(null);
        //noteManager.closeDB();

        //notes.add(new Note(getString(R.string.add_hint)));

        menus = MenuUtils.getMenuItems();

        mAdapter = new ListAddLayoutAdapter(this);

        this.setListAdapter(mAdapter);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                mAdapter.notifyDataSetChanged();
                doClickItem(position);
            }

            private void doClickItem(int position){

                Class<?> target = menus[position].getTarget();

                if(target != null) {
                    Context ctx = MenuItemListActivity.this;
                    Intent intent = new Intent(ctx,target);
                    startActivity(intent);
                }
            }
        });
    }

    /*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HolloMainActivity.class);
        startActivity(intent);
        finish();
    }
    */

    public class ListAddLayoutAdapter extends BaseAdapter {

        private Context context;

        public ListAddLayoutAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return menus.length;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View arg1, ViewGroup arg2) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(0, 8, 0, 8);

            layout.addView(addTitleView(position));

            return layout;
        }

        private View addTitleView(int i) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView iv = new ImageView(context);

            int resId = R.drawable.tbc;

            String icon = menus[i].getIcon();
            if(icon == null || icon.length() <= 0){
                icon = "ic_action_yinyang";
            }

            resId = getResources().getIdentifier(icon, "drawable" ,getPackageName());

            iv.setImageResource(resId);

            /*layout.addView(iv, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            */

            /**/

            TextView space = new TextView(context);
            space.setText(menus[i].getType() == 0?"    ":"");
            space.setTextSize(18f);

            layout.addView(space, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            /*icon*/
           /* if(false) {
                layout.addView(iv, new LinearLayout.LayoutParams(48,48));
            }else{
                layout.addView(iv, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            */
            TextView tv = new TextView(context);
            tv.setText(menus[i].getName());


            if(menus[i].getType() != 0){
                layout.addView(iv, new LinearLayout.LayoutParams(48,48));
                tv.setTextSize(20f);
            }else{
                layout.addView(iv, new LinearLayout.LayoutParams(48,48));
                tv.setTextSize(16f);
                tv.setTextColor(0xff606060);
            }

           layout.addView(tv, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            layout.setGravity(Gravity.LEFT);

            return layout;
        }
    }
}
