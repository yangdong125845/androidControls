package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dingda.app.R;
import com.jtkj.manager.NetConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import utils.AppConfig;
import utils.AppUtils;
import utils.DensityUtils;

/**
 * Created by yangdong on 2017/8/3.
 */

public class DingTabView extends RelativeLayout   {

    View mainView,animatorView,backView;
    int w, left, position = 0;
    TextView lastTextView, nowTextView, pikeBike, bthBike, powBike;
    TabClickListener tabClickListener;
    Context mContext;
     int animatorId;
    String nowTypeName;
   List<String> types = new ArrayList<String>();
    List<Integer>  tabIds = new ArrayList<Integer>();
    Map<Integer,String> tabIdType = new HashMap<Integer,String>();
   public  LinearLayout bgView,tabView;

    public DingTabView(Context context) {
        this(context, null);
    }

    public DingTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DingTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        backView = layoutInflater.inflate(R.layout.animator_layout, this);
        mainView = layoutInflater.inflate(R.layout.tab_layout, this);
        w = context.getResources().getDisplayMetrics().widthPixels;
        left = DensityUtils.dp2px(context, 10);
        setType(NetConfig.getServiceType());
    }

    public void setType(String typesInfo) {
        types.clear();
        tabIdType.clear();
        tabIds.clear();
        bgView = (LinearLayout) backView.findViewById(R.id.bgView);
        tabView = (LinearLayout) mainView.findViewById(R.id.tabView);
        bgView.removeAllViews();
        tabView.removeAllViews();
        int typesCount = -1;
        if(typesInfo.indexOf("1")!=-1) {
            types.add(++typesCount,"公共单车");
        }
        if(typesInfo.indexOf("3")!=-1) {
            types.add(++typesCount,"共享单车");
        }
        if(typesInfo.indexOf("3")==-1&&typesInfo.indexOf("5") !=-1) {
            types.add(++typesCount,"共享单车");
        }
        if(typesInfo.indexOf("4") !=-1) {
            types.add(++typesCount,"电动助力车");
        }
        if(typesInfo.indexOf("9") !=-1) {
            types.add(++typesCount,"公交");
        }

        Log.e("DingTabView", "typesInfo: " +typesInfo+"   types: "+types.toString());
        for(int i= 0;i<types.size();i++){
            bgView.addView(addBgView(i));
            tabView.addView(addTab(i,types.get(i)));
        }
        if(bgView.getChildCount() ==1) {
            bgView.setVisibility(View.GONE);
            tabView.setVisibility(View.GONE);
        }else {
            bgView.setVisibility(View.VISIBLE);
            tabView.setVisibility(View.VISIBLE);
            animatorView = findViewById(animatorId);
            nowTextView = lastTextView = (TextView) mainView.findViewById(tabIds.get(0));
            for(final Map.Entry<Integer,String> entry :tabIdType.entrySet()) {
                findViewById(entry.getKey()).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeState(findViewById(entry.getKey()).getLeft());
                        nowTextView = (TextView) findViewById(entry.getKey());
                        nowTypeName = entry.getValue();
                        if(tabClickListener!=null) {
                            tabClickListener.click(entry.getValue());
                        }

                    }
                });
            }
        }

    }

    public void setTabClickListener(TabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }



    private void changeState(int x) {

        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(animatorView, "x", animatorView.getX(), x)
                .setDuration(300);
        xAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if (nowTextView != null) {
                    nowTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                lastTextView = nowTextView;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (lastTextView != null) {
                    lastTextView.setTextColor(getResources().getColor(R.color.editHintColor));
                }
            }
        });


        xAnimator.start();
    }

    public interface TabClickListener {
        void click(String type);
    }




    private View addBgView(int position) {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        viewParams.weight = 1;
        viewParams.setMargins(AppUtils.dip2px(mContext,15),0,AppUtils.dip2px(mContext,15),0);
        View view = new View(mContext);
        view.setLayoutParams(viewParams);
        if(position ==0) {
            animatorId = AppUtils.generateViewId();
            view.setId(animatorId);
            view.setBackground(getResources().getDrawable(R.drawable.bike_type));
        }
        return view;
    }

    private TextView   addTab(int position,String type) {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        viewParams.weight = 1;
        viewParams.setMargins(AppUtils.dip2px(mContext,15),0,AppUtils.dip2px(mContext,15),0);
        TextView textview = new TextView(mContext);
        textview.setLayoutParams(viewParams);
        textview.setText(type);
        textview.setGravity(Gravity.CENTER);
        int tabId = AppUtils.generateViewId();
        tabIds.add(tabId);
        tabIdType.put(tabId,type);
        textview.setId(tabId);
        if(position ==0) {
            textview.setTextColor(getResources().getColor(R.color.colorWhite));
        }else {
            textview.setTextColor(getResources().getColor(R.color.editHintColor));
        }
        textview.setTextSize(14);
         return textview;
    }
}
