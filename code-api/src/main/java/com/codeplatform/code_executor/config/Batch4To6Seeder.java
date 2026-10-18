package com.codeplatform.code_executor.config;

import com.codeplatform.code_executor.dto.ProblemCreateRequest;
import com.codeplatform.code_executor.dto.TestCaseCreateRequest;
import com.codeplatform.code_executor.entity.Problem;
import com.codeplatform.code_executor.entity.Problem.Difficulty;
import com.codeplatform.code_executor.entity.TestCase;
import com.codeplatform.code_executor.repository.ProblemRepository;
import com.codeplatform.code_executor.repository.TestCaseRepository;
import com.codeplatform.code_executor.service.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Batch4To6Seeder {

    private static final Logger logger = LoggerFactory.getLogger(Batch4To6Seeder.class);

    public void seedAll(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        seedBatch4(problemService, problemRepository, testCaseRepository);
        seedBatch5(problemService, problemRepository, testCaseRepository);
        seedBatch6(problemService, problemRepository, testCaseRepository);
    }

    // =========================================================================
    // BATCH 4: Sliding Window (Problems 36 - 45)
    // =========================================================================
    private void seedBatch4(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 36: Longest Substring Without Repeating Characters — Medium
        seed(problemService, problemRepository, testCaseRepository, "Longest Substring Without Repeating Characters", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Longest Substring Without Repeating Characters");
            p.setDescription("Given a string s, find the length of the longest substring without duplicate characters.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 0 <= s.length <= 5 * 10^4\n- s consists of English letters, digits, symbols and spaces.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int lengthOfLongestSubstring(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def lengthOfLongestSubstring(self, s: str) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int lengthOfLongestSubstring(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "String", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"abcabcbb\"", "3", true, false, "The answer is 'abc', with the length of 3."),
                new TestCaseCreateRequest("s = \"bbbbb\"", "1", true, false, "The answer is 'b', with the length of 1."),
                new TestCaseCreateRequest("s = \"pwwkew\"", "3", true, false, "The answer is 'wke', with the length of 3."),
                new TestCaseCreateRequest("s = \"\"", "0", false, true, null),
                new TestCaseCreateRequest("s = \"au\"", "2", false, true, null)
            ));
            return p;
        });

        // Problem 37: Longest Repeating Character Replacement — Medium
        seed(problemService, problemRepository, testCaseRepository, "Longest Repeating Character Replacement", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Longest Repeating Character Replacement");
            p.setDescription("You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most k times.\n\nReturn the length of the longest substring containing the same letter you can get after performing the above operations.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 10^5\n- s consists of only uppercase English letters.\n- 0 <= k <= s.length");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int characterReplacement(String s, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def characterReplacement(self, s: str, k: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int characterReplacement(string s, int k) {\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "String", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"ABAB\"\nk = 2", "4", true, false, "Replace the two 'A's with two 'B's or vice versa."),
                new TestCaseCreateRequest("s = \"AABABBA\"\nk = 1", "4", true, false, "Replace the one 'A' in the middle with 'B' and form 'AABBBBA'."),
                new TestCaseCreateRequest("s = \"AAAA\"\nk = 2", "4", false, true, null),
                new TestCaseCreateRequest("s = \"ABBB\"\nk = 2", "4", false, true, null)
            ));
            return p;
        });

        // Problem 38: Minimum Size Subarray Sum — Medium
        seed(problemService, problemRepository, testCaseRepository, "Minimum Size Subarray Sum", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Minimum Size Subarray Sum");
            p.setDescription("Given an array of positive integers nums and a positive integer target, return the minimal length of a subarray whose sum is greater than or equal to target. If there is no such subarray, return 0 instead.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= target <= 10^9\n- 1 <= nums.length <= 10^5\n- 1 <= nums[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int minSubArrayLen(int target, int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def minSubArrayLen(self, target: int, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int minSubArrayLen(int target, vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Array", "Binary Search")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("target = 7\nnums = [2,3,1,2,4,3]", "2", true, false, "Subarray [4,3] has minimal length under problem constraint."),
                new TestCaseCreateRequest("target = 4\nnums = [1,4,4]", "1", true, false, null),
                new TestCaseCreateRequest("target = 11\nnums = [1,1,1,1,1,1,1,1]", "0", true, false, null),
                new TestCaseCreateRequest("target = 6\nnums = [10,2,3]", "1", false, true, null)
            ));
            return p;
        });

        // Problem 39: Permutation in String — Medium
        seed(problemService, problemRepository, testCaseRepository, "Permutation in String", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Permutation in String");
            p.setDescription("Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise.\n\nIn other words, return true if one of s1's permutations is the substring of s2.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s1.length, s2.length <= 10^4\n- s1 and s2 consist of lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean checkInclusion(String s1, String s2) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def checkInclusion(self, s1: str, s2: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool checkInclusion(string s1, string s2) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Two Pointers", "Hash Table", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s1 = \"ab\"\ns2 = \"eidbaooo\"", "true", true, false, "s2 contains one permutation of s1 ('ba')."),
                new TestCaseCreateRequest("s1 = \"ab\"\ns2 = \"eidboaoo\"", "false", true, false, null),
                new TestCaseCreateRequest("s1 = \"a\"\ns2 = \"a\"", "true", false, true, null),
                new TestCaseCreateRequest("s1 = \"adc\"\ns2 = \"dcda\"", "true", false, true, null)
            ));
            return p;
        });

        // Problem 40: Find All Anagrams in a String — Medium
        seed(problemService, problemRepository, testCaseRepository, "Find All Anagrams in a String", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Find All Anagrams in a String");
            p.setDescription("Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length, p.length <= 3 * 10^4\n- s and p consist of lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<Integer> findAnagrams(String s, String p) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findAnagrams(self, s: str, p: str) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> findAnagrams(string s, string p) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Hash Table", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"cbaebabacd\"\np = \"abc\"", "[0, 6]", true, false, "Substring with start index = 0 is 'cba', start index = 6 is 'bac'."),
                new TestCaseCreateRequest("s = \"abab\"\np = \"ab\"", "[0, 1, 2]", true, false, null),
                new TestCaseCreateRequest("s = \"a\"\np = \"b\"", "[]", false, true, null),
                new TestCaseCreateRequest("s = \"aaaaaaaaaa\"\np = \"aaaaaaaaaaaaa\"", "[]", false, true, null)
            ));
            return p;
        });

        // Problem 41: Maximum Average Subarray I — Easy
        seed(problemService, problemRepository, testCaseRepository, "Maximum Average Subarray I", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Maximum Average Subarray I");
            p.setDescription("You are given an integer array nums consisting of n elements, and an integer k.\n\nFind a contiguous subarray whose length is equal to k that has the maximum average value and return this value.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- n == nums.length\n- 1 <= k <= n <= 10^5\n- -10^4 <= nums[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public double findMaxAverage(int[] nums, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findMaxAverage(self, nums: List[int], k: int) -> float:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    double findMaxAverage(vector<int>& nums, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,12,-5,-6,50,3]\nk = 4", "12.75000", true, false, "Maximum average is (12 - 5 - 6 + 50) / 4 = 51 / 4 = 12.75"),
                new TestCaseCreateRequest("nums = [5]\nk = 1", "5.00000", true, false, null),
                new TestCaseCreateRequest("nums = [-1]\nk = 1", "-1.00000", false, true, null),
                new TestCaseCreateRequest("nums = [0,4,0,3,2]\nk = 1", "4.00000", false, true, null)
            ));
            return p;
        });

        // Problem 42: Fruit Into Baskets — Medium
        seed(problemService, problemRepository, testCaseRepository, "Fruit Into Baskets", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Fruit Into Baskets");
            p.setDescription("You are visiting a farm that has a single row of fruit trees arranged from left to right. You have two baskets, and each basket can only hold a single type of fruit. There is no limit on the amount of fruit each basket can hold.\n\nStarting from any tree of your choice, you must pick exactly one fruit from every tree while moving to the right. The picked fruits must fit in one of your baskets. If you reach a tree with fruit that cannot fit in your baskets, you must stop.\n\nReturn the maximum number of fruits you can pick.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= fruits.length <= 10^5\n- 0 <= fruits[i] < fruits.length");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int totalFruit(int[] fruits) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def totalFruit(self, fruits: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int totalFruit(vector<int>& fruits) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Array", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("fruits = [1,2,1]", "3", true, false, "We can pick from all 3 trees."),
                new TestCaseCreateRequest("fruits = [0,1,2,2]", "3", true, false, "We can pick from [1,2,2]."),
                new TestCaseCreateRequest("fruits = [1,2,3,2,2]", "4", true, false, "We can pick from [2,3,2,2]."),
                new TestCaseCreateRequest("fruits = [3,3,3,1,2,1,1,2,3,3,4]", "5", false, true, null)
            ));
            return p;
        });

        // Problem 43: Max Consecutive Ones III — Medium
        seed(problemService, problemRepository, testCaseRepository, "Max Consecutive Ones III", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Max Consecutive Ones III");
            p.setDescription("Given a binary array nums and an integer k, return the maximum number of consecutive 1's in the array if you can flip at most k 0's.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^5\n- nums[i] is either 0 or 1.\n- 0 <= k <= nums.length");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int longestOnes(int[] nums, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def longestOnes(self, nums: List[int], k: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int longestOnes(vector<int>& nums, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Array", "Binary Search", "Prefix Sum")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,1,1,0,0,0,1,1,1,1,0]\nk = 2", "6", true, false, "[1,1,1,0,0,1,1,1,1,1,1] flipped bold numbers"),
                new TestCaseCreateRequest("nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1]\nk = 3", "10", true, false, null),
                new TestCaseCreateRequest("nums = [0,0,0,0]\nk = 0", "0", false, true, null),
                new TestCaseCreateRequest("nums = [1,1,1,1]\nk = 0", "4", false, true, null)
            ));
            return p;
        });

        // Problem 44: Minimum Window Substring — Hard
        seed(problemService, problemRepository, testCaseRepository, "Minimum Window Substring", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Minimum Window Substring");
            p.setDescription("Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that every character in t (including duplicates) is included in the window. If there is no such substring, return the empty string \"\".");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- m == s.length\n- n == t.length\n- 1 <= m, n <= 10^5\n- s and t consist of uppercase and lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public String minWindow(String s, String t) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def minWindow(self, s: str, t: str) -> str:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    string minWindow(string s, string t) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Hash Table", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"ADOBECODEBANC\"\nt = \"ABC\"", "BANC", true, false, "The minimum window substring 'BANC' includes 'A', 'B', and 'C' from string t."),
                new TestCaseCreateRequest("s = \"a\"\nt = \"a\"", "a", true, false, null),
                new TestCaseCreateRequest("s = \"a\"\nt = \"aa\"", "", true, false, "Both 'a's must be included in the window."),
                new TestCaseCreateRequest("s = \"ab\"\nt = \"b\"", "b", false, true, null)
            ));
            return p;
        });

        // Problem 45: Sliding Window Maximum — Hard
        seed(problemService, problemRepository, testCaseRepository, "Sliding Window Maximum", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Sliding Window Maximum");
            p.setDescription("You are given an array of integers nums, there is a sliding window of size k which is moving from the very left of the array to the very right. You can only see the k numbers in the window. Each time the sliding window moves right by one position.\n\nReturn the max sliding window.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(2000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^5\n- -10^4 <= nums[i] <= 10^4\n- 1 <= k <= nums.length");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] maxSlidingWindow(int[] nums, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxSlidingWindow(self, nums: List[int], k: int) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> maxSlidingWindow(vector<int>& nums, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Sliding Window", "Queue", "Array", "Monotonic Queue", "Heap")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,3,-1,-3,5,3,6,7]\nk = 3", "[3, 3, 5, 5, 6, 7]", true, false, "Window position max values are [3, 3, 5, 5, 6, 7]"),
                new TestCaseCreateRequest("nums = [1]\nk = 1", "[1]", true, false, null),
                new TestCaseCreateRequest("nums = [1,-1]\nk = 1", "[1, -1]", false, true, null),
                new TestCaseCreateRequest("nums = [9,11]\nk = 2", "[11]", false, true, null)
            ));
            return p;
        });
    }

    // =========================================================================
    // BATCH 5: Trees & BST (Problems 46 - 55)
    // =========================================================================
    private void seedBatch5(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 46: Subtree of Another Tree — Easy
        seed(problemService, problemRepository, testCaseRepository, "Subtree of Another Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Subtree of Another Tree");
            p.setDescription("Given the roots of two binary trees root and subRoot, return true if there is a subtree of root with the same structure and node values of subRoot and false otherwise.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the root tree is in the range [1, 2000].\n- The number of nodes in the subRoot tree is in the range [1, 1000].\n- -10^4 <= root.val <= 10^4\n- -10^4 <= subRoot.val <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isSubtree(TreeNode root, TreeNode subRoot) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isSubtree(self, root: Optional[TreeNode], subRoot: Optional[TreeNode]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isSubtree(TreeNode* root, TreeNode* subRoot) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [3,4,5,1,2]\nsubRoot = [4,1,2]", "true", true, false, null),
                new TestCaseCreateRequest("root = [3,4,5,1,2,null,null,null,null,0]\nsubRoot = [4,1,2]", "false", true, false, null),
                new TestCaseCreateRequest("root = [1,1]\nsubRoot = [1]", "true", false, true, null)
            ));
            return p;
        });

        // Problem 47: Same Tree — Easy
        seed(problemService, problemRepository, testCaseRepository, "Same Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Same Tree");
            p.setDescription("Given the roots of two binary trees p and q, write a function to check if they are the same or not.\n\nTwo binary trees are considered the same if they are structurally identical, and the nodes have the same value.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in both trees is in the range [0, 100].\n- -10^4 <= Node.val <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isSameTree(TreeNode p, TreeNode q) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isSameTree(self, p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isSameTree(TreeNode* p, TreeNode* q) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Breadth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("p = [1,2,3]\nq = [1,2,3]", "true", true, false, null),
                new TestCaseCreateRequest("p = [1,2]\nq = [1,null,2]", "false", true, false, null),
                new TestCaseCreateRequest("p = [1,2,1]\nq = [1,1,2]", "false", true, false, null),
                new TestCaseCreateRequest("p = []\nq = []", "true", false, true, null)
            ));
            return p;
        });

        // Problem 48: Path Sum — Easy
        seed(problemService, problemRepository, testCaseRepository, "Path Sum", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Path Sum");
            p.setDescription("Given the root of a binary tree and an integer targetSum, return true if the tree has a root-to-leaf path such that adding up all the values along the path equals targetSum.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 5000].\n- -1000 <= Node.val <= 1000\n- -1000 <= targetSum <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean hasPathSum(TreeNode root, int targetSum) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def hasPathSum(self, root: Optional[TreeNode], targetSum: int) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool hasPathSum(TreeNode* root, int targetSum) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Breadth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [5,4,8,11,null,13,4,7,2,null,null,null,1]\ntargetSum = 22", "true", true, false, "Path 5 -> 4 -> 11 -> 2 sums to 22."),
                new TestCaseCreateRequest("root = [1,2,3]\ntargetSum = 5", "false", true, false, null),
                new TestCaseCreateRequest("root = []\ntargetSum = 0", "false", false, true, null),
                new TestCaseCreateRequest("root = [1,2]\ntargetSum = 1", "false", false, true, null)
            ));
            return p;
        });

        // Problem 49: Symmetric Tree — Easy
        seed(problemService, problemRepository, testCaseRepository, "Symmetric Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Symmetric Tree");
            p.setDescription("Given the root of a binary tree, check whether it is a mirror of itself (i.e., symmetric around its center).");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [1, 1000].\n- -100 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isSymmetric(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isSymmetric(self, root: Optional[TreeNode]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isSymmetric(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Breadth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [1,2,2,3,4,4,3]", "true", true, false, null),
                new TestCaseCreateRequest("root = [1,2,2,null,3,null,3]", "false", true, false, null),
                new TestCaseCreateRequest("root = [1]", "true", false, true, null),
                new TestCaseCreateRequest("root = [1,2,3]", "false", false, true, null)
            ));
            return p;
        });

        // Problem 50: Diameter of Binary Tree — Easy
        seed(problemService, problemRepository, testCaseRepository, "Diameter of Binary Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Diameter of Binary Tree");
            p.setDescription("Given the root of a binary tree, return the length of the diameter of the tree.\n\nThe diameter of a binary tree is the length of the longest path between any two nodes in a tree. This path may or may not pass through the root.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [1, 10^4].\n- -100 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int diameterOfBinaryTree(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def diameterOfBinaryTree(self, root: Optional[TreeNode]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int diameterOfBinaryTree(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [1,2,3,4,5]", "3", true, false, "Path length 3 corresponds to the edges [4,2,1,3] or [5,2,1,3]."),
                new TestCaseCreateRequest("root = [1,2]", "1", true, false, null),
                new TestCaseCreateRequest("root = [1]", "0", false, true, null),
                new TestCaseCreateRequest("root = [2,3,null,1]", "2", false, true, null)
            ));
            return p;
        });

        // Problem 51: Balanced Binary Tree — Easy
        seed(problemService, problemRepository, testCaseRepository, "Balanced Binary Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Balanced Binary Tree");
            p.setDescription("Given a binary tree, determine if it is height-balanced (a binary tree in which the depth of the two subtrees of every node never differs by more than one).");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 5000].\n- -10^4 <= Node.val <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isBalanced(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isBalanced(self, root: Optional[TreeNode]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isBalanced(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [3,9,20,null,null,15,7]", "true", true, false, null),
                new TestCaseCreateRequest("root = [1,2,2,3,3,null,null,4,4]", "false", true, false, null),
                new TestCaseCreateRequest("root = []", "true", false, true, null),
                new TestCaseCreateRequest("root = [1,2,3,4,5,6,null,8]", "true", false, true, null)
            ));
            return p;
        });

        // Problem 52: Binary Tree Inorder Traversal — Easy
        seed(problemService, problemRepository, testCaseRepository, "Binary Tree Inorder Traversal", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Binary Tree Inorder Traversal");
            p.setDescription("Given the root of a binary tree, return the inorder traversal of its nodes' values.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 100].\n- -100 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<Integer> inorderTraversal(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def inorderTraversal(self, root: Optional[TreeNode]) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> inorderTraversal(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Stack", "Tree", "Depth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [1,null,2,3]", "[1, 3, 2]", true, false, null),
                new TestCaseCreateRequest("root = []", "[]", true, false, null),
                new TestCaseCreateRequest("root = [1]", "[1]", true, false, null),
                new TestCaseCreateRequest("root = [1,2,3,4,5]", "[4, 2, 5, 1, 3]", false, true, null)
            ));
            return p;
        });

        // Problem 53: Binary Tree Level Order Traversal — Medium
        seed(problemService, problemRepository, testCaseRepository, "Binary Tree Level Order Traversal", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Binary Tree Level Order Traversal");
            p.setDescription("Given the root of a binary tree, return the level order traversal of its nodes' values (i.e., from left to right, level by level).");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 2000].\n- -1000 <= Node.val <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> levelOrder(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> levelOrder(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Breadth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [3,9,20,null,null,15,7]", "[[3], [9, 20], [15, 7]]", true, false, null),
                new TestCaseCreateRequest("root = [1]", "[[1]]", true, false, null),
                new TestCaseCreateRequest("root = []", "[]", false, true, null),
                new TestCaseCreateRequest("root = [1,2,3,4,null,null,5]", "[[1], [2, 3], [4, 5]]", false, true, null)
            ));
            return p;
        });

        // Problem 54: Lowest Common Ancestor of a Binary Search Tree — Medium
        seed(problemService, problemRepository, testCaseRepository, "Lowest Common Ancestor of a Binary Search Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Lowest Common Ancestor of a Binary Search Tree");
            p.setDescription("Given a binary search tree (BST), find the lowest common ancestor (LCA) node of two given nodes in the BST.\n\nAccording to the definition of LCA on Wikipedia: “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).”");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [2, 10^5].\n- -10^9 <= Node.val <= 10^9\n- All Node.val are unique.\n- p != q\n- p and q will exist in the BST.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def lowestCommonAncestor(self, root: 'TreeNode', p: 'TreeNode', q: 'TreeNode') -> 'TreeNode':\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Search Tree", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [6,2,8,0,4,7,9,null,null,3,5]\np = 2\nq = 8", "6", true, false, "The LCA of nodes 2 and 8 is 6."),
                new TestCaseCreateRequest("root = [6,2,8,0,4,7,9,null,null,3,5]\np = 2\nq = 4", "2", true, false, "The LCA of nodes 2 and 4 is 2."),
                new TestCaseCreateRequest("root = [2,1]\np = 2\nq = 1", "2", false, true, null)
            ));
            return p;
        });

        // Problem 55: Binary Tree Maximum Path Sum — Hard
        seed(problemService, problemRepository, testCaseRepository, "Binary Tree Maximum Path Sum", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Binary Tree Maximum Path Sum");
            p.setDescription("A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them. A node can only appear in the sequence at most once. Note that the path does not need to pass through the root.\n\nThe path sum of a path is the sum of the node's values in the path.\n\nGiven the root of a binary tree, return the maximum path sum of any non-empty path.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [1, 3 * 10^4].\n- -1000 <= Node.val <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int maxPathSum(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxPathSum(self, root: Optional[TreeNode]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int maxPathSum(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Tree", "Depth-First Search", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [1,2,3]", "6", true, false, "Optimal path is 2 -> 1 -> 3 with path sum 2 + 1 + 3 = 6."),
                new TestCaseCreateRequest("root = [-10,9,20,null,null,15,7]", "42", true, false, "Optimal path is 15 -> 20 -> 7 with path sum 15 + 20 + 7 = 42."),
                new TestCaseCreateRequest("root = [-3]", "-3", false, true, null),
                new TestCaseCreateRequest("root = [2,-1]", "2", false, true, null)
            ));
            return p;
        });
    }

    // =========================================================================
    // BATCH 6: Heaps, BST & Two Pointers (Problems 56 - 65)
    // =========================================================================
    private void seedBatch6(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 56: Validate Binary Search Tree — Medium
        seed(problemService, problemRepository, testCaseRepository, "Validate Binary Search Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Validate Binary Search Tree");
            p.setDescription("Given the root of a binary tree, determine if it is a valid binary search tree (BST).\n\nA valid BST is defined as follows:\n- The left subtree of a node contains only nodes with keys less than the node's key.\n- The right subtree of a node contains only nodes with keys greater than the node's key.\n- Both the left and right subtrees must also be binary search trees.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [1, 10^4].\n- -2^31 <= Node.val <= 2^31 - 1");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isValidBST(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isValidBST(self, root: Optional[TreeNode]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isValidBST(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Search Tree", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [2,1,3]", "true", true, false, null),
                new TestCaseCreateRequest("root = [5,1,4,null,null,3,6]", "false", true, false, "The root node's value is 5 but its right child's value is 4."),
                new TestCaseCreateRequest("root = [1]", "true", false, true, null),
                new TestCaseCreateRequest("root = [5,4,6,null,null,3,7]", "false", false, true, null)
            ));
            return p;
        });

        // Problem 57: Kth Smallest Element in a BST — Medium
        seed(problemService, problemRepository, testCaseRepository, "Kth Smallest Element in a BST", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Kth Smallest Element in a BST");
            p.setDescription("Given the root of a binary search tree, and an integer k, return the kth smallest value (1-indexed) of all the values of the nodes in the tree.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is n.\n- 1 <= k <= n <= 10^4\n- 0 <= Node.val <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int kthSmallest(TreeNode root, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def kthSmallest(self, root: Optional[TreeNode], k: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int kthSmallest(TreeNode* root, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Depth-First Search", "Binary Search Tree", "Binary Tree")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [3,1,4,null,2]\nk = 1", "1", true, false, null),
                new TestCaseCreateRequest("root = [5,3,6,2,4,null,null,1]\nk = 3", "3", true, false, null),
                new TestCaseCreateRequest("root = [1]\nk = 1", "1", false, true, null)
            ));
            return p;
        });

        // Problem 58: Kth Largest Element in an Array — Medium
        seed(problemService, problemRepository, testCaseRepository, "Kth Largest Element in an Array", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Kth Largest Element in an Array");
            p.setDescription("Given an integer array nums and an integer k, return the kth largest element in the array.\n\nNote that it is the kth largest element in the sorted order, not the kth distinct element.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= k <= nums.length <= 10^5\n- -10^4 <= nums[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int findKthLargest(int[] nums, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findKthLargest(self, nums: List[int], k: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int findKthLargest(vector<int>& nums, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Divide and Conquer", "Sorting", "Heap", "Quickselect")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [3,2,1,5,6,4]\nk = 2", "5", true, false, null),
                new TestCaseCreateRequest("nums = [3,2,3,1,2,4,5,5,6]\nk = 4", "4", true, false, null),
                new TestCaseCreateRequest("nums = [1]\nk = 1", "1", false, true, null),
                new TestCaseCreateRequest("nums = [-1,-1]\nk = 2", "-1", false, true, null)
            ));
            return p;
        });

        // Problem 59: Top K Frequent Elements — Medium
        seed(problemService, problemRepository, testCaseRepository, "Top K Frequent Elements", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Top K Frequent Elements");
            p.setDescription("Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^5\n- -10^4 <= nums[i] <= 10^4\n- k is in the range [1, the number of unique elements in the array].\n- It is guaranteed that the answer is unique.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] topKFrequent(int[] nums, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def topKFrequent(self, nums: List[int], k: int) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> topKFrequent(vector<int>& nums, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Hash Table", "Divide and Conquer", "Sorting", "Heap", "Bucket Sort")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,1,1,2,2,3]\nk = 2", "[1, 2]|[2, 1]", true, false, null),
                new TestCaseCreateRequest("nums = [1]\nk = 1", "[1]", true, false, null),
                new TestCaseCreateRequest("nums = [4,1,-1,2,-1,2,3]\nk = 2", "[-1, 2]|[2, -1]", false, true, null)
            ));
            return p;
        });

        // Problem 60: Last Stone Weight — Easy
        seed(problemService, problemRepository, testCaseRepository, "Last Stone Weight", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Last Stone Weight");
            p.setDescription("You are given an array of integers stones where stones[i] is the weight of the ith stone.\n\nWe are playing a game with the stones. On each turn, we choose the heaviest two stones and smash them together. Suppose the heaviest two stones have weights x and y with x <= y. The result of this smash is:\n- If x == y, both stones are destroyed.\n- If x != y, the stone of weight x is destroyed, and the stone of weight y has new weight y - x.\n\nAt the end of the game, there is at most one stone left. Return the weight of the last remaining stone. If there are no stones left, return 0.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= stones.length <= 30\n- 1 <= stones[i] <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int lastStoneWeight(int[] stones) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def lastStoneWeight(self, stones: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int lastStoneWeight(vector<int>& stones) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Heap")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("stones = [2,7,4,1,8,1]", "1", true, false, "Combine 7 and 8 to get 1, array converts to [2,4,1,1,1]. Finally left with 1."),
                new TestCaseCreateRequest("stones = [1]", "1", true, false, null),
                new TestCaseCreateRequest("stones = [2,2]", "0", false, true, null),
                new TestCaseCreateRequest("stones = [3,7,2]", "2", false, true, null)
            ));
            return p;
        });

        // Problem 61: K Closest Points to Origin — Medium
        seed(problemService, problemRepository, testCaseRepository, "K Closest Points to Origin", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("K Closest Points to Origin");
            p.setDescription("Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane and an integer k, return the k closest points to the origin (0, 0).\n\nThe distance between two points on the X-Y plane is the Euclidean distance (i.e., √(x1 - x2)^2 + (y1 - y2)^2).\n\nYou may return the answer in any order. The answer is guaranteed to be unique (except for the order that it is in).");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= k <= points.length <= 10^4\n- -10^4 <= xi, yi <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[][] kClosest(int[][] points, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> kClosest(vector<vector<int>>& points, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Math", "Divide and Conquer", "Geometry", "Sorting", "Heap", "Quickselect")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("points = [[1,3],[-2,2]]\nk = 1", "[[-2, 2]]", true, false, "The Euclidean distance between (-2, 2) and the origin is sqrt(8). (1, 3) is sqrt(10)."),
                new TestCaseCreateRequest("points = [[3,3],[5,-1],[-2,4]]\nk = 2", "[[3, 3], [-2, 4]]|[[-2, 4], [3, 3]]", true, false, null),
                new TestCaseCreateRequest("points = [[0,1],[1,0]]\nk = 2", "[[0, 1], [1, 0]]|[[1, 0], [0, 1]]", false, true, null)
            ));
            return p;
        });

        // Problem 62: Task Scheduler — Medium
        seed(problemService, problemRepository, testCaseRepository, "Task Scheduler", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Task Scheduler");
            p.setDescription("You are given an array of CPU tasks, each represented by letters A to Z, and a cooling time, n. Each cycle or interval allows the completion of one task. Tasks can be completed in any order, but there's a constraint: identical tasks must be separated by at least n intervals due to cooling time.\n\nReturn the minimum number of intervals the CPU will take to finish all the given tasks.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= tasks.length <= 10^4\n- tasks[i] is uppercase English letter.\n- The integer n is in the range [0, 100].");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int leastInterval(char[] tasks, int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def leastInterval(self, tasks: List[str], n: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int leastInterval(vector<char>& tasks, int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Hash Table", "Greedy", "Sorting", "Heap", "Counting")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("tasks = [\"A\",\"A\",\"A\",\"B\",\"B\",\"B\"]\nn = 2", "8", true, false, "A -> B -> idle -> A -> B -> idle -> A -> B. Total 8 cycles."),
                new TestCaseCreateRequest("tasks = [\"A\",\"C\",\"A\",\"B\",\"D\",\"B\"]\nn = 1", "6", true, false, null),
                new TestCaseCreateRequest("tasks = [\"A\",\"A\",\"A\",\"B\",\"B\",\"B\"]\nn = 0", "6", false, true, null),
                new TestCaseCreateRequest("tasks = [\"B\"]\nn = 2", "1", false, true, null)
            ));
            return p;
        });

        // Problem 63: Sort Characters By Frequency — Medium
        seed(problemService, problemRepository, testCaseRepository, "Sort Characters By Frequency", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Sort Characters By Frequency");
            p.setDescription("Given a string s, sort it in decreasing order based on the frequency of the characters. The frequency of a character is the number of times it appears in the string.\n\nReturn the sorted string. If there are multiple answers, return any of them.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 5 * 10^5\n- s consists of uppercase and lowercase English letters and digits.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public String frequencySort(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def frequencySort(self, s: str) -> str:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    string frequencySort(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Hash Table", "String", "Sorting", "Heap", "Bucket Sort", "Counting")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"tree\"", "eert|eetr", true, false, "'e' appears twice, 'r' and 't' both appear once. 'eert' or 'eetr' are both valid."),
                new TestCaseCreateRequest("s = \"cccaaa\"", "aaaccc|cccaaa", true, false, "Both 'c' and 'a' appear three times, so 'aaaccc' and 'cccaaa' are valid."),
                new TestCaseCreateRequest("s = \"Aabb\"", "bbAa|bbaA", true, false, null),
                new TestCaseCreateRequest("s = \"a\"", "a", false, true, null)
            ));
            return p;
        });

        // Problem 64: Reorganize String — Medium
        seed(problemService, problemRepository, testCaseRepository, "Reorganize String", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Reorganize String");
            p.setDescription("Given a string s, rearrange the characters of s so that any two adjacent characters are not the same.\n\nReturn any possible rearrangement of s or return \"\" if not possible.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 500\n- s consists of lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public String reorganizeString(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def reorganizeString(self, s: str) -> str:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    string reorganizeString(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Hash Table", "String", "Greedy", "Sorting", "Heap", "Counting")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"aab\"", "aba", true, false, null),
                new TestCaseCreateRequest("s = \"aaab\"", "", true, false, "No valid rearrangement possible."),
                new TestCaseCreateRequest("s = \"vvvlo\"", "vlvov|vovlv", false, true, null),
                new TestCaseCreateRequest("s = \"a\"", "a", false, true, null)
            ));
            return p;
        });

        // Problem 65: Trapping Rain Water — Hard
        seed(problemService, problemRepository, testCaseRepository, "Trapping Rain Water", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Trapping Rain Water");
            p.setDescription("Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- n == height.length\n- 1 <= n <= 2 * 10^4\n- 0 <= height[i] <= 10^5");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int trap(int[] height) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def trap(self, height: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int trap(vector<int>& height) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Two Pointers", "Dynamic Programming", "Stack", "Monotonic Stack")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("height = [0,1,0,2,1,0,1,3,2,1,2,1]", "6", true, false, "The elevation map traps 6 units of rain water."),
                new TestCaseCreateRequest("height = [4,2,0,3,2,5]", "9", true, false, null),
                new TestCaseCreateRequest("height = [1,2,3,4,5]", "0", false, true, null),
                new TestCaseCreateRequest("height = [5,4,1,2]", "1", false, true, null)
            ));
            return p;
        });
    }

    private void seed(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository,
                      String title, ProblemSupplier supplier) {
        Problem existingProblem = problemRepository.findByTitle(title);
        if (existingProblem != null) {
            logger.info("Problem '{}' already exists, syncing test cases if needed.", title);
            syncTestCases(testCaseRepository, existingProblem, supplier.get());
            return;
        }

        logger.info("Seeding problem: {}", title);
        try {
            ProblemCreateRequest request = supplier.get();
            problemService.createProblem(request);
            logger.info("Successfully seeded problem: {}", title);
        } catch (Exception e) {
            logger.error("Failed to seed problem '{}': {}", title, e.getMessage(), e);
        }
    }

    private void syncTestCases(TestCaseRepository testCaseRepository, Problem problem, ProblemCreateRequest request) {
        if (request.getTestCases() == null || request.getTestCases().isEmpty()) {
            return;
        }
        List<TestCase> existingTestCases = testCaseRepository.findByProblem(problem);
        if (existingTestCases == null) {
            existingTestCases = Collections.emptyList();
        }

        for (TestCaseCreateRequest desired : request.getTestCases()) {
            if (desired.getInput() == null) continue;
            String desiredInput = desired.getInput().trim();
            TestCase match = existingTestCases.stream()
                    .filter(tc -> tc.getInput() != null && tc.getInput().trim().equals(desiredInput))
                    .findFirst()
                    .orElse(null);

            if (match != null) {
                if (!desired.getExpectedOutput().trim().equals(match.getExpectedOutput().trim())) {
                    logger.info("Updating test case expected output for problem '{}' [id={}]: old='{}' -> new='{}'",
                            problem.getTitle(), match.getId(), match.getExpectedOutput(), desired.getExpectedOutput());
                    match.setExpectedOutput(desired.getExpectedOutput().trim());
                    testCaseRepository.save(match);
                }
            } else {
                TestCase newTc = new TestCase();
                newTc.setProblem(problem);
                newTc.setInput(desired.getInput());
                newTc.setExpectedOutput(desired.getExpectedOutput());
                newTc.setIsSample(desired.getIsSample() != null ? desired.getIsSample() : false);
                newTc.setIsHidden(desired.getIsHidden() != null ? desired.getIsHidden() : false);
                newTc.setExplanation(desired.getExplanation());
                testCaseRepository.save(newTc);
                logger.info("Inserted missing test case for problem '{}': input='{}'", problem.getTitle(), desired.getInput());
            }
        }
    }

    @FunctionalInterface
    private interface ProblemSupplier {
        ProblemCreateRequest get();
    }
}
