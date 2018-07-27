package com.ijiakj.radio.framework;


public class Constant {
	
	public class DeliverMap{
		public static final String PLAY_KEY = "play_key";
		public static final String CATEGORY_KEY_ID = "category_key_id";
		public static final String CATEGORY_KEY_NAME = "category_key_name";
		public static final String OTHERID = "otherId"; // 数据传递key
		public static final String CATEGORY_KEY_LOCID = "category_key_locid";
		public static final String COMM_IMAG_URL = "comm_imag_url";
		public static final String NATIVEADDATAREF_AD = "nativeaddataref_ad";
	}
	public static final String UPDATA_BOTTOMVIEW = "updata_bottomview";

    public static final int DATA_ID_FLAG = 9999999;
    public static final String ALBUM_KEY_ID = "album_key_id";
    public static final String ALBUM_KEY_NAME = "album_key_name";
    public static final String RADIO_KEY_NAME = "radio_key_name";
    public static final String RADIO_KEY_INDEX = "radio_key_index";
    public static final String CAST_KEY = "cast_key";
    public static final String CAST_POSITION_KEY = "cast_position_key";
    public static final String WEB_URL = "web_url"; // 数据传递key
    
    /** 系统崩溃日志文件存放路径 */
	public static final String CRASH_FILENAME = "/sdcard/MoMoRadio/";

    public static boolean IS_REPLAY = true;
    public static boolean IS_BANNER_PLAYE = false;

    //定位
    public static final String CLOSE_APP_ALL = "close_app_all";

    public static final String API_KEY = "olderDesktop";//公用参数0

  //测试用服务器
//    public static final String HOST = "http://112.74.29.75:8080/desktop/";

 // 俊杰测试服务器
//    public static final String HOST = "http://192.168.1.157:8088/desktop/";
 // 最新服务器地址
   public static final String HOST = "http://desktop.niui.com.cn:8090/yunTP/";

   /** 检查更新接口 */
	public static final String updateApp = HOST + "api/common/app/getLastestApp";
	public static int PLATFORM;

	//3K 地址
    public static final String HOST_3K_URL = "http://5597755.com/Lottery_server/get_init_data.php?type=android&appid=jqi0233";
}


