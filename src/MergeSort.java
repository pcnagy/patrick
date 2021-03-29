import java.util.Arrays;
import java.util.Random; 

public class MergeSort {
    
    public static void main(String[] args) {
        int[] arr = new int[100];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = i; 
        }
        shuffle(arr);
        System.out.println(Arrays.toString(arr)); 
        sort(arr, 0, arr.length - 1); 
        System.out.println(Arrays.toString(arr)); 
    }


    private static void sort(int[] arr, int start, int end) {
        int middle = (start + end + 1) / 2;
        
        if (middle < end) {

            sort(arr, start, middle); 
            sort(arr, middle + 1, end); 

            merge(arr, start, middle, end); 

        }
    }

    private static void merge(int[] arr, int start, int middle, int end) {
        int size = end - start + 1; 
        int[] aux = new int[size]; 
        for(int i = start, pos = 0; i <= end; i++ ) {
            aux[pos++] = arr[i]; 
        }
        //System.out.println(Arrays.toString(aux)); 
        int s1 = middle - start, s2 = end - middle; 
        int p1 = 0; int p2 = middle - start; 
        int pos = 0; 
        while(p1 <= middle && p2 <= end) {
            System.out.println(p1 + " - " + p2); 
            if(aux[p1] < aux[p2]) {
                arr[pos++] = aux[p1++]; 
            } else {
                arr[pos++] = aux[p2++]; 
            }
        }
        while(p1 <= s1) {
            arr[pos++] = aux[p1++];
        }
        while(p2 <= s2) {
            arr[pos++] = aux[p2++];
        }
    }

    private static void shuffle (int[] arr) {
        Random random = new Random(); 
        for(int i = 0; i < arr.length; i++) {
            int temp = arr[i]; 
            int rand = random.nextInt(i + 1); 
            arr[i] = arr[rand]; 
            arr[rand] = temp; 
        }
    }

    private static boolean isSorted (int[] arr) {
        for(int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false; 
            }
        }
        return true; 
    }

}