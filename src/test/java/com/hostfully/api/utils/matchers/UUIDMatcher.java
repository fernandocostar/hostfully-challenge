package com.hostfully.api.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import java.util.UUID;

public class UUIDMatcher extends TypeSafeMatcher<String> {

    @Override
    protected boolean matchesSafely(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a valid UUID");
    }

    public static UUIDMatcher isValidUUID() {
        return new UUIDMatcher();
    }
}
