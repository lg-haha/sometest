package com.lg.leetcodedemo.lengthoflongestsubstring;

import java.util.*;

/**
 * @PackageName: com.lg.leetcodedemo.lengthoflongestsubstring
 * @ClassName: LengthOfLongestSubstring
 * @Description: 无重复字符的最长子串
 * @author: lg
 * @data: 2025/12/27 11:25
 */
public class LengthOfLongestSubstring {
    public static int lengthOfLongestSubstring(String s) {
        Map<Character,Integer> n=new HashMap<>();
        int length=0;
        for(int i=0;i<s.length();i++){
            if(n.containsKey(s.charAt(i))){
                i=n.get(s.charAt(i))+1;
                n.clear();
            }
            n.put(s.charAt(i),i);
            length=Math.max(length,n.size());
        }
        return length;
    }

    /**
     * 使用队列，先进先出
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring1(String s) {
        Queue<Character> que = new LinkedList<>();
        int max=0;
        for (char c:s.toCharArray()) {
            while (que.contains(c)){
                que.poll();
            }
            que.add(c);
            max=Math.max(max,que.size());
        }
        return max;
    }

    /**
     * 标准 ASCII 码范围 0-127
     * 数字字符 ('0'-'9') 48-57
     * 大写字母 ('A'-'Z') 65-90
     * 小写字母 ('a'-'z') 97-122
     * 符号字符 (' ') 32
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring2(String s) {
        int[] last = new int[128];
        for(int i = 0; i < 128; i++) {
            last[i] = -1;
        }
        int n = s.length();
        int len = 0;
        int start = 0;
        for(int i = 0; i < n; i++) {
            int index = s.charAt(i);
            start = Math.max(start, last[index] + 1);
            len   = Math.max(len, i - start + 1);
            last[index] = i;
        }
        return len;
    }

    public static void main(String[] args) {
        int i = lengthOfLongestSubstring2("abcbabb");
        System.out.println(i);
    }
}
