package com.dev.logBook.services.utils;

import com.dev.logBook.entities.User;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;

import java.util.UUID;

public class CheckOwnership {
    public static boolean checkOwnership(User user, UUID objAuthorId) {
        UUID userId = user.getId();
        if (userId.equals(objAuthorId)) return true;
        throw new UnauthorizedAccessException("You are not authorized to update this object. It does not belong to you");
    }
}
