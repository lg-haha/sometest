package com.lg.leetcodedemo.findmediansortedarrays;

/**
 * @PackageName: com.lg.leetcodedemo.findmediansortedarrays
 * @ClassName: FindMedianSortedArrays
 * @Description: 寻找两个正序数组的中位数
 * @author: lg
 * @data: 2025/12/29 09:15
 */
public class FindMedianSortedArrays {
    /**
     * 由于数组有序，并且数组长度已经知道，那么可以定义两个指针，移动两个指针对比，
     * 每次移动最小的那个指针记录移动次数，当移动的次数是数组中间位置时，则返回中间数
     * @param nums1
     * @param nums2
     * @return
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int allindex=nums1.length+nums2.length;
        if(allindex%2==1){ //奇数
            int midindex=(allindex+1)/2;
            int end1=0;
            int end2=0;
            int searchIndex=0;
            double re=0;
            for (int i = 0; i < allindex; i++){
                if(end1>nums1.length-1){
                    re=nums2[end2];
                    end2++;
                }else if(end2>nums2.length-1){
                    re=nums1[end1];
                    end1++;
                }else if(nums1[end1]<=nums2[end2]){
                    re=nums1[end1];
                    end1++;
                }else if(nums1[end1]>nums2[end2]){
                    re=nums2[end2];
                    end2++;
                }
                searchIndex++;
                if(searchIndex==midindex){
                    return re;
                }
            }
        }else{//偶数
            int midindex1=(allindex)/2;
            int midindex2=midindex1+1;
            int end1=0;
            int end2=0;
            int searchIndex=0;
            double re=0;
            double re1=0;
            double re2=0;
            for (int i = 0; i < allindex; i++){
                if(end1>nums1.length-1){
                    re=nums2[end2];
                    end2++;
                }else if(end2>nums2.length-1){
                    re=nums1[end1];
                    end1++;
                }else if(nums1[end1]<=nums2[end2]){
                    re=nums1[end1];
                    end1++;
                }else if(nums1[end1]>nums2[end2]){
                    re=nums2[end2];
                    end2++;
                }
                searchIndex++;
                if(searchIndex==midindex1){
                    re1=re;
                }
                if(searchIndex==midindex2){
                    re2=re;
                    break;
                }
            }
            return (re1+re2)/2;
        }
        return 0;
    }

    public static void main(String[] args) {
        int[] nums1=new int[]{};
        int[] nums2=new int[]{1};
        double i = new FindMedianSortedArrays().findMedianSortedArrays(nums1, nums2);
        System.out.println(i);
    }
}
