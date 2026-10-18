package com.codeplatform.code_executor.executor;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Registry of judgeable DSA problem definitions mapping exact normalized titles
 * and method signatures to their corresponding archetype and method names.
 */
public class DsaProblemRegistry {

    private static final Map<String, DsaProblemDef> TITLE_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<String, DsaProblemDef> METHOD_REGISTRY = new ConcurrentHashMap<>();

    static {
        // 1. Two Sum
        register(new DsaProblemDef("Two Sum", "twoSum", DsaArchetype.INT_ARRAY_TARGET_TO_INT_ARRAY),
                "Two Sum");

        // 2. Reverse a String
        register(new DsaProblemDef("Reverse a String", "reverseString", DsaArchetype.IN_PLACE_CHAR_ARRAY),
                "Reverse a String", "Reverse String");

        // 3. Valid Parentheses
        register(new DsaProblemDef("Valid Parentheses", "isValid", DsaArchetype.STRING_TO_BOOL),
                "Valid Parentheses");

        // 4. Binary Search
        register(new DsaProblemDef("Binary Search", "search", DsaArchetype.INT_ARRAY_TARGET_TO_INT),
                "Binary Search");

        // 5. Maximum Subarray Sum
        register(new DsaProblemDef("Maximum Subarray Sum", "maxSubArray", DsaArchetype.INT_ARRAY_TO_INT),
                "Maximum Subarray Sum", "Maximum Subarray", "Max Subarray");

        // 6. Contains Duplicate
        register(new DsaProblemDef("Contains Duplicate", "containsDuplicate", DsaArchetype.INT_ARRAY_TO_BOOL),
                "Contains Duplicate");

        // 7. Valid Anagram
        register(new DsaProblemDef("Valid Anagram", "isAnagram", DsaArchetype.TWO_STRINGS_TO_BOOL),
                "Valid Anagram");

        // 8. Best Time to Buy and Sell Stock
        register(new DsaProblemDef("Best Time to Buy and Sell Stock", "maxProfit", DsaArchetype.INT_ARRAY_TO_INT),
                "Best Time to Buy and Sell Stock", "Buy and Sell Stock");

        // 9. Valid Palindrome
        register(new DsaProblemDef("Valid Palindrome", "isPalindrome", DsaArchetype.STRING_TO_BOOL),
                "Valid Palindrome");

        // 10. Search Insert Position
        register(new DsaProblemDef("Search Insert Position", "searchInsert", DsaArchetype.INT_ARRAY_TARGET_TO_INT),
                "Search Insert Position");

        // 11. Move Zeroes
        register(new DsaProblemDef("Move Zeroes", "moveZeroes", DsaArchetype.IN_PLACE_INT_ARRAY),
                "Move Zeroes");

        // 12. Climbing Stairs
        register(new DsaProblemDef("Climbing Stairs", "climbStairs", DsaArchetype.INT_TO_INT),
                "Climbing Stairs");

        // 13. Single Number
        register(new DsaProblemDef("Single Number", "singleNumber", DsaArchetype.INT_ARRAY_TO_INT),
                "Single Number");

        // 14. Two Sum II - Input Array Is Sorted
        register(new DsaProblemDef("Two Sum II - Input Array Is Sorted", "twoSum", DsaArchetype.INT_ARRAY_TARGET_TO_INT_ARRAY),
                "Two Sum II - Input Array Is Sorted", "Two Sum II", "Two Sum 2");

        // 15. Product of Array Except Self
        register(new DsaProblemDef("Product of Array Except Self", "productExceptSelf", DsaArchetype.INT_ARRAY_TO_INT_ARRAY),
                "Product of Array Except Self");

        // 16. Container With Most Water
        register(new DsaProblemDef("Container With Most Water", "maxArea", DsaArchetype.INT_ARRAY_TO_INT),
                "Container With Most Water", "Most Water");

        // 17. Find Minimum in Rotated Sorted Array
        register(new DsaProblemDef("Find Minimum in Rotated Sorted Array", "findMin", DsaArchetype.INT_ARRAY_TO_INT),
                "Find Minimum in Rotated Sorted Array", "Find Minimum Rotated Sorted Array");

        // 18. Search in Rotated Sorted Array
        register(new DsaProblemDef("Search in Rotated Sorted Array", "search", DsaArchetype.INT_ARRAY_TARGET_TO_INT),
                "Search in Rotated Sorted Array", "Search Rotated Sorted Array");

        // 19. Longest Common Prefix
        register(new DsaProblemDef("Longest Common Prefix", "longestCommonPrefix", DsaArchetype.STRING_ARRAY_TO_STRING),
                "Longest Common Prefix");

        // 20. Length of Last Word
        register(new DsaProblemDef("Length of Last Word", "lengthOfLastWord", DsaArchetype.STRING_TO_INT),
                "Length of Last Word");

        // 21. Reverse Linked List
        register(new DsaProblemDef("Reverse Linked List", "reverseList", DsaArchetype.LINKED_LIST_TO_LINKED_LIST),
                "Reverse Linked List");

        // 22. Merge Two Sorted Lists
        register(new DsaProblemDef("Merge Two Sorted Lists", "mergeTwoLists", DsaArchetype.TWO_LINKED_LISTS_TO_LINKED_LIST),
                "Merge Two Sorted Lists");

        // 23. Maximum Depth of Binary Tree
        register(new DsaProblemDef("Maximum Depth of Binary Tree", "maxDepth", DsaArchetype.TREE_TO_INT),
                "Maximum Depth of Binary Tree", "Max Depth of Binary Tree", "Max Depth");

        // 24. Invert Binary Tree
        register(new DsaProblemDef("Invert Binary Tree", "invertTree", DsaArchetype.TREE_TO_TREE),
                "Invert Binary Tree");

        // 25. Unique Paths
        register(new DsaProblemDef("Unique Paths", "uniquePaths", DsaArchetype.TWO_INTS_TO_INT),
                "Unique Paths");

        // 26. Reverse Linked List (registered above as #21)

        // 27. Middle of the Linked List
        register(new DsaProblemDef("Middle of the Linked List", "middleNode", DsaArchetype.LINKED_LIST_TO_LINKED_LIST),
                "Middle of the Linked List", "Middle of Linked List");

        // 28. Merge Two Sorted Lists (registered above as #22)

        // 29. Delete Node in a Linked List
        register(new DsaProblemDef("Delete Node in a Linked List", "deleteNode", DsaArchetype.LINKED_LIST_DELETE_NODE),
                "Delete Node in a Linked List", "Delete Node");

        // 30. Remove Nth Node From End of List
        register(new DsaProblemDef("Remove Nth Node From End of List", "removeNthFromEnd", DsaArchetype.LINKED_LIST_INT_TO_LINKED_LIST),
                "Remove Nth Node From End of List", "Remove Nth Node From End");

        // 31. Reorder List
        register(new DsaProblemDef("Reorder List", "reorderList", DsaArchetype.IN_PLACE_LINKED_LIST),
                "Reorder List");

        // 32. Odd Even Linked List
        register(new DsaProblemDef("Odd Even Linked List", "oddEvenList", DsaArchetype.LINKED_LIST_TO_LINKED_LIST),
                "Odd Even Linked List");

        // 33. Rotate List
        register(new DsaProblemDef("Rotate List", "rotateRight", DsaArchetype.LINKED_LIST_INT_TO_LINKED_LIST),
                "Rotate List");

        // 34. Partition List
        register(new DsaProblemDef("Partition List", "partition", DsaArchetype.LINKED_LIST_INT_TO_LINKED_LIST),
                "Partition List");

        // 35. Merge k Sorted Lists
        register(new DsaProblemDef("Merge k Sorted Lists", "mergeKLists", DsaArchetype.LINKED_LIST_ARRAY_TO_LINKED_LIST),
                "Merge k Sorted Lists", "Merge K Sorted Lists");

        // --- BATCH 4: Sliding Window (36 - 45) ---
        // 36. Longest Substring Without Repeating Characters
        register(new DsaProblemDef("Longest Substring Without Repeating Characters", "lengthOfLongestSubstring", DsaArchetype.STRING_TO_INT),
                "Longest Substring Without Repeating Characters", "Longest Substring Without Repeating");

        // 37. Longest Repeating Character Replacement
        register(new DsaProblemDef("Longest Repeating Character Replacement", "characterReplacement", DsaArchetype.STRING_INT_TO_INT),
                "Longest Repeating Character Replacement");

        // 38. Minimum Size Subarray Sum
        register(new DsaProblemDef("Minimum Size Subarray Sum", "minSubArrayLen", DsaArchetype.INT_TARGET_INT_ARRAY_TO_INT),
                "Minimum Size Subarray Sum");

        // 39. Permutation in String
        register(new DsaProblemDef("Permutation in String", "checkInclusion", DsaArchetype.TWO_STRINGS_TO_BOOL),
                "Permutation in String");

        // 40. Find All Anagrams in a String
        register(new DsaProblemDef("Find All Anagrams in a String", "findAnagrams", DsaArchetype.TWO_STRINGS_TO_INT_ARRAY),
                "Find All Anagrams in a String", "Find All Anagrams");

        // 41. Maximum Average Subarray I
        register(new DsaProblemDef("Maximum Average Subarray I", "findMaxAverage", DsaArchetype.INT_ARRAY_INT_TO_DOUBLE),
                "Maximum Average Subarray I", "Maximum Average Subarray 1");

        // 42. Fruit Into Baskets
        register(new DsaProblemDef("Fruit Into Baskets", "totalFruit", DsaArchetype.INT_ARRAY_TO_INT),
                "Fruit Into Baskets");

        // 43. Max Consecutive Ones III
        register(new DsaProblemDef("Max Consecutive Ones III", "longestOnes", DsaArchetype.INT_ARRAY_INT_TO_INT),
                "Max Consecutive Ones III", "Max Consecutive Ones 3");

        // 44. Minimum Window Substring
        register(new DsaProblemDef("Minimum Window Substring", "minWindow", DsaArchetype.TWO_STRINGS_TO_STRING),
                "Minimum Window Substring");

        // 45. Sliding Window Maximum
        register(new DsaProblemDef("Sliding Window Maximum", "maxSlidingWindow", DsaArchetype.INT_ARRAY_INT_TO_INT_ARRAY),
                "Sliding Window Maximum");

        // --- BATCH 5: Trees & BST (46 - 55) ---
        // 46. Subtree of Another Tree
        register(new DsaProblemDef("Subtree of Another Tree", "isSubtree", DsaArchetype.TWO_TREES_TO_BOOL),
                "Subtree of Another Tree");

        // 47. Same Tree
        register(new DsaProblemDef("Same Tree", "isSameTree", DsaArchetype.TWO_TREES_TO_BOOL),
                "Same Tree");

        // 48. Path Sum
        register(new DsaProblemDef("Path Sum", "hasPathSum", DsaArchetype.TREE_INT_TO_BOOL),
                "Path Sum");

        // 49. Symmetric Tree
        register(new DsaProblemDef("Symmetric Tree", "isSymmetric", DsaArchetype.TREE_TO_BOOL),
                "Symmetric Tree");

        // 50. Diameter of Binary Tree
        register(new DsaProblemDef("Diameter of Binary Tree", "diameterOfBinaryTree", DsaArchetype.TREE_TO_INT),
                "Diameter of Binary Tree");

        // 51. Balanced Binary Tree
        register(new DsaProblemDef("Balanced Binary Tree", "isBalanced", DsaArchetype.TREE_TO_BOOL),
                "Balanced Binary Tree");

        // 52. Binary Tree Inorder Traversal
        register(new DsaProblemDef("Binary Tree Inorder Traversal", "inorderTraversal", DsaArchetype.TREE_TO_INT_ARRAY),
                "Binary Tree Inorder Traversal");

        // 53. Binary Tree Level Order Traversal
        register(new DsaProblemDef("Binary Tree Level Order Traversal", "levelOrder", DsaArchetype.TREE_TO_INT_LIST_LIST),
                "Binary Tree Level Order Traversal", "Level Order Traversal");

        // 54. Lowest Common Ancestor of a Binary Search Tree
        register(new DsaProblemDef("Lowest Common Ancestor of a Binary Search Tree", "lowestCommonAncestor", DsaArchetype.TREE_TWO_NODES_TO_NODE),
                "Lowest Common Ancestor of a Binary Search Tree", "Lowest Common Ancestor of BST", "Lowest Common Ancestor");

        // 55. Binary Tree Maximum Path Sum
        register(new DsaProblemDef("Binary Tree Maximum Path Sum", "maxPathSum", DsaArchetype.TREE_TO_INT),
                "Binary Tree Maximum Path Sum", "Binary Tree Max Path Sum");

        // --- BATCH 6: Heaps, BST & Two Pointers (56 - 65) ---
        // 56. Validate Binary Search Tree
        register(new DsaProblemDef("Validate Binary Search Tree", "isValidBST", DsaArchetype.TREE_TO_BOOL),
                "Validate Binary Search Tree", "Validate BST");

        // 57. Kth Smallest Element in a BST
        register(new DsaProblemDef("Kth Smallest Element in a BST", "kthSmallest", DsaArchetype.TREE_INT_TO_INT),
                "Kth Smallest Element in a BST", "Kth Smallest BST");

        // 58. Kth Largest Element in an Array
        register(new DsaProblemDef("Kth Largest Element in an Array", "findKthLargest", DsaArchetype.INT_ARRAY_INT_TO_INT),
                "Kth Largest Element in an Array", "Kth Largest Element");

        // 59. Top K Frequent Elements
        register(new DsaProblemDef("Top K Frequent Elements", "topKFrequent", DsaArchetype.INT_ARRAY_INT_TO_INT_ARRAY),
                "Top K Frequent Elements");

        // 60. Last Stone Weight
        register(new DsaProblemDef("Last Stone Weight", "lastStoneWeight", DsaArchetype.INT_ARRAY_TO_INT),
                "Last Stone Weight");

        // 61. K Closest Points to Origin
        register(new DsaProblemDef("K Closest Points to Origin", "kClosest", DsaArchetype.MATRIX_INT_TO_MATRIX),
                "K Closest Points to Origin", "K Closest Points");

        // 62. Task Scheduler
        register(new DsaProblemDef("Task Scheduler", "leastInterval", DsaArchetype.CHAR_ARRAY_INT_TO_INT),
                "Task Scheduler");

        // 63. Sort Characters By Frequency
        register(new DsaProblemDef("Sort Characters By Frequency", "frequencySort", DsaArchetype.STRING_TO_STRING),
                "Sort Characters By Frequency");

        // 64. Reorganize String
        register(new DsaProblemDef("Reorganize String", "reorganizeString", DsaArchetype.STRING_TO_STRING),
                "Reorganize String");

        // 65. Trapping Rain Water
        register(new DsaProblemDef("Trapping Rain Water", "trap", DsaArchetype.INT_ARRAY_TO_INT),
                "Trapping Rain Water");

        // --- BATCH 7: Dynamic Programming (66 - 75) ---
        // 66. House Robber
        register(new DsaProblemDef("House Robber", "rob", DsaArchetype.INT_ARRAY_TO_INT),
                "House Robber");

        // 67. House Robber II
        register(new DsaProblemDef("House Robber II", "rob", DsaArchetype.INT_ARRAY_TO_INT),
                "House Robber II", "House Robber 2");

        // 68. Coin Change
        register(new DsaProblemDef("Coin Change", "coinChange", DsaArchetype.INT_ARRAY_TARGET_TO_INT),
                "Coin Change");

        // 69. Longest Increasing Subsequence
        register(new DsaProblemDef("Longest Increasing Subsequence", "lengthOfLIS", DsaArchetype.INT_ARRAY_TO_INT),
                "Longest Increasing Subsequence", "LIS");

        // 70. Word Break
        register(new DsaProblemDef("Word Break", "wordBreak", DsaArchetype.STRING_LIST_TO_BOOL),
                "Word Break");

        // 71. Min Cost Climbing Stairs
        register(new DsaProblemDef("Min Cost Climbing Stairs", "minCostClimbingStairs", DsaArchetype.INT_ARRAY_TO_INT),
                "Min Cost Climbing Stairs");

        // 72. Longest Common Subsequence
        register(new DsaProblemDef("Longest Common Subsequence", "longestCommonSubsequence", DsaArchetype.TWO_STRINGS_TO_INT),
                "Longest Common Subsequence", "LCS");

        // 73. Decode Ways
        register(new DsaProblemDef("Decode Ways", "numDecodings", DsaArchetype.STRING_TO_INT),
                "Decode Ways");

        // 74. Edit Distance
        register(new DsaProblemDef("Edit Distance", "minDistance", DsaArchetype.TWO_STRINGS_TO_INT),
                "Edit Distance");

        // 75. Regular Expression Matching
        register(new DsaProblemDef("Regular Expression Matching", "isMatch", DsaArchetype.TWO_STRINGS_TO_BOOL),
                "Regular Expression Matching", "Regex Matching");

        // --- BATCH 8: Graphs & BFS/DFS (76 - 85) ---
        // 76. Number of Islands
        register(new DsaProblemDef("Number of Islands", "numIslands", DsaArchetype.CHAR_MATRIX_TO_INT),
                "Number of Islands");

        // 77. Max Area of Island
        register(new DsaProblemDef("Max Area of Island", "maxAreaOfIsland", DsaArchetype.INT_MATRIX_TO_INT),
                "Max Area of Island");

        // 78. Flood Fill
        register(new DsaProblemDef("Flood Fill", "floodFill", DsaArchetype.MATRIX_INT_INT_INT_TO_MATRIX),
                "Flood Fill");

        // 79. Course Schedule
        register(new DsaProblemDef("Course Schedule", "canFinish", DsaArchetype.INT_MATRIX_TO_BOOL),
                "Course Schedule");

        // 80. Course Schedule II
        register(new DsaProblemDef("Course Schedule II", "findOrder", DsaArchetype.INT_MATRIX_TO_INT_ARRAY),
                "Course Schedule II", "Course Schedule 2");

        // 81. Pacific Atlantic Water Flow
        register(new DsaProblemDef("Pacific Atlantic Water Flow", "pacificAtlantic", DsaArchetype.INT_MATRIX_TO_LIST_LIST),
                "Pacific Atlantic Water Flow");

        // 82. Surrounded Regions
        register(new DsaProblemDef("Surrounded Regions", "solve", DsaArchetype.IN_PLACE_CHAR_MATRIX),
                "Surrounded Regions");

        // 83. Rotting Oranges
        register(new DsaProblemDef("Rotting Oranges", "orangesRotting", DsaArchetype.INT_MATRIX_TO_INT),
                "Rotting Oranges");

        // 84. Word Ladder
        register(new DsaProblemDef("Word Ladder", "ladderLength", DsaArchetype.TWO_STRINGS_LIST_TO_INT),
                "Word Ladder");

        // 85. Alien Dictionary
        register(new DsaProblemDef("Alien Dictionary", "alienOrder", DsaArchetype.STRING_ARRAY_TO_STRING),
                "Alien Dictionary");

        // --- BATCH 9: Backtracking & Matrix Search (86 - 95) ---
        // 86. Subsets
        register(new DsaProblemDef("Subsets", "subsets", DsaArchetype.INT_ARRAY_TO_LIST_LIST),
                "Subsets");

        // 87. Combination Sum
        register(new DsaProblemDef("Combination Sum", "combinationSum", DsaArchetype.INT_ARRAY_TARGET_TO_LIST_LIST),
                "Combination Sum");

        // 88. Permutations
        register(new DsaProblemDef("Permutations", "permute", DsaArchetype.INT_ARRAY_TO_LIST_LIST),
                "Permutations");

        // 89. Subsets II
        register(new DsaProblemDef("Subsets II", "subsetsWithDup", DsaArchetype.INT_ARRAY_TO_LIST_LIST),
                "Subsets II", "Subsets 2");

        // 90. Generate Parentheses
        register(new DsaProblemDef("Generate Parentheses", "generateParenthesis", DsaArchetype.INT_TO_STRING_LIST),
                "Generate Parentheses");

        // 91. Word Search
        register(new DsaProblemDef("Word Search", "exist", DsaArchetype.CHAR_MATRIX_STRING_TO_BOOL),
                "Word Search");

        // 92. Letter Combinations of a Phone Number
        register(new DsaProblemDef("Letter Combinations of a Phone Number", "letterCombinations", DsaArchetype.STRING_TO_STRING_LIST),
                "Letter Combinations of a Phone Number", "Letter Combinations");

        // 93. Palindrome Partitioning
        register(new DsaProblemDef("Palindrome Partitioning", "partition", DsaArchetype.STRING_TO_LIST_LIST_STRING),
                "Palindrome Partitioning");

        // 94. N-Queens
        register(new DsaProblemDef("N-Queens", "solveNQueens", DsaArchetype.INT_TO_LIST_LIST_STRING),
                "N-Queens", "N Queens");

        // 95. Sudoku Solver
        register(new DsaProblemDef("Sudoku Solver", "solveSudoku", DsaArchetype.IN_PLACE_CHAR_MATRIX),
                "Sudoku Solver");

        // --- BATCH 10: Bit Manipulation & Advanced Hard (96 - 100) ---
        // 96. Counting Bits
        register(new DsaProblemDef("Counting Bits", "countBits", DsaArchetype.INT_TO_INT_ARRAY),
                "Counting Bits");

        // 97. Number of 1 Bits
        register(new DsaProblemDef("Number of 1 Bits", "hammingWeight", DsaArchetype.INT_TO_INT),
                "Number of 1 Bits", "Hamming Weight");

        // 98. Reverse Bits
        register(new DsaProblemDef("Reverse Bits", "reverseBits", DsaArchetype.INT_TO_INT),
                "Reverse Bits");

        // 99. Median of Two Sorted Arrays
        register(new DsaProblemDef("Median of Two Sorted Arrays", "findMedianSortedArrays", DsaArchetype.TWO_INT_ARRAYS_TO_DOUBLE),
                "Median of Two Sorted Arrays");

        // 100. First Missing Positive
        register(new DsaProblemDef("First Missing Positive", "firstMissingPositive", DsaArchetype.INT_ARRAY_TO_INT),
                "First Missing Positive");
    }

    public static void register(DsaProblemDef def, String... aliases) {
        String canonicalKey = normalize(def.getCanonicalTitle());
        TITLE_REGISTRY.put(canonicalKey, def);
        METHOD_REGISTRY.put(def.getMethodName().toLowerCase(), def);

        if (aliases != null) {
            for (String alias : aliases) {
                if (alias != null && !alias.trim().isEmpty()) {
                    TITLE_REGISTRY.put(normalize(alias), def);
                }
            }
        }
    }

    public static DsaProblemDef find(String problemTitle, String code) {
        // 1. Exact normalized title match
        if (problemTitle != null && !problemTitle.trim().isEmpty()) {
            String key = normalize(problemTitle);
            DsaProblemDef def = TITLE_REGISTRY.get(key);
            if (def != null) {
                return def;
            }
        }

        // 2. Fallback to method inspection in code if title is missing or unmapped
        if (code != null && !code.trim().isEmpty()) {
            for (Map.Entry<String, DsaProblemDef> entry : METHOD_REGISTRY.entrySet()) {
                String methodName = entry.getKey();
                // Check if code defines this method
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(methodName) + "\\s*\\(", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(code).find()) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public static boolean isProblemMode(String problemTitle, String code) {
        if (problemTitle != null && !problemTitle.trim().isEmpty()) {
            return true;
        }
        if (code != null && code.contains("class Solution")) {
            return !code.contains("public static void main") && !code.contains("int main(");
        }
        return false;
    }

    public static String normalize(String str) {
        if (str == null) return "";
        return str.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public static Map<String, DsaProblemDef> getAllDefinitions() {
        return Collections.unmodifiableMap(TITLE_REGISTRY);
    }
}
