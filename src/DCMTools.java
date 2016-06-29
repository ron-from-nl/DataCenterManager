import java.awt.Color;
import java.io.*;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DCMTools
{
      public static Color[] getColorArray()
      {
            int x;
            Color[] colorArray = new Color[36];

//            colorArray[0] = new Color(0,0,0);

            x = 255;

            for (int c=0;c<36;c+=6)
            {
                colorArray[c+0] = new Color(x,0,0);
                colorArray[c+1] = new Color(0,x,0);
                colorArray[c+2] = new Color(0,0,x);
                colorArray[c+3] = new Color(x,0,x);
                colorArray[c+4] = new Color(x,x,0);
                colorArray[c+5] = new Color(0,x,x);
                x -= 16;
            }
//            
//            colorArray[1] = new Color(x,0,0);
//            colorArray[2] = new Color(0,x,0);
//            colorArray[3] = new Color(0,0,x);
//            colorArray[4] = new Color(x,0,x);
//            colorArray[5] = new Color(x,x,0);
//            colorArray[6] = new Color(0,x,x);
//
//            x = 127;
//
//            colorArray[7] = new Color(x,0,0);
//            colorArray[8] = new Color(0,x,0);
//            colorArray[9] = new Color(0,0,x);
//            colorArray[10] = new Color(x,0,x);
//            colorArray[11] = new Color(x,x,0);
//            colorArray[12] = new Color(0,x,x);
//
//            x = 63;
//
//            colorArray[13] = new Color(x,0,0);
//            colorArray[14] = new Color(0,x,0);
//            colorArray[15] = new Color(0,0,x);
//            colorArray[16] = new Color(x,0,x);
//            colorArray[17] = new Color(x,x,0);
//            colorArray[18] = new Color(0,x,x);
//
//            x = 31;
//
//            colorArray[19] = new Color(x,0,0);
//            colorArray[20] = new Color(0,x,0);
//            colorArray[21] = new Color(0,0,x);
//            colorArray[22] = new Color(x,0,x);
//            colorArray[23] = new Color(x,x,0);
//            colorArray[24] = new Color(0,x,x);
//
//            x = 16;
//
//            colorArray[25] = new Color(x,0,0);
//            colorArray[26] = new Color(0,x,0);
//            colorArray[27] = new Color(0,0,x);
//            colorArray[28] = new Color(x,0,x);
//            colorArray[29] = new Color(x,x,0);
//            colorArray[30] = new Color(0,x,x);
//
//            x = 8;
//
//            colorArray[31] = new Color(x,0,0);

            return colorArray;
      }

//      public static Color[] getColorArray()
//      {
//            Color[] colorArray = new Color[32];
//
//            int y=0;
//            int q;
//            for (Integer power=8; power>4; power--)
//            {
//                  q = (int) Math.pow(2D, power)-1; // Starts with 8 making 255, 127, ..
//                  for (int x=0; x<=7; x++)
//                  {
//                        switch (x)
//                        {
//                            case 0: { colorArray[y] = new Color(0,0,0); break; }
//                            case 1: { colorArray[y] = new Color(0,0,q); break; }
//                            case 2: { colorArray[y] = new Color(0,q,0); break; }
//                            case 3: { colorArray[y] = new Color(0,q,q); break; }
//                            case 4: { colorArray[y] = new Color(q,0,0); break; }
//                            case 5: { colorArray[y] = new Color(q,0,q); break; }
//                            case 6: { colorArray[y] = new Color(q,q,0); break; }
//                            case 7: { colorArray[y] = new Color(q,q,q); break; }
//                        }
//                        y++;
//                  } 
//            }
//            return colorArray;
//      }

      public static String startsWith(final String messageParam, final String patternParam)
      {
            BufferedReader reader = new BufferedReader(new StringReader(messageParam));
            String line = "";
            String output = "";

            try
            {
                while ((line = reader.readLine()) != null)
                {
                    if (line.length() > 2)
                    {
                        if (line.startsWith(patternParam)) { output += line + "\r\n"; }
                    }
                }

            } catch(IOException e) { e.printStackTrace(); }
            return output;
      }

      public static String contains(final String messageParam, final String patternParam)
      {
            BufferedReader reader = new BufferedReader(new StringReader(messageParam));
            String line = "";
            String output = "";

            try
            {
                while ((line = reader.readLine()) != null)
                {
                    if (line.length() > 2)
                    {
                        if (line.contains(patternParam)) { output += line + "\r\n"; }
                    }
                }

            } catch(IOException e) { e.printStackTrace(); }
            return output;
      }

      public static String getHumanDate(Calendar cal)
      {
            String dateString = "";
            dateString = "" +
            String.format("%04d", cal.get(Calendar.YEAR)) + "-" +
            String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
            String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
//            String.format("%02d", cal.get(Calendar.SECOND));

//            dateString += " [" + cal.getTimeInMillis() +"]";
            return dateString;
      }

      public static String getHumanDateLong(Calendar cal)
      {
            String dateString = "";
            dateString = "" +
            String.format("%04d", cal.get(Calendar.YEAR)) + "-" +
            String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
            String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + " " +
            String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" +
            String.format("%02d", cal.get(Calendar.MINUTE)); // + ":" +
//            String.format("%02d", cal.get(Calendar.SECOND));

//            dateString += " [" + cal.getTimeInMillis() +"]";
            return dateString;
      }

      public static int getNumOfLines(final String messageParam)
      {
            BufferedReader reader = new BufferedReader(new StringReader(messageParam));
            String line = "";
            String output = "";
            int lineCounter = 0;

            try
            {
                while ((line = reader.readLine()) != null)
                {
                    lineCounter++;
                }

            } catch(IOException e) { e.printStackTrace(); }
            return lineCounter;
      }

      public static void writeToFile(final String filenameParam, final byte[] dataParam)
      {
            Thread logToFileThread = new Thread(new Runnable()
            {
                private FileWriter logFileWriter;
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    FileOutputStream fileoutputstream = null;
                    try { fileoutputstream = new FileOutputStream(filenameParam); } catch (FileNotFoundException ex) {}
                    try { fileoutputstream.write(dataParam); }                      catch (IOException ex) {}
                    try { fileoutputstream.close(); }                               catch (IOException ex) { }
                }
            });
            logToFileThread.setName("logToFileThread");
            logToFileThread.start();
      }

      public static boolean isSubString( String wholeString, String subString ) // Better Use java.lang.String.indexOf method
      {
        boolean output = false;

        String regex = new String("s/"+subString+"/");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(subString);
        if (matcher.find()) { output = true; }
        return output;
      }

      public static String substitude(String text, String regex)
      {
        //String text = "Hallo, hij zei: \"geef mijn 'pen' terug.\", dus; dat deed ik.";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuffer newtext = new StringBuffer();
        while (matcher.find())
        {
          matcher.appendReplacement(newtext,"");
        }
        matcher.appendTail(newtext);
        return newtext.toString();
      }

      public static String split(String text, String separator)
      {
        //String text = "Hallo, hij zei: \"geef mijn 'pen' terug.\", dus; dat deed ik.";

        Pattern pattern = Pattern.compile(separator);
        Matcher matcher = pattern.matcher(text);
        StringBuffer newtext = new StringBuffer();
        while (matcher.find())
        {
          matcher.appendReplacement(newtext,"");
        }
        matcher.appendTail(newtext);
        return newtext.toString();
      }

      public static String validateSIPAddress(String text)
      {
        StringBuffer finaltext = new StringBuffer("");
        StringBuffer newtext = new StringBuffer("");
        // Stage 1
        Pattern pattern = Pattern.compile("[^\\w\\-\\@\\:\\<\\>\\&\\.\\' ]");
        Matcher matcher = pattern.matcher(text);
        newtext = new StringBuffer(); while (matcher.find()) { matcher.appendReplacement(newtext,""); } matcher.appendTail(newtext);
        // Stage 2
        pattern = Pattern.compile("\\'");
        matcher = pattern.matcher(newtext);
        finaltext = new StringBuffer(); while (matcher.find()) { matcher.appendReplacement(finaltext,"\\\\\'"); } matcher.appendTail(finaltext);
        return finaltext.toString();

      }

      public static String getRandomString(int digitsParam)
      {
        String output = "";
        for (int counter = 0; counter < digitsParam;counter++)
        {
            output += Integer.toString((int)(Math.random()*9));    
        }
        return output;
      }
      
      public static long getRandomNumber(int maxnumber)
      {
        Long result = Math.round(Math.random() * maxnumber);
        return result;
      }
      
        public static boolean isLong(String i)
        {
           try { Long.parseLong(i); return true; } catch(NumberFormatException nfe) { return false; }
        }
}
