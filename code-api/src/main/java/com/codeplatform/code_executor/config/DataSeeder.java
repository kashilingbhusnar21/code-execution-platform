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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private Batch4To6Seeder batch4To6Seeder;

    @Autowired
    private Batch7To10Seeder batch7To10Seeder;

    @Override
    public void run(String... args) throws Exception {
        seedProblems();
    }

    private void seedProblems() {
        // Problem 1: Two Sum — Arrays — Easy
        seedProblemIfNotExists("Two Sum", () -> {
            ProblemCreateRequest twoSum = new ProblemCreateRequest();
            twoSum.setTitle("Two Sum");
            twoSum.setDescription("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nYou can return the answer in any order.");
            twoSum.setDifficulty(Difficulty.EASY);
            twoSum.setTimeLimit(1000);
            twoSum.setMemoryLimit(256);
            twoSum.setConstraints("- 2 <= nums.length <= 10^4\n- -10^9 <= nums[i] <= 10^9\n- -10^9 <= target <= 10^9\n- Only one valid answer exists.");
            twoSum.setStarterCode("{\"java\": \"class Solution {\\n    public int[] twoSum(int[] nums, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def twoSum(self, nums: List[int], target: int) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> twoSum(vector<int>& nums, int target) {\\n        // Your code here\\n    }\\n};\"}");
            twoSum.setTopics(new HashSet<>(Arrays.asList("Arrays")));
            twoSum.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [2,7,11,15]\ntarget = 9", "[0,1]", true, false, "Because nums[0] + nums[1] == 9, we return [0, 1]."),
                new TestCaseCreateRequest("nums = [3,2,4]\ntarget = 6", "[1,2]", true, false, "Because nums[1] + nums[2] == 6, we return [1, 2]."),
                new TestCaseCreateRequest("nums = [3,3]\ntarget = 6", "[0,1]", true, false, "Because nums[0] + nums[1] == 6, we return [0, 1]."),
                new TestCaseCreateRequest("nums = [1,5,3,7,2]\ntarget = 9", "[3,4]", false, true, null),
                new TestCaseCreateRequest("nums = [10,20,30,40,50]\ntarget = 60", "[0,4]|[1,3]", false, true, null)
            ));
            return twoSum;
        });

        // Problem 2: Reverse a String — Strings — Easy
        seedProblemIfNotExists("Reverse a String", () -> {
            ProblemCreateRequest reverseString = new ProblemCreateRequest();
            reverseString.setTitle("Reverse a String");
            reverseString.setDescription("Write a function that reverses a string. The input string is given as an array of characters s. You must do this by modifying the input array in-place with O(1) extra memory.");
            reverseString.setDifficulty(Difficulty.EASY);
            reverseString.setTimeLimit(1000);
            reverseString.setMemoryLimit(256);
            reverseString.setConstraints("- 1 <= s.length <= 10^5\n- s[i] is a printable ASCII character");
            reverseString.setStarterCode("{\"java\": \"class Solution {\\n    public void reverseString(char[] s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def reverseString(self, s: List[str]) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void reverseString(vector<char>& s) {\\n        // Your code here\\n    }\\n};\"}");
            reverseString.setTopics(new HashSet<>(Arrays.asList("Strings")));
            reverseString.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = [\"h\",\"e\",\"l\",\"l\",\"o\"]", "[\"o\",\"l\",\"l\",\"e\",\"h\"]", true, false, "The string \"hello\" reversed is \"olleh\""),
                new TestCaseCreateRequest("s = [\"H\",\"a\",\"n\",\"n\",\"a\",\"h\"]", "[\"h\",\"a\",\"n\",\"n\",\"a\",\"H\"]", true, false, "The string \"Hannah\" reversed is \"hannaH\""),
                new TestCaseCreateRequest("s = [\"a\"]", "[\"a\"]", true, false, "Single character remains the same"),
                new TestCaseCreateRequest("s = [\"a\",\"b\"]", "[\"b\",\"a\"]", false, true, null),
                new TestCaseCreateRequest("s = [\"a\",\"b\",\"c\",\"d\"]", "[\"d\",\"c\",\"b\",\"a\"]", false, true, null)
            ));
            return reverseString;
        });

        // Problem 3: Valid Parentheses — Stack — Easy
        seedProblemIfNotExists("Valid Parentheses", () -> {
            ProblemCreateRequest validParentheses = new ProblemCreateRequest();
            validParentheses.setTitle("Valid Parentheses");
            validParentheses.setDescription("Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.\n\nAn input string is valid if:\n- Open brackets must be closed by the same type of brackets.\n- Open brackets must be closed in the correct order.\n- Every close bracket has a corresponding open bracket of the same type.");
            validParentheses.setDifficulty(Difficulty.EASY);
            validParentheses.setTimeLimit(1000);
            validParentheses.setMemoryLimit(256);
            validParentheses.setConstraints("- 1 <= s.length <= 10^4\n- s consists of parentheses only '()[]{}'.");
            validParentheses.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isValid(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isValid(self, s: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isValid(string s) {\\n        // Your code here\\n    }\\n};\"}");
            validParentheses.setTopics(new HashSet<>(Arrays.asList("Stack")));
            validParentheses.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"()\"", "true", true, false, "Valid"),
                new TestCaseCreateRequest("s = \"()[]{}\"", "true", true, false, "Valid"),
                new TestCaseCreateRequest("s = \"(]\"", "false", true, false, "Invalid"),
                new TestCaseCreateRequest("s = \"([)]\"", "false", true, false, "Invalid"),
                new TestCaseCreateRequest("s = \"{[]}\"", "true", false, true, null),
                new TestCaseCreateRequest("s = \"((()))\"", "true", false, true, null)
            ));
            return validParentheses;
        });

        // Problem 4: Binary Search — Searching — Easy
        seedProblemIfNotExists("Binary Search", () -> {
            ProblemCreateRequest binarySearch = new ProblemCreateRequest();
            binarySearch.setTitle("Binary Search");
            binarySearch.setDescription("Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1.\n\nYou must write an algorithm with O(log n) runtime complexity.");
            binarySearch.setDifficulty(Difficulty.EASY);
            binarySearch.setTimeLimit(1000);
            binarySearch.setMemoryLimit(256);
            binarySearch.setConstraints("- 1 <= nums.length <= 10^4\n- -10^4 < nums[i], target < 10^4\n- All the integers in nums are unique.\n- nums is sorted in ascending order.");
            binarySearch.setStarterCode("{\"java\": \"class Solution {\\n    public int search(int[] nums, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def search(self, nums: List[int], target: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int search(vector<int>& nums, int target) {\\n        // Your code here\\n    }\\n};\"}");
            binarySearch.setTopics(new HashSet<>(Arrays.asList("Searching")));
            binarySearch.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [-1,0,3,5,9,12]\ntarget = 9", "4", true, false, "9 exists in nums and its index is 4"),
                new TestCaseCreateRequest("nums = [-1,0,3,5,9,12]\ntarget = 2", "-1", true, false, "2 does not exist in nums so return -1"),
                new TestCaseCreateRequest("nums = [5]\ntarget = 5", "0", true, false, "Single element, target found"),
                new TestCaseCreateRequest("nums = [1,2,3,4,5]\ntarget = 1", "0", false, true, null),
                new TestCaseCreateRequest("nums = [1,2,3,4,5]\ntarget = 5", "4", false, true, null)
            ));
            return binarySearch;
        });

        // Problem 5: Maximum Subarray Sum — Arrays — Medium
        seedProblemIfNotExists("Maximum Subarray Sum", () -> {
            ProblemCreateRequest maxSubarray = new ProblemCreateRequest();
            maxSubarray.setTitle("Maximum Subarray Sum");
            maxSubarray.setDescription("Given an integer array nums, find the subarray with the largest sum, and return its sum.\n\nA subarray is a contiguous non-empty sequence of elements within an array.");
            maxSubarray.setDifficulty(Difficulty.MEDIUM);
            maxSubarray.setTimeLimit(2000);
            maxSubarray.setMemoryLimit(256);
            maxSubarray.setConstraints("- 1 <= nums.length <= 10^5\n- -10^4 <= nums[i] <= 10^4");
            maxSubarray.setStarterCode("{\"java\": \"class Solution {\\n    public int maxSubArray(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxSubArray(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int maxSubArray(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            maxSubarray.setTopics(new HashSet<>(Arrays.asList("Arrays")));
            maxSubarray.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [-2,1,-3,4,-1,2,1,-5,4]", "6", true, false, "The subarray [4,-1,2,1] has the largest sum = 6"),
                new TestCaseCreateRequest("nums = [1]", "1", true, false, "Single element"),
                new TestCaseCreateRequest("nums = [5,4,-1,7,8]", "23", true, false, "The entire array has the largest sum"),
                new TestCaseCreateRequest("nums = [-1,-2,-3]", "-1", false, true, null),
                new TestCaseCreateRequest("nums = [1,2,3,4,5]", "15", false, true, null)
            ));
            return maxSubarray;
        });

        // Problem 6: Contains Duplicate — Arrays & Hashing — Easy
        seedProblemIfNotExists("Contains Duplicate", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Contains Duplicate");
            p.setDescription("Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^5\n- -10^9 <= nums[i] <= 10^9");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean containsDuplicate(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def containsDuplicate(self, nums: List[int]) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool containsDuplicate(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Arrays", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,3,1]", "true", true, false, "1 appears at indices 0 and 3"),
                new TestCaseCreateRequest("nums = [1,2,3,4]", "false", true, false, "All elements are distinct"),
                new TestCaseCreateRequest("nums = [1,1,1,3,3,4,3,2,4,2]", "true", true, false, "Multiple duplicates exist"),
                new TestCaseCreateRequest("nums = [0]", "false", false, true, null),
                new TestCaseCreateRequest("nums = [1000000000,-1000000000,1000000000]", "true", false, true, null)
            ));
            return p;
        });

        // Problem 7: Valid Anagram — Strings & Hashing — Easy
        seedProblemIfNotExists("Valid Anagram", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Valid Anagram");
            p.setDescription("Given two strings s and t, return true if t is an anagram of s, and false otherwise.\n\nAn Anagram is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length, t.length <= 5 * 10^4\n- s and t consist of lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isAnagram(String s, String t) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isAnagram(self, s: str, t: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isAnagram(string s, string t) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Strings", "Hash Table")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"anagram\"\nt = \"nagaram\"", "true", true, false, "\"nagaram\" is an anagram of \"anagram\""),
                new TestCaseCreateRequest("s = \"rat\"\nt = \"car\"", "false", true, false, "\"car\" is not an anagram of \"rat\""),
                new TestCaseCreateRequest("s = \"a\"\nt = \"a\"", "true", false, true, null),
                new TestCaseCreateRequest("s = \"ab\"\nt = \"a\"", "false", false, true, null),
                new TestCaseCreateRequest("s = \"listen\"\nt = \"silent\"", "true", false, true, null)
            ));
            return p;
        });

        // Problem 8: Best Time to Buy and Sell Stock — Arrays — Easy
        seedProblemIfNotExists("Best Time to Buy and Sell Stock", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Best Time to Buy and Sell Stock");
            p.setDescription("You are given an array prices where prices[i] is the price of a given stock on the ith day.\n\nYou want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.\n\nReturn the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= prices.length <= 10^5\n- 0 <= prices[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int maxProfit(int[] prices) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxProfit(self, prices: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int maxProfit(vector<int>& prices) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Arrays", "Dynamic Programming")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("prices = [7,1,5,3,6,4]", "5", true, false, "Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6 - 1 = 5."),
                new TestCaseCreateRequest("prices = [7,6,4,3,1]", "0", true, false, "In this case, no transactions are done and max profit = 0."),
                new TestCaseCreateRequest("prices = [1,2]", "1", false, true, null),
                new TestCaseCreateRequest("prices = [2,4,1]", "2", false, true, null),
                new TestCaseCreateRequest("prices = [3,3,3,3]", "0", false, true, null)
            ));
            return p;
        });

        // Problem 9: Valid Palindrome — Two Pointers — Easy
        seedProblemIfNotExists("Valid Palindrome", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Valid Palindrome");
            p.setDescription("A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and numbers.\n\nGiven a string s, return true if it is a palindrome, or false otherwise.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 2 * 10^5\n- s consists only of printable ASCII characters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public boolean isPalindrome(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def isPalindrome(self, s: str) -> bool:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    bool isPalindrome(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Two Pointers", "Strings")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"A man, a plan, a canal: Panama\"", "true", true, false, "\"amanaplanacanalpanama\" is a palindrome."),
                new TestCaseCreateRequest("s = \"race a car\"", "false", true, false, "\"raceacar\" is not a palindrome."),
                new TestCaseCreateRequest("s = \" \"", "true", true, false, "Empty string reads same forward and backward."),
                new TestCaseCreateRequest("s = \"0P\"", "false", false, true, null),
                new TestCaseCreateRequest("s = \"ab_a\"", "true", false, true, null)
            ));
            return p;
        });

        // Problem 10: Search Insert Position — Binary Search — Easy
        seedProblemIfNotExists("Search Insert Position", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Search Insert Position");
            p.setDescription("Given a sorted array of distinct integers and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.\n\nYou must write an algorithm with O(log n) runtime complexity.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^4\n- -10^4 <= nums[i] <= 10^4\n- nums contains distinct values sorted in ascending order.\n- -10^4 <= target <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int searchInsert(int[] nums, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def searchInsert(self, nums: List[int], target: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int searchInsert(vector<int>& nums, int target) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Binary Search", "Arrays")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,3,5,6]\ntarget = 5", "2", true, false, "5 exists at index 2"),
                new TestCaseCreateRequest("nums = [1,3,5,6]\ntarget = 2", "1", true, false, "2 would be inserted at index 1"),
                new TestCaseCreateRequest("nums = [1,3,5,6]\ntarget = 7", "4", true, false, "7 would be inserted at index 4"),
                new TestCaseCreateRequest("nums = [1,3,5,6]\ntarget = 0", "0", false, true, null),
                new TestCaseCreateRequest("nums = [1]\ntarget = 0", "0", false, true, null)
            ));
            return p;
        });

        // Problem 11: Move Zeroes — Two Pointers — Easy
        seedProblemIfNotExists("Move Zeroes", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Move Zeroes");
            p.setDescription("Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements.\n\nNote that you must do this in-place without making a copy of the array.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 10^4\n- -2^31 <= nums[i] <= 2^31 - 1");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public void moveZeroes(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def moveZeroes(self, nums: List[int]) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void moveZeroes(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Arrays", "Two Pointers")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [0,1,0,3,12]", "[1, 3, 12, 0, 0]", true, false, "All zeros moved to end"),
                new TestCaseCreateRequest("nums = [0]", "[0]", true, false, "Single zero unchanged"),
                new TestCaseCreateRequest("nums = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("nums = [2,1]", "[2, 1]", false, true, null),
                new TestCaseCreateRequest("nums = [0,0,1]", "[1, 0, 0]", false, true, null)
            ));
            return p;
        });

        // Problem 12: Climbing Stairs — Dynamic Programming — Easy
        seedProblemIfNotExists("Climbing Stairs", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Climbing Stairs");
            p.setDescription("You are climbing a staircase. It takes n steps to reach the top.\n\nEach time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= n <= 45");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int climbStairs(int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def climbStairs(self, n: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int climbStairs(int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Math")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("n = 2", "2", true, false, "1. 1 step + 1 step\n2. 2 steps"),
                new TestCaseCreateRequest("n = 3", "3", true, false, "1. 1+1+1\n2. 1+2\n3. 2+1"),
                new TestCaseCreateRequest("n = 1", "1", false, true, null),
                new TestCaseCreateRequest("n = 4", "5", false, true, null),
                new TestCaseCreateRequest("n = 5", "8", false, true, null),
                new TestCaseCreateRequest("n = 10", "89", false, true, null)
            ));
            return p;
        });

        // Problem 13: Single Number — Bit Manipulation — Easy
        seedProblemIfNotExists("Single Number", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Single Number");
            p.setDescription("Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.\n\nYou must implement a solution with a linear runtime complexity and use only constant extra space.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 3 * 10^4\n- -3 * 10^4 <= nums[i] <= 3 * 10^4\n- Each element in the array appears twice except for one element which appears only once.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int singleNumber(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def singleNumber(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int singleNumber(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Bit Manipulation", "Arrays")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [2,2,1]", "1", true, false, "1 appears once"),
                new TestCaseCreateRequest("nums = [4,1,2,1,2]", "4", true, false, "4 appears once"),
                new TestCaseCreateRequest("nums = [1]", "1", true, false, "Single element"),
                new TestCaseCreateRequest("nums = [-1,-1,-2]", "-2", false, true, null),
                new TestCaseCreateRequest("nums = [0,1,0]", "1", false, true, null)
            ));
            return p;
        });

        // Problem 14: Two Sum II - Input Array Is Sorted — Two Pointers — Medium
        seedProblemIfNotExists("Two Sum II - Input Array Is Sorted", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Two Sum II - Input Array Is Sorted");
            p.setDescription("Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number.\n\nReturn the indices of the two numbers, index1 and index2, added by one as an integer array [index1, index2] of length 2.\n\nThe tests are generated such that there is exactly one solution. You may not use the same element twice.\n\nYour solution must use only constant extra space.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 2 <= numbers.length <= 3 * 10^4\n- -1000 <= numbers[i] <= 1000\n- numbers is sorted in non-decreasing order.\n- -1000 <= target <= 1000\n- The tests are generated such that there is exactly one solution.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] twoSum(int[] numbers, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def twoSum(self, numbers: List[int], target: int) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> twoSum(vector<int>& numbers, int target) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Two Pointers", "Arrays", "Binary Search")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("numbers = [2,7,11,15]\ntarget = 9", "[1, 2]", true, false, "2 + 7 = 9. 1-indexed: index1 = 1, index2 = 2."),
                new TestCaseCreateRequest("numbers = [2,3,4]\ntarget = 6", "[1, 3]", true, false, "2 + 4 = 6. 1-indexed: index1 = 1, index2 = 3."),
                new TestCaseCreateRequest("numbers = [-1,0]\ntarget = -1", "[1, 2]", true, false, "-1 + 0 = -1. 1-indexed: index1 = 1, index2 = 2."),
                new TestCaseCreateRequest("numbers = [0,0,3,4]\ntarget = 0", "[1, 2]", false, true, null),
                new TestCaseCreateRequest("numbers = [5,25,75]\ntarget = 100", "[2, 3]", false, true, null)
            ));
            return p;
        });

        // Problem 15: Product of Array Except Self — Arrays — Medium
        seedProblemIfNotExists("Product of Array Except Self", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Product of Array Except Self");
            p.setDescription("Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].\n\nThe product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.\n\nYou must write an algorithm that runs in O(n) time and without using the division operation.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1500);
            p.setMemoryLimit(256);
            p.setConstraints("- 2 <= nums.length <= 10^5\n- -30 <= nums[i] <= 30\n- The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int[] productExceptSelf(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def productExceptSelf(self, nums: List[int]) -> List[int]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    vector<int> productExceptSelf(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Arrays", "Prefix Sum")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [1,2,3,4]", "[24, 12, 8, 6]", true, false, "Each element is product of all others"),
                new TestCaseCreateRequest("nums = [-1,1,0,-3,3]", "[0, 0, 9, 0, 0]", true, false, "Zero handling test"),
                new TestCaseCreateRequest("nums = [2,3]", "[3, 2]", false, true, null),
                new TestCaseCreateRequest("nums = [0,0]", "[0, 0]", false, true, null),
                new TestCaseCreateRequest("nums = [1,0]", "[0, 1]", false, true, null)
            ));
            return p;
        });

        // Problem 16: Container With Most Water — Two Pointers — Medium
        seedProblemIfNotExists("Container With Most Water", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Container With Most Water");
            p.setDescription("You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).\n\nFind two lines that together with the x-axis form a container, such that the container contains the most water.\n\nReturn the maximum amount of water a container can store.\n\nNotice that you may not slant the container.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- n == height.length\n- 2 <= n <= 10^5\n- 0 <= height[i] <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int maxArea(int[] height) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def maxArea(self, height: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int maxArea(vector<int>& height) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Two Pointers", "Arrays")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("height = [1,8,6,2,5,4,8,3,7]", "49", true, false, "The vertical lines are at indices 1 (height 8) and 8 (height 7). Area = 7 * min(8, 7) = 49."),
                new TestCaseCreateRequest("height = [1,1]", "1", true, false, "Distance is 1, height is 1, max area = 1."),
                new TestCaseCreateRequest("height = [4,3,2,1,4]", "16", false, true, null),
                new TestCaseCreateRequest("height = [1,2,1]", "2", false, true, null),
                new TestCaseCreateRequest("height = [1,2,4,3]", "4", false, true, null)
            ));
            return p;
        });

        // Problem 17: Find Minimum in Rotated Sorted Array — Binary Search — Medium
        seedProblemIfNotExists("Find Minimum in Rotated Sorted Array", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Find Minimum in Rotated Sorted Array");
            p.setDescription("Suppose an array of length n sorted in ascending order is rotated between 1 and n times.\n\nGiven the sorted rotated array nums of unique elements, return the minimum element of this array.\n\nYou must write an algorithm that runs in O(log n) time.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- n == nums.length\n- 1 <= n <= 5000\n- -5000 <= nums[i] <= 5000\n- All the integers of nums are unique.\n- nums is sorted and rotated between 1 and n times.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int findMin(int[] nums) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def findMin(self, nums: List[int]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int findMin(vector<int>& nums) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Binary Search", "Arrays")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [3,4,5,1,2]", "1", true, false, "The original array was [1,2,3,4,5] rotated 3 times."),
                new TestCaseCreateRequest("nums = [4,5,6,7,0,1,2]", "0", true, false, "The original array was [0,1,2,4,5,6,7] and it was rotated 4 times."),
                new TestCaseCreateRequest("nums = [11,13,15,17]", "11", true, false, "The array is rotated 4 times (fully sorted)."),
                new TestCaseCreateRequest("nums = [2,1]", "1", false, true, null),
                new TestCaseCreateRequest("nums = [1]", "1", false, true, null)
            ));
            return p;
        });

        // Problem 18: Search in Rotated Sorted Array — Binary Search — Medium
        seedProblemIfNotExists("Search in Rotated Sorted Array", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Search in Rotated Sorted Array");
            p.setDescription("There is an integer array nums sorted in ascending order (with distinct values).\n\nPrior to being passed to your function, nums is possibly rotated at an unknown pivot index k.\n\nGiven the array nums after the possible rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.\n\nYou must write an algorithm with O(log n) runtime complexity.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= nums.length <= 5000\n- -10^4 <= nums[i] <= 10^4\n- All values of nums are unique.\n- nums is an ascending array that is possibly rotated.\n- -10^4 <= target <= 10^4");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int search(int[] nums, int target) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def search(self, nums: List[int], target: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int search(vector<int>& nums, int target) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Binary Search", "Arrays")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("nums = [4,5,6,7,0,1,2]\ntarget = 0", "4", true, false, "0 exists in nums at index 4"),
                new TestCaseCreateRequest("nums = [4,5,6,7,0,1,2]\ntarget = 3", "-1", true, false, "3 does not exist in nums"),
                new TestCaseCreateRequest("nums = [1]\ntarget = 0", "-1", true, false, "0 does not exist in nums"),
                new TestCaseCreateRequest("nums = [1]\ntarget = 1", "0", false, true, null),
                new TestCaseCreateRequest("nums = [5,1,3]\ntarget = 5", "0", false, true, null)
            ));
            return p;
        });

        // Problem 19: Longest Common Prefix — Strings — Easy
        seedProblemIfNotExists("Longest Common Prefix", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Longest Common Prefix");
            p.setDescription("Write a function to find the longest common prefix string amongst an array of strings.\n\nIf there is no common prefix, return an empty string \"\".");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= strs.length <= 200\n- 0 <= strs[i].length <= 200\n- strs[i] consists of only lowercase English letters.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public String longestCommonPrefix(String[] strs) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def longestCommonPrefix(self, strs: List[str]) -> str:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    string longestCommonPrefix(vector<string>& strs) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Strings")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("strs = [\"flower\",\"flow\",\"flight\"]", "fl", true, false, "The longest common prefix is 'fl'."),
                new TestCaseCreateRequest("strs = [\"dog\",\"racecar\",\"car\"]", "", true, false, "There is no common prefix among the input strings."),
                new TestCaseCreateRequest("strs = [\"a\"]", "a", false, true, null),
                new TestCaseCreateRequest("strs = [\"cir\",\"car\"]", "c", false, true, null),
                new TestCaseCreateRequest("strs = [\"reflower\",\"flow\",\"flight\"]", "", false, true, null)
            ));
            return p;
        });

        // Problem 20: Length of Last Word — Strings — Easy
        seedProblemIfNotExists("Length of Last Word", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Length of Last Word");
            p.setDescription("Given a string s consisting of words and spaces, return the length of the last word in the string.\n\nA word is a maximal substring consisting of non-space characters only.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= s.length <= 10^4\n- s consists of only English letters and spaces ' '.\n- There will be at least one word in s.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int lengthOfLastWord(String s) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def lengthOfLastWord(self, s: str) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int lengthOfLastWord(string s) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Strings")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("s = \"Hello World\"", "5", true, false, "The last word is 'World' with length 5."),
                new TestCaseCreateRequest("s = \"   fly me   to   the moon  \"", "4", true, false, "The last word is 'moon' with length 4."),
                new TestCaseCreateRequest("s = \"luffy is still joyboy\"", "6", true, false, "The last word is 'joyboy' with length 6."),
                new TestCaseCreateRequest("s = \"a\"", "1", false, true, null),
                new TestCaseCreateRequest("s = \"a \"", "1", false, true, null)
            ));
            return p;
        });

        // Problem 21: Reverse Linked List — Linked List — Easy
        seedProblemIfNotExists("Reverse Linked List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Reverse Linked List");
            p.setDescription("Given the head of a singly linked list, reverse the list, and return the reversed list.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is the range [0, 5000].\n- -5000 <= Node.val <= 5000");
            p.setStarterCode("{\"java\": \"/**\\n * Definition for singly-linked list.\\n * public class ListNode {\\n *     int val;\\n *     ListNode next;\\n *     ListNode() {}\\n *     ListNode(int val) { this.val = val; }\\n *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }\\n * }\\n */\\nclass Solution {\\n    public ListNode reverseList(ListNode head) {\\n        // Your code here\\n    }\\n}\", \"python\": \"# Definition for singly-linked list.\\n# class ListNode:\\n#     def __init__(self, val=0, next=None):\\n#         self.val = val\\n#         self.next = next\\nclass Solution:\\n    def reverseList(self, head: Optional[ListNode]) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"/**\\n * Definition for singly-linked list.\\n * struct ListNode {\\n *     int val;\\n *     ListNode *next;\\n *     ListNode() : val(0), next(nullptr) {}\\n *     ListNode(int x) : val(x), next(nullptr) {}\\n *     ListNode(int x, ListNode *next) : val(x), next(next) {}\\n * };\\n */\\nclass Solution {\\npublic:\\n    ListNode* reverseList(ListNode* head) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Recursion")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4,5]", "[5, 4, 3, 2, 1]", true, false, "List reversed"),
                new TestCaseCreateRequest("head = [1,2]", "[2, 1]", true, false, "Two-node list reversed"),
                new TestCaseCreateRequest("head = []", "[]", true, false, "Empty list returns empty"),
                new TestCaseCreateRequest("head = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [1,2,3]", "[3, 2, 1]", false, true, null)
            ));
            return p;
        });

        // Problem 22: Merge Two Sorted Lists — Linked List — Easy
        seedProblemIfNotExists("Merge Two Sorted Lists", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Merge Two Sorted Lists");
            p.setDescription("You are given the heads of two sorted linked lists list1 and list2.\n\nMerge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.\n\nReturn the head of the merged linked list.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in both lists is in the range [0, 50].\n- -100 <= Node.val <= 100\n- Both list1 and list2 are sorted in non-decreasing order.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def mergeTwoLists(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* mergeTwoLists(ListNode* list1, ListNode* list2) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Recursion")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("list1 = [1,2,4]\nlist2 = [1,3,4]", "[1, 1, 2, 3, 4, 4]", true, false, "Merged in sorted order"),
                new TestCaseCreateRequest("list1 = []\nlist2 = []", "[]", true, false, "Both lists empty"),
                new TestCaseCreateRequest("list1 = []\nlist2 = [0]", "[0]", true, false, "One list empty"),
                new TestCaseCreateRequest("list1 = [5]\nlist2 = [1,2,4]", "[1, 2, 4, 5]", false, true, null),
                new TestCaseCreateRequest("list1 = [2]\nlist2 = [1]", "[1, 2]", false, true, null)
            ));
            return p;
        });

        // Problem 23: Maximum Depth of Binary Tree — Tree — Easy
        seedProblemIfNotExists("Maximum Depth of Binary Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Maximum Depth of Binary Tree");
            p.setDescription("Given the root of a binary tree, return its maximum depth.\n\nA binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 10^4].\n- -100 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"/**\\n * Definition for a binary tree node.\\n * public class TreeNode {\\n *     int val;\\n *     TreeNode left;\\n *     TreeNode right;\\n *     TreeNode() {}\\n *     TreeNode(int val) { this.val = val; }\\n *     TreeNode(int val, TreeNode left, TreeNode right) {\\n *         this.val = val;\\n *         this.left = left;\\n *         this.right = right;\\n *     }\\n * }\\n */\\nclass Solution {\\n    public int maxDepth(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"# Definition for a binary tree node.\\n# class TreeNode:\\n#     def __init__(self, val=0, left=None, right=None):\\n#         self.val = val\\n#         self.left = left\\n#         self.right = right\\nclass Solution:\\n    def maxDepth(self, root: Optional[TreeNode]) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"/**\\n * Definition for a binary tree node.\\n * struct TreeNode {\\n *     int val;\\n *     TreeNode *left;\\n *     TreeNode *right;\\n *     TreeNode() : val(0), left(nullptr), right(nullptr) {}\\n *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}\\n *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}\\n * };\\n */\\nclass Solution {\\npublic:\\n    int maxDepth(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Binary Tree", "Recursion")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [3,9,20,null,null,15,7]", "3", true, false, "Maximum depth is 3 (3 -> 20 -> 15 or 7)"),
                new TestCaseCreateRequest("root = [1,null,2]", "2", true, false, "Maximum depth is 2"),
                new TestCaseCreateRequest("root = []", "0", false, true, null),
                new TestCaseCreateRequest("root = [0]", "1", false, true, null),
                new TestCaseCreateRequest("root = [1,2,3,4,null,null,5]", "3", false, true, null)
            ));
            return p;
        });

        // Problem 24: Invert Binary Tree — Tree — Easy
        seedProblemIfNotExists("Invert Binary Tree", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Invert Binary Tree");
            p.setDescription("Given the root of a binary tree, invert the tree, and return its root.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the tree is in the range [0, 100].\n- -100 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public TreeNode invertTree(TreeNode root) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def invertTree(self, root: Optional[TreeNode]) -> Optional[TreeNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    TreeNode* invertTree(TreeNode* root) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Tree", "Binary Tree", "Recursion")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("root = [4,2,7,1,3,6,9]", "[4, 7, 2, 9, 6, 3, 1]", true, false, "Left and right subtrees inverted recursively"),
                new TestCaseCreateRequest("root = [2,1,3]", "[2, 3, 1]", true, false, "Simple binary tree inverted"),
                new TestCaseCreateRequest("root = []", "[]", true, false, "Empty tree remains empty"),
                new TestCaseCreateRequest("root = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("root = [1,2]", "[1, null, 2]", false, true, null)
            ));
            return p;
        });

        // Problem 25: Unique Paths — Dynamic Programming — Medium
        seedProblemIfNotExists("Unique Paths", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Unique Paths");
            p.setDescription("There is a robot on an m x n grid. The robot is initially located at the top-left corner (i.e., grid[0][0]). The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.\n\nGiven the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- 1 <= m, n <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public int uniquePaths(int m, int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def uniquePaths(self, m: int, n: int) -> int:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    int uniquePaths(int m, int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Dynamic Programming", "Math", "Combinatorics")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("m = 3\nn = 7", "28", true, false, "Total paths in a 3x7 grid is 28"),
                new TestCaseCreateRequest("m = 3\nn = 2", "3", true, false, "3 ways: Right->Down->Down, Down->Down->Right, Down->Right->Down"),
                new TestCaseCreateRequest("m = 1\nn = 1", "1", false, true, null),
                new TestCaseCreateRequest("m = 1\nn = 10", "1", false, true, null),
                new TestCaseCreateRequest("m = 7\nn = 3", "28", false, true, null)
            ));
            return p;
        });

        // Problem 27: Middle of the Linked List — Linked List — Easy
        seedProblemIfNotExists("Middle of the Linked List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Middle of the Linked List");
            p.setDescription("Given the head of a singly linked list, return the middle node of the linked list.\n\nIf there are two middle nodes, return the second middle node.");
            p.setDifficulty(Difficulty.EASY);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is in the range [1, 100].\n- 1 <= Node.val <= 100");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode middleNode(ListNode head) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def middleNode(self, head: Optional[ListNode]) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* middleNode(ListNode* head) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Two Pointers")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4,5]", "[3, 4, 5]", true, false, "The middle node of the list is node 3."),
                new TestCaseCreateRequest("head = [1,2,3,4,5,6]", "[4, 5, 6]", true, false, "Since the list has two middle nodes with values 3 and 4, we return the second one."),
                new TestCaseCreateRequest("head = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [1,2]", "[2]", false, true, null),
                new TestCaseCreateRequest("head = [10,20,30,40]", "[30, 40]", false, true, null)
            ));
            return p;
        });

        // Problem 29: Delete Node in a Linked List — Linked List — Medium
        seedProblemIfNotExists("Delete Node in a Linked List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Delete Node in a Linked List");
            p.setDescription("There is a singly-linked list head and we want to delete a node in it.\n\nYou are given the node to be deleted node. You will not be given access to the first node of head.\n\nAll the values of the linked list are unique, and it is guaranteed that the given node is not the last node in the linked list.\n\nDelete the given node. Note that by deleting the node, we do not mean removing it from memory. We mean:\n- The value of the given node should not exist in the linked list.\n- The number of nodes in the linked list should decrease by one.\n- All the values before node should be in the same order.\n- All the values after node should be in the same order.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of the nodes in the given list is in the range [2, 1000].\n- -1000 <= Node.val <= 1000\n- The value of each node in the list is unique.\n- The node to be deleted is in the list and is not a tail node.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public void deleteNode(ListNode node) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def deleteNode(self, node: ListNode) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void deleteNode(ListNode* node) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [4,5,1,9]\nnode = 5", "[4, 1, 9]", true, false, "You are given the second node with value 5, the linked list should become 4 -> 1 -> 9 after calling your function."),
                new TestCaseCreateRequest("head = [4,5,1,9]\nnode = 1", "[4, 5, 9]", true, false, "You are given the third node with value 1, the linked list should become 4 -> 5 -> 9 after calling your function."),
                new TestCaseCreateRequest("head = [1,2,3,4]\nnode = 3", "[1, 2, 4]", false, true, null),
                new TestCaseCreateRequest("head = [0,1]\nnode = 0", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [-3,5,-99]\nnode = 5", "[-3, -99]", false, true, null)
            ));
            return p;
        });

        // Problem 30: Remove Nth Node From End of List — Linked List — Medium
        seedProblemIfNotExists("Remove Nth Node From End of List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Remove Nth Node From End of List");
            p.setDescription("Given the head of a linked list, remove the nth node from the end of the list and return its head.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is sz.\n- 1 <= sz <= 30\n- 0 <= Node.val <= 100\n- 1 <= n <= sz");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode removeNthFromEnd(ListNode head, int n) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* removeNthFromEnd(ListNode* head, int n) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Two Pointers")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4,5]\nn = 2", "[1, 2, 3, 5]", true, false, "The 2nd node from end is 4."),
                new TestCaseCreateRequest("head = [1]\nn = 1", "[]", true, false, "Single node removed leaves empty list."),
                new TestCaseCreateRequest("head = [1,2]\nn = 1", "[1]", true, false, "Last node removed."),
                new TestCaseCreateRequest("head = [1,2]\nn = 2", "[2]", false, true, null),
                new TestCaseCreateRequest("head = [1,2,3,4,5]\nn = 5", "[2, 3, 4, 5]", false, true, null)
            ));
            return p;
        });

        // Problem 31: Reorder List — Linked List — Medium
        seedProblemIfNotExists("Reorder List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Reorder List");
            p.setDescription("You are given the head of a singly linked-list. The list can be represented as:\n\nL0 -> L1 -> … -> Ln - 1 -> Ln\n\nReorder the list to be on the following form:\n\nL0 -> Ln -> L1 -> Ln - 1 -> L2 -> Ln - 2 -> …\n\nYou may not modify the values in the list's nodes. Only nodes themselves may be changed.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is in the range [1, 5 * 10^4].\n- 1 <= Node.val <= 1000");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public void reorderList(ListNode head) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def reorderList(self, head: Optional[ListNode]) -> None:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    void reorderList(ListNode* head) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Two Pointers", "Stack")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4]", "[1, 4, 2, 3]", true, false, "Reordered list"),
                new TestCaseCreateRequest("head = [1,2,3,4,5]", "[1, 5, 2, 4, 3]", true, false, "Reordered list"),
                new TestCaseCreateRequest("head = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [1,2]", "[1, 2]", false, true, null),
                new TestCaseCreateRequest("head = [1,2,3]", "[1, 3, 2]", false, true, null)
            ));
            return p;
        });

        // Problem 32: Odd Even Linked List — Linked List — Medium
        seedProblemIfNotExists("Odd Even Linked List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Odd Even Linked List");
            p.setDescription("Given the head of a singly linked list, group all the nodes with odd indices together followed by the nodes with even indices, and return the reordered list.\n\nThe first node is considered odd, and the second node is even, and so on.\n\nNote that the relative order inside both the even and odd groups should remain as it was in the input.\n\nYou must solve the problem in O(1) extra space complexity and O(n) time complexity.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the linked list is in the range [0, 10^4].\n- -10^6 <= Node.val <= 10^6");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode oddEvenList(ListNode head) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def oddEvenList(self, head: Optional[ListNode]) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* oddEvenList(ListNode* head) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4,5]", "[1, 3, 5, 2, 4]", true, false, "Odd nodes [1, 3, 5] followed by even nodes [2, 4]"),
                new TestCaseCreateRequest("head = [2,1,3,5,6,4,7]", "[2, 3, 6, 7, 1, 5, 4]", true, false, "Odd nodes followed by even nodes"),
                new TestCaseCreateRequest("head = []", "[]", false, true, null),
                new TestCaseCreateRequest("head = [1]", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [1,2]", "[1, 2]", false, true, null)
            ));
            return p;
        });

        // Problem 33: Rotate List — Linked List — Medium
        seedProblemIfNotExists("Rotate List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Rotate List");
            p.setDescription("Given the head of a linked list, rotate the list to the right by k places.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is in the range [0, 500].\n- -100 <= Node.val <= 100\n- 0 <= k <= 2 * 10^9");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode rotateRight(ListNode head, int k) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def rotateRight(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* rotateRight(ListNode* head, int k) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Two Pointers")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,2,3,4,5]\nk = 2", "[4, 5, 1, 2, 3]", true, false, "Rotated right by 2"),
                new TestCaseCreateRequest("head = [0,1,2]\nk = 4", "[2, 0, 1]", true, false, "Rotated right by 4 (equivalent to rotating by 1)"),
                new TestCaseCreateRequest("head = []\nk = 0", "[]", false, true, null),
                new TestCaseCreateRequest("head = [1,2]\nk = 0", "[1, 2]", false, true, null),
                new TestCaseCreateRequest("head = [1,2]\nk = 2", "[1, 2]", false, true, null)
            ));
            return p;
        });

        // Problem 34: Partition List — Linked List — Medium
        seedProblemIfNotExists("Partition List", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Partition List");
            p.setDescription("Given the head of a linked list and a value x, partition it such that all nodes less than x come before nodes greater than or equal to x.\n\nYou should preserve the original relative order of the nodes in each of the two partitions.");
            p.setDifficulty(Difficulty.MEDIUM);
            p.setTimeLimit(1000);
            p.setMemoryLimit(256);
            p.setConstraints("- The number of nodes in the list is in the range [0, 200].\n- -100 <= Node.val <= 100\n- -200 <= x <= 200");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode partition(ListNode head, int x) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def partition(self, head: Optional[ListNode], x: int) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* partition(ListNode* head, int x) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Two Pointers")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("head = [1,4,3,2,5,2]\nx = 3", "[1, 2, 2, 4, 3, 5]", true, false, "Nodes less than 3 are [1, 2, 2], nodes >= 3 are [4, 3, 5]."),
                new TestCaseCreateRequest("head = [2,1]\nx = 2", "[1, 2]", true, false, "1 comes before 2."),
                new TestCaseCreateRequest("head = []\nx = 0", "[]", false, true, null),
                new TestCaseCreateRequest("head = [1]\nx = 2", "[1]", false, true, null),
                new TestCaseCreateRequest("head = [1,1]\nx = 2", "[1, 1]", false, true, null)
            ));
            return p;
        });

        // Problem 35: Merge k Sorted Lists — Linked List — Hard
        seedProblemIfNotExists("Merge k Sorted Lists", () -> {
            ProblemCreateRequest p = new ProblemCreateRequest();
            p.setTitle("Merge k Sorted Lists");
            p.setDescription("You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.\n\nMerge all the linked-lists into one sorted linked-list and return it.");
            p.setDifficulty(Difficulty.HARD);
            p.setTimeLimit(2000);
            p.setMemoryLimit(256);
            p.setConstraints("- k == lists.length\n- 0 <= k <= 10^4\n- 0 <= lists[i].length <= 500\n- -10^4 <= lists[i][j] <= 10^4\n- lists[i] is sorted in ascending order.\n- The sum of lists[i].length will not exceed 10^4.");
            p.setStarterCode("{\"java\": \"class Solution {\\n    public ListNode mergeKLists(ListNode[] lists) {\\n        // Your code here\\n    }\\n}\", \"python\": \"class Solution:\\n    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:\\n        # Your code here\\n        pass\", \"cpp\": \"class Solution {\\npublic:\\n    ListNode* mergeKLists(vector<ListNode*>& lists) {\\n        // Your code here\\n    }\\n};\"}");
            p.setTopics(new HashSet<>(Arrays.asList("Linked List", "Divide and Conquer", "Heap")));
            p.setTestCases(Arrays.asList(
                new TestCaseCreateRequest("lists = [[1,4,5],[1,3,4],[2,6]]", "[1, 1, 2, 3, 4, 4, 5, 6]", true, false, "Merging them into one sorted list"),
                new TestCaseCreateRequest("lists = []", "[]", true, false, "Empty array of lists"),
                new TestCaseCreateRequest("lists = [[]]", "[]", true, false, "Array containing one empty list"),
                new TestCaseCreateRequest("lists = [[1]]", "[1]", false, true, null),
                new TestCaseCreateRequest("lists = [[-1,1],[-2,2],[-3,3]]", "[-3, -2, -1, 1, 2, 3]", false, true, null)
            ));
            return p;
        });

        // Seed Batches 4, 5, and 6 (Problems 36 - 65)
        if (batch4To6Seeder != null) {
            batch4To6Seeder.seedAll(problemService, problemRepository, testCaseRepository);
        }

        // Seed Batches 7, 8, 9, and 10 (Problems 66 - 100)
        if (batch7To10Seeder != null) {
            batch7To10Seeder.seedAll(problemService, problemRepository, testCaseRepository);
        }
    }

    private void seedProblemIfNotExists(String title, ProblemSupplier supplier) {
        Problem existingProblem = problemRepository.findByTitle(title);
        if (existingProblem != null) {
            logger.info("Problem '{}' already exists, syncing test cases if needed.", title);
            syncTestCases(existingProblem, supplier.get());
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

    private void syncTestCases(Problem problem, ProblemCreateRequest request) {
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
