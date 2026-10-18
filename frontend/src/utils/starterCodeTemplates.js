/**
 * Language-specific starter code templates for DSA Problem Solving.
 * Provides fallback templates for the five known problems if the backend
 * does not return them, as well as a helper function to extract or fallback.
 */

export const DEFAULT_PROBLEM_TEMPLATES = {
  java: `class Solution {
    // Write your code here
}`,
  python: `class Solution:
    # Write your code here
    pass`,
  cpp: `class Solution {
public:
    // Write your code here
};`,
};

export const PROBLEM_FALLBACK_TEMPLATES = {
  'Two Sum': {
    java: `class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Your code here
    }
}`,
    python: `class Solution:
    def twoSum(self, nums: List[int], target: int) -> List[int]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        // Your code here
    }
};`,
  },
  'Reverse a String': {
    java: `class Solution {
    public void reverseString(char[] s) {
        // Your code here
    }
}`,
    python: `class Solution:
    def reverseString(self, s: List[str]) -> None:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    void reverseString(vector<char>& s) {
        // Your code here
    }
};`,
  },
  'Valid Parentheses': {
    java: `class Solution {
    public boolean isValid(String s) {
        // Your code here
    }
}`,
    python: `class Solution:
    def isValid(self, s: str) -> bool:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    bool isValid(string s) {
        // Your code here
    }
};`,
  },
  'Binary Search': {
    java: `class Solution {
    public int search(int[] nums, int target) {
        // Your code here
    }
}`,
    python: `class Solution:
    def search(self, nums: List[int], target: int) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int search(vector<int>& nums, int target) {
        // Your code here
    }
};`,
  },
  'Maximum Subarray Sum': {
    java: `class Solution {
    public int maxSubArray(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int maxSubArray(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Contains Duplicate': {
    java: `class Solution {
    public boolean containsDuplicate(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def containsDuplicate(self, nums: List[int]) -> bool:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    bool containsDuplicate(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Valid Anagram': {
    java: `class Solution {
    public boolean isAnagram(String s, String t) {
        // Your code here
    }
}`,
    python: `class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    bool isAnagram(string s, string t) {
        // Your code here
    }
};`,
  },
  'Best Time to Buy and Sell Stock': {
    java: `class Solution {
    public int maxProfit(int[] prices) {
        // Your code here
    }
}`,
    python: `class Solution:
    def maxProfit(self, prices: List[int]) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int maxProfit(vector<int>& prices) {
        // Your code here
    }
};`,
  },
  'Valid Palindrome': {
    java: `class Solution {
    public boolean isPalindrome(String s) {
        // Your code here
    }
}`,
    python: `class Solution:
    def isPalindrome(self, s: str) -> bool:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    bool isPalindrome(string s) {
        // Your code here
    }
};`,
  },
  'Search Insert Position': {
    java: `class Solution {
    public int searchInsert(int[] nums, int target) {
        // Your code here
    }
}`,
    python: `class Solution:
    def searchInsert(self, nums: List[int], target: int) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int searchInsert(vector<int>& nums, int target) {
        // Your code here
    }
};`,
  },
  'Move Zeroes': {
    java: `class Solution {
    public void moveZeroes(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def moveZeroes(self, nums: List[int]) -> None:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    void moveZeroes(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Climbing Stairs': {
    java: `class Solution {
    public int climbStairs(int n) {
        // Your code here
    }
}`,
    python: `class Solution:
    def climbStairs(self, n: int) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int climbStairs(int n) {
        // Your code here
    }
};`,
  },
  'Single Number': {
    java: `class Solution {
    public int singleNumber(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int singleNumber(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Two Sum II - Input Array Is Sorted': {
    java: `class Solution {
    public int[] twoSum(int[] numbers, int target) {
        // Your code here
    }
}`,
    python: `class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    vector<int> twoSum(vector<int>& numbers, int target) {
        // Your code here
    }
};`,
  },
  'Product of Array Except Self': {
    java: `class Solution {
    public int[] productExceptSelf(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def productExceptSelf(self, nums: List[int]) -> List[int]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    vector<int> productExceptSelf(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Container With Most Water': {
    java: `class Solution {
    public int maxArea(int[] height) {
        // Your code here
    }
}`,
    python: `class Solution:
    def maxArea(self, height: List[int]) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int maxArea(vector<int>& height) {
        // Your code here
    }
};`,
  },
  'Find Minimum in Rotated Sorted Array': {
    java: `class Solution {
    public int findMin(int[] nums) {
        // Your code here
    }
}`,
    python: `class Solution:
    def findMin(self, nums: List[int]) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int findMin(vector<int>& nums) {
        // Your code here
    }
};`,
  },
  'Search in Rotated Sorted Array': {
    java: `class Solution {
    public int search(int[] nums, int target) {
        // Your code here
    }
}`,
    python: `class Solution:
    def search(self, nums: List[int], target: int) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int search(vector<int>& nums, int target) {
        // Your code here
    }
};`,
  },
  'Longest Common Prefix': {
    java: `class Solution {
    public String longestCommonPrefix(String[] strs) {
        // Your code here
    }
}`,
    python: `class Solution:
    def longestCommonPrefix(self, strs: List[str]) -> str:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    string longestCommonPrefix(vector<string>& strs) {
        // Your code here
    }
};`,
  },
  'Length of Last Word': {
    java: `class Solution {
    public int lengthOfLastWord(String s) {
        // Your code here
    }
}`,
    python: `class Solution:
    def lengthOfLastWord(self, s: str) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int lengthOfLastWord(string s) {
        // Your code here
    }
};`,
  },
  'Reverse Linked List': {
    java: `/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode reverseList(ListNode head) {
        // Your code here
    }
}`,
    python: `# Definition for singly-linked list.
# class ListNode:
#     def __init__(self, val=0, next=None):
#         self.val = val
#         self.next = next
class Solution:
    def reverseList(self, head: Optional[ListNode]) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
    ListNode* reverseList(ListNode* head) {
        // Your code here
    }
};`,
  },
  'Merge Two Sorted Lists': {
    java: `class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        // Your code here
    }
}`,
    python: `class Solution:
    def mergeTwoLists(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* mergeTwoLists(ListNode* list1, ListNode* list2) {
        // Your code here
    }
};`,
  },
  'Maximum Depth of Binary Tree': {
    java: `/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public int maxDepth(TreeNode root) {
        // Your code here
    }
}`,
    python: `# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution:
    def maxDepth(self, root: Optional[TreeNode]) -> int:
        # Your code here
        pass`,
    cpp: `/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */
class Solution {
public:
    int maxDepth(TreeNode* root) {
        // Your code here
    }
};`,
  },
  'Invert Binary Tree': {
    java: `class Solution {
    public TreeNode invertTree(TreeNode root) {
        // Your code here
    }
}`,
    python: `class Solution:
    def invertTree(self, root: Optional[TreeNode]) -> Optional[TreeNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    TreeNode* invertTree(TreeNode* root) {
        // Your code here
    }
};`,
  },
  'Unique Paths': {
    java: `class Solution {
    public int uniquePaths(int m, int n) {
        // Your code here
    }
}`,
    python: `class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    int uniquePaths(int m, int n) {
        // Your code here
    }
};`,
  },
  'Middle of the Linked List': {
    java: `class Solution {
    public ListNode middleNode(ListNode head) {
        // Your code here
    }
}`,
    python: `class Solution:
    def middleNode(self, head: Optional[ListNode]) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* middleNode(ListNode* head) {
        // Your code here
    }
};`,
  },
  'Delete Node in a Linked List': {
    java: `class Solution {
    public void deleteNode(ListNode node) {
        // Your code here
    }
}`,
    python: `class Solution:
    def deleteNode(self, node: ListNode) -> None:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    void deleteNode(ListNode* node) {
        // Your code here
    }
};`,
  },
  'Remove Nth Node From End of List': {
    java: `class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // Your code here
    }
}`,
    python: `class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* removeNthFromEnd(ListNode* head, int n) {
        // Your code here
    }
};`,
  },
  'Reorder List': {
    java: `class Solution {
    public void reorderList(ListNode head) {
        // Your code here
    }
}`,
    python: `class Solution:
    def reorderList(self, head: Optional[ListNode]) -> None:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    void reorderList(ListNode* head) {
        // Your code here
    }
};`,
  },
  'Odd Even Linked List': {
    java: `class Solution {
    public ListNode oddEvenList(ListNode head) {
        // Your code here
    }
}`,
    python: `class Solution:
    def oddEvenList(self, head: Optional[ListNode]) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* oddEvenList(ListNode* head) {
        // Your code here
    }
};`,
  },
  'Rotate List': {
    java: `class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        // Your code here
    }
}`,
    python: `class Solution:
    def rotateRight(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* rotateRight(ListNode* head, int k) {
        // Your code here
    }
};`,
  },
  'Partition List': {
    java: `class Solution {
    public ListNode partition(ListNode head, int x) {
        // Your code here
    }
}`,
    python: `class Solution:
    def partition(self, head: Optional[ListNode], x: int) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* partition(ListNode* head, int x) {
        // Your code here
    }
};`,
  },
  'Merge k Sorted Lists': {
    java: `class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        // Your code here
    }
}`,
    python: `class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        # Your code here
        pass`,
    cpp: `class Solution {
public:
    ListNode* mergeKLists(vector<ListNode*>& lists) {
        // Your code here
    }
};`,
  },
};

/**
 * Extracts starter code for a specific language from a problem object.
 * Priority:
 * 1. problem.starterCode (parsed JSON or object from backend)
 * 2. Fallback template by problem title
 * 3. Fallback generic DSA solution template
 */
export function getProblemStarterCode(problem, language) {
  if (!problem) return '';
  const lang = String(language || 'java').toLowerCase();

  // 1. Try parsing problem.starterCode from backend
  if (problem.starterCode) {
    let parsed = null;
    if (typeof problem.starterCode === 'object') {
      parsed = problem.starterCode;
    } else if (typeof problem.starterCode === 'string') {
      try {
        parsed = JSON.parse(problem.starterCode);
      } catch (e) {
        // Might be plain text if single language
        if (problem.starterCode.trim().length > 0) {
          parsed = { [lang]: problem.starterCode };
        }
      }
    }

    if (parsed && typeof parsed === 'object') {
      const codeForLang = parsed[lang];
      if (typeof codeForLang === 'string' && codeForLang.trim().length > 0) {
        return codeForLang;
      }
    }
  }

  // 2. Try problem title match
  if (problem.title && PROBLEM_FALLBACK_TEMPLATES[problem.title.trim()]) {
    const templates = PROBLEM_FALLBACK_TEMPLATES[problem.title.trim()];
    if (templates[lang]) {
      return templates[lang];
    }
  }

  // 3. Fallback to generic DSA problem template
  return DEFAULT_PROBLEM_TEMPLATES[lang] || '';
}
