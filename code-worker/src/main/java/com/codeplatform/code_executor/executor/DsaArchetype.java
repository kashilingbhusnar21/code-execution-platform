package com.codeplatform.code_executor.executor;

/**
 * Defines the functional signature and I/O shapes for judgeable DSA problems.
 */
public enum DsaArchetype {
    // Array / Target patterns
    INT_ARRAY_TARGET_TO_INT_ARRAY, // e.g. twoSum(int[] nums, int target) -> int[]
    INT_ARRAY_TARGET_TO_INT,       // e.g. search(int[] nums, int target) -> int
    INT_ARRAY_TO_INT,              // e.g. maxSubArray(int[] nums) -> int
    INT_ARRAY_TO_BOOL,             // e.g. containsDuplicate(int[] nums) -> boolean
    INT_ARRAY_TO_INT_ARRAY,        // e.g. plusOne(int[] nums) -> int[]
    IN_PLACE_INT_ARRAY,            // e.g. moveZeroes(int[] nums) -> void or removeDuplicates(int[] nums) -> int
    INT_ARRAY_INT_TO_INT,          // e.g. findKthLargest(int[] nums, int k) -> int
    INT_ARRAY_INT_TO_INT_ARRAY,    // e.g. rotate(int[] nums, int k) -> int[] / void

    // String patterns
    STRING_TO_BOOL,                // e.g. isValid(String s) -> boolean, isPalindrome(String s) -> boolean
    STRING_TO_INT,                 // e.g. lengthOfLastWord(String s) -> int, romanToInt(String s) -> int
    TWO_STRINGS_TO_BOOL,           // e.g. isAnagram(String s, String t) -> boolean, isSubsequence(String s, String t) -> boolean
    TWO_STRINGS_TO_INT,            // e.g. minDistance(String s, String t) -> int
    STRING_ARRAY_TO_STRING,        // e.g. longestCommonPrefix(String[] strs) -> String
    IN_PLACE_CHAR_ARRAY,           // e.g. reverseString(char[] s) -> void

    // Math / Number patterns
    INT_TO_INT,                    // e.g. climbStairs(int n) -> int, mySqrt(int x) -> int
    INT_TO_BOOL,                   // e.g. isPalindrome(int x) -> boolean, isPowerOfTwo(int n) -> boolean
    INT_TO_INT_ARRAY,              // e.g. countBits(int n) -> int[]
    TWO_INTS_TO_INT,               // e.g. uniquePaths(int m, int n) -> int

    // Data Structure patterns
    LINKED_LIST_TO_LINKED_LIST,        // e.g. reverseList(ListNode head) -> ListNode
    TWO_LINKED_LISTS_TO_LINKED_LIST,   // e.g. mergeTwoLists(ListNode list1, ListNode list2) -> ListNode
    LINKED_LIST_INT_TO_LINKED_LIST,    // e.g. removeNthFromEnd(ListNode head, int n) -> ListNode, rotateRight, partition
    IN_PLACE_LINKED_LIST,              // e.g. reorderList(ListNode head) -> void
    LINKED_LIST_DELETE_NODE,           // e.g. deleteNode(ListNode node) -> void
    LINKED_LIST_ARRAY_TO_LINKED_LIST,  // e.g. mergeKLists(ListNode[] lists) -> ListNode
    TREE_TO_INT,                       // e.g. maxDepth(TreeNode root) -> int
    TREE_TO_BOOL,                      // e.g. isSameTree(TreeNode p, TreeNode q) -> boolean, isSymmetric(TreeNode root) -> boolean
    TREE_TO_TREE,                      // e.g. invertTree(TreeNode root) -> TreeNode
    TREE_TO_INT_ARRAY,                 // e.g. inorderTraversal(TreeNode root) -> List<Integer>

    // Batch 4, 5, 6 archetypes
    STRING_INT_TO_INT,                 // e.g. characterReplacement(String s, int k) -> int
    INT_TARGET_INT_ARRAY_TO_INT,       // e.g. minSubArrayLen(int target, int[] nums) -> int
    TWO_STRINGS_TO_INT_ARRAY,          // e.g. findAnagrams(String s, String p) -> List<Integer>
    INT_ARRAY_INT_TO_DOUBLE,           // e.g. findMaxAverage(int[] nums, int k) -> double
    TWO_STRINGS_TO_STRING,             // e.g. minWindow(String s, String t) -> String
    TWO_TREES_TO_BOOL,                 // e.g. isSameTree(TreeNode p, TreeNode q) -> boolean, isSubtree(TreeNode root, TreeNode subRoot)
    TREE_INT_TO_BOOL,                  // e.g. hasPathSum(TreeNode root, int targetSum) -> boolean
    TREE_INT_TO_INT,                   // e.g. kthSmallest(TreeNode root, int k) -> int
    TREE_TO_INT_LIST_LIST,             // e.g. levelOrder(TreeNode root) -> List<List<Integer>>
    TREE_TWO_NODES_TO_NODE,            // e.g. lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) -> TreeNode
    MATRIX_INT_TO_MATRIX,              // e.g. kClosest(int[][] points, int k) -> int[][]
    CHAR_ARRAY_INT_TO_INT,             // e.g. leastInterval(char[] tasks, int n) -> int
    STRING_TO_STRING,                  // e.g. frequencySort(String s) -> String, reorganizeString(String s) -> String

    // Batch 7, 8, 9, 10 archetypes (DP, Graphs, Backtracking, Advanced)
    STRING_LIST_TO_BOOL,               // e.g. wordBreak(String s, List<String> wordDict) -> boolean
    CHAR_MATRIX_TO_INT,                // e.g. numIslands(char[][] grid) -> int
    INT_MATRIX_TO_INT,                 // e.g. maxAreaOfIsland(int[][] grid) -> int, orangesRotting(int[][] grid) -> int
    MATRIX_INT_INT_INT_TO_MATRIX,      // e.g. floodFill(int[][] image, int sr, int sc, int color) -> int[][]
    INT_MATRIX_TO_BOOL,                // e.g. canFinish(int numCourses, int[][] prerequisites) -> boolean
    INT_MATRIX_TO_INT_ARRAY,           // e.g. findOrder(int numCourses, int[][] prerequisites) -> int[]
    INT_MATRIX_TO_LIST_LIST,           // e.g. pacificAtlantic(int[][] heights) -> List<List<Integer>>
    IN_PLACE_CHAR_MATRIX,              // e.g. solve(char[][] board) -> void, solveSudoku(char[][] board) -> void
    TWO_STRINGS_LIST_TO_INT,           // e.g. ladderLength(String beginWord, String endWord, List<String> wordList) -> int
    INT_ARRAY_TO_LIST_LIST,            // e.g. subsets(int[] nums) -> List<List<Integer>>, permute
    INT_ARRAY_TARGET_TO_LIST_LIST,     // e.g. combinationSum(int[] candidates, int target) -> List<List<Integer>>
    INT_TO_STRING_LIST,                // e.g. generateParenthesis(int n) -> List<String>
    CHAR_MATRIX_STRING_TO_BOOL,        // e.g. exist(char[][] board, String word) -> boolean
    STRING_TO_STRING_LIST,             // e.g. letterCombinations(String digits) -> List<String>
    STRING_TO_LIST_LIST_STRING,        // e.g. partition(String s) -> List<List<String>>
    INT_TO_LIST_LIST_STRING,           // e.g. solveNQueens(int n) -> List<List<String>>
    TWO_INT_ARRAYS_TO_DOUBLE           // e.g. findMedianSortedArrays(int[] nums1, int[] nums2) -> double
}
