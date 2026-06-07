package com.aistore.module.checkin.enums;

/** 打卡出勤状态：正常 / 迟到 / 缺勤 / 请假（存库为枚举名 VARCHAR） */
public enum CheckinStatus {
    NORMAL,
    LATE,
    ABSENT,
    LEAVE
}
