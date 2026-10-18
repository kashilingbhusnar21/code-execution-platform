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
public class Batch7To10Seeder {

    private static final Logger logger = LoggerFactory.getLogger(Batch7To10Seeder.class);

    public void seedAll(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        seedBatch7(problemService, problemRepository, testCaseRepository);
        seedBatch8(problemService, problemRepository, testCaseRepository);
        seedBatch9(problemService, problemRepository, testCaseRepository);
        seedBatch10(problemService, problemRepository, testCaseRepository);
    }

    // =========================================================================
    // BATCH 7: Dynamic Programming (Problems 66 - 75)
    // =========================================================================
    private void seedBatch7(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 66: House Robber — Medium
        seed(problemService, problemRepository, testCaseRepository, "House Robber", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("House Robber");
            p.setDescription("You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night.\n\nGiven an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 100\n- 0 <= nums[i] <= 400");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int rob(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def rob(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int rob(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,3,1]", "4", true, false, "Rob house 1 (money = 1) and then rob house 3 (money = 3). Total = 4."),
                new TestCaseCreateRequest("nums = [2,7,9,3,1]", "12", true, false, "Rob house 1 (2), house 3 (9) and house 5 (1). Total = 12."),
                new TestCaseCreateRequest("nums = [0]", "0", false, true, null),
                new TestCaseCreateRequest("nums = [2,1,1,2]", "4", false, true, null)
            ));
            return p;
        });

        // Problem 67: House Robber II — Medium
        seed(problemService, problemRepository, testCaseRepository, "House Robber II", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("House Robber II");
            p.setDescription("You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed. All houses at this place are arranged in a circle. That means the first house is the neighbor of the last one. Meanwhile, adjacent houses have a security system connected, and it will automatically contact the police if two adjacent houses were broken into on the same night.\n\nGiven an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 100\n- 0 <= nums[i] <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int rob(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def rob(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int rob(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [2,3,2]", "3", true, false, "You cannot rob house 1 and 3 because they are neighbors."),
                new TestCaseCreateRequest("nums = [1,2,3,1]", "4", true, false, null),
                new TestCaseCreateRequest("nums = [1,2,3]", "3", true, false, null),
                new TestCaseCreateRequest("nums = [0]", "0", false, true, null)
            ));
            return p;
        });

        // Problem 68: Coin Change — Medium
        seed(problemService, problemRepository, testCaseRepository, "Coin Change", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Coin Change");
            p.setDescription("You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.\n\nReturn the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.\n\nYou may assume that you have an infinite number of each kind of coin.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= coins.length <= 12\n- 1 <= coins[i] <= 2^31 - 1\n- 0 <= amount <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int coinChange(int[] coins, int amount) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def coinChange(self, coins: List[int], amount: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int coinChange(vector<int>& coins, int amount) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Breadth-First Search", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("coins = [1,2,5]\namount = 11", "3", true, false, "11 = 5 + 5 + 1"),
                new TestCaseCreateRequest("coins = [2]\namount = 3", "-1", true, false, null),
                new TestCaseCreateRequest("coins = [1]\namount = 0", "0", true, false, null),
                new TestCaseCreateRequest("coins = [1,3,4,5]\namount = 7", "2", false, true, "7 = 3 + 4")
            ));
            return p;
        });

        // Problem 69: Longest Increasing Subsequence — Medium
        seed(problemService, problemRepository, testCaseRepository, "Longest Increasing Subsequence", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Longest Increasing Subsequence");
            p.setDescription("Given an integer array nums, return the length of the longest strictly increasing subsequence.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 2500\n- -10^4 <= nums[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int lengthOfLIS(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def lengthOfLIS(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int lengthOfLIS(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Binary Search", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [10,9,2,5,3,7,101,18]", "4", true, false, "The longest increasing subsequence is [2,3,7,101], length 4."),
                new TestCaseCreateRequest("nums = [0,1,0,3,2,3]", "4", true, false, null),
                new TestCaseCreateRequest("nums = [7,7,7,7,7,7,7]", "1", true, false, null),
                new TestCaseCreateRequest("nums = [4,10,4,3,8,9]", "3", false, true, null)
            ));
            return p;
        });

        // Problem 70: Word Break — Medium
        seed(problemService, problemRepository, testCaseRepository, "Word Break", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Word Break");
            p.setDescription("Given a string s and a dictionary of strings wordDict, return true if s can be segmented into a space-separated sequence of one or more dictionary words.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 300\n- 1 <= wordDict.length <= 1000\n- 1 <= wordDict[i].length <= 20\n- s and wordDict[i] consist of only lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean wordBreak(String s, List<String> wordDict) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def wordBreak(self, s: str, wordDict: List[str]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool wordBreak(string s, vector<string>& wordDict) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Hash Table", "String", "Trie")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"leetcode\"\nwordDict = [\"leet\",\"code\"]", "true", true, false, "Return true because 'leetcode' can be segmented as 'leet code'."),
                new TestCaseCreateRequest("s = \"applepenapple\"\nwordDict = [\"apple\",\"pen\"]", "true", true, false, null),
                new TestCaseCreateRequest("s = \"catsandog\"\nwordDict = [\"cats\",\"dog\",\"sand\",\"and\",\"cat\"]", "false", true, false, null),
                new TestCaseCreateRequest("s = \"a\"\nwordDict = [\"b\"]", "false", false, true, null)
            ));
            return p;
        });

        // Problem 71: Min Cost Climbing Stairs — Easy
        seed(problemService, problemRepository, testCaseRepository, "Min Cost Climbing Stairs", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Min Cost Climbing Stairs");
            p.setDescription("You are given an integer array cost where cost[i] is the cost of ith step on a staircase. Once you pay the cost, you can either climb one or two steps.\n\nYou can either start from the step with index 0, or the step with index 1.\n\nReturn the minimum cost to reach the top of the floor.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 2 <= cost.length <= 1000\n- 0 <= cost[i] <= 999");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int minCostClimbingStairs(int[] cost) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def minCostClimbingStairs(self, cost: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int minCostClimbingStairs(vector<int>& cost) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("cost = [10,15,20]", "15", true, false, "Start on step 1, pay 15 and climb two steps to reach the top."),
                new TestCaseCreateRequest("cost = [1,100,1,1,1,100,1,1,100,1]", "6", true, false, null),
                new TestCaseCreateRequest("cost = [0,0,0,0]", "0", false, true, null),
                new TestCaseCreateRequest("cost = [1,2]", "1", false, true, null)
            ));
            return p;
        });

        // Problem 72: Longest Common Subsequence — Medium
        seed(problemService, problemRepository, testCaseRepository, "Longest Common Subsequence", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Longest Common Subsequence");
            p.setDescription("Given two strings text1 and text2, return the length of their longest common subsequence. If there is no common subsequence, return 0.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= text1.length, text2.length <= 1000\n- text1 and text2 consist of only lowercase English characters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int longestCommonSubsequence(String text1, String text2) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def longestCommonSubsequence(self, text1: str, text2: str) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int longestCommonSubsequence(string text1, string text2) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("text1 = \"abcde\"\ntext2 = \"ace\"", "3", true, false, "The longest common subsequence is 'ace' and its length is 3."),
                new TestCaseCreateRequest("text1 = \"abc\"\ntext2 = \"abc\"", "3", true, false, null),
                new TestCaseCreateRequest("text1 = \"abc\"\ntext2 = \"def\"", "0", true, false, null),
                new TestCaseCreateRequest("text1 = \"bsbininm\"\ntext2 = \"jmjkbkjkv\"", "1", false, true, null)
            ));
            return p;
        });

        // Problem 73: Decode Ways — Medium
        seed(problemService, problemRepository, testCaseRepository, "Decode Ways", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Decode Ways");
            p.setDescription("A message containing letters from A-Z can be encoded into numbers using the mapping: 'A' -> \"1\", 'B' -> \"2\", ..., 'Z' -> \"26\".\n\nGiven a string s containing only digits, return the number of ways to decode it.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 100\n- s contains only digits and may contain leading zero(s).");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int numDecodings(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def numDecodings(self, s: str) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int numDecodings(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"12\"", "2", true, false, "'12' could be decoded as 'AB' (1 2) or 'L' (12)."),
                new TestCaseCreateRequest("s = \"226\"", "3", true, false, "'226' could be decoded as 'BZ' (2 26), 'VF' (22 6), or 'BBF' (2 2 6)."),
                new TestCaseCreateRequest("s = \"06\"", "0", true, false, "No valid grouping starts with 0."),
                new TestCaseCreateRequest("s = \"10\"", "1", false, true, null)
            ));
            return p;
        });

        // Problem 74: Edit Distance — Medium
        seed(problemService, problemRepository, testCaseRepository, "Edit Distance", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Edit Distance");
            p.setDescription("Given two strings word1 and word2, return the minimum number of operations required to convert word1 to word2.\n\nYou have the following three operations permitted on a word:\n- Insert a character\n- Delete a character\n- Replace a character");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 0 <= word1.length, word2.length <= 500\n- word1 and word2 consist of lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int minDistance(String word1, String word2) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def minDistance(self, word1: str, word2: str) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int minDistance(string word1, string word2) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("word1 = \"horse\"\nword2 = \"ros\"", "3", true, false, "horse -> rorse -> rose -> ros (3 ops)"),
                new TestCaseCreateRequest("word1 = \"intention\"\nword2 = \"execution\"", "5", true, false, null),
                new TestCaseCreateRequest("word1 = \"\"\nword2 = \"a\"", "1", false, true, null),
                new TestCaseCreateRequest("word1 = \"abc\"\nword2 = \"abc\"", "0", false, true, null)
            ));
            return p;
        });

        // Problem 75: Regular Expression Matching — Hard
        seed(problemService, problemRepository, testCaseRepository, "Regular Expression Matching", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Regular Expression Matching");
            p.setDescription("Given an input string s and a pattern p, implement regular expression matching with support for '.' and '*' where:\n- '.' Matches any single character.\n- '*' Matches zero or more of the preceding element.\n\nThe matching should cover the entire input string (not partial).");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 20\n- 1 <= p.length <= 20\n- s contains only lowercase English letters.\n- p contains only lowercase English letters, '.', and '*'.\n- It is guaranteed for each appearance of the character '*', there will be a previous valid character to match.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isMatch(String s, String p) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isMatch(self, s: str, p: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isMatch(string s, string p) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "String", "Recursion")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"aa\"\np = \"a\"", "false", true, false, "'a' does not match the entire string 'aa'."),
                new TestCaseCreateRequest("s = \"aa\"\np = \"a*\"", "true", true, false, "'*' means zero or more of the preceding element, 'a'."),
                new TestCaseCreateRequest("s = \"ab\"\np = \".*\"", "true", true, false, "'.*' means 'zero or more (*) of any character (.)'."),
                new TestCaseCreateRequest("s = \"aab\"\np = \"c*a*b\"", "true", false, true, null)
            ));
            return p;
        });
    }

    // =========================================================================
    // BATCH 8: Graphs & BFS/DFS (Problems 76 - 85)
    // =========================================================================
    private void seedBatch8(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 76: Number of Islands — Medium
        seed(problemService, problemRepository, testCaseRepository, "Number of Islands", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Number of Islands");
            p.setDescription("Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.\n\nAn island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- m == grid.length\n- n == grid[i].length\n- 1 <= m, n <= 300\n- grid[i][j] is '0' or '1'.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int numIslands(char[][] grid) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def numIslands(self, grid: List[List[str]]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int numIslands(vector<vector<char>>& grid) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Union Find", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("grid = [[\"1\",\"1\",\"1\",\"1\",\"0\"],[\"1\",\"1\",\"0\",\"1\",\"0\"],[\"1\",\"1\",\"0\",\"0\",\"0\"],[\"0\",\"0\",\"0\",\"0\",\"0\"]]", "1", true, false, null),
                new TestCaseCreateRequest("grid = [[\"1\",\"1\",\"0\",\"0\",\"0\"],[\"1\",\"1\",\"0\",\"0\",\"0\"],[\"0\",\"0\",\"1\",\"0\",\"0\"],[\"0\",\"0\",\"0\",\"1\",\"1\"]]", "3", true, false, null),
                new TestCaseCreateRequest("grid = [[\"0\"]]", "0", false, true, null),
                new TestCaseCreateRequest("grid = [[\"1\"]]", "1", false, true, null)
            ));
            return p;
        });

        // Problem 77: Max Area of Island — Medium
        seed(problemService, problemRepository, testCaseRepository, "Max Area of Island", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Max Area of Island");
            p.setDescription("You are given an m x n binary matrix grid. An island is a group of 1's (representing land) connected 4-directionally (horizontal or vertical.) You may assume all four edges of the grid are surrounded by water.\n\nThe area of an island is the number of cells with a value 1 in the island.\n\nReturn the maximum area of an island in grid. If there is no island, return 0.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- m == grid.length\n- n == grid[i].length\n- 1 <= m, n <= 50\n- grid[i][j] is either 0 or 1.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int maxAreaOfIsland(int[][] grid) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int maxAreaOfIsland(vector<vector<int>>& grid) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("grid = [[0,0,1,0,0,0,0,1,0,0,0,0,0],[0,0,0,0,0,0,0,1,1,1,0,0,0],[0,1,1,0,1,0,0,0,0,0,0,0,0],[0,1,0,0,1,1,0,0,1,0,1,0,0],[0,1,0,0,1,1,0,0,1,1,1,0,0],[0,0,0,0,0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,1,1,0,0,0],[0,0,0,0,0,0,0,1,1,0,0,0,0]]", "6", true, false, "The answer is not 11, because the island must be connected 4-directionally."),
                new TestCaseCreateRequest("grid = [[0,0,0,0,0,0,0,0]]", "0", true, false, null),
                new TestCaseCreateRequest("grid = [[1,1],[1,0]]", "3", false, true, null)
            ));
            return p;
        });

        // Problem 78: Flood Fill — Easy
        seed(problemService, problemRepository, testCaseRepository, "Flood Fill", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Flood Fill");
            p.setDescription("An image is represented by an m x n integer grid image where image[i][j] represents the pixel value of the image.\n\nYou are also given three integers sr, sc, and color. You should perform a flood fill on the image starting from the pixel image[sr][sc].\n\nReturn the modified image after performing the flood fill.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- m == image.length\n- n == image[i].length\n- 1 <= m, n <= 50\n- 0 <= image[i][j], color < 2^16\n- 0 <= sr < m\n- 0 <= sc < n");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[][] floodFill(int[][] image, int sr, int sc, int color) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> floodFill(vector<vector<int>>& image, int sr, int sc, int color) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("image = [[1,1,1],[1,1,0],[1,0,1]]\nsr = 1\nsc = 1\ncolor = 2", "[[2, 2, 2], [2, 2, 0], [2, 0, 1]]", true, false, "From the center, all 4-directionally connected 1s are colored with 2."),
                new TestCaseCreateRequest("image = [[0,0,0],[0,0,0]]\nsr = 0\nsc = 0\ncolor = 0", "[[0, 0, 0], [0, 0, 0]]", true, false, null),
                new TestCaseCreateRequest("image = [[0,0,0],[0,1,1]]\nsr = 1\nsc = 1\ncolor = 1", "[[0, 0, 0], [0, 1, 1]]", false, true, null)
            ));
            return p;
        });

        // Problem 79: Course Schedule — Medium
        seed(problemService, problemRepository, testCaseRepository, "Course Schedule", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Course Schedule");
            p.setDescription("There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.\n\nReturn true if you can finish all courses. Otherwise, return false.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= numCourses <= 2000\n- 0 <= prerequisites.length <= 5000\n- prerequisites[i].length == 2\n- 0 <= ai, bi < numCourses\n- All the pairs prerequisites[i] are unique.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean canFinish(int numCourses, int[][] prerequisites) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def canFinish(self, numCourses: int, prerequisites: List[List[int]]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool canFinish(int numCourses, vector<vector<int>>& prerequisites) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Topological Sort")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("numCourses = 2\nprerequisites = [[1,0]]", "true", true, false, "To take course 1 you should have finished course 0. So it is possible."),
                new TestCaseCreateRequest("numCourses = 2\nprerequisites = [[1,0],[0,1]]", "false", true, false, "Cycle detected between courses 0 and 1."),
                new TestCaseCreateRequest("numCourses = 1\nprerequisites = []", "true", false, true, null),
                new TestCaseCreateRequest("numCourses = 3\nprerequisites = [[0,1],[0,2],[1,2]]", "true", false, true, null)
            ));
            return p;
        });

        // Problem 80: Course Schedule II — Medium
        seed(problemService, problemRepository, testCaseRepository, "Course Schedule II", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Course Schedule II");
            p.setDescription("There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.\n\nReturn the ordering of courses you should take to finish all courses. If there are many valid answers, return any of them. If it is impossible to finish all courses, return an empty array.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= numCourses <= 2000\n- 0 <= prerequisites.length <= numCourses * (numCourses - 1)\n- prerequisites[i].length == 2\n- 0 <= ai, bi < numCourses\n- ai != bi\n- All pairs [ai, bi] are distinct.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] findOrder(int numCourses, int[][] prerequisites) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findOrder(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> findOrder(int numCourses, vector<vector<int>>& prerequisites) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Topological Sort")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("numCourses = 2\nprerequisites = [[1,0]]", "[0, 1]", true, false, "To take course 1 you should have finished course 0."),
                new TestCaseCreateRequest("numCourses = 4\nprerequisites = [[1,0],[2,0],[3,1],[3,2]]", "[0, 2, 1, 3]|[0, 1, 2, 3]", true, false, "Valid topological orderings."),
                new TestCaseCreateRequest("numCourses = 1\nprerequisites = []", "[0]", false, true, null),
                new TestCaseCreateRequest("numCourses = 2\nprerequisites = [[0,1],[1,0]]", "[]", false, true, null)
            ));
            return p;
        });

        // Problem 81: Pacific Atlantic Water Flow — Medium
        seed(problemService, problemRepository, testCaseRepository, "Pacific Atlantic Water Flow", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Pacific Atlantic Water Flow");
            p.setDescription("There is an m x n rectangular island that borders both the Pacific Ocean and Atlantic Ocean. The Pacific Ocean touches the island's left and top edges, and the Atlantic Ocean touches the island's right and bottom edges.\n\nReturn a 2D list of grid coordinates result where result[i] = [ri, ci] denotes that rain water can flow from cell (ri, ci) to both the Pacific and Atlantic oceans.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- m == heights.length\n- n == heights[r].length\n- 1 <= m, n <= 200\n- 0 <= heights[r][c] <= 10^5");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> pacificAtlantic(int[][] heights) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def pacificAtlantic(self, heights: List[List[int]]) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> pacificAtlantic(vector<vector<int>>& heights) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]", "[[0, 4], [1, 3], [1, 4], [2, 2], [3, 0], [3, 1], [4, 0]]", true, false, null),
                new TestCaseCreateRequest("heights = [[1]]", "[[0, 0]]", true, false, null),
                new TestCaseCreateRequest("heights = [[2,1],[1,2]]", "[[0, 0], [0, 1], [1, 0], [1, 1]]", false, true, null)
            ));
            return p;
        });

        // Problem 82: Surrounded Regions — Medium
        seed(problemService, problemRepository, testCaseRepository, "Surrounded Regions", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Surrounded Regions");
            p.setDescription("Given an m x n matrix board containing 'X' and 'O', capture all regions that are 4-directionally surrounded by 'X'.\n\nA region is captured by flipping all 'O's into 'X's in that surrounded region.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- m == board.length\n- n == board[i].length\n- 1 <= m, n <= 200\n- board[i][j] is 'X' or 'O'.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public void solve(char[][] board) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def solve(self, board: List[List[str]]) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void solve(vector<vector<char>>& board) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Depth-First Search", "Breadth-First Search", "Union Find", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("board = [[\"X\",\"X\",\"X\",\"X\"],[\"X\",\"O\",\"O\",\"X\"],[\"X\",\"X\",\"O\",\"X\"],[\"X\",\"O\",\"X\",\"X\"]]", "[[\"X\", \"X\", \"X\", \"X\"], [\"X\", \"X\", \"X\", \"X\"], [\"X\", \"X\", \"X\", \"X\"], [\"X\", \"O\", \"X\", \"X\"]]", true, false, "Surrounded 'O's are flipped to 'X's. Border connected 'O' remains."),
                new TestCaseCreateRequest("board = [[\"X\"]]", "[[\"X\"]]", true, false, null),
                new TestCaseCreateRequest("board = [[\"O\",\"O\"],[\"O\",\"O\"]]", "[[\"O\", \"O\"], [\"O\", \"O\"]]", false, true, null)
            ));
            return p;
        });

        // Problem 83: Rotting Oranges — Medium
        seed(problemService, problemRepository, testCaseRepository, "Rotting Oranges", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Rotting Oranges");
            p.setDescription("You are given an m x n grid where each cell can have one of three values:\n- 0 representing an empty cell,\n- 1 representing a fresh orange, or\n- 2 representing a rotten orange.\n\nEvery minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten.\n\nReturn the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- m == grid.length\n- n == grid[i].length\n- 1 <= m, n <= 10\n- grid[i][j] is 0, 1, or 2.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int orangesRotting(int[][] grid) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def orangesRotting(self, grid: List[List[int]]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int orangesRotting(vector<vector<int>>& grid) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Breadth-First Search", "Matrix")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("grid = [[2,1,1],[1,1,0],[0,1,1]]", "4", true, false, null),
                new TestCaseCreateRequest("grid = [[2,1,1],[0,1,1],[1,0,1]]", "-1", true, false, "The fresh orange at (2,0) cannot be reached."),
                new TestCaseCreateRequest("grid = [[0,2]]", "0", true, false, "Since there are already no fresh oranges, the answer is 0."),
                new TestCaseCreateRequest("grid = [[1]]", "-1", false, true, null)
            ));
            return p;
        });

        // Problem 84: Word Ladder — Hard
        seed(problemService, problemRepository, testCaseRepository, "Word Ladder", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Word Ladder");
            p.setDescription("A transformation sequence from word beginWord to word endWord using a dictionary wordList is a sequence of words beginWord -> s1 -> s2 -> ... -> sk such that:\n- Every adjacent pair of words differs by a single letter.\n- Every si for 1 <= i <= k is in wordList. Note that beginWord does not need to be in wordList.\n- sk == endWord\n\nGiven two words, beginWord and endWord, and a dictionary wordList, return the number of words in the shortest transformation sequence from beginWord to endWord, or 0 if no such sequence exists.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(2000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= beginWord.length <= 10\n- endWord.length == beginWord.length\n- 1 <= wordList.length <= 5000\n- wordList[i].length == beginWord.length\n- beginWord, endWord, and wordList[i] consist of lowercase English letters.\n- beginWord != endWord\n- All the words in wordList are unique.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int ladderLength(String beginWord, String endWord, List<String> wordList) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def ladderLength(self, beginWord: str, endWord: str, wordList: List[str]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int ladderLength(string beginWord, string endWord, vector<string>& wordList) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Breadth-First Search", "Hash Table", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("beginWord = \"hit\"\nendWord = \"cog\"\nwordList = [\"hot\",\"dot\",\"dog\",\"lot\",\"log\",\"cog\"]", "5", true, false, "One shortest transformation sequence is \"hit\" -> \"hot\" -> \"dot\" -> \"dog\" -> \"cog\", which is 5 words long."),
                new TestCaseCreateRequest("beginWord = \"hit\"\nendWord = \"cog\"\nwordList = [\"hot\",\"dot\",\"dog\",\"lot\",\"log\"]", "0", true, false, "The endWord \"cog\" is not in wordList."),
                new TestCaseCreateRequest("beginWord = \"a\"\nendWord = \"c\"\nwordList = [\"a\",\"b\",\"c\"]", "2", false, true, null)
            ));
            return p;
        });

        // Problem 85: Alien Dictionary — Hard
        seed(problemService, problemRepository, testCaseRepository, "Alien Dictionary", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Alien Dictionary");
            p.setDescription("There is a new alien language that uses the English alphabet. However, the order among letters is unknown to you.\n\nYou are given a list of strings words from the alien language's dictionary, where the strings in words are sorted lexicographically by the rules of this new language.\n\nReturn a string of the unique letters in the new alien language sorted in lexicographically increasing order by the new language's rules. If there is no solution, return \"\". If there are multiple solutions, return any of them.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= words.length <= 100\n- 1 <= words[i].length <= 100\n- words[i] consists of only lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public String alienOrder(String[] words) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def alienOrder(self, words: List[str]) -> str:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    string alienOrder(vector<string>& words) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Graph", "Topological Sort", "Array", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("words = [\"wrt\",\"wrf\",\"erf\",\"ett\",\"rftt\"]", "wertf", true, false, "The valid alien ordering is \"wertf\"."),
                new TestCaseCreateRequest("words = [\"z\",\"x\"]", "zx", true, false, null),
                new TestCaseCreateRequest("words = [\"z\",\"x\",\"z\"]", "", true, false, "Cycle detected, return empty string."),
                new TestCaseCreateRequest("words = [\"abc\",\"ab\"]", "", false, true, "Prefix cannot appear after longer word.")
            ));
            return p;
        });
    }

    // =========================================================================
    // BATCH 9: Backtracking & Matrix Search (Problems 86 - 95)
    // =========================================================================
    private void seedBatch9(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 86: Subsets — Medium
        seed(problemService, problemRepository, testCaseRepository, "Subsets", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Subsets");
            p.setDescription("Given an integer array nums of unique elements, return all possible subsets (the power set).\n\nThe solution set must not contain duplicate subsets. Return the solution in any order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10\n- -10 <= nums[i] <= 10\n- All the numbers of nums are unique.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> subsets(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def subsets(self, nums: List[int]) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> subsets(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Array", "Bit Manipulation")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,3]", "[[], [1], [2], [1, 2], [3], [1, 3], [2, 3], [1, 2, 3]]", true, false, null),
                new TestCaseCreateRequest("nums = [0]", "[[], [0]]", true, false, null),
                new TestCaseCreateRequest("nums = [1,2]", "[[], [1], [2], [1, 2]]", false, true, null)
            ));
            return p;
        });

        // Problem 87: Combination Sum — Medium
        seed(problemService, problemRepository, testCaseRepository, "Combination Sum", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Combination Sum");
            p.setDescription("Given an array of distinct integers candidates and a target integer target, return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.\n\nThe same number may be chosen from candidates an unlimited number of times. Two combinations are unique if the frequency of at least one of the chosen numbers is different.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= candidates.length <= 30\n- 2 <= candidates[i] <= 40\n- All elements of candidates are distinct.\n- 1 <= target <= 40");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> combinationSum(int[] candidates, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> combinationSum(vector<int>& candidates, int target) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("candidates = [2,3,6,7]\ntarget = 7", "[[2, 2, 3], [7]]", true, false, "2 and 3 are candidates, and 2 + 2 + 3 = 7. 7 is also a candidate. These are the only two combinations."),
                new TestCaseCreateRequest("candidates = [2,3,5]\ntarget = 8", "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]", true, false, null),
                new TestCaseCreateRequest("candidates = [2]\ntarget = 1", "[]", true, false, null),
                new TestCaseCreateRequest("candidates = [3,5]\ntarget = 8", "[[3, 5]]", false, true, null)
            ));
            return p;
        });

        // Problem 88: Permutations — Medium
        seed(problemService, problemRepository, testCaseRepository, "Permutations", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Permutations");
            p.setDescription("Given an array nums of distinct integers, return all the possible permutations. You can return the answer in any order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 6\n- -10 <= nums[i] <= 10\n- All the integers of nums are unique.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> permute(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def permute(self, nums: List[int]) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> permute(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,3]", "[[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]]", true, false, null),
                new TestCaseCreateRequest("nums = [0,1]", "[[0, 1], [1, 0]]", true, false, null),
                new TestCaseCreateRequest("nums = [1]", "[[1]]", true, false, null)
            ));
            return p;
        });

        // Problem 89: Subsets II — Medium
        seed(problemService, problemRepository, testCaseRepository, "Subsets II", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Subsets II");
            p.setDescription("Given an integer array nums that may contain duplicates, return all possible subsets (the power set).\n\nThe solution set must not contain duplicate subsets. Return the solution in any order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10\n- -10 <= nums[i] <= 10");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<Integer>> subsetsWithDup(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<int>> subsetsWithDup(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Array", "Bit Manipulation")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,2]", "[[], [1], [1, 2], [1, 2, 2], [2], [2, 2]]", true, false, null),
                new TestCaseCreateRequest("nums = [0]", "[[], [0]]", true, false, null),
                new TestCaseCreateRequest("nums = [4,4,4,1,4]", "[[], [1], [1, 4], [1, 4, 4], [1, 4, 4, 4], [1, 4, 4, 4, 4], [4], [4, 4], [4, 4, 4], [4, 4, 4, 4]]", false, true, null)
            ));
            return p;
        });

        // Problem 90: Generate Parentheses — Medium
        seed(problemService, problemRepository, testCaseRepository, "Generate Parentheses", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Generate Parentheses");
            p.setDescription("Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= n <= 8");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<String> generateParenthesis(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def generateParenthesis(self, n: int) -> List[str]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<string> generateParenthesis(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "String", "Dynamic Programming")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 3", "[\"((()))\", \"(()())\", \"(())()\", \"()(())\", \"()()()\"]", true, false, null),
                new TestCaseCreateRequest("n = 1", "[\"()\"]", true, false, null),
                new TestCaseCreateRequest("n = 2", "[\"(())\", \"()()\"]", false, true, null)
            ));
            return p;
        });

        // Problem 91: Word Search — Medium
        seed(problemService, problemRepository, testCaseRepository, "Word Search", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Word Search");
            p.setDescription("Given an m x n grid of characters board and a string word, return true if word exists in the grid.\n\nThe word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring. The same letter cell may not be used more than once.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- m == board.length\n- n = board[i].length\n- 1 <= m, n <= 6\n- 1 <= word.length <= 15\n- board and word consists of only lowercase and uppercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean exist(char[][] board, String word) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def exist(self, board: List[List[str]], word: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool exist(vector<vector<char>>& board, string word) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Matrix", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("board = [[\"A\",\"B\",\"C\",\"E\"],[\"S\",\"F\",\"C\",\"S\"],[\"A\",\"D\",\"E\",\"E\"]]\nword = \"ABCCED\"", "true", true, false, null),
                new TestCaseCreateRequest("board = [[\"A\",\"B\",\"C\",\"E\"],[\"S\",\"F\",\"C\",\"S\"],[\"A\",\"D\",\"E\",\"E\"]]\nword = \"SEE\"", "true", true, false, null),
                new TestCaseCreateRequest("board = [[\"A\",\"B\",\"C\",\"E\"],[\"S\",\"F\",\"C\",\"S\"],[\"A\",\"D\",\"E\",\"E\"]]\nword = \"ABCB\"", "false", true, false, null),
                new TestCaseCreateRequest("board = [[\"a\"]]\nword = \"a\"", "true", false, true, null)
            ));
            return p;
        });

        // Problem 92: Letter Combinations of a Phone Number — Medium
        seed(problemService, problemRepository, testCaseRepository, "Letter Combinations of a Phone Number", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Letter Combinations of a Phone Number");
            p.setDescription("Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent. Return the answer in any order.\n\nA mapping of digits to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 0 <= digits.length <= 4\n- digits[i] is a digit in the range ['2', '9'].");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<String> letterCombinations(String digits) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def letterCombinations(self, digits: str) -> List[str]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<string> letterCombinations(string digits) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "String", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("digits = \"23\"", "[\"ad\", \"ae\", \"af\", \"bd\", \"be\", \"bf\", \"cd\", \"ce\", \"cf\"]", true, false, null),
                new TestCaseCreateRequest("digits = \"\"", "[]", true, false, null),
                new TestCaseCreateRequest("digits = \"2\"", "[\"a\", \"b\", \"c\"]", true, false, null),
                new TestCaseCreateRequest("digits = \"9\"", "[\"w\", \"x\", \"y\", \"z\"]", false, true, null)
            ));
            return p;
        });

        // Problem 93: Palindrome Partitioning — Medium
        seed(problemService, problemRepository, testCaseRepository, "Palindrome Partitioning", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Palindrome Partitioning");
            p.setDescription("Given a string s, partition s such that every substring of the partition is a palindrome. Return all possible palindrome partitioning of s.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 16\n- s contains only lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<String>> partition(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def partition(self, s: str) -> List[List[str]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<string>> partition(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Dynamic Programming", "String")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"aab\"", "[[\"a\", \"a\", \"b\"], [\"aa\", \"b\"]]", true, false, null),
                new TestCaseCreateRequest("s = \"a\"", "[[\"a\"]]", true, false, null),
                new TestCaseCreateRequest("s = \"bb\"", "[[\"b\", \"b\"], [\"bb\"]]", false, true, null)
            ));
            return p;
        });

        // Problem 94: N-Queens — Hard
        seed(problemService, problemRepository, testCaseRepository, "N-Queens", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("N-Queens");
            p.setDescription("The n-queens puzzle is the problem of placing n queens on an n x n chessboard such that no two queens attack each other.\n\nGiven an integer n, return all distinct solutions to the n-queens puzzle. You may return the answer in any order.\n\nEach solution contains a distinct board configuration of the n-queens' placement, where 'Q' and '.' both indicate a queen and an empty space, respectively.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(2000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= n <= 9");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public List<List<String>> solveNQueens(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def solveNQueens(self, n: int) -> List[List[str]]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<vector<string>> solveNQueens(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 4", "[[\".Q..\", \"...Q\", \"Q...\", \"..Q.\"], [\"..Q.\", \"Q...\", \"...Q\", \".Q..\"]]", true, false, "There exist two distinct solutions to the 4-queens puzzle."),
                new TestCaseCreateRequest("n = 1", "[[\"Q\"]]", true, false, null),
                new TestCaseCreateRequest("n = 2", "[]", false, true, null),
                new TestCaseCreateRequest("n = 3", "[]", false, true, null)
            ));
            return p;
        });

        // Problem 95: Sudoku Solver — Hard
        seed(problemService, problemRepository, testCaseRepository, "Sudoku Solver", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Sudoku Solver");
            p.setDescription("Write a program to solve a Sudoku puzzle by filling the empty cells.\n\nA sudoku solution must satisfy all of the following rules:\n- Each of the digits 1-9 must occur exactly once in each row.\n- Each of the digits 1-9 must occur exactly once in each column.\n- Each of the digits 1-9 must occur exactly once in each of the 9 3x3 sub-boxes of the grid.\n\nThe '.' character indicates empty cells.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(2000);
            p.setMemoryLimit(256);
            p.setConstraints("- board.length == 9\n- board[i].length == 9\n- board[i][j] is a digit or '.'.\n- It is guaranteed that the input board has only one solution.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public void solveSudoku(char[][] board) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def solveSudoku(self, board: List[List[str]]) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void solveSudoku(vector<vector<char>>& board) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Backtracking", "Matrix", "Array")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("board = [[\"5\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\"6\",\".\",\".\",\"1\",\"9\",\"5\",\".\",\".\",\".\"],[\".\",\"9\",\"8\",\".\",\".\",\".\",\".\",\"6\",\".\"],[\"8\",\".\",\".\",\".\",\"6\",\".\",\".\",\".\",\"3\"],[\"4\",\".\",\".\",\"8\",\".\",\"3\",\".\",\".\",\"1\"],[\"7\",\".\",\".\",\".\",\"2\",\".\",\".\",\".\",\"6\"],[\".\",\"6\",\".\",\".\",\".\",\".\",\"2\",\"8\",\".\"],[\".\",\".\",\".\",\"4\",\"1\",\"9\",\".\",\".\",\"5\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]", "[[\"5\", \"3\", \"4\", \"6\", \"7\", \"8\", \"9\", \"1\", \"2\"], [\"6\", \"7\", \"2\", \"1\", \"9\", \"5\", \"3\", \"4\", \"8\"], [\"1\", \"9\", \"8\", \"3\", \"4\", \"2\", \"5\", \"6\", \"7\"], [\"8\", \"5\", \"9\", \"7\", \"6\", \"1\", \"4\", \"2\", \"3\"], [\"4\", \"2\", \"6\", \"8\", \"5\", \"3\", \"7\", \"9\", \"1\"], [\"7\", \"1\", \"3\", \"9\", \"2\", \"4\", \"8\", \"5\", \"6\"], [\"9\", \"6\", \"1\", \"5\", \"3\", \"7\", \"2\", \"8\", \"4\"], [\"2\", \"8\", \"7\", \"4\", \"1\", \"9\", \"6\", \"3\", \"5\"], [\"3\", \"4\", \"5\", \"2\", \"8\", \"6\", \"1\", \"7\", \"9\"]]", true, false, null)
            ));
            return p;
        });
    }

    // =========================================================================
    // BATCH 10: Bit Manipulation & Advanced Hard (Problems 96 - 100)
    // =========================================================================
    private void seedBatch10(ProblemService problemService, ProblemRepository problemRepository, TestCaseRepository testCaseRepository) {
        // Problem 96: Counting Bits — Easy
        seed(problemService, problemRepository, testCaseRepository, "Counting Bits", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Counting Bits");
            p.setDescription("Given an integer n, return an array ans of length n + 1 such that for each i (0 <= i <= n), ans[i] is the number of 1's in the binary representation of i.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 0 <= n <= 10^5");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] countBits(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def countBits(self, n: int) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> countBits(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Bit Manipulation", "Dynamic Programming")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 2", "[0, 1, 1]", true, false, "0 --> 0\n1 --> 1\n2 --> 10"),
                new TestCaseCreateRequest("n = 5", "[0, 1, 1, 2, 1, 2]", true, false, "0 --> 0\n1 --> 1\n2 --> 10\n3 --> 11\n4 --> 100\n5 --> 101"),
                new TestCaseCreateRequest("n = 0", "[0]", false, true, null),
                new TestCaseCreateRequest("n = 1", "[0, 1]", false, true, null)
            ));
            return p;
        });

        // Problem 97: Number of 1 Bits — Easy
        seed(problemService, problemRepository, testCaseRepository, "Number of 1 Bits", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Number of 1 Bits");
            p.setDescription("Given a positive integer n, write a function that returns the number of set bits it has (also known as the Hamming weight).");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= n <= 2^31 - 1");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int hammingWeight(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def hammingWeight(self, n: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int hammingWeight(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Bit Manipulation", "Divide and Conquer")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 11", "3", true, false, "The input binary string 1011 has a total of three set bits."),
                new TestCaseCreateRequest("n = 128", "1", true, false, "The input binary string 10000000 has a total of one set bit."),
                new TestCaseCreateRequest("n = 2147483645", "30", true, false, null),
                new TestCaseCreateRequest("n = 1", "1", false, true, null)
            ));
            return p;
        });

        // Problem 98: Reverse Bits — Easy
        seed(problemService, problemRepository, testCaseRepository, "Reverse Bits", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Reverse Bits");
            p.setDescription("Reverse bits of a given 32 bits signed integer.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The input must be a binary string of length 32 or signed 32-bit integer.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int reverseBits(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def reverseBits(self, n: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int reverseBits(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Bit Manipulation", "Divide and Conquer")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 43261596", "964176192", true, false, null),
                new TestCaseCreateRequest("n = 1", "-2147483648|1073741824", true, false, null),
                new TestCaseCreateRequest("n = 0", "0", false, true, null)
            ));
            return p;
        });

        // Problem 99: Median of Two Sorted Arrays — Hard
        seed(problemService, problemRepository, testCaseRepository, "Median of Two Sorted Arrays", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Median of Two Sorted Arrays");
            p.setDescription("Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.\n\nThe overall run time complexity should be O(log (m+n)).");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- nums1.length == m\n- nums2.length == n\n- 0 <= m <= 1000\n- 0 <= n <= 1000\n- 1 <= m + n <= 2000\n- -10^6 <= nums1[i], nums2[i] <= 10^6");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public double findMedianSortedArrays(int[] nums1, int[] nums2) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    double findMedianSortedArrays(vector<int>& nums1, vector<int>& nums2) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Binary Search", "Array", "Divide and Conquer")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums1 = [1,3]\nnums2 = [2]", "2.00000", true, false, "merged array = [1,2,3] and median is 2."),
                new TestCaseCreateRequest("nums1 = [1,2]\nnums2 = [3,4]", "2.50000", true, false, "merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5."),
                new TestCaseCreateRequest("nums1 = [0,0]\nnums2 = [0,0]", "0.00000", false, true, null),
                new TestCaseCreateRequest("nums1 = []\nnums2 = [1]", "1.00000", false, true, null)
            ));
            return p;
        });

        // Problem 100: First Missing Positive — Hard
        seed(problemService, problemRepository, testCaseRepository, "First Missing Positive", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("First Missing Positive");
            p.setDescription("Given an unsorted integer array nums. Return the smallest positive integer that is not present in nums.\n\nYou must implement an algorithm that runs in O(n) time and uses O(1) auxiliary space.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^5\n- -2^31 <= nums[i] <= 2^31 - 1");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int firstMissingPositive(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def firstMissingPositive(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int firstMissingPositive(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Array", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,0]", "3", true, false, "The numbers in the range [1,2] are all in the array."),
                new TestCaseCreateRequest("nums = [3,4,-1,1]", "2", true, false, "1 is in the array but 2 is missing."),
                new TestCaseCreateRequest("nums = [7,8,9,11,12]", "1", true, false, "The smallest positive integer 1 is missing."),
                new TestCaseCreateRequest("nums = [1]", "2", false, true, null)
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
