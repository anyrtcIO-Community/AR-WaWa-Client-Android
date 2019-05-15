package org.anyrtc.wawa;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by liuxiaozhong on 2017/12/7.
 */

public class RoomAdapter extends BaseQuickAdapter<RoomBean.DataBean.RoomlistBean,BaseViewHolder> {
    public static final int TYPE_1 = 0;
    public RoomAdapter() {
        super(R.layout.room_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomBean.DataBean.RoomlistBean item) {
        helper.setText(R.id.tv_name,item.getRoom_username());
        helper.setText(R.id.tv_num,item.getRoom_member()+"");
        TextView textView=helper.getView(R.id.state);
        ImageView state=helper.getView(R.id.iv_state);
        if (item.getRoom_state()==0){//正常
                if (item.getRoom_member()==0){
                    state.setImageResource(R.drawable.icon_kongxian);
                    textView.setTextColor(Color.parseColor("#3bc2fe"));
                    textView.setText("空闲中");
                }else {
                    state.setImageResource(R.drawable.icon_gameing);
                    textView.setTextColor(Color.parseColor("#ff7522"));
                    textView.setText("游戏中");
                }

        }else if (item.getRoom_state()==1){
            state.setImageResource(R.drawable.icon_weihuing);
            textView.setTextColor(Color.parseColor("#aaaaaa"));
            textView.setText("维护中");
        }

    }
    @Override
    public int getItemViewType(int position) {
            return TYPE_1;
    }
}
