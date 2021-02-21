package com.apollo.course.constant;

public interface RoutingConstant {

    String COURSE_ID = "courseId";
    String CHAPTER_ID = "chapterId";
    String OWNER_ID = "ownerId";
    String USER_ID = "userId";
    String FLAG = "flag";
    String COURSE_PATH = "/course";
    String CHAPTER_PATH = "/chapter";
    String LECTURE_PATH = "/lecture";
    String USER_PATH = "/user";
    String COURSE_USER_PATH = COURSE_PATH + USER_PATH;
    String COURSE_ENROLL_REQUEST = "/enroll";
    String USER_ID_PATH = "/{" + USER_ID + "}";
    String OWNER_ID_PATH = "/{" + OWNER_ID + "}";
    String COURSE_ID_PATH = "/{" + COURSE_ID + "}";
    String CHAPTER_ID_PATH = "/{" + CHAPTER_ID + "}";
    String FLAG_PATH = "/{" + FLAG + "}";
    String COURSE_CHAPTERS = CHAPTER_PATH + COURSE_ID_PATH;
    String CHAPTER_LECTURES = CHAPTER_PATH + LECTURE_PATH + CHAPTER_ID_PATH;
    String CHAPTER_OWNER_ID_PATH = CHAPTER_ID_PATH + OWNER_ID_PATH;
    String OWNER = "/owner";
    String MEMBER = "/member";
    String CHAPTER = "/chapter";
    String LECTURE = "/lecture";
    String ADD = "/add";
    String ADD_OWNER = ADD + OWNER + FLAG_PATH;
    String ADD_MEMBER = ADD + MEMBER + FLAG_PATH;
    String ADD_CHAPTER = ADD + CHAPTER + FLAG_PATH;
    String ADD_LECTURE = ADD + LECTURE + FLAG_PATH;
    String COURSE_OWNER_ID = COURSE_ID_PATH + OWNER_ID_PATH;
    String COURSE_ENROLLMENT = "/enroll/requests" + COURSE_OWNER_ID;
}
