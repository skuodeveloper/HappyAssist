package com.example.skuo.happyassist.Javis.MyView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.skuo.happyassist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author wuwenqi
 */
public class MyCalendar extends LinearLayout {

    public static View viewIn;
    public static String positionIn;
    static long nd = 1000 * 24L * 60L * 60L;//一天的毫秒数
    private static Context context;
    private static String nowday = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");//日期格式化
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");//日期格式化
    private Date theInDay;
    private ArrayList<String> signday;
    private List<String> gvList;//存放天

    /**
     * 构造函数
     *
     * @param context
     */
    public MyCalendar(Context context) {
        super(context);
        MyCalendar.context = context;
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public MyCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        MyCalendar.context = context;
    }

    public void setSignDay(ArrayList<String> signday) {
        this.signday = signday;
        init();
    }

    public void setTheDay(Date dateIn) {
        this.theInDay = dateIn;
        // init();
    }

    /**
     * 初始化日期以及view等控件
     */
    private void init() {
        gvList = new ArrayList<String>();//存放天
        final Calendar cal = Calendar.getInstance();//获取日历实例
        cal.setTime(theInDay);//cal设置为当天的
        cal.set(Calendar.DATE, 1);//cal设置当前day为当前月第一天

        int tempSum = countNeedHowMuchEmpety(cal);//获取当前月第一天为星期几
        int dayNumInMonth = getDayNumInMonth(cal);//获取当前月有多少天
        setGvListData(tempSum, dayNumInMonth, cal.get(Calendar.YEAR) + "-" + getMonth((cal.get(Calendar.MONTH) + 1)));

        View view = LayoutInflater.from(context).inflate(R.layout.comm_calendar, this, true);//获取布局，开始初始化
        TextView tv_year = (TextView) view.findViewById(R.id.tv_year);
        if (cal.get(Calendar.YEAR) > new Date().getYear()) {
            tv_year.setVisibility(View.VISIBLE);
            tv_year.setText(cal.get(Calendar.YEAR) + "年");
        }
        TextView tv_month = (TextView) view.findViewById(R.id.tv_month);
        tv_month.setText(String.valueOf(theInDay.getMonth() + 1));


        MyGridView gv = (MyGridView) view.findViewById(R.id.gv_calendar);
        calendarGridViewAdapter gridViewAdapter = new calendarGridViewAdapter(gvList, signday);
        gv.setAdapter(gridViewAdapter);
    }


    /**
     * 为gridview中添加需要展示的数据
     *
     * @param tempSum
     * @param dayNumInMonth
     */
    private void setGvListData(int tempSum, int dayNumInMonth, String YM) {
        gvList.clear();
        for (int i = 0; i < tempSum; i++) {
            gvList.add(" , ");
        }
        for (int j = 1; j <= dayNumInMonth; j++) {
            gvList.add(YM + "," + String.valueOf(j));
        }
    }

    private String getMonth(int month) {
        String mon = "";
        if (month < 10) {
            mon = "0" + month;
        } else {
            mon = "" + month;
        }
        return mon;
    }

    /**
     * 获取当前月的总共天数
     *
     * @param cal
     * @return
     */
    private int getDayNumInMonth(Calendar cal) {
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取当前月第一天在第一个礼拜的第几天，得出第一天是星期几
     *
     * @param cal
     * @return
     */
    private int countNeedHowMuchEmpety(Calendar cal) {
        int firstDayInWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return firstDayInWeek;
    }


    /**
     * gridview中adapter的viewholder
     *
     * @author Administrator
     */
    static class GrideViewHolder {
        TextView tvDay, tv;
    }

    /**
     * gridview的adapter
     *
     * @author Administrator
     */
    static class calendarGridViewAdapter extends BaseAdapter {

        List<String> gvList;//存放天
        ArrayList<String> signday;

        public calendarGridViewAdapter(List<String> gvList, ArrayList<String> signday) {
            super();
            this.gvList = gvList;
            this.signday = signday;
        }

        @Override
        public int getCount() {
            return gvList.size();
        }

        @Override
        public String getItem(int position) {
            return gvList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GrideViewHolder holder;
            if (convertView == null) {
                holder = new GrideViewHolder();
                convertView = inflate(context, R.layout.adapter_calendar_item, null);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_calendar);
                holder.tvDay = (TextView) convertView.findViewById(R.id.tv_calendar_day);
                convertView.setTag(holder);
            } else {
                holder = (GrideViewHolder) convertView.getTag();
            }

            String[] date = getItem(position).split(",");
            holder.tvDay.setText(date[1]);

            if ((position + 1) % 7 == 0 || (position) % 7 == 0) {
                holder.tvDay.setTextColor(Color.parseColor("#339900"));
            }

            if (!date[1].equals(" ")) {
                String day = date[1];
                if (Integer.parseInt(date[1]) < 10) {
                    day = "0" + date[1];
                }

                if ((date[0] + "-" + day).equals(nowday)) {
                    holder.tvDay.setTextColor(Color.parseColor("#FF6600"));
                    holder.tvDay.setTextSize(15);
                    holder.tvDay.setText("今天");
                }

                if (signday.contains(getItem(position))) {
                    convertView.setBackgroundColor(Color.parseColor("#33B5E5"));
                    holder.tvDay.setTextColor(Color.WHITE);
                    holder.tvDay.setText(date[1]);
                    holder.tv.setText("签到");
                }
            }

            return convertView;
        }
    }
}
