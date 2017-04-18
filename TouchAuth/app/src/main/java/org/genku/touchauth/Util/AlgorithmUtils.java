package org.genku.touchauth.Util;

/**
 * Created by genku on 4/18/2017.
 */

public class AlgorithmUtils {

    public static void quickSort(double[] nums, int l, int r) {
        if (l >= r) return;
        int i = l, j = r;
        double tmp = nums[l];
        while (i < j) {
            while (i < j && nums[j] > tmp) --j;
            if (i < j) {
                nums[i++] = nums[j];
            }
            while (i < j && nums[i] < tmp) ++i;
            if (i < j) {
                nums[j--] = nums[i];
            }
        }
        nums[i] = tmp;
        quickSort(nums, l, i - 1);
        quickSort(nums, i + 1, r);
    }
}
