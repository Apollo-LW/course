package com.apollo.course.constant;

public abstract class RoutingConstant {


    public static final String COURSE_ID = "courseId";
    public static final String CHAPTER_ID = "chapterId";
    public static final String OWNER_ID = "ownerId";
    public static final String USER_ID = "userId";
    public static final String COURSE_PATH = "/course";
    public static final String CHAPTER_PATH = "/chapter";
    public static final String LECTURE_PATH = "/lecture";
    public static final String USER_PATH = "/user";
    public static final String COURSE_USER_PATH = COURSE_PATH + USER_PATH;
    public static final String COURSE_ENROLL_REQUEST = "/enroll";
    public static final String USER_ID_PATH = "/{" + USER_ID + "}";
    public static final String OWNER_ID_PATH = "/{" + OWNER_ID + "}";
    public static final String COURSE_ID_PATH = "/{" + COURSE_ID + "}";
    public static final String CHAPTER_ID_PATH = "/{" + CHAPTER_ID + "}";
    public static final String SHARE = "/share/{flag}";
    public static final String COURSE_CHAPTERS = CHAPTER_PATH + COURSE_ID_PATH;
    public static final String CHAPTER_LECTURES = CHAPTER_PATH + LECTURE_PATH + CHAPTER_ID_PATH;
    private static final String OWNER = "owner";
    private static final String MEMBER = "member";
    private static final String CHAPTER = "chapter";
    private static final String LECTURE = "lecture";
    private static final String ADD = "/add/";
    public static final String ADD_OWNER = ADD + OWNER;
    public static final String ADD_MEMBER = ADD + MEMBER;
    public static final String ADD_CHAPTER = ADD + CHAPTER;
    public static final String ADD_LECTURE = ADD + LECTURE;
    private static final String COURSE_OWNER_ID = COURSE_ID_PATH + OWNER_ID_PATH;
    public static final String COURSE_ENROLLMENT = "/enroll/requests" + COURSE_OWNER_ID;
}
