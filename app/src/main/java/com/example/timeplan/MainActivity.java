// MainActivity.java
package com.example.timeplan;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TimePlanPrefs";
    private static final String KEY_DATA = "RecyclerViewData";

    private TextView tvSelectDate;
    private EditText etDescription;
    private Button btnShowDatePicker, btnSetAlarm;
    private TimePicker timePicker;
    private RecyclerView recyclerView;
    private DateTimeAdapter adapter;
    private List<DateTimeItem> dateTimeList;

    private int jam, menit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectDate = findViewById(R.id.tvSelectedDate);
        etDescription = findViewById(R.id.etDescription);
        btnShowDatePicker = findViewById(R.id.btnShowDatePicker);
        timePicker = findViewById(R.id.timePicker);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        recyclerView = findViewById(R.id.recyclerView);

        dateTimeList = loadData();
        setupRecyclerView();

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            jam = hourOfDay;
            menit = minute;
        });

        btnSetAlarm.setOnClickListener(v -> {
            String description = etDescription.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedDate = tvSelectDate.getText().toString();
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            DateTimeItem dateTimeItem = new DateTimeItem(selectedDate, jam, menit, description);
            dateTimeList.add(dateTimeItem);
            adapter.notifyDataSetChanged();
            saveData();

            Toast.makeText(MainActivity.this, "Alarm set: " + dateTimeItem.getFormattedDateTime(), Toast.LENGTH_SHORT).show();
            setTimer(dateTimeItem);
            createNotificationChannel();
        });

        btnShowDatePicker.setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupRecyclerView() {
        adapter = new DateTimeAdapter(dateTimeList, this::removeItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Mengatur orientasi horizontal
        recyclerView.setAdapter(adapter);
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvSelectDate.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Notification";
            String description = "Alarm Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("Notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setTimer(DateTimeItem dateTimeItem) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar cal_alarm = Calendar.getInstance();

        String[] dateParts = dateTimeItem.getDate().split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        cal_alarm.set(Calendar.YEAR, year);
        cal_alarm.set(Calendar.MONTH, month);
        cal_alarm.set(Calendar.DAY_OF_MONTH, day);
        cal_alarm.set(Calendar.HOUR_OF_DAY, dateTimeItem.getHour());
        cal_alarm.set(Calendar.MINUTE, dateTimeItem.getMinute());
        cal_alarm.set(Calendar.SECOND, 0);

        Intent i = new Intent(MainActivity.this, BroadCastReceiver.class);
        i.putExtra("description", dateTimeItem.getDescription());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
    }

    private void removeItem(int position) {
        dateTimeList.remove(position);
        adapter.notifyDataSetChanged();
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dateTimeList);
        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    private List<DateTimeItem> loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_DATA, null);
        Type type = new TypeToken<ArrayList<DateTimeItem>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
