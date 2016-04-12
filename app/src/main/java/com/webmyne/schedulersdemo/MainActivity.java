package com.webmyne.schedulersdemo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtDate, edtTime;
    private Button btnSetAlarm;
    private RadioGroup repeatOptions;
    private RadioButton checkboxRepeatDaily, checkboxRepeatWeekly;
    private boolean isOnce = true, isDaily = false, isWeekly = false, isCustom = false;
    private FlowLayout weekDaysLayout;
    private static final String[] WEEK_DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private List<Integer> customDays = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtDate = (EditText) findViewById(R.id.edtDate);
        edtDate.setOnClickListener(this);
        edtTime = (EditText) findViewById(R.id.edtTime);
        edtTime.setOnClickListener(this);
        btnSetAlarm = (Button) findViewById(R.id.btnSetAlarm);
        btnSetAlarm.setOnClickListener(this);

        repeatOptions = (RadioGroup) findViewById(R.id.repeatOptions);
        checkboxRepeatDaily = (RadioButton) findViewById(R.id.checkboxRepeatDaily);
        checkboxRepeatWeekly = (RadioButton) findViewById(R.id.checkboxRepeatWeekly);
        weekDaysLayout = (FlowLayout) findViewById(R.id.weekDaysLayout);

        repeatOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                if (radioButton.getText().toString().equalsIgnoreCase("Schedule Once")) {
                    isOnce = true;
                    isDaily = false;
                    isWeekly = false;
                    isCustom = false;
                    edtDate.setEnabled(true);
                    weekDaysLayout.removeAllViews();
                } else if (radioButton.getText().toString().equalsIgnoreCase("Repeat Daily")) {
                    isDaily = true;
                    isWeekly = false;
                    isCustom = false;
                    edtDate.setEnabled(false);
                    weekDaysLayout.removeAllViews();
                } else if (radioButton.getText().toString().equalsIgnoreCase("Repeat Weekly")) {
                    isWeekly = true;
                    isDaily = false;
                    isCustom = false;
                    edtDate.setEnabled(false);
                    weekDaysLayout.removeAllViews();
                } else if (radioButton.getText().toString().equalsIgnoreCase("Schedule Custom")) {
                    isCustom = true;
                    isWeekly = false;
                    isDaily = false;
                    edtDate.setEnabled(false);
                    initWeekDays();
                }
            }
        });
    }


    private void setAlarm() {
        String[] time = edtTime.getText().toString().trim().split(":");
        String[] date = edtDate.getText().toString().trim().split("-");

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (isDaily) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            Random random = new Random();
            int RQS_1 = random.nextInt(9999 - 1000) + 1000;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, RQS_1, intent, PendingIntent.FLAG_CANCEL_CURRENT);


            Calendar calendar = Calendar.getInstance();
          //  calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            Log.e("ALARM", "Repeat Daily Alarm set at: " + time);
            Toast.makeText(this, "Repeat Daily Alarm set at: " + time, Toast.LENGTH_LONG).show();
        } else if (isWeekly) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            Random random = new Random();
            int RQS_1 = random.nextInt(9999 - 1000) + 1000;
            Log.e("UNIQUE_ID", RQS_1+"");
            intent.putExtra("ID", RQS_1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, RQS_1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar c = Calendar.getInstance();
            /*final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;
            try {
                date1 = dateFormatter.parse(edtDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTime(date1);*/
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Log.e("DAY_OF_WEEK ", "" + dayOfWeek);
            long triggerTime = System.currentTimeMillis();
            setDayOfWeekWiseAlarm(time, pendingIntent, alarmManager, triggerTime, dayOfWeek);
        } else if (isCustom) { //custom days -> user defined
            long triggerTime = System.currentTimeMillis();
            for (int i = 0; i < customDays.size(); i++) {
                Log.e("day ", "" + customDays.get(i));

                Intent intent = new Intent(this, AlarmReceiver.class);
                Random random = new Random();
                int RQS_1 = random.nextInt(9999 - 1000) + 1000;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, RQS_1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                setDayOfWeekWiseAlarm(time, pendingIntent, alarmManager, triggerTime, customDays.get(i));
            }

        } else if (isOnce) { // only once on the specified date
            Intent intent = new Intent(this, AlarmReceiver.class);
            Random random = new Random();
            int RQS_1 = random.nextInt(9999 - 1000) + 1000;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, RQS_1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            setOnlySelectedDateAlarm(time, date, pendingIntent, alarmManager);
        }
    }

    private void setDayOfWeekWiseAlarm(String[] time, PendingIntent pendingIntent, AlarmManager alarmManager, long triggerTime,  int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        //long interval = calendar.getTimeInMillis() + 604800000L;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        final SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Log.e("ALARM", "Repeat Weekly Alarm set at: " + dateFormatter1.format(calendar.getTime()));
        Toast.makeText(this, "Repeat Weekly Alarm set at: " + dateFormatter1.format(calendar.getTime()), Toast.LENGTH_LONG).show();
    }

    private void setOnlySelectedDateAlarm(String[] time, String[] date, PendingIntent pendingIntent, AlarmManager alarmManager) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, Integer.parseInt(date[0]));
        calendar.set(Calendar.MONTH, (Integer.parseInt(date[1])) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.e("ALARM", "Single Alarm set at: " + calendar.getTimeInMillis());
        Toast.makeText(this, "Single Alarm set at: " + calendar.getTimeInMillis(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetAlarm:
                if(edtTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, "Please Select Time", Toast.LENGTH_SHORT).show();
                } else {
                    setAlarm();
                }
                break;
            case R.id.edtDate:
                setDate();
                break;
            case R.id.edtTime:
                setTime();
                break;
        }
    }

    private void setDate() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar newCalendar = Calendar.getInstance();
        int year, month, day;
        int mYear, mMonth, mDay;

        year = newCalendar.get(Calendar.YEAR);
        month = newCalendar.get(Calendar.MONTH);
        day = newCalendar.get(Calendar.DAY_OF_MONTH);

        mYear = newCalendar.get(Calendar.YEAR);
        mMonth = newCalendar.get(Calendar.MONTH);
        mDay = newCalendar.get(Calendar.DAY_OF_MONTH);

        if (edtDate.getText().toString().trim().length() != 0) {
            String[] date = edtDate.getText().toString().trim().split("-");
            year = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]) - 1;
            day = Integer.parseInt(date[2]);
        }

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, year, month, day);
        fromDatePickerDialog.show();
        fromDatePickerDialog.setCancelable(true);
        try {
            String today = mYear + "-" + mMonth + "-" + mDay;
            fromDatePickerDialog.getDatePicker().setMinDate(dateFormatter.parse(today).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        if (edtTime.getText().toString().trim().length() != 0) {
            String[] time = edtTime.getText().toString().trim().split(":");
            mHour = Integer.parseInt(time[0]);
            mMinute = Integer.parseInt(time[1]);
        }

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edtTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
            }
        }, mHour, mMinute, false);
        tpd.show();
    }

    private void initWeekDays() {
        weekDaysLayout.removeAllViews();

        for (int i = 0; i < WEEK_DAYS.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.week_check_item, null);
            CheckBox checkBox = (CheckBox) view;
            checkBox.setText(WEEK_DAYS[i]);
            checkBox.setOnCheckedChangeListener(new OnWeekDaySelected(i + 1, WEEK_DAYS[i]));
            weekDaysLayout.addView(checkBox);
        }
    }

    public class OnWeekDaySelected implements CompoundButton.OnCheckedChangeListener {
        private int dayId;
        private String dayName;

        public OnWeekDaySelected(int dayId, String dayName) {
            this.dayId = dayId;
            this.dayName = dayName;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(MainActivity.this, this.dayId + " " + this.dayName + " " + isChecked, Toast.LENGTH_SHORT).show();
            if (isChecked) {
                customDays.add(dayId);
            } else {
                customDays.remove(customDays.indexOf(dayId));
            }
        }
    }
}
