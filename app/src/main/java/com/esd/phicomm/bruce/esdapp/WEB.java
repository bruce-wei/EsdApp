package com.esd.phicomm.bruce.esdapp;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.conn.HttpHostConnectException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

class WEB {

    private static String TAG = "WEB";
    private static String SERVER_URL = "http://170.1.13.231/SFIS_ESD/Web_EsdReport.asmx";
    private static final String PACE = "http://tempuri.org/";
    private static String METHOD = null;
    private static String SOAPACTION = null;
    private static String returntype;

    static void setMethod(String Method) {
        METHOD = Method;
        SOAPACTION = PACE + METHOD;
        Log.d(TAG, "setMethod:" + METHOD);
    }

    @SuppressWarnings("hiding")
    static Object WebServices(HashMap<String, String> map) {

        for (Entry<String, String> element : map.entrySet()) {
            Log.d(TAG,
                    METHOD + " " + element.getKey() + ":" + element.getValue());
        }

        final HttpTransportSE httpSE = new HttpTransportSE(SERVER_URL);
        httpSE.debug = true;
        SoapObject soapObject = new SoapObject(PACE, METHOD);
        for (Entry<String, String> element : map.entrySet()) {
            soapObject.addProperty(element.getKey(), element.getValue());
        }
        final SoapSerializationEnvelope soapserial = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        soapserial.bodyOut = soapObject;
        soapserial.dotNet = true;
        try {
            httpSE.call(SOAPACTION, soapserial);
        } catch (SocketTimeoutException e1) {
            e1.printStackTrace();
            Log.d(TAG, e1.getMessage());
            return null;
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.d(TAG, e1.getMessage());
            return null;
        } catch (XmlPullParserException e1) {
            e1.printStackTrace();
            Log.d(TAG, e1.getMessage());
            return null;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
        try {
            if (soapserial.getResponse() != null) {
                SoapObject result = (SoapObject) soapserial.bodyIn;
                Log.d(TAG, "Result:");
                Log.d(TAG, result.toString());
                if (returntype != null && returntype.equalsIgnoreCase("list")) {
                    SoapObject detail = (SoapObject) result.getProperty(METHOD
                            + "Result");

                    List<String> arraylist = new ArrayList<String>();
                    Log.d(TAG, "Result:");
                    for (int i = 0; i < detail.getPropertyCount(); i++) {
                        arraylist.add(detail.getProperty(i).toString());
                        Log.d(TAG, detail.getProperty(i).toString());
                    }
                    return arraylist;
                }
                return result.toString();
            }
            return "";

        } catch (SoapFault e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public static void changeURL(String URL) {
        SERVER_URL = URL;
    }

    public static void setRturnType(String Type) {
        returntype = Type;
    }
}
