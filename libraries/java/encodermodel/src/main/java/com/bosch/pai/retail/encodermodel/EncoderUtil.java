package com.bosch.pai.retail.encodermodel;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EncoderUtil {


    private static String[] salts = new String[]{
                            "682611111",
                            "1201301923",
                            "1629101493",
                            "1135102485",
                            "860820728",
                            "375562764",
                            "418087486",
                            "838101182",
                            "957166477",
                            "549411673" };
    private static int iteration = 1025;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final String ALGORITHM = "SHA-256";
    private static MessageDigest messageDigest;


    private EncoderUtil() {
        //To hide public constructor
    }

    private static synchronized void setMessageDigestAlgorithm() throws EncoderException{
        if (messageDigest == null){
            try {

                messageDigest = MessageDigest.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new EncoderException("Error in encrypting data.",e);
            }
        }
    }

   /* public static String encode(String value) {
        value = value.toUpperCase();
        String encoded = Base64.getEncoder().encodeToString(value.getBytes(Charset.forName("UTF-8")));
        String salt = salts[new Random().nextInt(salts.length)];

        String finalValue = salt+encoded+salt;
        for(int i=0;i<iteration;i++){
            finalValue = Base64.getEncoder().encodeToString(finalValue.getBytes(Charset.forName("UTF-8")));
        }
        return finalValue;
    }

    public static boolean match(String original, String encodedValue) {
        original = original.toUpperCase();
        String encoded = Base64.getEncoder().encodeToString(original.getBytes(Charset.forName("UTF-8")));

        List<String> encodedValues = getEncodedValues(encoded);

        if(encodedValues.contains(encodedValue)){
            return true;
        }
        return false;
    }

    public static List<String> getEncodedValues(String value) {
        value = value.toUpperCase();
        List<String> encodedValues = new ArrayList<>();
        String encoded = Base64.getEncoder().encodeToString(value.getBytes(Charset.forName("UTF-8")));
        for(String salt : salts){

            String finalValue = salt+encoded + salt;
            for (int i = 0; i < iteration; i++) {
                finalValue = Base64.getEncoder().encodeToString(
                        finalValue.getBytes(Charset.forName("UTF-8")));
            }
            encodedValues.add(finalValue);
        }
        return encodedValues;
    }*/

    public static synchronized String encode(String value) throws EncoderException{
        setMessageDigestAlgorithm();
        value = value.toUpperCase();
        String salt = salts[new SecureRandom().nextInt(salts.length)];

        byte[] finalDigest = (salt+value+salt).getBytes(UTF_8);

        for(int i=0;i<iteration;i++){
            finalDigest = messageDigest.digest(finalDigest);
        }
        return encoder.encodeToString(finalDigest);
    }

    public static synchronized boolean match(String original, String encodedValue) throws EncoderException{
        setMessageDigestAlgorithm();
        original = original.toUpperCase();
        List<String> encodedValues = getEncodedValues(original);

        for(String value : encodedValues){

            if(messageDigest.isEqual(encodedValue.getBytes(UTF_8), value.getBytes(UTF_8))){
                return true;
            }

        }
        return false;
    }

    public static synchronized List<String> getEncodedValues(String value) throws EncoderException{
        setMessageDigestAlgorithm();
        value = value.toUpperCase();
        List<String> encodedValues = new ArrayList<>();
        for(String salt : salts){

            byte[] finalDigest = (salt+value+salt).getBytes(UTF_8);
            for (int i = 0; i < iteration; i++) {
                finalDigest = messageDigest.digest(finalDigest);
            }
            encodedValues.add(encoder.encodeToString(finalDigest));
        }
        return encodedValues;
    }


}
