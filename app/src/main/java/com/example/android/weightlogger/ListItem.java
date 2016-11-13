package com.example.android.weightlogger;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dmitry on 25.08.2016.
 */
public class ListItem implements Parcelable{

    public static final long EMPTY_ID = -1;

    private long id = EMPTY_ID;
    private int year = 1979, month = 04, day = 23;
    private float weight = 0;

    long getId() {
        return id;
    }

    int getYear() {
        return year;
    }

    int getMonth() {
        return month;
    }

    int getDay() {
        return day;
    }

    float getWeight() {
        return weight;
    }

    void setWeight(float weight) {
        this.weight = weight;
    }

    //конструктор для новой записи
    public ListItem() {
        this.id = EMPTY_ID;
        Calendar c = Calendar.getInstance();
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH);
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.weight = 0;
    }

    //конструктор для существующей записи с разбивкой даты
    public ListItem(long id, int year, int month, int day, float weight) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.weight = weight;
    }

    //конструктор для существующей записи без разбивки даты
    public ListItem(long id, int date_int, float weight) {
        this.id = id;
        this.year = getYearFromDateInt(date_int);
        this.month = getMonthFromDateInt(date_int);
        this.day = getDayFromDateInt(date_int);
        this.weight = weight;
    }

    private ListItem(Parcel parcel) {
        this.id = parcel.readLong();
        this.year = parcel.readInt();
        this.month = parcel.readInt();
        this.day = parcel.readInt();
        this.weight = parcel.readFloat();
    }

    //функция возвращает дату в формате хранения ее в базе
    public static int dateToInt(int year, int month, int day){
        return year * 10000 + month * 100 + day;
    }

    //функция возвращает год из даты в формате хранения в базе данных
    public static int getYearFromDateInt(int date_int){
        return (int)(date_int / 10000);
    }

    //функция возвращает месяц из даты в формате хранения в базе данных
    public static int getMonthFromDateInt(int date_int){
        return (int)((date_int % 10000) / 100);
    }

    //функция возвращает месяц из даты в формате хранения в базе данных
    public static int getDayFromDateInt(int date_int){
        return (int)(date_int % 100);
    }

    public void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static String dateToString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        return sdf.format(c.getTime());
    }

    public static String dateToString(int dateInt) {
        return dateToString(getYearFromDateInt(dateInt),
                getMonthFromDateInt(dateInt),
                getDayFromDateInt(dateInt));
    }

    public String getFormattedStringDate() {
        return dateToString(year, month, day);
    }

    public int getDateInt() {
        return dateToInt(year, month, day);
    }

    public boolean isValid() {
        return weight > 0 && weight < 400;
    }

    public boolean isNew(){
        return id == EMPTY_ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(getId());
        parcel.writeInt(getYear());
        parcel.writeInt(getMonth());
        parcel.writeInt(getDay());
        parcel.writeFloat(getWeight());
    }

    public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>(){

        @Override
        public ListItem createFromParcel(Parcel parcel) {
            return new ListItem(parcel);
        }

        @Override
        public ListItem[] newArray(int i) {
            return new ListItem[i];
        }
    };

    public Date getCalendarDate() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTime();
    }
}
