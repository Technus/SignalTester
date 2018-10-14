package com.github.technus.signalTester.utility;

public class Utility {
    private Utility(){}

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    //region convert
    public static String bytesToHex(byte[] bytes,int bytesPerLine,boolean split8) {
        int lim=bytesPerLine-1;
        if (bytes == null) return null;
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = j % bytesPerLine == lim ? '\n' : (split8 && j%8==7?'\t':' ');
        }
        return new String(hexChars);
    }

    public static String bytesToHex(byte[] bytes,int bytesPerLine) {
        int lim=bytesPerLine-1;
        if (bytes == null) return null;
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = j % bytesPerLine == lim ? '\n' : ' ';
        }
        return new String(hexChars);
    }

    public static String bytesToHex(byte[] bytes) {
        if(bytes==null) return null;
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex){
        if(hex==null) return null;
        hex=hex.toUpperCase().replaceAll("0X","").replaceAll("[^0-9A-F]","");
        if(hex.length()%2==1) {
            return null;
        }
        byte[] bytes=new byte[hex.length()>>1];
        for (int i = 0, len=bytes.length; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
    }

    public static String doublesToStr(double[] arr){
        if(arr==null || arr.length==0) return null;
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<arr.length;i++){
            stringBuilder.append(arr[i]).append(' ');
        }
        stringBuilder.setLength(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    public static double[] strToDoubles(String str){
        if(str==null) return null;
        String[] split=str.split(" ");
        double[] doubles=new double[split.length];
        for(int i=0;i<doubles.length;i++){
            try{
                doubles[i]=Double.parseDouble(split[i]);
            }catch (Exception e){
                return null;
            }
        }
        return doubles;
    }
    //endregion
}
