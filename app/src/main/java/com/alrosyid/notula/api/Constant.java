package com.alrosyid.notula.api;

public class Constant {
    public static final String URL = "https://app.jeevva.my.id/";
//    public static final String URL = "http://10.0.2.2:81/";
//    public static final String URL = "http://192.168.100.4:81/";
//public static final String URL = "http://192.168.100.116:81/";
//    public static final String URL = "http://192.168.64.133:81/";
//    public static final String URL = "http://10.100.141.228:81/";
//    public static final String EXPORT= URL+"generate-pdf";
    //    public static final String URL = "http://172.10.52.31:81/";


    //Root
    public static final String HOME = URL + "api";
    public static final String VERSION = HOME + "/version";
    //auth
    public static final String LOGIN = HOME + "/login";
    public static final String LOGOUT = HOME + "/logout";
    public static final String REGISTER = HOME + "/register";
    public static final String ACCOUNT = HOME + "/myAccount";
    public static final String CHANGE_PASSWORD = HOME + "/changePassword";
    public static final String UPDATE_USER = HOME + "/userUpdate";


    //Notes
    public static final String NOTES = HOME + "/notes";
    public static final String LIST_NOTES = NOTES + "/listNotes";
    public static final String DETAIL_NOTES = NOTES + "/detailNotes/";
    public static final String DELETE_NOTES = NOTES + "/delete";
    public static final String CREATE_NOTES = NOTES + "/create";
    public static final String UPDATE_NOTES = NOTES + "/update";

    //Meetings
    public static final String MEETING = HOME + "/meetings";
    public static final String LIST_MEETING = MEETING + "/listMeetings";
    public static final String DETAIL_MEETING = MEETING + "/detailMeetings/";
    public static final String DELETE_MEETINGS = MEETING + "/delete";
    public static final String CREATE_MEETINGS = MEETING + "/create";
    public static final String UPDATE_MEETINGS = MEETING + "/update";
    public static final String LIST_MEETING_TODAY = MEETING + "/listMeetingsToday";
    public static final String SPINNER_MEETING = MEETING + "/spinners";
    public static final String JSON_ARRAY = "meetings";
    public static final String MT_TITLE = "title";
    public static final String MT_ID = "id";
    public static final String NAME = "name";


    //Attendances
    public static final String ATTENDANCES = HOME + "/attendances";
    public static final String LIST_ATTENDANCES = ATTENDANCES + "/listAttendances/";
    public static final String DETAIL_ATTENDANCES = ATTENDANCES + "/detailAttendances/";
    public static final String DELETE_ATTENDANCES = ATTENDANCES + "/delete";
    public static final String CREATE_ATTENDANCES = ATTENDANCES + "/create";
    public static final String UPDATE_ATTENDANCES = ATTENDANCES + "/update";
    //Notula
    public static final String NOTULA = HOME + "/notulas";
    public static final String LIST_NOTULA = NOTULA + "/listNotulas/";
    public static final String DETAIL_NOTULA = NOTULA + "/detailNotulas/";
    public static final String CREATE_NOTULA = NOTULA + "/create";
    public static final String DELETE_NOTULA = NOTULA + "/delete";
    public static final String UPDATE_NOTULA = NOTULA + "/update";
    //Points
    public static final String POINTS = HOME + "/points";
    public static final String LIST_POINTS = POINTS + "/listPoints/";
    public static final String DETAIL_POINTS = POINTS + "/detailPoints/";
    public static final String CREATE_POINTS = POINTS + "/create";
    public static final String DELETE_POINTS = POINTS + "/delete";
    public static final String UPDATE_POINTS = POINTS + "/update";
    //FollowUP
    public static final String FOLLOW_UP = HOME + "/followUp";
    public static final String LIST_FOLLOW_UP = FOLLOW_UP + "/listFollowUp/";
    public static final String DETAIL_FOLLOW_UP = FOLLOW_UP + "/detailFollowUp/";
    public static final String CREATE_FOLLOW_UP = FOLLOW_UP + "/create";
    public static final String DELETE_FOLLOW_UP = FOLLOW_UP + "/delete";
    public static final String UPDATE_FOLLOW_UP = FOLLOW_UP + "/update";
    //Photos
    public static final String PHOTOS = HOME + "/photos";
    public static final String LIST_PHOTOS = PHOTOS + "/listPhotos/";
    public static final String DETAIL_PHOTOS = PHOTOS + "/detailPhotos/";
    public static final String CREATE_PHOTOS = PHOTOS + "/create";
    public static final String DELETE_PHOTOS = PHOTOS + "/delete";
    public static final String UPDATE_PHOTOS = PHOTOS + "/update";
    //Records
    public static final String RECORDS = HOME + "/records";
    public static final String LIST_RECORDS = RECORDS + "/listRecords/";
    public static final String DETAIL_RECORDS = RECORDS + "/detailRecords/";
    public static final String CREATE_RECORDS = RECORDS + "/create";
    public static final String DELETE_RECORDS = RECORDS + "/delete";
    public static final String UPDATE_RECORDS = RECORDS + "/update";


}
