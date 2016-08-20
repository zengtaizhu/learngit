package com.example.zengtaizhu.myapp;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by zengtaizhu on 2016/8/19.
 */
public class DialogAdapter extends Dialog{


    public DialogAdapter(Context context) {
        super(context);
    }

    public DialogAdapter(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder{
        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context){
            this.context = context;
        }

        /**
         * 从资源文件获取标题
         * @param title
         * @return
         */
        public Builder setTitle(int title){
            this.title = (String)context.getText(title);
            return this;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setContentView(Context context){
            this.context = context;
            return this;
        }

        /**
         * 通过资源文件获取“确认”按钮名
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                OnClickListener listener){
            this.negativeButtonClickListener = listener;
            this.positiveButtonText = (String)context.getText(positiveButtonText);
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                 OnClickListener listener){
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * 通过资源文件获取“取消”按钮名
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener){
            this.negativeButtonClickListener = listener;
            this.negativeButtonText = (String)context.getText(negativeButtonText);
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener){
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public DialogAdapter create(int choice){
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DialogAdapter dialog = new DialogAdapter(context, R.style.Dialog);
            View layout = null;
            //根据choice选择对应的布局
            switch (choice){
                case 0:
                    layout = inflater.inflate(R.layout.receive_dialog, null);
                    break;
                case 1:
                    layout = inflater.inflate(R.layout.receive_dialog, null);
                    break;
                case 2:
                    layout = inflater.inflate(R.layout.receive_dialog, null);
                    break;
                case 3:
                    layout = inflater.inflate(R.layout.receive_dialog, null);
                    break;
                case 4:
                default:
                    layout = inflater.inflate(R.layout.receive_dialog, null);
                    break;
            }
            dialog.addContentView(layout, new LayoutParams(MATCH_PARENT,WRAP_CONTENT));
            if(null != positiveButtonText){
                //设置按钮文字
//                ((Button) layout.findViewById(R.id.positiveButton))
//                        .setText(positiveButtonText);
                if(null != positiveButtonClickListener){
                    //绑定按钮事件
//                    ((Button) layout.findViewById(R.id.positiveButton))
//                            .setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//                                    positiveButtonClickListener.onClick(dialog,
//                                            DialogInterface.BUTTON_POSITIVE);
//                                }
//                            });
                }
            }
            else{
                //如果没有设置按钮的文字，则设置按钮不可见
//                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
