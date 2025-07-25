package com.doublez.common.core.constants;


public class CacheConstants {
    public final static String Login_Token_Key = "logintoken:";
    public final static Long Login_Token_expire = 720L;
    public final static Long LOGIN_TOKEN_REFRESH = 180L;

    public final static String EMAIL_CODE_KEY = "e:c:";//email:count 当日发生数量
    public final static String EMAIL_CODE_KEY_LIMIT = "e:t:"; // email:limit 发生邮件间隔时长

    public final static String EXAM_UNFINISHED_LIST = "e:t:l"; // 未完赛竞赛列表

    public final static String EXAM_HISTORY_LIST = "e:h:l";  // 历史竞赛列表

    public final static String EXAM_DETAIL = "e:d:";    //竞赛详情信息

    public static final String USER_MESSAGE_LIST = "u:m:l:";

    public static final String MESSAGE_DETAIL = "m:d:";

    public static final String EXAM_RANK_LIST = "e:r:l:";

    public final static String USER_EXAM_LIST = "u:e:l:";   //用户竞赛列表

    public final static String USER_DETAIL = "u:d:";   //用户详情信息

    public final static long USER_EXP = 10;//用户信息过期时间

    public static final String USER_UPLOAD_TIMES_KEY = "u:u:t";

    public static final String QUESTION_LIST = "q:l"; // 题目列表

    public static final String QUESTION_HOST_LIST = "q:h:l";

    public static final long DEFAULT_START = 0;

    public static final long DEFAULT_END = -1;

}
