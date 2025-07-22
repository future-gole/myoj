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

}
