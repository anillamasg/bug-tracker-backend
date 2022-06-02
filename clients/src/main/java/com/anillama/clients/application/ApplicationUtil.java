package com.anillama.clients.application;

public final class ApplicationUtil {
    public static final String CREATION = "creation";
    public static final String UPDATE = "update";
    public static final String RETRIEVAL = "retrieval";
    public static final String DELETION = "deletion";
    public static final String VALID = "valid";
    public static final String INVALID = "invalid";
    public static final String ADMIN = "admin";
    public static final String ACCESS = "access";
    public static final String REVOKE = "revoke";

    public static final String TICKET = "Ticket ";
    public static final String PROJECT = "Project ";
    public static final String PROFILE = "Profile ";
    public static final String APPLICATION_USER = "User ";

    public static final String AUTHORIZATION = "Authorization";

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    public static final String INTERNAL_CHECK_SESSION_ROUTING_KEY = "internal.checkSession.routing-key";
    public static final String INTERNAL_REGISTER_SESSION_ROUTING_KEY = "internal.registerSession.routing-key";
    public static final String INTERNAL_REMOVE_ALL_TICKETS_BY_PROJECT_ROUTING_KEY = "internal.removeAllTicketsByProject.routing-key";
    public static final String INTERNAL_REGISTER_USER_PROJECT_ROUTING_KEY = "internal.registerUserProject.routing-key";
    public static final String INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY = "internal.checkUserProject.routing-key";
    public static final String INTERNAL_REMOVE_USER_PROJECT_ROUTING_KEY = "internal.removeUserProject.routing-key";
    public static final String INTERNAL_REMOVE_ALL_USER_PROJECTS_BY_PROJECT_ROUTING_KEY = "internal.removeAllUserProjectsByProject.routing-key";
    public static final String INTERNAL_REMOVE_ALL_USER_PROJECTS_BY_USER_ROUTING_KEY = "internal.removeAllUserProjectsByUser.routing-key";
    public static final String INTERNAL_GET_ALL_PROJECTS_FOR_USER_ROUTING_KEY = "internal.getAllProjectsForUser.routing-key";
    public static final String INTERNAL_GET_ALL_USERS_OF_PROJECT_ROUTING_KEY = "internal.getAllUsersOfProject.routing-key";
    public static final String INTERNAL_PROJECT_EXISTS_ROUTING_KEY = "internal.projectExists.routing-key";
    public static final String INTERNAL_REMOVE_PROFILE_ROUTING_KEY = "internal.removeProfile.routing-key";
    public static final String INTERNAL_GET_NAME_PROFILE_ROUTING_KEY = "internal.getNameProfile.routing-key";
    public static final String INTERNAL_CREATE_PROFILE_FROM_QUEUE_ROUTING_KEY = "internal.createProfileFromQueue.routing-key";

    public static String invalidUserFailed(String serviceName, String operation) {
        return "Invalid user. " + serviceName + operation + " failed.";
    }

    public static String unauthorizedUserFailed(String serviceName, String operation) {
        return "User unauthorized. " + serviceName + operation + " failed.";
    }

    public static String serviceSuccessful(String serviceName, String operation) {
        return serviceName + operation + " successful.";
    }

    public static String serviceDoesNotExist(String serviceName, String operation) {
        return serviceName + "does not exist. " + serviceName + operation + " failed.";
    }

    public static String serviceAlreadyExists(String serviceName, String operation) {
        return serviceName + "already exists. " + serviceName + operation + " failed.";
    }
}
