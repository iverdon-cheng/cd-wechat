package cn.iverdon.service;

import java.util.Scanner;

/**
 * @author iverdon
 * @date 2020/10/25 11:00
 */
public class test {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int size = in.nextInt();
        int[] nums = new int[size];
        for (int i = 0; i<nums.length; i++){
            nums[i] = in.nextInt();
        }
        for (int num : nums){
            int j = 1;
            while ( j*j*j < num){
                int a = num-j*j*j;
                int b = 1;
                while ( b*b*b < a){
                    b=b+1;
                }
                if (b*b*b == a){
                    System.out.println("Yes");
                    break;
                }
                j= j+1;
                if (j*j*j >= num){
                    System.out.println("No");
                }
            }
        }
    }
}
