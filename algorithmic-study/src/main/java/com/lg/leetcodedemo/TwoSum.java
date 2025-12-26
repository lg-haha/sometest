package com.lg.leetcodedemo;
import java.util.HashMap;
import java.util.Map;
/**
 * @PackageName: com.lg.leetcodedemo
 * @ClassName: TwoSum
 * @Description: 两数之和
 * @author: lg
 * @data: 2025/12/26 23:57
 */
public class TwoSum {
    public static void main(String[] args) {
        int[] ints = twoSum(new int[]{1, 2, 3, 4, 5}, 8);
        System.out.println(ints[0]+"  "+ints[1]);
        int[] ints1 = twoSum1(new int[]{1, 2, 4, 5}, 7);
        System.out.println(ints1[0]+" "+ints1[1]);
    }

    /**正常思路，遍历数组，判断当前数和下一个数之和是否等于target
     * @param nums
     * @param target
     * @return
     */
    public  static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        out:
        for(int i=0;i<nums.length;i++){
            for (int j=i+1;j<nums.length;j++) {
                if(nums[i]+nums[j]==target) {
                    result[0]=i;
                    result[1]=j;
                    break out;
                }
            }
        }
        return result;
    }

    /**利用map中哈希表查找特性降低时间复杂度
     * @param nums
     * @param target
     * @return
     */
    public  static int[] twoSum1(int[] nums, int target) {
       Map<Integer,Integer> map=new HashMap<>();
        for(int i=0;i<nums.length;i++){
            int complement=target-nums[i];
            if(map.containsKey(complement)){
                return new int[]{map.get(complement),i};
            }
            map.put(nums[i],i);
        }
        return new int[2];
    }

}
