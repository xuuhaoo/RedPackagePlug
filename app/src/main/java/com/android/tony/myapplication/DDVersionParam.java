package com.android.tony.myapplication;


public class DDVersionParam {

    public static final String DD_PACKAGE_NAME = "com.alibaba.android.rimet";

    public static String ApiEventListener = "bmk";
    public static String ConversationChangeMaid = "cdz";
    public static String ServiceFactory = "gcz";
    public static String RedPacketsRpc = "bes";
    public static String MessageDs = "fmf";

    public static void init(String version) {
        switch (version) {
            case "3.0.0":
                ApiEventListener = "afm";
                ConversationChangeMaid = "anv";
                ServiceFactory = "cvs";
                RedPacketsRpc = "abc";
                break;
            case "3.0.1":
                ApiEventListener = "afm";
                ConversationChangeMaid = "anx";
                ServiceFactory = "cvx";
                RedPacketsRpc = "abc";
                break;
            case "3.0.3":
                ApiEventListener = "afn";
                ConversationChangeMaid = "any";
                ServiceFactory = "cwa";
                RedPacketsRpc = "abd";
                break;
            case "3.1.0":
                ApiEventListener = "age";
                ConversationChangeMaid = "aoq";
                ServiceFactory = "cxg";
                RedPacketsRpc = "abt";
                break;
            case "3.1.1":
            case "3.1.2":
                ApiEventListener = "agi";
                ConversationChangeMaid = "apg";
                ServiceFactory = "cxt";
                RedPacketsRpc = "abx";
                break;
            case "3.2.0":
                ApiEventListener = "ajt";
                ConversationChangeMaid = "asv";
                ServiceFactory = "dce";
                RedPacketsRpc = "aff";
                break;
            case "3.3.0":
            case "3.3.1":
                ApiEventListener = "alm";
                ConversationChangeMaid = "avq";
                ServiceFactory = "dhp";
                RedPacketsRpc = "agr";
                MessageDs = "cuw";
                break;
            case "3.3.3":
            case "3.3.5":
                ApiEventListener = "amx";
                ConversationChangeMaid = "axm";
                ServiceFactory = "dkk";
                RedPacketsRpc = "ahy";
                MessageDs = "cwz";
                break;
            case "3.4.0":
                ApiEventListener = "asi";
                ConversationChangeMaid = "bdz";
                ServiceFactory = "dht";
                RedPacketsRpc = "ana";
                MessageDs = "cuy";
                break;
            case "3.4.6":
                ApiEventListener = "arw";
                ConversationChangeMaid = "bfk";
                ServiceFactory = "dkv";
                RedPacketsRpc = "amj";
                MessageDs = "cxw";
                break;
            case "3.4.8":
                ApiEventListener = "ayj";
                ConversationChangeMaid = "bme";
                ServiceFactory = "dsp";
                RedPacketsRpc = "asq";
                MessageDs = "dey";
                break;
            case "3.4.10":
                ApiEventListener = "ayg";
                ConversationChangeMaid = "bmb";
                ServiceFactory = "dsp";
                RedPacketsRpc = "asn";
                MessageDs = "dey";
                break;
            case "3.5.0":
                ApiEventListener = "awm";
                ConversationChangeMaid = "bky";
                ServiceFactory = "ehb";
                RedPacketsRpc = "aqi";
                MessageDs = "dtk";
                break;
            case "3.5.1":
                ApiEventListener = "bfe";
                ConversationChangeMaid = "buk";
                ServiceFactory = "eyl";
                RedPacketsRpc = "ayt";
                MessageDs = "ekb";
                break;
            case "3.5.2":
            case "3.5.2.20":
                ApiEventListener = "biu";
                ConversationChangeMaid = "byf";
                ServiceFactory = "ezn";
                RedPacketsRpc = "bce";
                MessageDs = "eli";
                break;
            case "3.5.3":
                ApiEventListener = "bls";
                ConversationChangeMaid = "cbj";
                ServiceFactory = "fdy";
                RedPacketsRpc = "bfb";
                MessageDs = "ept";
                break;
            case "3.5.6":
                ApiEventListener = "blv";
                ConversationChangeMaid = "cbs";
                ServiceFactory = "ffk";
                RedPacketsRpc = "bfb";
                MessageDs = "erf";
                break;
            case "4.0.0":
            case "4.0.1":
            case "4.0.2":
                ApiEventListener = "bon";
                ConversationChangeMaid = "cfl";
                ServiceFactory = "fkd";
                RedPacketsRpc = "bhb";
                MessageDs = "ewd";
                break;
            case "4.1.0":
            case "4.1.5":
                ApiEventListener = "bsv";
                ConversationChangeMaid = "ckf";
                ServiceFactory = "fou";
                RedPacketsRpc = "blf";
                MessageDs = "fcu";
                break;
            case "4.2.0":
            case "4.2.1":
                ApiEventListener = "bkf";
                ConversationChangeMaid = "cbj";
                ServiceFactory = "ftg";
                RedPacketsRpc = "bcq";
                MessageDs = "fhe";
                break;
            case "4.2.6":
                ApiEventListener = "bmk";
                ConversationChangeMaid = "cdz";
                ServiceFactory = "gfb";
                RedPacketsRpc = "bes";
                MessageDs = "fmf";
                break;
            case "4.2.8":
                ApiEventListener = "boo";
                ConversationChangeMaid = "cgm";
                ServiceFactory = "gip";
                RedPacketsRpc = "bgx";
                MessageDs = "fqb";
                break;
            default:
                ApiEventListener = "bmk";
                ConversationChangeMaid = "cdz";
                ServiceFactory = "gfb";
                RedPacketsRpc = "bes";
                MessageDs = "fmf";
        }
    }
}