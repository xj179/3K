package com.ijiakj.radio.bean;

import java.util.List;

/**
 * 创建者     曹自飞
 * 创建时间   2016/10/17 0017 14:49
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BanerBean {

    /**
     * ret_code : 0
     * advertising : [{"webUrl":"","advImg":"http://192.168.1.104:8090/yunTP/img/upload//advertising/2016/08/15/9D88574811194BCC802A9B6F732ABB0B.jpg","advTitle":"老人机","advType":"1","otherId":"","openType":"","advDesc":""},{"webUrl":"","advImg":"http://192.168.1.104:8090/yunTP/img/upload//advertising/2016/07/26/A3E1266FC5574A8CB70AC3A7313B016E.png","advTitle":"防诈骗","advType":"4","otherId":"","openType":"","advDesc":""},{"webUrl":"http://desktop.niui.com.cn:8090/yunTP/wap/fest/detail?fes_id=56","advImg":"http://192.168.1.104:8090/yunTP/img/upload//advertising/2016/10/09/0AD09704120B4FB6BD43875A0B37E53A.jpg","advTitle":"guoqing","advType":"1","otherId":"","openType":"","advDesc":""}]
     * ret_msg : succesfully!
     * shopping_url : http://union.click.jd.com/jdc?e=&p=AyIEZRldEQAVBF0eXCUAFANXHFgdBhM3EUQDS10iXhBeGh4cDFkPBABAHUBZCQUHC
     */
    public int ret_code;
    public List<AdvertisingEntity> advertising;
    public String ret_msg;
    public String shopping_url;

    public static class AdvertisingEntity {
        /**
         * webUrl :
         * advImg : http://192.168.1.104:8090/yunTP/img/upload//advertising/2016/08/15/9D88574811194BCC802A9B6F732ABB0B.jpg
         * advTitle : 老人机
         * advType : 1
         * otherId :
         * openType :
         * advDesc :
         */
        public String webUrl;
        public String advImg;
        public String advTitle;
        public String advType;
        public String otherId;
        public String openType;
        public String advDesc;
    }
}
