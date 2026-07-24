package com.lg.algorithmconcept.array;

/**
 * 冒泡排序
 *
 * @PackageName: com.lg.algorithmconcept.array
 * @ClassName: BubbleSort
 * @Description:
 * @author: lg
 * @data: 2026/4/15 11:18
 */
public class BubbleSort {

    /**
     * 算法逻辑： 循环n-1次，每次循环比较相邻的元素，如果前一个元素比后一个元素大，则交换位置
     * @param arr 需要排序的数组
     */
    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {1, 9, 4, 2, 8, 7, 6, 3};
        bubbleSort(arr);
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
}
