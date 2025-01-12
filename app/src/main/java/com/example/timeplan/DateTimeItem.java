package com.example.timeplan;

public class DateTimeItem {
    private final String date; // Tanggal dalam format DD/MM/YYYY
    private final int hour;   // Jam
    private final int minute; // Menit
    private final String description; // Deskripsi

    // Konstruktor untuk inisialisasi
    public DateTimeItem(String date, int hour, int minute, String description) {
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.description = description;
    }

    // Getter untuk tanggal
    public String getDate() {
        return date;
    }

    // Getter untuk jam
    public int getHour() {
        return hour;
    }

    // Getter untuk menit
    public int getMinute() {
        return minute;
    }

    // Getter untuk deskripsi
    public String getDescription() {
        return description;
    }

    // Format gabungan tanggal dan waktu
    public String getFormattedDateTime() {
        return String.format("%s %02d:%02d", date, hour, minute);
    }
}
