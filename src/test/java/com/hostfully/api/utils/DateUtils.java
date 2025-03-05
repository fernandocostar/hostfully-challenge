package com.hostfully.api.utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.github.javafaker.Faker;

public class DateUtils {

    private static final Faker FAKER_INSTANCE = new Faker();

    /**
     * Converts a date string to a list of integers [year, month, day].
     * @param dateString The date string in "yyyy-MM-dd" format.
     * @return A list containing the year, month, and day as integers.
     */
    public static List<Integer> castToDateList(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        return Arrays.asList(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * Generates a random start date from today, within a specified range of days.
     * @return A LocalDate object representing a random start date.
     */
    public static LocalDate getStartDate() {
        return LocalDate.now().plusDays(FAKER_INSTANCE.number().numberBetween(1, 30000));
    }

    /**
     * Generates a random end date by adding 2 or 3 days to the given start date.
     * @param startDate The start date to calculate the end date from.
     * @return A LocalDate object representing a random end date.
     */
    public static LocalDate getEndDate(LocalDate startDate) {
        return startDate.plusDays(FAKER_INSTANCE.number().numberBetween(2, 3));
    }

    /**
     * Calculates the start date by subtracting 2 or 3 days from the given end date.
     * @param endDate The end date to calculate the start date from.
     * @return A LocalDate object representing the calculated start date.
     */
    public static LocalDate calculateStartDateFromEndDate(LocalDate endDate) {
        return endDate.plusDays(FAKER_INSTANCE.number().numberBetween(-3, -2)); // Adjusted range for clarity
    }

    /**
     * Adds a specified number of days to a given date.
     * @param date The base date.
     * @param days The number of days to add.
     * @return A LocalDate object representing the resulting date.
     */
    public static LocalDate addDaysToDate(LocalDate date, int days) {
        return date.plusDays(days);
    }
}
