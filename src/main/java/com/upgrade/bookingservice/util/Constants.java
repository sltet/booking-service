package com.upgrade.bookingservice.util;

import java.time.LocalTime;

public class Constants {

    public static LocalTime DEFAULT_CHECK_IN_TIME = LocalTime.NOON;
    public static LocalTime DEFAULT_CHECK_OUT_TIME = LocalTime.NOON;

    public static final int MAXIMUM_DAYS_ALLOWED_FOR_A_RESERVATION = 3;

    public static final int MAXIMAL_RESERVATION_REQUEST_MONTHS = 1;
    public static final int MINIMAL_RESERVATION_REQUEST_HOURS = 24;
}
