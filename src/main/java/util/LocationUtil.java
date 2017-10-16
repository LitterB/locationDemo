package util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LocationUtil {

    //申请的百度地图api密钥
    private static final String KEY = "LWGqfKwdynM3O37xhgGFx8Ma3RerjGHf";
    //根据经纬度获取城市信息转换解析地址
    private static final String URL = "https://api.map.baidu.com/geocoder/v2/?ak=%s&location=%s&output=json&pois=1";

    /**
     * 根据经纬度获取详细的地址信息
     * @param lng
     * @param lat
     * @return
     * @throws IOException
     */
    public static JSONObject getLocationInfo(String lng, String lat) throws IOException{
        StringBuilder resultData = new StringBuilder();
        JSONObject result = new JSONObject();
        try {
            lng = URLEncoder.encode(lng, "UTF-8");
            lat = URLEncoder.encode(lat, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            //TODO: handle exception
            e1.printStackTrace();
        }
        //要解析的经度和纬度格式
        String lngAndlat = lat + ',' + lng;
        //百度地图解析地址格式化
        String url = String.format(URL, KEY ,lngAndlat);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        try {
            httpsConn = (URLConnection)myURL.openConnection();//不使用代理
            if(httpsConn != null){
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                while((data = br.readLine()) != null){
                    resultData.append(data);
                }
                //获取到解析之后的完整地址信息
                result = JSONObject.parseObject(resultData.toString());
            }
        } catch (IOException e) {
            //TODO: handle exception
            e.printStackTrace();
        }finally{
            if(insr != null){
                insr.close();
            }
            if(br != null){
                br.close();
            }
        }
        return result;
    }

    /**
     * 根据传入的经纬度获取地级行政单位城市名
     * @param lng
     * @param lat
     * @return
     * @throws IOException
     */
    public static String getCity(String lng, String lat) throws IOException{
        String city = "";
        JSONObject para = getLocationInfo(lng, lat);
        if(para != null){
            String result = para.getString("result");
            JSONObject paras = JSONObject.parseObject(result);
            if(paras != null){
                String addressComponent = paras.getString("addressComponent");
                JSONObject paras1 = JSONObject.parseObject(addressComponent);
                if(paras1 != null){
                    city = paras1.getString("city");
                }
            }
        }
        return city;
    }

    /**
     * 根据经纬度获取地址信息（省 地级市 县级市）
     * @param lng
     * @param lat
     * @return
     * @throws IOException
     */
    public static String getFormattedAddress(String lng, String lat) throws IOException{
        String formatted_address = "";
        JSONObject para = getLocationInfo(lng, lat);
        if(para != null){
            String result = para.getString("result");
            JSONObject paras = JSONObject.parseObject(result);
            if(paras != null){
                formatted_address = paras.getString("formatted_address");
            }
        }
        return formatted_address;
    }


    public static void main(String[] args) throws IOException {
//        String city = getCity("-106.34677099999999", "56.130365999999995");
        String formattedAddress = getFormattedAddress("119.93324820672","31.683600756172");
        String city = getCity("119.922","31.680273");
        System.out.println(formattedAddress);
        System.out.println(city);
    }
}
