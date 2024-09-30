package com.kongzue.dialog.v3;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kongzue.dialog.R;
import com.kongzue.dialog.interfaces.NoMoreClickListener;

import java.util.List;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/4/21
 * 描    述：选择对话框
 * 修订历史：
 * ================================================
 */
public class CustomSelectListDialog extends Dialog implements OnClickListener,OnItemClickListener {

    private SelectDialogListener mListener;
    private AddLogoListener mAddLogoListener;
    private DeleteItemListener mDeleteItemListener;
    private SelectDialogCancelListener mCancelListener;

    private Activity mActivity;
    private List<ItemData> mName;
    private String mTitle;
    private String mTitle_sub;
    private boolean mUseCustomColor = false;
    private int mFirstItemColor;
    private int mOtherItemColor;

    /**
     * 设置item监听事件
     */
    public interface SelectDialogListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    public interface AddLogoListener{
        void onAddLogo(View view , ItemData itemData);
    }
    public interface DeleteItemListener{
        void ondeleteItem(ItemData itemData);
    }

    /**
     * 取消事件监听接口
     */
    public interface SelectDialogCancelListener {
        void onCancelClick(View v);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param names             菜单项名称
     */
    public CustomSelectListDialog(Activity activity, int theme, SelectDialogListener listener , AddLogoListener addLogoListener, List<ItemData> names ) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mAddLogoListener = addLogoListener;
        this.mName=names;
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param cancelListener    取消事件
     * @param names             菜单项名称
     */
    public CustomSelectListDialog(Activity activity, int theme, SelectDialogListener listener, AddLogoListener addLogoListener , SelectDialogCancelListener cancelListener , List<ItemData> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mAddLogoListener = addLogoListener;
        mCancelListener = cancelListener;
        this.mName=names;
        // 设置是否点击外围不解散
        setCanceledOnTouchOutside(false);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param names             菜单项名称
     * @param title             菜单标题文字
     *
     */
    public CustomSelectListDialog(Activity activity, int theme, SelectDialogListener listener, AddLogoListener addLogoListener, DeleteItemListener deleteItemListener,List<ItemData> names, String title, String subTitle) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mAddLogoListener = addLogoListener;
        mDeleteItemListener = deleteItemListener;
        this.mName=names;
        mTitle = title;
        this.mTitle_sub = subTitle;
        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);

    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param cancelListener    取消点击事件
     * @param names             名称
     * @param title             标题
     */
    public CustomSelectListDialog(Activity activity, int theme, SelectDialogListener listener , AddLogoListener addLogoListener , SelectDialogCancelListener cancelListener, List<ItemData> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mAddLogoListener = addLogoListener;
        mCancelListener = cancelListener;
        this.mName=names;
        mTitle = title;
        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(false);
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public void setmTitle(String mTitle, String subTitle) {
        this.mTitle = mTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_select_list, null);
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        // 设置显示动画
        if (window != null) {
            window.setWindowAnimations(R.style.bottomMenuAnim);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = LayoutParams.MATCH_PARENT;
            wl.height = LayoutParams.WRAP_CONTENT;
            // 设置显示位置
            onWindowAttributesChanged(wl);
        }
        initViews();
    }

    private void initViews() {
        DialogAdapter dialogAdapter=new DialogAdapter(mName);
        ListView dialogList=(ListView) findViewById(R.id.dialog_list);
        TextView mTv_Title = (TextView) findViewById(R.id.mTv_Title);
        TextView mTv_Title_sub = (TextView) findViewById(R.id.mTv_Title_sub);
        LinearLayout mTv_Title_lay = (LinearLayout) findViewById(R.id.mTv_Title_lay);

        dialogList.setOnItemClickListener(this);
        dialogList.setAdapter(dialogAdapter);

        if(!TextUtils.isEmpty(mTitle)){
            mTv_Title_lay.setVisibility(View.VISIBLE);
            mTv_Title.setVisibility(View.VISIBLE);
            mTv_Title.setText(mTitle);
        }else{
            mTv_Title_lay.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mTitle_sub)){
            mTv_Title_sub.setText(mTitle_sub);
            mTv_Title_sub.setVisibility(View.VISIBLE);
        }else {
            mTv_Title_sub.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemClick(parent, view, position, id);
        dismiss();
    }

    private class DialogAdapter extends BaseAdapter {

        private List<ItemData> itemData;
        private ViewHolder viewholder;
        private LayoutInflater layoutInflater;
        DialogAdapter(List<ItemData> strings) {
            this.itemData = strings;
            this.layoutInflater=mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return itemData.size();
        }

        @Override
        public Object getItem(int position) {
            return itemData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                viewholder=new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.view_dialog_list_item, null);
                viewholder.dialogItemLogo=convertView.findViewById(R.id.dialog_item_logo);
                viewholder.dialogItemButton=convertView.findViewById(R.id.dialog_item_bt);
                viewholder.dialogItemDelButton= convertView.findViewById(R.id.dialog_item_delete);
                viewholder.dialogItemLay= convertView.findViewById(R.id.dialog_item_lay);
                convertView.setTag(viewholder);
            }else{
                viewholder=(ViewHolder) convertView.getTag();
            }
            if (!TextUtils.isEmpty(itemData.get(position).logoUrl)){
                if (null != mAddLogoListener){
                    mAddLogoListener.onAddLogo(viewholder.dialogItemLogo, itemData.get(position));
                }
            }
            if (null != mDeleteItemListener && itemData.get(position).isShowDel){
                viewholder.dialogItemDelButton.setVisibility(View.VISIBLE);
                viewholder.dialogItemDelButton.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(@NonNull View view) {
                        mDeleteItemListener.ondeleteItem(itemData.get(position));
                    }

                    @Override
                    protected void OnMoreErrorClick() {}
                });

            }else {
                viewholder.dialogItemDelButton.setVisibility(View.GONE);
            }
            viewholder.dialogItemButton.setText(itemData.get(position).title);
            if (!mUseCustomColor) {
                mFirstItemColor = mActivity.getResources().getColor(R.color.black);
                mOtherItemColor = mActivity.getResources().getColor(R.color.black);
            }
//            if (1 == mStrings.size()) {
//                viewholder.dialogItemButton.setTextColor(mFirstItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.shape_dialog_item_bg_only);
//            } else if (position == 0) {
//                viewholder.dialogItemButton.setTextColor(mFirstItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_top);
//            } else if (position == mStrings.size() - 1) {
//                viewholder.dialogItemButton.setTextColor(mOtherItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_buttom);
//            } else {
//            }
            viewholder.dialogItemButton.setTextColor(mOtherItemColor);
            viewholder.dialogItemLay.setBackgroundResource(R.drawable.select_dialog_item_bg_center);
            return convertView;
        }

    }

    public static class ViewHolder {
        TextView dialogItemButton;
        TextView dialogItemDelButton;
        ConstraintLayout dialogItemLay;
        ImageView dialogItemLogo;
    }

    /**
     * 设置列表项的文本颜色
     */
    public void setItemColor(int firstItemColor, int otherItemColor) {
        mFirstItemColor = firstItemColor;
        mOtherItemColor = otherItemColor;
        mUseCustomColor = true;
    }
}
