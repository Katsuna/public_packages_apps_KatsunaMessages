package gr.crystalogic.sms.dao.metadata;

public class ConversationColumns extends BaseColumns {

//4.1.1
//  0:body | 1:person | 2:sub | 3:subject | 4:retr_st | 5:type | 6:date | 7:ct_cls | 8:sub_cs | 9:_id | 10:read |
// 11:ct_l | 12:tr_id | 13:st | 14:msg_box | 15:thread_id | 16:reply_path_present | 17:m_cls | 18:read_status |
// 19:ct_t | 20:status | 21:retr_txt_cs | 22:d_rpt | 23:error_code | 24:m_id | 25:date_sent | 26:m_type | 27:v |
// 28:exp | 29:pri | 30:service_center | 31:address | 32:rr | 33:rpt_a | 34:resp_txt | 35:locked | 36:resp_st | 37:m_size |

//4.1.1 simple
// 0:_id | 1:date | 2:message_count | 3:recipient_ids | 4:snippet | 5:snippet_cs | 6:read | 7:type | 8:error | 9:has_attachment |

    public static final String BODY = "body";
    public static final String PERSON = "person";
    public static final String SUB = "sub";
    public static final String SUBJECT = "subject";
    public static final String RETR_ST = "retr_st";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String DATE_SENT = "date_sent";
    public static final String CR_CLS = "ct_cls";
    public static final String SUB_CS = "sub_cs";
    public static final String READ = "read";

    public static final String CT_T = "ct_t";
    public static final String THREAD_ID = "thread_id";
    public static final String ADDRESS = "address";

    //extra simple  uri columns
    public static final String UNREAD_COUNT = "unread_count";
    public static final String RECIPIENT_IDS = "recipient_ids";
    public static final String SNIPPET = "snippet";
    public static final String SNIPPET_CS = "snippet_cs";
    public static final String ERROR = "error";
    public static final String HAS_ATTACHMENT = "has_attachment";

    public static final String[] PROJECTION_ULTRA = new String[]{
            "body", "person", "sub", "subject", "retr_st", "type", "date", "ct_cls", "sub_cs", "_id", "read",
            "ct_l", "tr_id", "st", "msg_box", "thread_id", "reply_path_present", "m_cls", "read_status",
            "ct_t", "status", "retr_txt_cs", "d_rpt", "error_code", "m_id", "date_sent", "m_type", "v",
            "exp", "pri", "service_center", "address", "rr", "rpt_a", "resp_txt", "locked", "resp_st", "m_size"};

    public static final String[] PROJECTION_FULL = new String[]{THREAD_ID, ID, BODY, ADDRESS, PERSON, SUB, SUBJECT, RETR_ST,
            TYPE, DATE, CR_CLS, SUB_CS, READ, CT_T};

    public static final String[] PROJECTION_SIMPLE = new String[]{ID, DATE, UNREAD_COUNT, RECIPIENT_IDS, SNIPPET, SNIPPET_CS,
            READ, TYPE, ERROR, HAS_ATTACHMENT};

    public static final String[] PROJECTION_MESSAGES = new String[]{ID, ADDRESS, BODY, DATE, READ};

}
