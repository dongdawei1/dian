package com.dian.mmall.common;



import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by geely
 */
public class Const {
	public static final float BAOZHANGJIN=0.1F;
	
	public static final int SHUCAIP=4;
	public static final int LIANGYOUP=5;
	public static final int TIAOLIAO=6;
	public static final int QINGJIEP=9;
	public static final int ZHUOYIP=11;
	public static final int SHUICHAN=29;
	
	public static final int ZHIWEIP=30;
	public static final int DIANMIANP=14;
	public static final int TANWEIP=15;
	public static final int JIANLIP=31;
	
	//工服101/百货102/绿植103/装饰用品104
	public static final int GONGFUP=101;
	public static final int BAIHUOP=102;
	public static final int LVZHIP=103;
	public static final int ZHUANGSHIP=104;
	//7酒水，8消毒餐具
	public static final int JIUSHUIP=7;
	public static final int CANJUP=8;
	//32电器设备维修，33设备出售，34二手设备
	public static final int SHEBEIXIUP=32;
	public static final int SHEBEIMAIP=33;
	public static final int SHEBEIJIUP=34;
	//13菜谱 ，17装修，19灭虫
	public static final int CAIPIP=13;
	public static final int ZHUANGXIUP=17;
	public static final int MIECHONGP=19;
	//批发 401菜 ，405粮油出售，406调料/副食出售,429水产/禽蛋出售,409清洁用品
	public static final int PCAIP=401;
	public static final int PYOUP=405;
	public static final int PTIAOLIAOP=406;
	public static final int PSHUICHANP=429;
	public static final int PQINGJIEP=409;
	
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
    
    public static final String PCAPI = "/api/v1/vp/";  //共用接口
    public static final String WXAPI = "/api/vx/"; //微信回调
    public static final String APIV1 = "/api/v1/";
    public static final String APIV3 = "/api/v3/";  //图片上传相关
    
    public static final String APIV5 = "/api/v5/";  //管理员操作接口
    
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
