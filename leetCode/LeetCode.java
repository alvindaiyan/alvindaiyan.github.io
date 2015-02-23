public class LeetCode{
	// 172
	public static int trailingZeroes(int n) {      
        int res = 0;

        int t = n;
        while(t != 0 && t / 5 >= 1)
        {
            t = t / 5;
            res += t;
        }

        return res;
    }

    // 6
    public static String convert(String s, int nRows) {
        if(nRows == 1 || s.length() <= nRows)
        {
            return s;
        }
        String[] lines = new String[nRows];
        int[] indexes = new int[s.length()];
        int delta = 2 * (nRows - 1);
        int count = 0;

        // first line
        for(int i = 0; i < s.length(); i += delta)
        {
            indexes[count++] = i;
        }        
        int startIndx = 1;

        if(nRows > 2)
        {
            // body
            while(startIndx < nRows - 1)
            {
                int t = 1;
                for(int i = startIndx; i < s.length() && i > 0 && count < indexes.length; i = (t++) * delta - i)
                {
                    indexes[count++] = i;
                }
                startIndx++;

            }
        }   

        // tail line
        for(int i = nRows - 1; i < s.length(); i += delta)
        {
            indexes[count++] = i;
        }

        // construct the String
        java.lang.StringBuilder rel = new StringBuilder();
        for(int i = 0; i < indexes.length; i++)
        {
            rel.append(s.charAt(indexes[i]));
        }
        return rel.toString();
    }

    // 136
    public int singleNumber(int[] A) {
        int result = 0; 
        for (int i : A) result ^= i ; 
            return result ; 
    } 


    // countAndSay
    public static String countAndSay(int n) {
        if( n == 1) return "1";
        else if( n == 2) return countAndSayRecr(countAndSay(n - 1));
        else return countAndSayRecr(countAndSay(n - 1));
    }

    public static String countAndSayRecr(String n) {
        java.lang.StringBuilder sb = new StringBuilder();        
        String s = n + "";
        int count = 1;
        char pre = s.charAt(0);
        for (int i = 1; i <= s.length(); i++)
        {
            if(i >= s.length() || pre != s.charAt(i))
            {
                sb.append(count).append(pre);

                if (i < s.length()) 
                {
                    pre = s.charAt(i);
                }
                count = 1;
            }
            else 
            {
                count++;
            }
        }

        return sb.toString();        
    }


    public static void main(String[] arg)
    {
        // int[] x = {1,0,1};
        // System.out.println("result = " + singleNumber(x));

        System.out.println( Integer.MAX_VALUE );
    }
}