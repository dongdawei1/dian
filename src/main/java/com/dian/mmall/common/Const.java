package com.dian.mmall.common;



import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by geely
 */
public class Const {
    
	public static final String SERVICETYPENO = "(商品类型不存在，如果合规请手动添加: )";
	public static final String SERVICETYPEDAI = "(待审批的商品类型: )";
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String TOKEN_PREFIX = "token_";
  
    public static final String PCAPPID = "DIANTOP";
    public static final String APPAPPID = "DIANTOA";
    public static final String APPAPPIDP = "p";  //pc
    public static final String APPAPPIDA = "a";  //pc
    
    public static final String PCAPI = "/api/v1/vp/";  //pc
    public static final String APPAPI = "/api/v1/va/"; //app
    public static final String APIV1 = "/api/v1/";
    public static final String APIV3 = "/api/v3/";  //图片上传相关
    
    public static final String APIV5 = "/api/v5/";  //共用创建操作接口
    
    public static final String APIV2 = "/api/v2/"; //登陆获取用户信息相关
    
    public static String  PATH_E_IMG="E:/img/";
    public static String  MY_C0MMONMENU="myCommonMenu";
    public static String  ALL_C0MMONMENU="allCommonMenu";
    
    public static String ORDER_REDIS="order";
    
    public static String  XITONGSHOUKUAN="系统收款账户";
    
    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60 * 1000;//1000分钟
        int REDIS_SESSION_EXTIMEAPP = 60 * 72000;// 存放20天
        int REDIS_SESSION_CommonMenu = 60 * 3600;//3600分钟
        int REDIS_ORDER_TIME=60 * 45;//订单存放45分钟
    }
    public interface ProductListOrderBy{
        //没有导入jar包   暂时没有实现
    //    Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }


    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }
    }
    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }



    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }


        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }

    }


    public interface REDIS_LOCK{
        String CLOSE_ORDER_TASK_LOCK = "CLOSE_ORDER_TASK_LOCK"; //关闭订单分布式锁
    }




}
