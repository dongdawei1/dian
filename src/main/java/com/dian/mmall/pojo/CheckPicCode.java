package com.dian.mmall.pojo;


import org.apache.commons.codec.binary.Base64;
 
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
 
public class CheckPicCode {
    private static String picCode;
 
    public static String getPicCode() {
        return picCode;
    }
 
    public static void setPicCode(String picCode) {
        CheckPicCode.picCode = picCode;
    }
 
    public static String getCheckCode(){
        //验证码中的字符由数字和大小写字母组成
        String code = "23456789qwertyuipasdfghjkxcvbnmQWERTYUPASDFGHJKXCVBNM";
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            sb.append(code.charAt(r.nextInt(code.length())));
        }
 
        return sb.toString();
    }
 
    public static BufferedImage showPic() throws IOException {
        String code=getCheckCode();
        setPicCode(code);
        int width=80;
        int height=30;
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g=image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0,width,height);
        Random r=new Random();
        for (int i=0;i<3;i++){
            g.setColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
            g.drawLine(r.nextInt(80),r.nextInt(30),r.nextInt(80),r.nextInt(80));
        }
        g.setColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
        g.setFont(new Font("黑体",Font.BOLD,24));
        g.drawString(code,15,20);
        g.dispose();//图像生效
        return image;
    }
    public static String encodeBase64ImgCode() throws IOException{
        BufferedImage codeImg=showPic();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(codeImg, "JPEG", out);
        byte[] b = out.toByteArray();
        String imgString = Base64.encodeBase64String(b);
        return "data:image/JPEG;base64," + imgString;
    }
 
    public static void main(String[] args) throws IOException {
        String base64PicCodeImage = encodeBase64ImgCode();
        String checkCode = getPicCode();
        System.out.println("验证码==》"+checkCode);
        System.out.println("base64图片字符串==》"+base64PicCodeImage);
    }

}