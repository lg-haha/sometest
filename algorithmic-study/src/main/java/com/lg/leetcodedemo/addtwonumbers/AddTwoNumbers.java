package com.lg.leetcodedemo.addtwonumbers;

/**
 * @PackageName: com.lg.leetcodedemo.addtwonumbers
 * @ClassName: AddTwoNumbers
 * @Description: 两数和
 * @author: lg
 * @data: 2025/12/27 10:10
 */
public class AddTwoNumbers {
    //Definition for singly-linked list.
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode();
        ListNode node = dummy;
        int carry = 0;
        while(l1!=null || l2!=null || carry>0){
            int sum = (l1==null?0:l1.val) + (l2==null?0:l2.val) + carry;
            carry = sum/10;
            node.next = new ListNode(sum%10);
            node = node.next;
            l1 = l1!=null?l1.next:null;
            l2 = l2!=null?l2.next:null;
        }
        return dummy.next;
    }


    public static void main(String[] args) {
        ListNode list = new ListNode(2, new ListNode(4, new ListNode(3)));
        ListNode list1 = new ListNode(5, new ListNode(6, new ListNode(4)));
        ListNode listNode = addTwoNumbers(list, list1);
        while (listNode!=null){
            System.out.print(listNode.val);
            listNode=listNode.next;
        }
    }
}
